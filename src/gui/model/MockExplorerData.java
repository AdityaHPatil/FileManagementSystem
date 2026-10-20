package gui.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Cleaned mock file system data provider containing only:
 * - Desktop (empty)
 * - Documents (empty)
 * - Downloads (empty)
 */
public class MockExplorerData {
    private final ExplorerItem root;
    private final List<ExplorerItem> quickAccessItems = new ArrayList<>();

    public MockExplorerData() {
        this.root = buildMockHierarchy();
    }

    public ExplorerItem getRoot() {
        return root;
    }

    public List<ExplorerItem> getQuickAccessItems() {
        return quickAccessItems;
    }

    private ExplorerItem buildMockHierarchy() {
        LocalDateTime now = LocalDateTime.now();

        // Root
        ExplorerItem rootDir = new ExplorerItem("This PC", "/", true, 0, now, "System Folder");

        // Macintosh HD Drive
        ExplorerItem macDrive = new ExplorerItem("Macintosh HD", "/Macintosh HD", true, 0, now, "Local Disk");
        rootDir.addChild(macDrive);

        ExplorerItem users = new ExplorerItem("Users", "/Macintosh HD/Users", true, 0, now, "File Folder");
        macDrive.addChild(users);

        ExplorerItem akarshHome = new ExplorerItem("akarsh", "/Macintosh HD/Users/akarsh", true, 0, now, "Home Directory");
        users.addChild(akarshHome);

        // Clean user folders - only Desktop, Documents, Downloads (all empty)
        ExplorerItem desktop = new ExplorerItem("Desktop", "/Macintosh HD/Users/akarsh/Desktop", true, 0, now, "Desktop Folder");
        ExplorerItem documents = new ExplorerItem("Documents", "/Macintosh HD/Users/akarsh/Documents", true, 0, now, "Documents Folder");
        ExplorerItem downloads = new ExplorerItem("Downloads", "/Macintosh HD/Users/akarsh/Downloads", true, 0, now, "Downloads Folder");

        akarshHome.addChild(desktop);
        akarshHome.addChild(documents);
        akarshHome.addChild(downloads);

        // Quick Access Favorites
        quickAccessItems.add(desktop);
        quickAccessItems.add(documents);
        quickAccessItems.add(downloads);

        return rootDir;
    }

    /**
     * Search recursively through mock file system for items containing query string.
     */
    public List<ExplorerItem> search(String query) {
        List<ExplorerItem> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return results;
        }
        String lower = query.toLowerCase().trim();
        searchRecursive(root, lower, results);
        return results;
    }

    private void searchRecursive(ExplorerItem item, String query, List<ExplorerItem> results) {
        if (!item.equals(root) && item.getName().toLowerCase().contains(query)) {
            results.add(item);
        }
        for (ExplorerItem child : item.getChildren()) {
            searchRecursive(child, query, results);
        }
    }

    /**
     * Finds an item matching the exact path string.
     */
    public ExplorerItem findByPath(String path) {
        if (path == null) return null;
        return findByPathRecursive(root, path);
    }

    private ExplorerItem findByPathRecursive(ExplorerItem current, String path) {
        if (current.getPath().equalsIgnoreCase(path)) {
            return current;
        }
        for (ExplorerItem child : current.getChildren()) {
            ExplorerItem match = findByPathRecursive(child, path);
            if (match != null) return match;
        }
        return null;
    }
}
