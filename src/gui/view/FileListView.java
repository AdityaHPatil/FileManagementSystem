package gui.view;

import gui.controller.ExplorerController;
import gui.model.ExplorerItem;
import gui.util.IconFactory;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * List View displaying files and folders in a clean, compact vertical list.
 */
public class FileListView extends ListView<ExplorerItem> {
    private final ExplorerController controller;

    public FileListView(ExplorerController controller) {
        this.controller = controller;
        this.getStyleClass().add("list-view");
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        setupCellFactory();
        setupSelectionListener();

        Label placeholder = new Label("This folder is empty.");
        placeholder.setStyle("-fx-text-fill: #71717a; -fx-font-size: 13px;");
        this.setPlaceholder(placeholder);
    }

    private void setupCellFactory() {
        this.setCellFactory(lv -> {
            ListCell<ExplorerItem> cell = new ListCell<>() {
                @Override
                protected void updateItem(ExplorerItem item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        setContextMenu(null);
                    } else {
                        HBox container = new HBox(12);
                        container.setAlignment(Pos.CENTER_LEFT);

                        Label nameLabel = new Label(item.getName());
                        nameLabel.setStyle("-fx-text-fill: inherit; -fx-font-weight: 500;");

                        Region spacer = new Region();
                        HBox.setHgrow(spacer, Priority.ALWAYS);

                        Label typeLabel = new Label(item.getType());
                        typeLabel.setStyle("-fx-text-fill: #71717a; -fx-font-size: 11px;");

                        Label sizeLabel = new Label(item.getFormattedSize());
                        sizeLabel.setStyle("-fx-text-fill: #a1a1aa; -fx-font-size: 11px;");
                        sizeLabel.setPrefWidth(80);
                        sizeLabel.setAlignment(Pos.CENTER_RIGHT);

                        container.getChildren().addAll(IconFactory.getIconForItem(item, 20), nameLabel, spacer, typeLabel, sizeLabel);
                        setGraphic(container);
                        setText(null);
                        setContextMenu(createItemContextMenu(item));
                    }
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !cell.isEmpty()) {
                    ExplorerItem item = cell.getItem();
                    if (item.isDirectory()) {
                        controller.navigateTo(item);
                    } else {
                        controller.openItem(item);
                    }
                }
            });

            return cell;
        });
    }

    private void setupSelectionListener() {
        this.getSelectionModel().getSelectedItems().addListener((javafx.collections.ListChangeListener<ExplorerItem>) c -> {
            controller.onSelectionChanged(this.getSelectionModel().getSelectedItems());
        });
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

    public void setItemsList(ObservableList<ExplorerItem> items) {
        this.setItems(items);
    }
}
