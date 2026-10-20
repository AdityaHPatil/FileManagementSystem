package gui.util;

import gui.model.ExplorerItem;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

/**
 * Utility for producing modern, high-DPI vector icons for files, folders, and UI actions.
 */
public class IconFactory {

    /**
     * Create an icon for a file or folder item.
     * @param item ExplorerItem
     * @param size icon size in pixels (e.g. 16, 24, 48)
     */
    public static Node getIconForItem(ExplorerItem item, double size) {
        if (item == null) {
            return getGenericFileIcon(size, "#888888", "📄");
        }

        if (item.isDirectory()) {
            return getFolderIcon(size, "#F59E0B"); // Warm modern folder color
        }

        String cat = item.getFileCategory();
        switch (cat) {
            case "code":
                return getFileBadgeIcon(size, "#3B82F6", "</>");
            case "pdf":
                return getFileBadgeIcon(size, "#EF4444", "PDF");
            case "doc":
                return getFileBadgeIcon(size, "#2563EB", "DOC");
            case "sheet":
                return getFileBadgeIcon(size, "#10B981", "XLS");
            case "image":
                return getFileBadgeIcon(size, "#8B5CF6", "IMG");
            case "audio":
                return getFileBadgeIcon(size, "#EC4899", "♪");
            case "video":
                return getFileBadgeIcon(size, "#F97316", "▶");
            case "archive":
                return getFileBadgeIcon(size, "#EAB308", "ZIP");
            default:
                return getGenericFileIcon(size, "#64748B", "FILE");
        }
    }

    /**
     * Modern folder vector icon.
     */
    public static Node getFolderIcon(double size, String hexColor) {
        StackPane pane = new StackPane();
        pane.setPrefSize(size, size);
        pane.setMinSize(size, size);
        pane.setMaxSize(size, size);
        pane.setAlignment(Pos.CENTER);

        SVGPath path = new SVGPath();
        // SVG Folder path
        path.setContent("M10 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2h-8l-2-2z");
        path.setFill(Color.web(hexColor));
        double scale = size / 24.0;
        path.setScaleX(scale);
        path.setScaleY(scale);

        pane.getChildren().add(path);
        return pane;
    }

    /**
     * Modern stylized file badge icon.
     */
    private static Node getFileBadgeIcon(double size, String colorHex, String badgeText) {
        StackPane pane = new StackPane();
        pane.setPrefSize(size, size);
        pane.setMinSize(size, size);
        pane.setMaxSize(size, size);
        pane.setAlignment(Pos.CENTER);

        SVGPath docBase = new SVGPath();
        // Modern document outline with folded corner
        docBase.setContent("M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6z M13 9V3.5L18.5 9H13z");
        docBase.setFill(Color.web(colorHex).deriveColor(0, 1, 1, 0.85));
        double scale = size / 24.0;
        docBase.setScaleX(scale);
        docBase.setScaleY(scale);

        Label label = new Label(badgeText);
        label.setStyle("-fx-text-fill: white; -fx-font-size: " + Math.max(7, (int)(size * 0.22)) + "px; -fx-font-weight: bold; -fx-alignment: center;");
        label.setTranslateY(size * 0.12);

        pane.getChildren().addAll(docBase, label);
        return pane;
    }

    /**
     * Generic document icon.
     */
    private static Node getGenericFileIcon(double size, String colorHex, String text) {
        return getFileBadgeIcon(size, colorHex, text);
    }

    /**
     * Navigation & Action Icons.
     */
    public static Node createActionIcon(String type, double size) {
        Label lbl = new Label();
        lbl.setAlignment(Pos.CENTER);
        lbl.setStyle("-fx-font-size: " + (int)size + "px; -fx-text-fill: inherit;");
        switch (type.toLowerCase()) {
            case "back":
                lbl.setText("←");
                break;
            case "forward":
                lbl.setText("→");
                break;
            case "up":
                lbl.setText("↑");
                break;
            case "refresh":
                lbl.setText("↻");
                break;
            case "home":
                lbl.setText("⌂");
                break;
            case "search":
                lbl.setText("🔍");
                break;
            case "newfolder":
                lbl.setText("📁+");
                break;
            case "newfile":
                lbl.setText("📄+");
                break;
            case "copy":
                lbl.setText("📋");
                break;
            case "cut":
                lbl.setText("✂");
                break;
            case "paste":
                lbl.setText("📥");
                break;
            case "delete":
                lbl.setText("🗑");
                break;
            case "rename":
                lbl.setText("✎");
                break;
            case "view_details":
                lbl.setText("☰");
                break;
            case "view_list":
                lbl.setText("⁝⁝");
                break;
            case "view_tiles":
                lbl.setText("▦");
                break;
            case "view_graph":
                lbl.setText("🕸");
                break;
            case "info":
                lbl.setText("ⓘ");
                break;
            default:
                lbl.setText("•");
                break;
        }
        return lbl;
    }
}
