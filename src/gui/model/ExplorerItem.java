package gui.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Model representing a file or folder in the File Explorer GUI.
 */
public class ExplorerItem {
    private String name;
    private String path;
    private boolean isDirectory;
    private long sizeBytes;
    private LocalDateTime lastModified;
    private LocalDateTime dateCreated;
    private String type;
    private String permissions;
    private ExplorerItem parent;
    private final List<ExplorerItem> children = new ArrayList<>();
    private final List<String> tags = new ArrayList<>();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy  hh:mm a");

    public ExplorerItem(String name, String path, boolean isDirectory, long sizeBytes, LocalDateTime lastModified, String type) {
        this.name = name;
        this.path = path;
        this.isDirectory = isDirectory;
        this.sizeBytes = sizeBytes;
        this.lastModified = lastModified != null ? lastModified : LocalDateTime.now();
        this.dateCreated = this.lastModified.minusDays(3);
        this.type = type != null ? type : (isDirectory ? "File Folder" : "File");
        this.permissions = isDirectory ? "drwxr-xr-x" : "-rw-r--r--";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public ExplorerItem getParent() {
        return parent;
    }

    public void setParent(ExplorerItem parent) {
        this.parent = parent;
    }

    public List<ExplorerItem> getChildren() {
        return children;
    }

    public void addChild(ExplorerItem child) {
        child.setParent(this);
        children.add(child);
    }

    public void removeChild(ExplorerItem child) {
        children.remove(child);
        child.setParent(null);
    }

    public List<String> getTags() {
        return tags;
    }

    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public String getExtension() {
        if (isDirectory) return "";
        int dotIndex = name.lastIndexOf('.');
        return (dotIndex > 0 && dotIndex < name.length() - 1) ? name.substring(dotIndex + 1).toLowerCase() : "";
    }

    public String getFormattedSize() {
        if (isDirectory) {
            int childCount = children.size();
            return childCount + (childCount == 1 ? " item" : " items");
        }
        if (sizeBytes < 1024) {
            return sizeBytes + " B";
        }
        int exp = (int) (Math.log(sizeBytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", sizeBytes / Math.pow(1024, exp), pre);
    }

    public String getFormattedModifiedDate() {
        return lastModified != null ? lastModified.format(DATE_FORMATTER) : "--";
    }

    public String getFormattedCreatedDate() {
        return dateCreated != null ? dateCreated.format(DATE_FORMATTER) : "--";
    }

    public String getFileCategory() {
        if (isDirectory) return "folder";
        String ext = getExtension();
        switch (ext) {
            case "java":
            case "py":
            case "js":
            case "ts":
            case "html":
            case "css":
            case "json":
            case "xml":
            case "c":
            case "cpp":
                return "code";
            case "pdf":
                return "pdf";
            case "doc":
            case "docx":
            case "txt":
            case "md":
            case "rtf":
                return "doc";
            case "xls":
            case "xlsx":
            case "csv":
                return "sheet";
            case "png":
            case "jpg":
            case "jpeg":
            case "gif":
            case "svg":
            case "webp":
                return "image";
            case "mp3":
            case "wav":
            case "ogg":
            case "flac":
                return "audio";
            case "mp4":
            case "mov":
            case "avi":
            case "mkv":
                return "video";
            case "zip":
            case "tar":
            case "gz":
            case "rar":
            case "7z":
                return "archive";
            default:
                return "file";
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
