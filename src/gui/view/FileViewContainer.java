package gui.view;

import gui.controller.ExplorerController;
import gui.model.ExplorerItem;
import gui.model.FileViewMode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Switcher container managing Detailed, List, Tile, and Graph views.
 */
public class FileViewContainer extends StackPane {
    private final ExplorerController controller;
    private final DetailedTableView detailedTableView;
    private final FileListView fileListView;
    private final TileGridView tileGridView;
    private final GraphVisualizerView graphVisualizerView;

    private final ObservableList<ExplorerItem> currentItems = FXCollections.observableArrayList();
    private FileViewMode currentMode = FileViewMode.DETAILS;

    public FileViewContainer(ExplorerController controller) {
        this.controller = controller;
        this.getStyleClass().add("content-area-container");

        detailedTableView = new DetailedTableView(controller);
        fileListView = new FileListView(controller);
        tileGridView = new TileGridView(controller);
        graphVisualizerView = new GraphVisualizerView(controller);

        VBox.setVgrow(this, Priority.ALWAYS);

        // Initially show Detailed Table View
        this.getChildren().add(detailedTableView);
    }

    public void setViewMode(FileViewMode mode) {
        this.currentMode = mode;
        this.getChildren().clear();

        switch (mode) {
            case DETAILS:
                detailedTableView.setItemsList(currentItems);
                this.getChildren().add(detailedTableView);
                break;
            case LIST:
                fileListView.setItemsList(currentItems);
                this.getChildren().add(fileListView);
                break;
            case TILES:
                tileGridView.displayItems(currentItems);
                this.getChildren().add(tileGridView);
                break;
            case GRAPH:
                graphVisualizerView.displayGraph(controller.getCurrentDirectory());
                this.getChildren().add(graphVisualizerView);
                break;
        }
    }

    public void updateItems(List<ExplorerItem> items) {
        currentItems.setAll(items);
        if (currentMode == FileViewMode.DETAILS) {
            detailedTableView.setItemsList(currentItems);
        } else if (currentMode == FileViewMode.LIST) {
            fileListView.setItemsList(currentItems);
        } else if (currentMode == FileViewMode.TILES) {
            tileGridView.displayItems(currentItems);
        } else if (currentMode == FileViewMode.GRAPH) {
            graphVisualizerView.displayGraph(controller.getCurrentDirectory());
        }
    }

    public FileViewMode getCurrentMode() {
        return currentMode;
    }

    public DetailedTableView getDetailedTableView() {
        return detailedTableView;
    }

    public FileListView getFileListView() {
        return fileListView;
    }

    public TileGridView getTileGridView() {
        return tileGridView;
    }

    public GraphVisualizerView getGraphVisualizerView() {
        return graphVisualizerView;
    }
}
