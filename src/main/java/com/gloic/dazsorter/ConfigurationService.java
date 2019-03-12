package com.gloic.dazsorter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
//@EnableConfigurationProperties
//@ConfigurationProperties("configuration")
public class ConfigurationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);

    private final ConfigurationMapper configurationMapper;
    private boolean canExtract = true;

    @Autowired
    public ConfigurationService(ConfigurationMapper configurationMapper) {
        this.configurationMapper = configurationMapper;
    }

    public String getSevenZipExec() {
        return configurationMapper.getSevenZipPath() + File.separator + "7z.exe";
    }

    private String getWorkingFolder() {
        String workingFolder = configurationMapper.getWorkingFolder();
        return workingFolder.endsWith(File.separator) ? workingFolder : workingFolder + File.separator;
    }

    public String getLibraryFolder() {
        return getWorkingFolder() + "Library";
    }

    public String getUnsortedFolder() {
        return getWorkingFolder() + "Unsorted";
    }

    public String getArchivesFolder() {
        return getWorkingFolder() + "Archives";
    }

    public String getIMFolder() {
        return getWorkingFolder() + "IM";
    }

    public String getTrashFolder() {
        return getWorkingFolder() + "Trash";
    }

    public String getPoserFolder() {
        return getLibraryFolder() + File.separator + "Runtime";
    }

    public List<String> getAutoDeleteFolders() {
        return configurationMapper.getAutoDeleteFolders();
    }

    public List<String> getAutoTrashExtensions() {
        return configurationMapper.getAutoTrashExtensions();
    }


    public List<String> getAutoDeleteFilenames() {
        return configurationMapper.getAutoDeleteFilenames();
    }

    public List<String> getPoserPossiblesFolders() {
        return configurationMapper.getPoserPossiblesFolders();
    }

    public List<String> getLibraryPossiblesFolders() {
        return configurationMapper.getLibraryPossiblesFolders();
    }

    @PostConstruct
    private void postConstruct() {
        // Init destinations
        try {
            createDir(getIMFolder());
            createDir(getLibraryFolder());
            createDir(getPoserFolder());
            createDir(getUnsortedFolder());
            createDir(getArchivesFolder());
            createDir(getTrashFolder());
        } catch (IOException e) {
            throw new RuntimeException("Impossible to create default folders", e);
        }
        LOGGER.info("Output folders created");

        // Validate configuration
        File sevenZipExecutable = new File(getSevenZipExec());
        if (!sevenZipExecutable.exists() || !sevenZipExecutable.canExecute()) {
            canExtract = false;
            LOGGER.warn("7z executable cannot be found or is not executable, not decompression will be done");
        }
    }

    private void createDir(String downloadsFolder) throws IOException {
        Files.createDirectories(Paths.get(downloadsFolder));
    }

    public boolean canExtract() {
        return canExtract;
    }
}
