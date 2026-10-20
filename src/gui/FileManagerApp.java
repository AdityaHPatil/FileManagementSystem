package gui;

import gui.controller.ExplorerController;
import gui.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Main JavaFX Application for the File Management System.
 */
public class FileManagerApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        ExplorerController controller = new ExplorerController();
        MainView mainView = new MainView(controller);
        controller.setMainView(mainView);

        Scene scene = new Scene(mainView, 1200, 760);

        // Load custom styles
        URL cssResource = getClass().getResource("/gui/styles.css");
        if (cssResource != null) {
            scene.getStylesheets().add(cssResource.toExternalForm());
        } else {
            // Fallback to relative file URL if running in dev mode
            URL fallbackCss = getClass().getResource("styles.css");
            if (fallbackCss != null) {
                scene.getStylesheets().add(fallbackCss.toExternalForm());
            }
        }

        // Initialize controller state and load workspace
        controller.initialize();

        primaryStage.setTitle("File Management System — Explorer");
        primaryStage.setMinWidth(960);
        primaryStage.setMinHeight(620);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
