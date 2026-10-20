package gui.view;

import gui.controller.ExplorerController;
import gui.model.ExplorerItem;
import gui.util.IconFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Right inspector panel showing detailed file metadata and quick actions.
 */
public class DetailsInspectorView extends VBox {
    private final ExplorerController controller;

    private final VBox heroBox;
    private final StackPane iconContainer;
    private final Label nameLabel;
    private final Label categoryBadge;

    private final GridPane metaGrid;
    private final Label pathValue;
    private final Label sizeValue;
    private final Label typeValue;
    private final Label modifiedValue;
    private final Label createdValue;
    private final Label permValue;

    private final HBox actionButtonsBox;
    private final Button openBtn;
    private final Button renameBtn;
    private final Button deleteBtn;

    private ExplorerItem currentItem;

    public DetailsInspectorView(ExplorerController controller) {
        this.controller = controller;
        this.getStyleClass().add("details-panel");
        this.setPrefWidth(280);
        this.setMinWidth(220);
        this.setMaxWidth(380);
        this.setSpacing(14);

        // Header
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("FILE DETAILS");
        title.getStyleClass().add("section-header");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #71717a; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> controller.toggleDetailsInspector());
        header.getChildren().addAll(title, spacer, closeBtn);

        // Hero Preview Box
        heroBox = new VBox(8);
        heroBox.getStyleClass().add("preview-hero-box");
        heroBox.setAlignment(Pos.CENTER);

        iconContainer = new StackPane();
        nameLabel = new Label("No Selection");
        nameLabel.setStyle("-fx-text-fill: #f4f4f5; -fx-font-weight: bold; -fx-font-size: 13px; -fx-alignment: center;");
        nameLabel.setWrapText(true);

        categoryBadge = new Label("None");
        categoryBadge.setStyle("-fx-background-color: #27272a; -fx-text-fill: #a1a1aa; -fx-padding: 2 8 2 8; -fx-background-radius: 10px; -fx-font-size: 10px;");

        heroBox.getChildren().addAll(iconContainer, nameLabel, categoryBadge);

        // Metadata Grid
        metaGrid = new GridPane();
        metaGrid.setHgap(10);
        metaGrid.setVgap(8);
        metaGrid.setPadding(new Insets(4, 0, 4, 0));

        pathValue = addMetaRow(metaGrid, 0, "Location:");
        sizeValue = addMetaRow(metaGrid, 1, "Size:");
        typeValue = addMetaRow(metaGrid, 2, "Type:");
        modifiedValue = addMetaRow(metaGrid, 3, "Modified:");
        createdValue = addMetaRow(metaGrid, 4, "Created:");
        permValue = addMetaRow(metaGrid, 5, "Permissions:");

        // Copy Path Button
        Button copyPathBtn = new Button("Copy Full Path");
        copyPathBtn.setStyle("-fx-background-color: #27272a; -fx-text-fill: #38bdf8; -fx-background-radius: 4px; -fx-font-size: 11px; -fx-cursor: hand;");
        copyPathBtn.setOnAction(e -> {
            if (currentItem != null) {
                ClipboardContent content = new ClipboardContent();
                content.putString(currentItem.getPath());
                Clipboard.getSystemClipboard().setContent(content);
                controller.showSuccessNotification("Copied path to clipboard!");
            }
        });

        // Quick Actions
        actionButtonsBox = new HBox(8);
        actionButtonsBox.setAlignment(Pos.CENTER);
        openBtn = new Button("Open");
        openBtn.getStyleClass().add("action-button-primary");
        openBtn.setOnAction(e -> {
            if (currentItem != null) {
                if (currentItem.isDirectory()) controller.navigateTo(currentItem);
                else controller.openItem(currentItem);
            }
        });

        renameBtn = new Button("Rename");
        renameBtn.getStyleClass().add("action-button");
        renameBtn.setOnAction(e -> {
            if (currentItem != null) controller.promptRenameItem(currentItem);
        });

        deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #7f1d1d; -fx-text-fill: #fecaca; -fx-background-radius: 6px; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> {
            if (currentItem != null) controller.deleteItem(currentItem);
        });

        actionButtonsBox.getChildren().addAll(openBtn, renameBtn, deleteBtn);

        ScrollPane scrollWrapper = new ScrollPane(new VBox(12, heroBox, metaGrid, copyPathBtn, new Separator(), actionButtonsBox));
        scrollWrapper.setFitToWidth(true);
        scrollWrapper.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        VBox.setVgrow(scrollWrapper, Priority.ALWAYS);

        this.getChildren().addAll(header, scrollWrapper);
        clearDetails();
    }

    private Label addMetaRow(GridPane grid, int row, String title) {
        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("property-title");

        Label valueLbl = new Label("--");
        valueLbl.getStyleClass().add("property-value");
        valueLbl.setWrapText(true);
        valueLbl.setMaxWidth(160);

        grid.add(titleLbl, 0, row);
        grid.add(valueLbl, 1, row);
        return valueLbl;
    }

    public void displayItem(ExplorerItem item) {
        this.currentItem = item;
        if (item == null) {
            clearDetails();
            return;
        }

        iconContainer.getChildren().clear();
        iconContainer.getChildren().add(IconFactory.getIconForItem(item, 52));

        nameLabel.setText(item.getName());
        categoryBadge.setText(item.getFileCategory().toUpperCase());

        pathValue.setText(item.getPath());
        sizeValue.setText(item.getFormattedSize());
        typeValue.setText(item.getType());
        modifiedValue.setText(item.getFormattedModifiedDate());
        createdValue.setText(item.getFormattedCreatedDate());
        permValue.setText(item.getPermissions());

        actionButtonsBox.setVisible(true);
        openBtn.setText(item.isDirectory() ? "Open Folder" : "Open File");
    }

    public void clearDetails() {
        this.currentItem = null;
        iconContainer.getChildren().clear();
        iconContainer.getChildren().add(IconFactory.getFolderIcon(48, "#4B5563"));

        nameLabel.setText("No item selected");
        categoryBadge.setText("NONE");

        pathValue.setText("--");
        sizeValue.setText("--");
        typeValue.setText("--");
        modifiedValue.setText("--");
        createdValue.setText("--");
        permValue.setText("--");

        actionButtonsBox.setVisible(false);
    }
}
