package gui.view;

import gui.controller.ExplorerController;
import gui.model.ExplorerItem;
import gui.util.IconFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Grid / Tile View displaying files as visual cards with large icons.
 */
public class TileGridView extends ScrollPane {
    private final ExplorerController controller;
    private final FlowPane flowPane;
    private final ObservableList<ExplorerItem> selectedItems = FXCollections.observableArrayList();

    public TileGridView(ExplorerController controller) {
        this.controller = controller;
        this.getStyleClass().add("tile-scroll-pane");
        this.setFitToWidth(true);

        flowPane = new FlowPane();
        flowPane.getStyleClass().add("tile-container");
        flowPane.setHgap(14);
        flowPane.setVgap(14);
        flowPane.setPadding(new Insets(16));

        this.setContent(flowPane);

        // Click outside tiles clears selection
        flowPane.setOnMouseClicked(event -> {
            if (event.getTarget() == flowPane) {
                clearSelection();
            }
        });
    }

    public void displayItems(List<ExplorerItem> items) {
        flowPane.getChildren().clear();
        clearSelection();

        if (items.isEmpty()) {
            Label emptyLbl = new Label("This folder is empty.");
            emptyLbl.setStyle("-fx-text-fill: #71717a; -fx-font-size: 13px; -fx-padding: 30;");
            flowPane.getChildren().add(emptyLbl);
            return;
        }

        for (ExplorerItem item : items) {
            VBox card = createTileCard(item);
            flowPane.getChildren().add(card);
        }
    }

    private VBox createTileCard(ExplorerItem item) {
        VBox card = new VBox(6);
        card.getStyleClass().add("file-tile-card");
        card.setAlignment(Pos.CENTER);
        card.setUserData(item);

        // Large icon
        card.getChildren().add(IconFactory.getIconForItem(item, 44));

        // Name
        Label nameLabel = new Label(item.getName());
        nameLabel.getStyleClass().add("tile-name-label");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(100);
        nameLabel.setAlignment(Pos.CENTER);

        // Subtitle (size or item count)
        Label subLabel = new Label(item.getFormattedSize());
        subLabel.setStyle("-fx-text-fill: #71717a; -fx-font-size: 10px;");

        card.getChildren().addAll(nameLabel, subLabel);

        // Mouse handlers
        card.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (event.getClickCount() == 2) {
                    if (item.isDirectory()) {
                        controller.navigateTo(item);
                    } else {
                        controller.openItem(item);
                    }
                } else {
                    if (event.isControlDown() || event.isMetaDown()) {
                        toggleSelection(card, item);
                    } else {
                        selectSingle(card, item);
                    }
                }
            }
        });

        // Context Menu
        card.setOnContextMenuRequested(event -> {
            if (!selectedItems.contains(item)) {
                selectSingle(card, item);
            }
            ContextMenu menu = createItemContextMenu(item);
            menu.show(card, event.getScreenX(), event.getScreenY());
            event.consume();
        });

        return card;
    }

    private void selectSingle(VBox card, ExplorerItem item) {
        clearSelectionVisuals();
        selectedItems.clear();
        selectedItems.add(item);
        card.getStyleClass().add("selected");
        controller.onSelectionChanged(selectedItems);
    }

    private void toggleSelection(VBox card, ExplorerItem item) {
        if (selectedItems.contains(item)) {
            selectedItems.remove(item);
            card.getStyleClass().remove("selected");
        } else {
            selectedItems.add(item);
            card.getStyleClass().add("selected");
        }
        controller.onSelectionChanged(selectedItems);
    }

    private void clearSelection() {
        clearSelectionVisuals();
        selectedItems.clear();
        controller.onSelectionChanged(selectedItems);
    }

    private void clearSelectionVisuals() {
        for (javafx.scene.Node node : flowPane.getChildren()) {
            node.getStyleClass().remove("selected");
        }
    }

    private ContextMenu createItemContextMenu(ExplorerItem item) {
        ContextMenu menu = new ContextMenu();
        MenuItem openItem = new MenuItem(item.isDirectory() ? "Open Folder" : "Open File");
        openItem.setOnAction(e -> {
            if (item.isDirectory()) controller.navigateTo(item);
            else controller.openItem(item);
        });

        MenuItem cutItem = new MenuItem("Cut");
        cutItem.setOnAction(e -> controller.cutSelectedItems());

        MenuItem copyItem = new MenuItem("Copy");
        copyItem.setOnAction(e -> controller.copySelectedItems());

        MenuItem pasteItem = new MenuItem("Paste Here");
        pasteItem.setOnAction(e -> controller.pasteItems());
        pasteItem.setDisable(!controller.canPaste());

        MenuItem renameItem = new MenuItem("Rename...");
        renameItem.setOnAction(e -> controller.promptRenameItem(item));

        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> controller.deleteItem(item));

        MenuItem propertiesItem = new MenuItem("Properties");
        propertiesItem.setOnAction(e -> controller.showPropertiesDialog(item));

        menu.getItems().addAll(openItem, new SeparatorMenuItem(), cutItem, copyItem, pasteItem, new SeparatorMenuItem(), renameItem, deleteItem, new SeparatorMenuItem(), propertiesItem);
        return menu;
    }
}
