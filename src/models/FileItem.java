package models;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.*;

public class FileItem extends FileSystemItem{
  public FileItem(Path path){
    super(path);
  }

  @Override 
  public void delete() throws IOException{
    Files.delete(path);
  }

  public void open() throws IOException{
    Desktop.getDesktop().open(path.toFile());
  }

  @Override 
  public FileItem copyTo(Path targetFolder) throws IOException{
    Path target = resolveUniqueTarget(targetFolder);
    Files.copy(path, target);
    return new FileItem(target);
  }

  //We need to write a function to reveal a file or folder in the explorer but for that I think some of the GUI
  //needs to be built first

  

}