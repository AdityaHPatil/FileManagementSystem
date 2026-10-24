package app;

import filesystem.FileSystem;
import models.FolderItem;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class MainApp extends Application {
    private final FileSystem fileSystem = new FileSystem();
    private final Label status = new Label("Ready  ·  Choose a folder to begin");
    private final Label title = new Label("File Explorer");
    private final Label subtitle = new Label("Visualize folders and explore your files");
    private final Label zoomLabel = new Label("100%");
    private final Button openFolder = new Button("Open folder");
    private final Button zoomOut = new Button("−");
    private final Button zoomIn = new Button("+");
    private final Button zoomReset = new Button("Reset");
    private final ToggleButton themeToggle = new ToggleButton();
    private final FolderGraphView graphView =
            new FolderGraphView(fileSystem,
                    exception -> showError("Could not read folder", exception), status::setText);
    private final BorderPane layout = new BorderPane();
    private final HBox toolbar = new HBox(12);
    private final ScrollPane scrollPane = new ScrollPane(graphView);
    private final GraphZoomController zoomController =
            new GraphZoomController(scrollPane, graphView, zoomLabel);
    private boolean darkMode;

    @Override
    public void start(Stage stage) {
        configureActions(stage);
        configureToolbar();
        configureGraphViewport();

        layout.setCenter(scrollPane);
        layout.setTop(toolbar);
        layout.setBottom(status);
        BorderPane.setMargin(status, new Insets(12, 24, 14, 24));
        applyTheme();

        stage.setScene(new Scene(layout, 1100, 720));
        stage.setTitle("File Management System");
        stage.setMinWidth(760);
        stage.setMinHeight(520);
        stage.show();
    }

    private void configureActions(Stage stage) {
        openFolder.setOnAction(event -> chooseWorkspace(stage));
        themeToggle.setOnAction(event -> {
            darkMode = themeToggle.isSelected();
            graphView.setDarkMode(darkMode);
            applyTheme();
        });
        zoomController.bindControls(zoomOut, zoomIn, zoomReset);
    }

    private void configureToolbar() {
        VBox heading = new VBox(5, title, subtitle);
        HBox zoomControls = new HBox(6, zoomOut, zoomLabel, zoomIn, zoomReset);
        zoomControls.setAlignment(Pos.CENTER);
        HBox actions = new HBox(10, zoomControls, openFolder, themeToggle);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        toolbar.getChildren().setAll(heading, spacer, actions);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(18, 24, 18, 24));
    }

    private void configureGraphViewport() {
        scrollPane.setPannable(true);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        zoomController.attachGestures();
    }

    private void applyTheme() {
        String background = darkMode ? "#171a21" : "#f4f6f8";
        String toolbarBackground = darkMode ? "#202631" : "#ffffff";
        String text = darkMode ? "#e7edf5" : "#17212f";
        String mutedText = darkMode ? "#9ba9bb" : "#64748b";
        String quietButtonBackground = darkMode ? "#343e4d" : "#eef2f7";

        layout.setStyle("-fx-background-color: " + background + ";");
        toolbar.setStyle("-fx-background-color: " + toolbarBackground
                + "; -fx-border-color: transparent transparent "
                + (darkMode ? "#303846" : "#e5eaf0") + " transparent;"
                + " -fx-border-width: 0 0 1 0;");
        scrollPane.setStyle("-fx-background: " + background
                + "; -fx-background-color: " + background + "; -fx-padding: 12;");
        title.setStyle("-fx-font-size: 21px; -fx-font-weight: bold; -fx-text-fill: " + text + ";");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: " + mutedText + ";");
        status.setStyle("-fx-font-size: 11px; -fx-text-fill: " + mutedText + ";");
        zoomLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;"
                + " -fx-text-fill: " + text + "; -fx-min-width: 42px; -fx-alignment: center;");

        openFolder.setStyle("-fx-background-color: #3978e8; -fx-text-fill: white;"
                + " -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 9;"
                + " -fx-padding: 10 16 10 16; -fx-cursor: hand;");
        String quietButtonStyle = "-fx-background-color: " + quietButtonBackground
                + "; -fx-text-fill: " + text + ";"
                + " -fx-font-size: 12px; -fx-background-radius: 9;"
                + " -fx-padding: 8 11 8 11; -fx-cursor: hand;";
        zoomOut.setStyle(quietButtonStyle);
        zoomIn.setStyle(quietButtonStyle);
        zoomReset.setStyle(quietButtonStyle);
        themeToggle.setStyle(quietButtonStyle);
        themeToggle.setText(darkMode ? "Switch to light mode" : "Switch to dark mode");
    }

    private void chooseWorkspace(Stage stage) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose a folder to visualize");

        File selected = chooser.showDialog(stage);
        if (selected == null) {
            return;
        }

        try {
            FolderItem workspace = fileSystem.openWorkspace(selected.toPath());
            graphView.setWorkspace(workspace);
        } catch (IOException exception) {
            showError("Could not open folder", exception);
        }
    }

    private void showError(String header, IOException exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("File system error");
        alert.setHeaderText(header);
        alert.setContentText(exception.getMessage());
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
