package filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.FileVisitResult;
import java.nio.file.SimpleFileVisitor;
import java.util.ArrayList;


import models.*;

public class FileSystem implements FileManagerService{
    private enum ClipboardMode {COPY, CUT};
    private FolderItem workspace;
    private final ArrayList<FileSystemItem> clipboard = new ArrayList<>();
    private ClipboardMode clipboardMode;

    

    @Override 
    public FolderItem openWorkspace(Path rootFolder) throws IOException{
        if(!Files.isDirectory(rootFolder)){
            throw new IOException("Not a valid Folder: " + rootFolder);
        } 

        workspace = new FolderItem(rootFolder);
        return workspace;
    }

    @Override 
    public ArrayList<FileSystemItem> listContents(Path folder) throws IOException{
        return new FolderItem(folder).getChildren();
    }

    @Override 
    public FileItem createFile(Path folder, String name) throws IOException{
        return new FolderItem(folder).createFile(name);
    }

    @Override 
    public FolderItem createFolder(Path folder, String name) throws IOException{
        return new FolderItem(folder).createFolder(name);
    }

    @Override 
    public void rename(FileSystemItem item, String newName) throws IOException{
        item.renameFile(newName);
    }

    @Override 
    public void move(FileSystemItem item, Path targetFolder) throws IOException{
        item.moveFile(targetFolder);
    }

    @Override
    public void delete(FileSystemItem item) throws IOException{
        item.delete();
    }

    @Override 
    public void open(FileItem item) throws IOException{
        item.open();
    }

    @Override
    public BasicFileAttributes getMetadata(FileSystemItem item) throws IOException{
        return item.getMetadata();
    }

    @Override 
    public void copy(ArrayList<FileSystemItem> items){
        clipboard.clear();
        clipboard.addAll(items);
        clipboardMode = ClipboardMode.COPY;
    }

    @Override 
    public void cut(ArrayList<FileSystemItem> items){
        clipboard.clear();
        clipboard.addAll(items);
        clipboardMode = ClipboardMode.CUT;
    }


    @Override 
    public boolean canPaste(){
        return !clipboard.isEmpty();
    }

    @Override 
    public ArrayList<FileSystemItem> paste(Path targetFolder) throws IOException{
        if(!canPaste()) throw new IllegalStateException("Clipboard is Empty!");

        ArrayList<FileSystemItem> results = new ArrayList<>();
        for(FileSystemItem item : clipboard){
            if(clipboardMode == ClipboardMode.COPY){
                results.add(item.copyTo(targetFolder));
            } else{
                if(!item.getPath().getParent().equals(targetFolder.toAbsolutePath())){
                    item.moveFile(targetFolder);
                }
                results.add(item);
            }
        }
        if(clipboardMode == ClipboardMode.CUT){
            clipboard.clear();
        }
        return results;
    }

    @Override 
    public FileSystemItem duplicate(FileSystemItem item) throws IOException{
        Path parent = item.getPath().toAbsolutePath().getParent();
        if(parent == null){
            throw new IOException("Cannot Duplicate, Item has no parent Folder!");
        }
        
        return item.copyTo(parent);
    }

    @Override 
    public ArrayList<FileSystemItem> search(String query) throws IOException{
        if(workspace == null){
            throw new IllegalStateException("No Workspace is Open");
        }

        ArrayList<FileSystemItem> results = new ArrayList<>();
        if(query == null || query.isBlank()){
            return results;
        }

        String needle = query.trim().toLowerCase();
        Path root = workspace.getPath();

        Files.walkFileTree(root, new SimpleFileVisitor<Path>(){
            @Override 
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attr){
                if(!dir.equals(root) && matches(dir, needle)){
                    results.add(new FolderItem(dir));
                }
                return FileVisitResult.CONTINUE;
            }

            @Override 
            public FileVisitResult visitFile(Path file, BasicFileAttributes attr){
                if(matches(file, needle)){
                    results.add(new FileItem(file));
                }
                return FileVisitResult.CONTINUE;
            }

            @Override 
            public FileVisitResult visitFileFailed(Path file, IOException e){
                return FileVisitResult.CONTINUE;
            }
        });

        return results;
    }

    private static boolean matches(Path p, String needle){
        Path name = p.getFileName();
        return name != null && name.toString().toLowerCase().contains(needle);
    }
}
