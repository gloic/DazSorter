package com.gloic.dazsorter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("configuration")
public class ConfigurationMapper {

    private String workingFolder;
    private String sevenZipPath;
    private List<String> autoDeleteFilenames;
    private List<String> autoDeleteFolders;
    private List<String> libraryPossiblesFolders;
    private List<String> poserPossiblesFolders;
    private List<String> autoTrashExtensions;

    public List<String> getAutoDeleteFilenames() {
        return autoDeleteFilenames;
    }

    public void setAutoDeleteFilenames(List<String> autoDeleteFilenames) {
        this.autoDeleteFilenames = autoDeleteFilenames;
    }

    public List<String> getAutoDeleteFolders() {
        return autoDeleteFolders;
    }

    public void setAutoDeleteFolders(List<String> autoDeleteFolders) {
        this.autoDeleteFolders = autoDeleteFolders;
    }

    public List<String> getLibraryPossiblesFolders() {
        return libraryPossiblesFolders;
    }

    public void setLibraryPossiblesFolders(List<String> libraryPossiblesFolders) {
        this.libraryPossiblesFolders = libraryPossiblesFolders;
    }

    public List<String> getPoserPossiblesFolders() {
        return poserPossiblesFolders;
    }

    public void setPoserPossiblesFolders(List<String> poserPossiblesFolders) {
        this.poserPossiblesFolders = poserPossiblesFolders;
    }

    public String getWorkingFolder() {
        return workingFolder;
    }

    public void setWorkingFolder(String workingFolder) {
        this.workingFolder = workingFolder;
    }

    public List<String> getAutoTrashExtensions() {
        return autoTrashExtensions;
    }

    public void setAutoTrashExtensions(List<String> autoTrashExtensions) {
        this.autoTrashExtensions = autoTrashExtensions;
    }

    public String getSevenZipPath() {
        return sevenZipPath;
    }

    public void setSevenZipPath(String sevenZipPath) {
        this.sevenZipPath = sevenZipPath;
    }
}
