package gui.view;

import gui.controller.ExplorerController;
import gui.model.FileViewMode;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 * Top application MenuBar providing standard file actions, view toggles, and shortcuts.
 */
public class MenuBarComponent extends MenuBar {
    private final ExplorerController controller;

    public MenuBarComponent(ExplorerController controller) {
        this.controller = controller;
        this.getStyleClass().add("menu-bar");

        this.getMenus().addAll(createFileMenu(), createEditMenu(), createViewMenu(), createHelpMenu());
    }

    private Menu createFileMenu() {
        Menu fileMenu = new Menu("File");

        MenuItem newFolder = new MenuItem("New Folder");
        newFolder.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN));
        newFolder.setOnAction(e -> controller.promptCreateNewFolder(null));

        MenuItem newFile = new MenuItem("New File");
        newFile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
        newFile.setOnAction(e -> controller.promptCreateNewFile(null));

        MenuItem openItem = new MenuItem("Open Selected");
        openItem.setOnAction(e -> controller.openSelectedItem());

        MenuItem refreshItem = new MenuItem("Refresh");
        refreshItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN));
        refreshItem.setOnAction(e -> controller.refreshCurrentDirectory());

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN));
        exitItem.setOnAction(e -> System.exit(0));

        fileMenu.getItems().addAll(newFolder, newFile, new SeparatorMenuItem(), openItem, refreshItem, new SeparatorMenuItem(), exitItem);
        return fileMenu;
    }

    private Menu createEditMenu() {
        Menu editMenu = new Menu("Edit");

        MenuItem cutItem = new MenuItem("Cut");
        cutItem.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN));
        cutItem.setOnAction(e -> controller.cutSelectedItems());

        MenuItem copyItem = new MenuItem("Copy");
        copyItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));
        copyItem.setOnAction(e -> controller.copySelectedItems());

        MenuItem pasteItem = new MenuItem("Paste");
        pasteItem.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN));
        pasteItem.setOnAction(e -> controller.pasteItems());

        MenuItem duplicateItem = new MenuItem("Duplicate");
        duplicateItem.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.SHORTCUT_DOWN));
        duplicateItem.setOnAction(e -> controller.duplicateSelectedItem());

        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setAccelerator(new KeyCodeCombination(KeyCode.BACK_SPACE, KeyCombination.SHORTCUT_DOWN));
        deleteItem.setOnAction(e -> controller.deleteSelectedItems());

        MenuItem renameItem = new MenuItem("Rename");
        renameItem.setAccelerator(new KeyCodeCombination(KeyCode.F2));
        renameItem.setOnAction(e -> controller.renameSelectedItem());

        MenuItem selectAllItem = new MenuItem("Select All");
        selectAllItem.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN));
        selectAllItem.setOnAction(e -> controller.selectAll());

        editMenu.getItems().addAll(cutItem, copyItem, pasteItem, duplicateItem, new SeparatorMenuItem(), renameItem, deleteItem, new SeparatorMenuItem(), selectAllItem);
        return editMenu;
    }

    private Menu createViewMenu() {
        Menu viewMenu = new Menu("View");

        MenuItem detailsItem = new MenuItem("Detailed Table View");
        detailsItem.setOnAction(e -> controller.switchViewMode(FileViewMode.DETAILS));

        MenuItem listItem = new MenuItem("List View");
        listItem.setOnAction(e -> controller.switchViewMode(FileViewMode.LIST));

        MenuItem tilesItem = new MenuItem("Grid / Tiles View");
        tilesItem.setOnAction(e -> controller.switchViewMode(FileViewMode.TILES));

        MenuItem graphItem = new MenuItem("Obsidian Graph View");
        graphItem.setOnAction(e -> controller.switchViewMode(FileViewMode.GRAPH));

        MenuItem toggleSidebar = new MenuItem("Toggle Sidebar");
        toggleSidebar.setOnAction(e -> controller.toggleSidebar());

        MenuItem toggleInspector = new MenuItem("Toggle Details Inspector");
        toggleInspector.setOnAction(e -> controller.toggleDetailsInspector());

        viewMenu.getItems().addAll(detailsItem, listItem, tilesItem, graphItem, new SeparatorMenuItem(), toggleSidebar, toggleInspector);
        return viewMenu;
    }

    private Menu createHelpMenu() {
        Menu helpMenu = new Menu("Help");

        MenuItem shortcutsItem = new MenuItem("Keyboard Shortcuts");
        shortcutsItem.setOnAction(e -> controller.showShortcutsDialog());

        MenuItem aboutItem = new MenuItem("About File Explorer");
        aboutItem.setOnAction(e -> controller.showAboutDialog());

        helpMenu.getItems().addAll(shortcutsItem, new SeparatorMenuItem(), aboutItem);
        return helpMenu;
    }
}
