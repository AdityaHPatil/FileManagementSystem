package gui.view;

import gui.controller.ExplorerController;
import gui.model.ExplorerItem;
import gui.util.IconFactory;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Top navigation bar containing Back/Forward/Up/Home buttons,
 * dynamic breadcrumb address bar with text-edit toggle, and live search bar.
 */
public class NavigationToolBar extends HBox {
    private final ExplorerController controller;

    private final Button backBtn;
    private final Button forwardBtn;
    private final Button upBtn;
    private final Button refreshBtn;
    private final Button homeBtn;

    // Address Bar components
    private final StackPane addressBarStack;
    private final HBox breadcrumbsBox;
    private final TextField addressTextField;

    // Search Box components
    private final TextField searchField;
    private final Button searchClearBtn;

    public NavigationToolBar(ExplorerController controller) {
        this.controller = controller;
        this.getStyleClass().add("nav-bar-container");
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(8);

        // Navigation History Buttons
        backBtn = createIconButton("back", "Back (Alt+Left)");
        backBtn.setOnAction(e -> controller.goBack());
        backBtn.disableProperty().bind(controller.getNavigationHistory().canGoBackProperty().not());

        forwardBtn = createIconButton("forward", "Forward (Alt+Right)");
        forwardBtn.setOnAction(e -> controller.goForward());
        forwardBtn.disableProperty().bind(controller.getNavigationHistory().canGoForwardProperty().not());

        upBtn = createIconButton("up", "Up one level (Alt+Up)");
        upBtn.setOnAction(e -> controller.goUp());
        upBtn.disableProperty().bind(controller.getNavigationHistory().canGoUpProperty().not());

        refreshBtn = createIconButton("refresh", "Refresh (Ctrl+R / F5)");
        refreshBtn.setOnAction(e -> controller.refreshCurrentDirectory());

        homeBtn = createIconButton("home", "User Home");
        homeBtn.setOnAction(e -> controller.goHome());

        HBox navGroup = new HBox(4, backBtn, forwardBtn, upBtn, refreshBtn, homeBtn);
        navGroup.setAlignment(Pos.CENTER_LEFT);

        // Address Bar Stack (Breadcrumbs + Text Editor)
        addressBarStack = new StackPane();
        addressBarStack.getStyleClass().add("address-bar-box");
        HBox.setHgrow(addressBarStack, Priority.ALWAYS);

        breadcrumbsBox = new HBox(4);
        breadcrumbsBox.setAlignment(Pos.CENTER_LEFT);
        breadcrumbsBox.setOnMouseClicked(e -> {
            if (e.getTarget() == breadcrumbsBox) {
                switchToAddressEditMode();
            }
        });

        addressTextField = new TextField();
        addressTextField.getStyleClass().add("address-text-field");
        addressTextField.setVisible(false);
        addressTextField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                controller.navigateToPath(addressTextField.getText());
                switchToBreadcrumbMode();
            } else if (e.getCode() == KeyCode.ESCAPE) {
                switchToBreadcrumbMode();
            }
        });
        addressTextField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                switchToBreadcrumbMode();
            }
        });

        // Edit address button
        Button editAddressBtn = new Button("✎");
        editAddressBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #71717a; -fx-cursor: hand; -fx-padding: 0 4 0 4;");
        editAddressBtn.setTooltip(new Tooltip("Edit Address"));
        editAddressBtn.setOnAction(e -> switchToAddressEditMode());

        HBox breadcrumbWrapper = new HBox(6, breadcrumbsBox, editAddressBtn);
        breadcrumbWrapper.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(breadcrumbsBox, Priority.ALWAYS);

        addressBarStack.getChildren().addAll(breadcrumbWrapper, addressTextField);

        // Live Search Bar
        HBox searchBox = new HBox(6);
        searchBox.getStyleClass().add("search-box");
        searchBox.setAlignment(Pos.CENTER_LEFT);

        Label searchIcon = new Label("🔍");
        searchIcon.setStyle("-fx-text-fill: #71717a; -fx-font-size: 11px;");

        searchField = new TextField();
        searchField.getStyleClass().add("search-field");
        searchField.setPromptText("Search files & folders...");

        searchClearBtn = new Button("✕");
        searchClearBtn.getStyleClass().add("search-clear-btn");
        searchClearBtn.visibleProperty().bind(searchField.textProperty().isNotEmpty());
        searchClearBtn.setOnAction(e -> searchField.clear());

        searchField.textProperty().addListener((obs, oldVal, query) -> {
            controller.search(query);
        });

        searchBox.getChildren().addAll(searchIcon, searchField, searchClearBtn);

        this.getChildren().addAll(navGroup, addressBarStack, searchBox);
    }

    private Button createIconButton(String iconType, String tooltipText) {
        Button btn = new Button();
        btn.getStyleClass().add("nav-button");
        btn.setGraphic(IconFactory.createActionIcon(iconType, 13));
        btn.setTooltip(new Tooltip(tooltipText));
        return btn;
    }

    public void updateAddressBar(ExplorerItem item) {
        if (item == null) return;
        addressTextField.setText(item.getPath());

        breadcrumbsBox.getChildren().clear();

        List<ExplorerItem> chain = new ArrayList<>();
        ExplorerItem curr = item;
        while (curr != null) {
            chain.add(curr);
            curr = curr.getParent();
        }
        Collections.reverse(chain);

        for (int i = 0; i < chain.size(); i++) {
            ExplorerItem segment = chain.get(i);
            Button segBtn = new Button(segment.getName().isEmpty() ? "/" : segment.getName());
            segBtn.getStyleClass().add("breadcrumb-button");
            segBtn.setOnAction(e -> controller.navigateTo(segment));
            breadcrumbsBox.getChildren().add(segBtn);

            if (i < chain.size() - 1) {
                Label sep = new Label("›");
                sep.getStyleClass().add("breadcrumb-separator");
                breadcrumbsBox.getChildren().add(sep);
            }
        }
    }

    private void switchToAddressEditMode() {
        addressTextField.setVisible(true);
        addressTextField.requestFocus();
        addressTextField.selectAll();
    }

    private void switchToBreadcrumbMode() {
        addressTextField.setVisible(false);
    }

    public TextField getSearchField() {
        return searchField;
    }
}
