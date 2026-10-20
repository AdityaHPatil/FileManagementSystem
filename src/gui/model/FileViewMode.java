package gui.model;

/**
 * Supported view modes for displaying files in the main content area.
 */
public enum FileViewMode {
    DETAILS("Detailed View", "Shows files in a detailed table with name, size, type, and dates"),
    LIST("List View", "Shows items in a compact, organized list"),
    TILES("Grid View", "Shows items as large interactive tiles/cards"),
    GRAPH("Graph View", "Obsidian-like interactive node graph visualizer");

    private final String displayName;
    private final String description;

    FileViewMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
