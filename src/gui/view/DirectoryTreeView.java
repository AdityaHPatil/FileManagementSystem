package gui.view;

import gui.controller.ExplorerController;
import gui.model.ExplorerItem;
import gui.util.IconFactory;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Directory Tree component displaying hierarchical folder structure.
 */
public class DirectoryTreeView extends TreeView<ExplorerItem> {
    private final ExplorerController controller;

    public DirectoryTreeView(ExplorerController controller) {
        this.controller = controller;
        this.getStyleClass().add("tree-view");
        this.setShowRoot(true);

        setupCellFactory();
        setupSelectionListener();
    }

    /**
     * Initializes tree root with the provided explorer root.
     */
    public void populateTree(ExplorerItem rootItem) {
        TreeItem<ExplorerItem> rootNode = createTreeNode(rootItem);
        rootNode.setExpanded(true);
        this.setRoot(rootNode);

        // Expand first level child nodes
        for (TreeItem<ExplorerItem> child : rootNode.getChildren()) {
            child.setExpanded(true);
        }
    }

    private TreeItem<ExplorerItem> createTreeNode(ExplorerItem item) {
        TreeItem<ExplorerItem> node = new TreeItem<>(item);
        if (item.isDirectory()) {
            for (ExplorerItem child : item.getChildren()) {
                if (child.isDirectory()) {
                    node.getChildren().add(createTreeNode(child));
                }
            }
        }
        return node;
    }

    private void setupCellFactory() {
        this.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(ExplorerItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setContextMenu(null);
                } else {
                    setText(item.getName());
                    setGraphic(IconFactory.getIconForItem(item, 16));
                    setContextMenu(createTreeContextMenu(item));
                }
            }
        });
    }

    private void setupSelectionListener() {
        this.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getValue() != null) {
                controller.navigateTo(newVal.getValue());
            }
        });
    }

    /**
     * Highlights the given folder item in the tree view if present.
     */
    public void selectItemInTree(ExplorerItem target) {
        if (target == null || getRoot() == null) return;
        TreeItem<ExplorerItem> found = findTreeItem(getRoot(), target);
        if (found != null) {
            getSelectionModel().select(found);
            scrollTo(getRow(found));
        }
    }

    private TreeItem<ExplorerItem> findTreeItem(TreeItem<ExplorerItem> current, ExplorerItem target) {
        if (current.getValue() != null && current.getValue().getPath().equals(target.getPath())) {
            return current;
        }
        for (TreeItem<ExplorerItem> child : current.getChildren()) {
            TreeItem<ExplorerItem> found = findTreeItem(child, target);
            if (found != null) return found;
        }
        return null;
    }

    private ContextMenu createTreeContextMenu(ExplorerItem item) {
        ContextMenu menu = new ContextMenu();

        MenuItem openItem = new MenuItem("Open in Explorer");
        openItem.setOnAction(e -> controller.navigateTo(item));

        MenuItem newFolder = new MenuItem("New Folder Here");
        newFolder.setOnAction(e -> controller.promptCreateNewFolder(item));

        MenuItem newFile = new MenuItem("New File Here");
        newFile.setOnAction(e -> controller.promptCreateNewFile(item));

        MenuItem rename = new MenuItem("Rename...");
        rename.setOnAction(e -> controller.promptRenameItem(item));

        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(e -> controller.deleteItem(item));

        menu.getItems().addAll(openItem, new SeparatorMenuItem(), newFolder, newFile, new SeparatorMenuItem(), rename, delete);
        return menu;
    }
}
