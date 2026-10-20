package gui.controller;

import gui.model.ExplorerItem;
import gui.model.FileViewMode;
import gui.model.MockExplorerData;
import gui.model.NavigationHistory;
import gui.view.MainView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller orchestrating file exploration, navigation history,
 * search filtering, file operations, loading indicators, and notifications.
 */
public class ExplorerController {
    private final MockExplorerData dataProvider;
    private final NavigationHistory navigationHistory;
    private MainView mainView;

    private ExplorerItem currentDirectory;
    private final List<ExplorerItem> currentItems = new ArrayList<>();
    private final List<ExplorerItem> selectedItems = new ArrayList<>();

    // Clipboard
    private final List<ExplorerItem> clipboard = new ArrayList<>();
    private boolean isCutMode = false;

    public ExplorerController() {
        this.dataProvider = new MockExplorerData();
        this.navigationHistory = new NavigationHistory();
    }

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    public void initialize() {
        // Populate sidebar
        mainView.getSidebarView().populateQuickAccess(dataProvider.getQuickAccessItems());
        mainView.getSidebarView().getDirectoryTree().populateTree(dataProvider.getRoot());

        // Initial directory: navigate to the Desktop folder
        ExplorerItem initialDir = dataProvider.findByPath("/Macintosh HD/Users/akarsh/Desktop");
        if (initialDir == null) {
            initialDir = dataProvider.findByPath("/Macintosh HD/Users/akarsh");
        }
        if (initialDir == null) {
            initialDir = dataProvider.getRoot();
        }

        navigateTo(initialDir);
        showSuccessNotification("Opened: " + initialDir.getName());
    }

    // ==========================================
    // Navigation
    // ==========================================

    public void navigateTo(ExplorerItem item) {
        if (item == null) return;
        if (!item.isDirectory()) {
            openItem(item);
            return;
        }

        navigationHistory.navigateTo(item);
        loadDirectory(item);
    }

    public void navigateToPath(String path) {
        if (path == null || path.trim().isEmpty()) return;
        ExplorerItem found = dataProvider.findByPath(path.trim());
        if (found != null && found.isDirectory()) {
            navigateTo(found);
        } else {
            showErrorNotification("Directory not found: " + path);
        }
    }

    public void goBack() {
        ExplorerItem item = navigationHistory.goBack();
        if (item != null) {
            loadDirectory(item);
        }
    }

    public void goForward() {
        ExplorerItem item = navigationHistory.goForward();
        if (item != null) {
            loadDirectory(item);
        }
    }

    public void goUp() {
        ExplorerItem item = navigationHistory.goUp();
        if (item != null) {
            loadDirectory(item);
        }
    }

    public void goHome() {
        ExplorerItem home = dataProvider.findByPath("/Macintosh HD/Users/akarsh");
        if (home != null) {
            navigateTo(home);
        }
    }

    public void refreshCurrentDirectory() {
        if (currentDirectory != null) {
            mainView.getStatusBar().showLoading("Refreshing directory...");
            // Simulate smooth asynchronous fetch
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(350), e -> {
                loadDirectory(currentDirectory);
                mainView.getStatusBar().hideLoading();
                showInfoNotification("Refreshed contents.");
            }));
            timeline.play();
        }
    }

    private void loadDirectory(ExplorerItem folder) {
        this.currentDirectory = folder;

        // Clear active search query if any
        mainView.getNavigationToolBar().getSearchField().clear();

        // Update top address bar
        mainView.getNavigationToolBar().updateAddressBar(folder);

        // Highlight in sidebar tree
        mainView.getSidebarView().getDirectoryTree().selectItemInTree(folder);

        // Populate items
        currentItems.clear();
        currentItems.addAll(folder.getChildren());

        // Update content view
        mainView.getFileViewContainer().updateItems(currentItems);

        // Clear selection & inspector
        selectedItems.clear();
        mainView.getDetailsInspectorView().clearDetails();
        mainView.getActionToolBar().updateSelectionState(0);
        mainView.getStatusBar().updateMetrics(currentItems.size(), 0, 0);
    }

    // ==========================================
    // Search
    // ==========================================

    public void search(String query) {
        if (query == null || query.trim().isEmpty()) {
            // Restore current folder items
            if (currentDirectory != null) {
                currentItems.clear();
                currentItems.addAll(currentDirectory.getChildren());
                mainView.getFileViewContainer().updateItems(currentItems);
                mainView.getStatusBar().updateMetrics(currentItems.size(), 0, 0);
            }
            return;
        }

        String lowerQuery = query.toLowerCase().trim();
        mainView.getStatusBar().showLoading("Searching...");

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(150), e -> {
            List<ExplorerItem> searchResults = new ArrayList<>();

            // First search in current directory
            if (currentDirectory != null) {
                for (ExplorerItem item : currentDirectory.getChildren()) {
                    if (item.getName().toLowerCase().contains(lowerQuery)) {
                        searchResults.add(item);
                    }
                }
            }

            // If query is 2+ chars and local search yielded few results, search whole workspace
            if (searchResults.isEmpty() && lowerQuery.length() >= 2) {
                searchResults = dataProvider.search(lowerQuery);
            }

            currentItems.clear();
            currentItems.addAll(searchResults);
            mainView.getFileViewContainer().updateItems(currentItems);

            mainView.getStatusBar().hideLoading();
            mainView.getStatusBar().updateMetrics(currentItems.size(), 0, 0);

            if (searchResults.isEmpty()) {
                showErrorNotification("No matching files found for '" + query + "'");
            } else {
                showInfoNotification("Found " + searchResults.size() + " matches");
            }
        }));
        timeline.play();
    }

    // ==========================================
    // Selection Handling
    // ==========================================

    public void onSelectionChanged(List<ExplorerItem> selected) {
        selectedItems.clear();
        if (selected != null) {
            selectedItems.addAll(selected);
        }

        int count = selectedItems.size();
        long totalBytes = 0;
        for (ExplorerItem item : selectedItems) {
            if (!item.isDirectory()) {
                totalBytes += item.getSizeBytes();
            }
        }

        // Update Status Bar
        mainView.getStatusBar().updateMetrics(currentItems.size(), count, totalBytes);

        // Update Action Toolbar
        mainView.getActionToolBar().updateSelectionState(count);

        // Update Inspector
        if (count == 1) {
            mainView.getDetailsInspectorView().displayItem(selectedItems.get(0));
        } else if (count == 0) {
            mainView.getDetailsInspectorView().clearDetails();
        } else {
            // Multiple items selected: create a synthetic preview item
            ExplorerItem summaryItem = new ExplorerItem(
                count + " items selected",
                currentDirectory != null ? currentDirectory.getPath() : "",
                false,
                totalBytes,
                LocalDateTime.now(),
                "Multiple Items"
            );
            mainView.getDetailsInspectorView().displayItem(summaryItem);
        }
    }

    public void selectAll() {
        FileViewMode mode = mainView.getFileViewContainer().getCurrentMode();
        if (mode == FileViewMode.DETAILS) {
            mainView.getFileViewContainer().getDetailedTableView().getSelectionModel().selectAll();
        } else if (mode == FileViewMode.LIST) {
            mainView.getFileViewContainer().getFileListView().getSelectionModel().selectAll();
        }
    }

    // ==========================================
    // View Switching & Panels
    // ==========================================

    public void switchViewMode(FileViewMode mode) {
        mainView.getFileViewContainer().setViewMode(mode);
        mainView.getActionToolBar().updateViewModeSelection(mode);
        showInfoNotification("Switched to " + mode.getDisplayName());
    }

    public void toggleSidebar() {
        mainView.toggleSidebar();
    }

    public void toggleDetailsInspector() {
        mainView.toggleDetailsInspector();
    }

    // ==========================================
    // File Operations (CRUD & Actions)
    // ==========================================

    public void promptCreateNewFolder(ExplorerItem targetParent) {
        ExplorerItem parent = targetParent != null ? targetParent : currentDirectory;
        if (parent == null) return;

        TextInputDialog dialog = new TextInputDialog("New Folder");
        dialog.setTitle("Create New Folder");
        dialog.setHeaderText("Create a new directory in " + parent.getName());
        dialog.setContentText("Folder name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            String folderName = name.trim();
            if (!folderName.isEmpty()) {
                ExplorerItem newFolder = new ExplorerItem(
                    folderName,
                    parent.getPath() + "/" + folderName,
                    true,
                    0,
                    LocalDateTime.now(),
                    "File Folder"
                );
                parent.addChild(newFolder);
                loadDirectory(parent);
                showSuccessNotification("Created folder: " + folderName);
            }
        });
    }

    public void promptCreateNewFile(ExplorerItem targetParent) {
        ExplorerItem parent = targetParent != null ? targetParent : currentDirectory;
        if (parent == null) return;

        TextInputDialog dialog = new TextInputDialog("NewDocument.txt");
        dialog.setTitle("Create New File");
        dialog.setHeaderText("Create a new file in " + parent.getName());
        dialog.setContentText("File name (with extension):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            String fileName = name.trim();
            if (!fileName.isEmpty()) {
                ExplorerItem newFile = new ExplorerItem(
                    fileName,
                    parent.getPath() + "/" + fileName,
                    false,
                    0,
                    LocalDateTime.now(),
                    "Document"
                );
                parent.addChild(newFile);
                loadDirectory(parent);
                showSuccessNotification("Created file: " + fileName);
            }
        });
    }

    public void promptRenameItem(ExplorerItem item) {
        if (item == null) return;

        TextInputDialog dialog = new TextInputDialog(item.getName());
        dialog.setTitle("Rename Item");
        dialog.setHeaderText("Rename '" + item.getName() + "'");
        dialog.setContentText("New name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            String trimmed = newName.trim();
            if (!trimmed.isEmpty() && !trimmed.equals(item.getName())) {
                String oldName = item.getName();
                item.setName(trimmed);
                if (item.getParent() != null) {
                    item.setPath(item.getParent().getPath() + "/" + trimmed);
                }
                item.setLastModified(LocalDateTime.now());
                loadDirectory(currentDirectory);
                showSuccessNotification("Renamed '" + oldName + "' to '" + trimmed + "'");
            }
        });
    }

    public void renameSelectedItem() {
        if (selectedItems.size() == 1) {
            promptRenameItem(selectedItems.get(0));
        }
    }

    public void deleteItem(ExplorerItem item) {
        if (item == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete " + (item.isDirectory() ? "folder" : "file") + " '" + item.getName() + "'?");
        confirm.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (item.getParent() != null) {
                item.getParent().removeChild(item);
                loadDirectory(currentDirectory);
                showSuccessNotification("Deleted '" + item.getName() + "'");
            }
        }
    }

    public void deleteSelectedItems() {
        if (selectedItems.isEmpty()) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete " + selectedItems.size() + " selected item(s)?");
        confirm.setContentText("Are you sure you want to permanently delete these items?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            List<ExplorerItem> toDelete = new ArrayList<>(selectedItems);
            for (ExplorerItem item : toDelete) {
                if (item.getParent() != null) {
                    item.getParent().removeChild(item);
                }
            }
            loadDirectory(currentDirectory);
            showSuccessNotification("Deleted " + toDelete.size() + " items.");
        }
    }

    public void openItem(ExplorerItem item) {
        if (item == null) return;
        if (item.isDirectory()) {
            navigateTo(item);
        } else {
            // Simulated opening
            showSuccessNotification("Opened '" + item.getName() + "' with default application.");
        }
    }

    public void openSelectedItem() {
        if (!selectedItems.isEmpty()) {
            openItem(selectedItems.get(0));
        }
    }

    public void duplicateItem(ExplorerItem item) {
        if (item == null || item.getParent() == null) return;

        String copyName = item.getName() + " - Copy";
        ExplorerItem copy = new ExplorerItem(
            copyName,
            item.getParent().getPath() + "/" + copyName,
            item.isDirectory(),
            item.getSizeBytes(),
            LocalDateTime.now(),
            item.getType()
        );
        item.getParent().addChild(copy);
        loadDirectory(currentDirectory);
        showSuccessNotification("Duplicated: " + copyName);
    }

    public void duplicateSelectedItem() {
        if (selectedItems.size() == 1) {
            duplicateItem(selectedItems.get(0));
        }
    }

    public void cutSelectedItems() {
        if (selectedItems.isEmpty()) return;
        clipboard.clear();
        clipboard.addAll(selectedItems);
        isCutMode = true;
        mainView.getActionToolBar().updateSelectionState(selectedItems.size());
        showInfoNotification("Cut " + clipboard.size() + " items to clipboard.");
    }

    public void copySelectedItems() {
        if (selectedItems.isEmpty()) return;
        clipboard.clear();
        clipboard.addAll(selectedItems);
        isCutMode = false;
        mainView.getActionToolBar().updateSelectionState(selectedItems.size());
        showSuccessNotification("Copied " + clipboard.size() + " items to clipboard.");
    }

    public boolean canPaste() {
        return !clipboard.isEmpty();
    }

    public void pasteItems() {
        if (!canPaste() || currentDirectory == null) {
            showErrorNotification("Clipboard is empty!");
            return;
        }

        int pasteCount = 0;
        for (ExplorerItem item : clipboard) {
            if (isCutMode) {
                if (item.getParent() != null) {
                    item.getParent().removeChild(item);
                }
                item.setPath(currentDirectory.getPath() + "/" + item.getName());
                currentDirectory.addChild(item);
            } else {
                ExplorerItem pastedCopy = new ExplorerItem(
                    item.getName(),
                    currentDirectory.getPath() + "/" + item.getName(),
                    item.isDirectory(),
                    item.getSizeBytes(),
                    LocalDateTime.now(),
                    item.getType()
                );
                currentDirectory.addChild(pastedCopy);
            }
            pasteCount++;
        }

        if (isCutMode) {
            clipboard.clear();
            isCutMode = false;
        }

        loadDirectory(currentDirectory);
        showSuccessNotification("Pasted " + pasteCount + " items into " + currentDirectory.getName());
    }

    // ==========================================
    // Dialogs & Notifications
    // ==========================================

    public void showErrorNotification(String message) {
        if (mainView != null) {
            mainView.getStatusBar().showError(message);
        }
    }

    public void showSuccessNotification(String message) {
        if (mainView != null) {
            mainView.getStatusBar().showSuccess(message);
        }
    }

    public void showInfoNotification(String message) {
        if (mainView != null) {
            mainView.getStatusBar().showInfo(message);
        }
    }

    public void showPropertiesDialog(ExplorerItem item) {
        if (item == null) return;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Properties: " + item.getName());
        alert.setHeaderText(item.getName() + " (" + item.getType() + ")");
        alert.setContentText(
            "Location: " + item.getPath() + "\n" +
            "Size: " + item.getFormattedSize() + " (" + item.getSizeBytes() + " bytes)\n" +
            "Created: " + item.getFormattedCreatedDate() + "\n" +
            "Modified: " + item.getFormattedModifiedDate() + "\n" +
            "Permissions: " + item.getPermissions() + "\n" +
            "Category: " + item.getFileCategory().toUpperCase()
        );
        alert.showAndWait();
    }

    public void showShortcutsDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Keyboard Shortcuts");
        alert.setHeaderText("File Explorer Shortcuts");
        alert.setContentText(
            "• Ctrl+N / Cmd+N : New File\n" +
            "• Ctrl+Shift+N / Cmd+Shift+N : New Folder\n" +
            "• Ctrl+C / Cmd+C : Copy Selected\n" +
            "• Ctrl+X / Cmd+X : Cut Selected\n" +
            "• Ctrl+V / Cmd+V : Paste\n" +
            "• Ctrl+D / Cmd+D : Duplicate\n" +
            "• F2 : Rename\n" +
            "• Ctrl+A / Cmd+A : Select All\n" +
            "• F5 / Ctrl+R : Refresh Directory\n" +
            "• Alt+Left : Back\n" +
            "• Alt+Right : Forward\n" +
            "• Alt+Up : Parent Directory\n"
        );
        alert.showAndWait();
    }

    public void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About File Explorer");
        alert.setHeaderText("TheSophisticated - File Management System");
        alert.setContentText(
            "JavaFX File Explorer GUI\n" +
            "Built with Java 17+ and JavaFX 21\n\n" +
            "Features:\n" +
            "✓ Directory Tree & Quick Access\n" +
            "✓ Multiple Views (Detailed Table, List, Grid Cards, Obsidian Graph)\n" +
            "✓ Interactive Breadcrumbs & Search Bar\n" +
            "✓ Forward / Backward Navigation History\n" +
            "✓ File Inspection & CRUD Operations\n" +
            "✓ Loading Indicators & Error Displayers"
        );
        alert.showAndWait();
    }

    public NavigationHistory getNavigationHistory() {
        return navigationHistory;
    }

    public ExplorerItem getCurrentDirectory() {
        return currentDirectory;
    }
}
