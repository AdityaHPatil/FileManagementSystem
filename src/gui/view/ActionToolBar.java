package gui.view;

import gui.controller.ExplorerController;
import gui.model.FileViewMode;
import gui.util.IconFactory;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Secondary action toolbar containing file operations and view mode switchers.
 */
public class ActionToolBar extends HBox {
    private final ExplorerController controller;

    private final Button cutBtn;
    private final Button copyBtn;
    private final Button pasteBtn;
    private final Button deleteBtn;
    private final Button renameBtn;

    private final Button detailsViewBtn;
    private final Button listViewBtn;
    private final Button tilesViewBtn;
    private final Button graphViewBtn;

    public ActionToolBar(ExplorerController controller) {
        this.controller = controller;
        this.getStyleClass().add("action-bar-container");
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(6);

        // Primary creation buttons
        Button newFolderBtn = createActionButton("newfolder", "+ Folder", "Create New Folder");
        newFolderBtn.setOnAction(e -> controller.promptCreateNewFolder(null));

        Button newFileBtn = createActionButton("newfile", "+ File", "Create New File");
        newFileBtn.setOnAction(e -> controller.promptCreateNewFile(null));

        Separator sep1 = new Separator(javafx.geometry.Orientation.VERTICAL);

        // File Operation Buttons
        cutBtn = createActionButton("cut", "Cut", "Cut selected items (Ctrl+X)");
        cutBtn.setOnAction(e -> controller.cutSelectedItems());
        cutBtn.setDisable(true);

        copyBtn = createActionButton("copy", "Copy", "Copy selected items (Ctrl+C)");
        copyBtn.setOnAction(e -> controller.copySelectedItems());
        copyBtn.setDisable(true);

        pasteBtn = createActionButton("paste", "Paste", "Paste items from clipboard (Ctrl+V)");
        pasteBtn.setOnAction(e -> controller.pasteItems());
        pasteBtn.setDisable(!controller.canPaste());

        deleteBtn = createActionButton("delete", "Delete", "Delete selected items");
        deleteBtn.setOnAction(e -> controller.deleteSelectedItems());
        deleteBtn.setDisable(true);

        renameBtn = createActionButton("rename", "Rename", "Rename selected item (F2)");
        renameBtn.setOnAction(e -> controller.renameSelectedItem());
        renameBtn.setDisable(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // View Mode Switchers
        detailsViewBtn = createActionButton("view_details", "Details", "Switch to Detailed Table View");
        detailsViewBtn.setOnAction(e -> {
            controller.switchViewMode(FileViewMode.DETAILS);
            updateViewModeSelection(FileViewMode.DETAILS);
        });

        listViewBtn = createActionButton("view_list", "List", "Switch to List View");
        listViewBtn.setOnAction(e -> {
            controller.switchViewMode(FileViewMode.LIST);
            updateViewModeSelection(FileViewMode.LIST);
        });

        tilesViewBtn = createActionButton("view_tiles", "Tiles", "Switch to Grid / Tile View");
        tilesViewBtn.setOnAction(e -> {
            controller.switchViewMode(FileViewMode.TILES);
            updateViewModeSelection(FileViewMode.TILES);
        });

        graphViewBtn = createActionButton("view_graph", "Graph", "Switch to Obsidian Graph View");
        graphViewBtn.setOnAction(e -> {
            controller.switchViewMode(FileViewMode.GRAPH);
            updateViewModeSelection(FileViewMode.GRAPH);
        });

        Separator sep2 = new Separator(javafx.geometry.Orientation.VERTICAL);

        Button toggleInspectorBtn = createActionButton("info", "Details Panel", "Toggle File Details / Inspector");
        toggleInspectorBtn.setOnAction(e -> controller.toggleDetailsInspector());

        this.getChildren().addAll(
            newFolderBtn, newFileBtn,
            sep1,
            cutBtn, copyBtn, pasteBtn, renameBtn, deleteBtn,
            spacer,
            detailsViewBtn, listViewBtn, tilesViewBtn, graphViewBtn,
            sep2,
            toggleInspectorBtn
        );

        updateViewModeSelection(FileViewMode.DETAILS);
    }

    private Button createActionButton(String iconType, String text, String tooltipText) {
        Button btn = new Button(text);
        btn.getStyleClass().add("action-button");
        btn.setGraphic(IconFactory.createActionIcon(iconType, 12));
        btn.setTooltip(new Tooltip(tooltipText));
        return btn;
    }

    public void updateSelectionState(int selectionCount) {
        boolean hasSelection = selectionCount > 0;
        boolean singleSelection = selectionCount == 1;

        cutBtn.setDisable(!hasSelection);
        copyBtn.setDisable(!hasSelection);
        deleteBtn.setDisable(!hasSelection);
        renameBtn.setDisable(!singleSelection);
        pasteBtn.setDisable(!controller.canPaste());
    }

    public void updateViewModeSelection(FileViewMode activeMode) {
        detailsViewBtn.setStyle(activeMode == FileViewMode.DETAILS ? "-fx-background-color: #2563eb; -fx-text-fill: white;" : "");
        listViewBtn.setStyle(activeMode == FileViewMode.LIST ? "-fx-background-color: #2563eb; -fx-text-fill: white;" : "");
        tilesViewBtn.setStyle(activeMode == FileViewMode.TILES ? "-fx-background-color: #2563eb; -fx-text-fill: white;" : "");
        graphViewBtn.setStyle(activeMode == FileViewMode.GRAPH ? "-fx-background-color: #2563eb; -fx-text-fill: white;" : "");
    }
}
