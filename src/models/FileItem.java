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

}