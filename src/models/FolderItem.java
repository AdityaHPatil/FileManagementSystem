package models;

import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Stream;

public class FolderItem extends FileSystemItem{
    public FolderItem(Path path){
        super(path);
    }

    public ArrayList<FileSystemItem> getChildren() throws IOException{
        ArrayList<FileSystemItem> children = new ArrayList<>();
    
        try(Stream<Path> stream = Files.list(path)){
            stream.forEach(p -> {
                if(Files.isDirectory(p)){
                    children.add(new FolderItem(p));
                } else{
                    children.add(new FileItem(p));
                }
            });
        }

        children.sort(
            Comparator.comparing((FileSystemItem item) -> !(item instanceof FolderItem))
            .thenComparing(item -> item.getName().toLowerCase())
        );

        return children;
    }

    public FileItem createFile(String fileName) throws IOException{
        Path newFile = Files.createFile(path.resolve(fileName));
        return new FileItem(newFile);
    }

    public FolderItem createFolder(String folderName) throws IOException{
        Path newFolder = Files.createDirectory(path.resolve(folderName));
        return new FolderItem(newFolder);
    }

    @Override
    public void delete() throws IOException{
        Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
            @Override 
            public FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws IOException{
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override 
            public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException{
                if(e == null){
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else{
                    throw e;
                }
            }
        });
    }

}