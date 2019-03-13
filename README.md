# DazSorter
This project permits to sort and extract freebies downloaded for DazStudio.

The differents files and folders are moved into their dedicated destinations :
- IM Files
- Library
- Library for Poser (Runtime)

![Screenshot](https://github.com/gloic/DazSorter/blob/master/screenshot.png)


Requirements: 
- Java 8 or more
- Windows
- Permission in the folder
- 7zip (optional)

Preparation:
- Create a folder where you want, the tool will create subfolders automatically

Configuration:
- Edit the file application.yml
- Replace the value of "workingFolder" by the path of your directory
- Replace the value of "sevenZipPath" by the main folder of 7zip (7z.exe must exists)
- Place some file and folder in "unsorted" folder

Execution:
- Open a command prompt where you placed the jar and the config file
- Type : "java -jar daz-sorter-0.0.1-SNAPSHOT.jar"
- Wait until the end

Warning:
This tool is still in beta, some files can be forgotten or misplaced. As a security, nothing is deleted, the files are just placed in the directory "trash" for manual reviews.
