package com.gloic.dazsorter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DazFileHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DazFileHelper.class);
    private final ConfigurationService configurationService;

    private AtomicInteger nbIMFiles;
    private AtomicInteger nbDazFolders;
    private AtomicInteger nbPoserFolders;
    private AtomicInteger nbArchives;
    private AtomicInteger nbTrashFiles;

    @Autowired
    DazFileHelper(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    void init() {
        nbIMFiles = new AtomicInteger();
        nbDazFolders = new AtomicInteger();
        nbPoserFolders = new AtomicInteger();
        nbArchives = new AtomicInteger();
        nbTrashFiles = new AtomicInteger();
    }

    /**
     * @param filename
     * @return true if the filename matches with the pattern of a "IM" file
     */
    public boolean isIMFile(String filename) {
        return filename.startsWith("IM00");
    }

    /**
     * @param filename
     * @return true if the file is an archive
     */
    public boolean isArchive(String filename) {
        String ext = FilenameUtils.getExtension(filename);
        return ext.equalsIgnoreCase("zip") || ext.equalsIgnoreCase("rar") || ext.equalsIgnoreCase("7z");
    }

    /**
     * For Daz and Poser :
     * Check if the given folder's name matches with the list of pre-defined folder names in the configuration
     *
     * @param possibleFolders
     * @param file
     * @return true if the folder matches with the requirements
     */
    public boolean isEligibleFolder(List<String> possibleFolders, File file) {
        return possibleFolders.stream().anyMatch(x -> x.equalsIgnoreCase(file.getName()));
    }

    /**
     * Move a file or a folder to the given destination
     *
     * @param file
     * @param destination
     */
    private void moveToFolder(File file, String destination) {
        LOGGER.debug("Moving {} to {}", file.getName(), destination);

        try {
            if (file.isDirectory()) {
                // Move folder and content
                FileUtils.copyDirectory(file, new File(destination + File.separator + file.getName()));
                FileUtils.deleteDirectory(file);
            } else {
                // Move file
                if (!file.canWrite()) {
                    // Some files appear as read-only. By-passing it by changing this property
                    file.setWritable(true);
                }
                Files.move(file.toPath(), Paths.get(destination + File.separator + file.getName()), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            LOGGER.error("Impossible to move file or directory '{}'", file.getPath(), e);
        }
    }

    public void moveToArchive(File file) {
        moveToFolder(file, configurationService.getArchivesFolder());
        nbArchives.getAndIncrement();
    }

    public void moveToIM(File file) {
        moveToFolder(file, configurationService.getIMFolder());
        nbIMFiles.getAndIncrement();
    }

    public void moveToTrash(File file) {
        moveToFolder(file, configurationService.getTrashFolder());
        nbTrashFiles.getAndIncrement();
    }

    public void moveToLibrary(File file) {
        moveToFolder(file, configurationService.getLibraryFolder());
        nbDazFolders.getAndIncrement();
    }

    public void moveToPoser(File file) {
        moveToFolder(file, configurationService.getPoserFolder());
        nbPoserFolders.getAndIncrement();
    }

    public void logStats() {
        StringBuilder sb = new StringBuilder()
                .append("\n\t- Number of IM files moved : ").append(nbIMFiles)
                .append("\n\t- Number of DAZ folders moved : ").append(nbDazFolders)
                .append("\n\t- Number of Poser folders moved : ").append(nbPoserFolders)
                .append("\n\t- Number of archives moved : ").append(nbArchives)
                .append("\n\t- Number of files moved to trash : ").append(nbTrashFiles);
        LOGGER.info(sb.toString());
    }
}
