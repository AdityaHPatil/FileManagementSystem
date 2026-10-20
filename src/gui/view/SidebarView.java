package gui.view;

import gui.controller.ExplorerController;
import gui.model.ExplorerItem;
import gui.util.IconFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Left sidebar containing Quick Access shortcuts and the hierarchical Directory Tree.
 */
public class SidebarView extends VBox {
    private final ExplorerController controller;
    private final DirectoryTreeView directoryTree;
    private final VBox quickAccessBox;

    public SidebarView(ExplorerController controller) {
        this.controller = controller;
        this.getStyleClass().add("sidebar-panel");
        this.setPrefWidth(240);
        this.setMinWidth(180);
        this.setMaxWidth(380);
        this.setSpacing(6);

        // Quick Access Section
        Label quickAccessTitle = new Label("QUICK ACCESS");
        quickAccessTitle.getStyleClass().add("section-header");

        quickAccessBox = new VBox(2);
        quickAccessBox.setPadding(new Insets(2, 0, 8, 0));

        // Directory Tree Section
        Label drivesTitle = new Label("DIRECTORIES & DRIVES");
        drivesTitle.getStyleClass().add("section-header");

        directoryTree = new DirectoryTreeView(controller);
        VBox.setVgrow(directoryTree, Priority.ALWAYS);

        this.getChildren().addAll(quickAccessTitle, quickAccessBox, drivesTitle, directoryTree);
    }

    public void populateQuickAccess(List<ExplorerItem> items) {
        quickAccessBox.getChildren().clear();
        for (ExplorerItem item : items) {
            HBox itemRow = new HBox(8);
            itemRow.setAlignment(Pos.CENTER_LEFT);
            itemRow.getStyleClass().add("quick-access-item");

            itemRow.getChildren().addAll(
                IconFactory.getIconForItem(item, 16),
                new Label(item.getName())
            );

            itemRow.setOnMouseClicked(e -> controller.navigateTo(item));
            quickAccessBox.getChildren().add(itemRow);
        }
    }

    public DirectoryTreeView getDirectoryTree() {
        return directoryTree;
    }
}
