package models;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.*;

public abstract class FileSystemItem{
    protected Path path;

    protected FileSystemItem(Path path){
        this.path = path;
    }

    public String getName(){
        return (path.getFileName().toString());
    }

    public Path getPath(){
        return path;
    }

    public void renameFile(String newName) throws IOException{
        Path newPath = path.resolveSibling(newName);
        Files.move(path, newPath);
        path = newPath;
    }

    public void moveFile(Path targetFolder) throws IOException{
        Path newPath = targetFolder.resolve(path.getFileName());
        Files.move(path, newPath);
        path = newPath;
    }

    public BasicFileAttributes getMetadata() throws IOException{
        return Files.readAttributes(path, BasicFileAttributes.class);
    }

    public abstract void delete() throws IOException;
}