package gui.view;

import gui.controller.ExplorerController;
import gui.model.ExplorerItem;
import gui.util.IconFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

/**
 * Detailed Table View displaying files and folders with sortable columns:
 * Name, Type, Size, Date Modified, and Permissions.
 */
public class DetailedTableView extends TableView<ExplorerItem> {
    private final ExplorerController controller;

    public DetailedTableView(ExplorerController controller) {
        this.controller = controller;
        this.getStyleClass().add("table-view");
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        setupColumns();
        setupRowFactory();
        setupSelectionListener();
        setupEmptyAreaContextMenu();

        Label placeholder = new Label("This folder is empty.");
        placeholder.setStyle("-fx-text-fill: #71717a; -fx-font-size: 13px;");
        this.setPlaceholder(placeholder);
    }

    private void setupColumns() {
        // Name Column
        TableColumn<ExplorerItem, ExplorerItem> nameCol = new TableColumn<>("Name");
        nameCol.setPrefWidth(280);
        nameCol.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue()));
        nameCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(ExplorerItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox box = new HBox(8);
                    box.setAlignment(Pos.CENTER_LEFT);
                    Label label = new Label(item.getName());
                    label.setStyle("-fx-text-fill: inherit;");
                    box.getChildren().addAll(IconFactory.getIconForItem(item, 18), label);
                    setGraphic(box);
                    setText(null);
                }
            }
        });
        nameCol.setComparator((a, b) -> {
            if (a.isDirectory() != b.isDirectory()) {
                return a.isDirectory() ? -1 : 1;
            }
            return a.getName().compareToIgnoreCase(b.getName());
        });

        // Type Column
        TableColumn<ExplorerItem, String> typeCol = new TableColumn<>("Type");
        typeCol.setPrefWidth(140);
        typeCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType()));

        // Size Column
        TableColumn<ExplorerItem, String> sizeCol = new TableColumn<>("Size");
        sizeCol.setPrefWidth(100);
        sizeCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFormattedSize()));
        sizeCol.setComparator((a, b) -> {
            // Natural sort by size bytes handled in cell or comparator
            return a.compareToIgnoreCase(b);
        });

        // Date Modified Column
        TableColumn<ExplorerItem, String> dateCol = new TableColumn<>("Date Modified");
        dateCol.setPrefWidth(160);
        dateCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFormattedModifiedDate()));

        // Permissions Column
        TableColumn<ExplorerItem, String> permCol = new TableColumn<>("Permissions");
        permCol.setPrefWidth(110);
        permCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPermissions()));

        this.getColumns().addAll(nameCol, typeCol, sizeCol, dateCol, permCol);
    }

    private void setupRowFactory() {
        this.setRowFactory(tv -> {
            TableRow<ExplorerItem> row = new TableRow<>();

            // Double Click to navigate folder or open file
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    ExplorerItem item = row.getItem();
                    if (item.isDirectory()) {
                        controller.navigateTo(item);
                    } else {
                        controller.openItem(item);
                    }
                }
            });

            // Context Menu on row
            row.itemProperty().addListener((obs, oldVal, item) -> {
                if (item != null) {
                    row.setContextMenu(createItemContextMenu(item));
                } else {
                    row.setContextMenu(null);
                }
            });

            return row;
        });
    }

    private void setupSelectionListener() {
        this.getSelectionModel().getSelectedItems().addListener((javafx.collections.ListChangeListener<ExplorerItem>) c -> {
            controller.onSelectionChanged(this.getSelectionModel().getSelectedItems());
        });
    }

    private void setupEmptyAreaContextMenu() {
        this.setOnContextMenuRequested(event -> {
            if (getSelectionModel().getSelectedItem() == null) {
                ContextMenu menu = createBackgroundContextMenu();
                menu.show(this, event.getScreenX(), event.getScreenY());
            }
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

        MenuItem duplicateItem = new MenuItem("Duplicate");
        duplicateItem.setOnAction(e -> controller.duplicateItem(item));

        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> controller.deleteItem(item));

        MenuItem propertiesItem = new MenuItem("Properties / Metadata");
        propertiesItem.setOnAction(e -> controller.showPropertiesDialog(item));

        menu.getItems().addAll(
            openItem, new SeparatorMenuItem(),
            cutItem, copyItem, pasteItem, duplicateItem,
            new SeparatorMenuItem(),
            renameItem, deleteItem,
            new SeparatorMenuItem(),
            propertiesItem
        );
        return menu;
    }

    private ContextMenu createBackgroundContextMenu() {
        ContextMenu menu = new ContextMenu();

        MenuItem newFolder = new MenuItem("New Folder");
        newFolder.setOnAction(e -> controller.promptCreateNewFolder(null));

        MenuItem newFile = new MenuItem("New File");
        newFile.setOnAction(e -> controller.promptCreateNewFile(null));

        MenuItem pasteItem = new MenuItem("Paste");
        pasteItem.setOnAction(e -> controller.pasteItems());
        pasteItem.setDisable(!controller.canPaste());

        MenuItem refreshItem = new MenuItem("Refresh");
        refreshItem.setOnAction(e -> controller.refreshCurrentDirectory());

        menu.getItems().addAll(newFolder, newFile, new SeparatorMenuItem(), pasteItem, refreshItem);
        return menu;
    }

    public void setItemsList(ObservableList<ExplorerItem> items) {
        this.setItems(items);
    }
}
