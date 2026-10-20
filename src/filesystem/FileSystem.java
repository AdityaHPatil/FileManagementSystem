package filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
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
}
