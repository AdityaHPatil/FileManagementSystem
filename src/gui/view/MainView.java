package gui.view;

import gui.controller.ExplorerController;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Main application window layout assembling top toolbars, sidebar tree,
 * center file view container, right inspector, and bottom status bar.
 */
public class MainView extends BorderPane {
    private final ExplorerController controller;

    private final MenuBarComponent menuBar;
    private final NavigationToolBar navigationToolBar;
    private final ActionToolBar actionToolBar;

    private final SplitPane mainSplitPane;
    private final SidebarView sidebarView;
    private final FileViewContainer fileViewContainer;
    private final DetailsInspectorView detailsInspectorView;

    private final StatusBarComponent statusBar;

    private boolean isSidebarVisible = true;
    private boolean isInspectorVisible = true;

    public MainView(ExplorerController controller) {
        this.controller = controller;
        this.setStyle("-fx-background-color: #18181b;");

        // Top Bars
        menuBar = new MenuBarComponent(controller);
        navigationToolBar = new NavigationToolBar(controller);
        actionToolBar = new ActionToolBar(controller);

        VBox topContainer = new VBox(menuBar, navigationToolBar, actionToolBar);
        this.setTop(topContainer);

        // Center SplitPane
        mainSplitPane = new SplitPane();
        mainSplitPane.getStyleClass().add("split-pane");

        sidebarView = new SidebarView(controller);
        fileViewContainer = new FileViewContainer(controller);
        detailsInspectorView = new DetailsInspectorView(controller);

        mainSplitPane.getItems().addAll(sidebarView, fileViewContainer, detailsInspectorView);
        mainSplitPane.setDividerPositions(0.20, 0.78);

        this.setCenter(mainSplitPane);

        // Bottom Status Bar
        statusBar = new StatusBarComponent(controller);
        this.setBottom(statusBar);
    }

    public void toggleSidebar() {
        isSidebarVisible = !isSidebarVisible;
        if (isSidebarVisible) {
            if (!mainSplitPane.getItems().contains(sidebarView)) {
                mainSplitPane.getItems().add(0, sidebarView);
                mainSplitPane.setDividerPosition(0, 0.20);
            }
        } else {
            mainSplitPane.getItems().remove(sidebarView);
        }
    }

    public void toggleDetailsInspector() {
        isInspectorVisible = !isInspectorVisible;
        if (isInspectorVisible) {
            if (!mainSplitPane.getItems().contains(detailsInspectorView)) {
                mainSplitPane.getItems().add(detailsInspectorView);
                mainSplitPane.setDividerPosition(mainSplitPane.getItems().size() - 2, 0.78);
            }
        } else {
            mainSplitPane.getItems().remove(detailsInspectorView);
        }
    }

    public MenuBarComponent getMenuBarComponent() {
        return menuBar;
    }

    public NavigationToolBar getNavigationToolBar() {
        return navigationToolBar;
    }

    public ActionToolBar getActionToolBar() {
        return actionToolBar;
    }

    public SidebarView getSidebarView() {
        return sidebarView;
    }

    public FileViewContainer getFileViewContainer() {
        return fileViewContainer;
    }

    public DetailsInspectorView getDetailsInspectorView() {
        return detailsInspectorView;
    }

    public StatusBarComponent getStatusBar() {
        return statusBar;
    }
}
