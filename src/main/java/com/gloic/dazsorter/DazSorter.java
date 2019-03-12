package com.gloic.dazsorter;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Objects;

@Component
public class DazSorter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DazSorter.class);

    private final ConfigurationService configurationService;
    private final DazFileHelper dazFileHelper;

    @Autowired
    public DazSorter(ConfigurationService configurationService, DazFileHelper dazFileHelper) {
        this.configurationService = configurationService;
        this.dazFileHelper = dazFileHelper;
    }

    void run() throws IOException {
        String unsorted = configurationService.getUnsortedFolder();
        dazFileHelper.init();

        StopWatch sw = new StopWatch();
        sw.start();

        // Get archives only
        LOGGER.info("Processing archives");
        Files.walk(Paths.get(unsorted))
                .map(Path::toFile)
                .filter(f -> f.isFile() && dazFileHelper.isArchive(f.getName()))
                .forEach(this::processArchive);

        LOGGER.info("Beginning process");
        processPath(unsorted);

        // Clear empty folders
        LOGGER.info("Cleaning empty folders");
        Files.walk(Paths.get(unsorted))
                .map(Path::toFile)
                .filter(f -> (f.isDirectory() && !f.getAbsolutePath().equals(configurationService.getUnsortedFolder()))
                        || configurationService.getAutoDeleteFolders().contains(f.getName()))
                .sorted(Comparator.reverseOrder())
                .forEach(d -> {
                    if (Objects.requireNonNull(d.listFiles()).length == 0) {
                        dazFileHelper.moveToTrash(d);
                    }
                });

        // Trash leftovers from unsorted folder (root folder only)
        LOGGER.info("Cleaning leftovers");
        Files.list(Paths.get(unsorted))
                .filter(p -> Files.isRegularFile(p) && configurationService.getAutoTrashExtensions().contains(FilenameUtils.getExtension(p.toFile().getName())))
                .map(Path::toFile)
                .forEach(dazFileHelper::moveToTrash);


        // Log the process times and counters
        sw.stop();
        LOGGER.info("Work done in {}s.", sw.getTotalTimeSeconds());
        dazFileHelper.logStats();
    }

    private void processPath(String pathStr) throws IOException {
        Files.walk(Paths.get(pathStr))
                .map(Path::toFile)
                .forEach(f -> {
                    // Process files
                    if (f.isFile()) {
                        // Auto trash
                        if (configurationService.getAutoDeleteFilenames().contains(f.getName())) {
                            dazFileHelper.moveToTrash(f);
                        }
                        // Process archive
                        if (dazFileHelper.isArchive(f.getName())) {
                            processArchive(f);
                        }
                    } else if (f.isDirectory() /*&& !f.getAbsolutePath().equals(pathStr)*/) {
                        // Move to Library (Daz)
                        if (dazFileHelper.isEligibleFolder(configurationService.getLibraryPossiblesFolders(), f)) {
                            dazFileHelper.moveToLibrary(f);
                        } else if (dazFileHelper.isEligibleFolder(configurationService.getPoserPossiblesFolders(), f)) {
                            // Move to Runtime (Poser)
                            dazFileHelper.moveToPoser(f);
                        }
                    }
                });
    }

    private void processArchive(File f) {
        if (dazFileHelper.isIMFile(f.getName())) {
            // if archive matching with IM*** pattern : move to DL folder
            dazFileHelper.moveToIM(f);
        } else if (configurationService.canExtract()) {
            decompress(f);
        } else {
            dazFileHelper.moveToArchive(f);
        }
    }

    private void decompress(File archive) {
        LOGGER.info("Decompressing '{}'", archive.getName());

        try {
            String destination = configurationService.getUnsortedFolder() + File.separator + FilenameUtils.removeExtension(archive.getName());
            Process process = new ProcessBuilder(configurationService.getSevenZipExec(), "x", archive.getAbsolutePath(), "-o" + destination, "-y").start();
            int exitValue = process.waitFor();
            if (exitValue != 0) {
                throw new IOException("Extraction failed");
            }

            dazFileHelper.moveToTrash(archive);
            processPath(destination);
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Unable to decompress file {}", archive.getName(), e);
            dazFileHelper.moveToArchive(archive);
        }
    }
}
