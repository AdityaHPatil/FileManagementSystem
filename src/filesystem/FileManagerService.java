package filesystem;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import models.*;
import java.util.ArrayList;

public interface FileManagerService {
    FolderItem openWorkspace (Path rootFolder) throws IOException;
    ArrayList<FileSystemItem> listContents(Path folder) throws IOException;

    FileItem createFile(Path folder, String name) throws IOException;
    FolderItem createFolder(Path folder, String name) throws IOException;
    void rename(FileSystemItem item, String newName) throws IOException;
    void move(FileSystemItem item, Path targetFolder) throws IOException;
    void delete(FileSystemItem item) throws IOException;
    void open(FileItem item) throws IOException;
    BasicFileAttributes getMetadata(FileSystemItem item) throws IOException;
    void copy(ArrayList<FileSystemItem> items);
    void cut(ArrayList<FileSystemItem> items);
    boolean canPaste();
    ArrayList<FileSystemItem> paste(Path targetFolder) throws IOException;

    FileSystemItem duplicate(FileSystemItem item) throws IOException;
    ArrayList<FileSystemItem> search(String query) throws IOException;


}
