package gui.view;

import gui.controller.ExplorerController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 * Bottom status bar providing item metrics, animated loading indicators,
 * and error/notification displayers.
 */
public class StatusBarComponent extends HBox {
    private final ExplorerController controller;

    private final Label itemCountLabel;
    private final Label selectionLabel;

    private final HBox loadingBox;
    private final ProgressIndicator progressIndicator;
    private final Label loadingText;

    private final HBox notificationBanner;
    private final Label notificationText;
    private final Button dismissNotificationBtn;
    private Timeline autoDismissTimer;

    public StatusBarComponent(ExplorerController controller) {
        this.controller = controller;
        this.getStyleClass().add("status-bar-container");
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(14);

        // Item Metrics
        itemCountLabel = new Label("0 items");
        itemCountLabel.getStyleClass().add("status-label");

        selectionLabel = new Label("");
        selectionLabel.getStyleClass().add("status-label");
        selectionLabel.setStyle("-fx-text-fill: #38bdf8;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Loading Indicator Box
        loadingBox = new HBox(8);
        loadingBox.setAlignment(Pos.CENTER_LEFT);
        progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(14, 14);

        loadingText = new Label("Loading...");
        loadingText.getStyleClass().add("status-label");
        loadingBox.getChildren().addAll(progressIndicator, loadingText);
        loadingBox.setVisible(false);

        // Notification / Error Banner Box
        notificationBanner = new HBox(8);
        notificationBanner.setAlignment(Pos.CENTER_LEFT);
        notificationBanner.setVisible(false);

        notificationText = new Label();
        dismissNotificationBtn = new Button("✕");
        dismissNotificationBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: inherit; -fx-cursor: hand; -fx-padding: 0 4 0 4;");
        dismissNotificationBtn.setOnAction(e -> hideNotification());

        notificationBanner.getChildren().addAll(notificationText, dismissNotificationBtn);

        this.getChildren().addAll(itemCountLabel, selectionLabel, notificationBanner, spacer, loadingBox);
    }

    public void updateMetrics(int totalCount, int selectedCount, long selectedBytes) {
        itemCountLabel.setText(totalCount + (totalCount == 1 ? " item" : " items"));

        if (selectedCount == 0) {
            selectionLabel.setText("");
        } else {
            String sizeStr = formatBytes(selectedBytes);
            selectionLabel.setText(selectedCount + (selectedCount == 1 ? " item selected" : " items selected") + (selectedBytes > 0 ? " (" + sizeStr + ")" : ""));
        }
    }

    public void showLoading(String message) {
        loadingText.setText(message != null ? message : "Working...");
        loadingBox.setVisible(true);
    }

    public void hideLoading() {
        loadingBox.setVisible(false);
    }

    public void showError(String message) {
        showNotification(message, "notification-banner-error");
    }

    public void showSuccess(String message) {
        showNotification(message, "notification-banner-success");
    }

    public void showInfo(String message) {
        showNotification(message, "notification-banner-info");
    }

    private void showNotification(String message, String styleClass) {
        notificationBanner.getStyleClass().removeAll("notification-banner-error", "notification-banner-success", "notification-banner-info");
        notificationBanner.getStyleClass().add(styleClass);
        notificationText.setText(message);
        notificationBanner.setVisible(true);

        if (autoDismissTimer != null) {
            autoDismissTimer.stop();
        }

        // Auto-dismiss after 4.5 seconds
        autoDismissTimer = new Timeline(new KeyFrame(Duration.seconds(4.5), e -> hideNotification()));
        autoDismissTimer.play();
    }

    public void hideNotification() {
        notificationBanner.setVisible(false);
        if (autoDismissTimer != null) {
            autoDismissTimer.stop();
        }
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
