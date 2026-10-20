# File Management System (JavaFX)

A modern, desktop File Explorer application built in Java with JavaFX 21. Featuring hierarchical directory browsing, multiple file view modes (including an Obsidian-style graph visualizer), breadcrumb path navigation, live search, and a metadata inspector.

---

## Prerequisites

Before running the application, ensure you have:

- **Java JDK 17 or higher** (e.g., OpenJDK 21 or 27)
- **Apache Maven 3.8+**

> **Note for macOS (Homebrew OpenJDK users):**
> If you installed OpenJDK via Homebrew, ensure the JDK bin directory is in your `PATH` and `JAVA_HOME` is exported:
> ```bash
> export PATH="/opt/homebrew/opt/openjdk/bin:$PATH"
> export JAVA_HOME="/opt/homebrew/opt/openjdk"
> ```

---

## How to Run

### Option 1: Quick Launch via Maven (Recommended)

Run the following commands in the project root directory:

```bash
# 1. Set up environment paths (if using Homebrew OpenJDK)
export PATH="/opt/homebrew/opt/openjdk/bin:$PATH"
export JAVA_HOME="/opt/homebrew/opt/openjdk"

# 2. Compile and run the JavaFX application
mvn javafx:run
```

---

### Option 2: Build and Package as JAR

To build the executable package:

```bash
mvn clean package
```

The compiled JAR file will be generated in `target/file-management-system-1.0.0-SNAPSHOT.jar`.

---

## Key Features

1. **Main Window & Modern Dark Theme**
   - Responsive multi-panel layout using `SplitPane`.
   - Native `MenuBar` (`File`, `Edit`, `View`, `Help`) with keyboard accelerators.
   - Polished dark UI styling (`styles.css`) inspired by macOS Finder and Obsidian.

2. **Directory Tree & Quick Access**
   - Left sidebar featuring Quick Access shortcuts (**Desktop**, **Documents**, **Downloads**).
   - Expandable hierarchical directory tree with folder icons and click-to-navigate support.

3. **4 Switchable File Views**
   - **Detailed Table View**: Sortable columns for Name, Type, Size, Date Modified, and Permissions with custom icons.
   - **List View**: Compact vertical list displaying icons, names, types, and sizes.
   - **Grid / Tile View**: Visual cards with large 44px icons and item labels.
   - **Obsidian Graph View**: Interactive 2D node-link graph with physics simulation, connecting files and directories.

4. **Navigation & Breadcrumb Address Bar**
   - **History Navigation**: Back, Forward, and Up (parent folder) buttons with live state binding.
   - **Breadcrumb Navigation**: Click any ancestor folder segment to jump directly there.
   - **Direct Path Editing**: Click `✎` in the address bar to manually type or paste a folder path.

5. **Live Search & Multi-Selection**
   - Real-time search filter with instant results and a clear (`✕`) button.
   - Single-click, `Cmd/Ctrl+Click`, and `Shift+Click` multi-selection with total selected byte calculation.

6. **Action Toolbar & Metadata Inspector**
   - Quick action buttons: `+ Folder`, `+ File`, `Cut`, `Copy`, `Paste`, `Delete`, `Rename`.
   - Collapsible right-hand **Details Panel** displaying large icon preview, file size, location path with "Copy Path" button, timestamps, and permissions.

7. **Loading Indicators & Notifications**
   - Animated `ProgressIndicator` spinner during folder loading and searches.
   - Non-blocking banner notifications for errors (red), success messages (green), and info (blue) with auto-dismiss.

---

## Keyboard Shortcuts

| Shortcut | Action |
|---|---|
| `Cmd + N` / `Ctrl + N` | Create New File |
| `Cmd + Shift + N` / `Ctrl + Shift + N` | Create New Folder |
| `Cmd + C` / `Ctrl + C` | Copy Selected Items |
| `Cmd + X` / `Ctrl + X` | Cut Selected Items |
| `Cmd + V` / `Ctrl + V` | Paste Items from Clipboard |
| `Cmd + D` / `Ctrl + D` | Duplicate Selected Item |
| `F2` | Rename Selected Item |
| `Cmd + Backspace` / `Delete` | Delete Selected Items |
| `Cmd + A` / `Ctrl + A` | Select All Items in View |
| `Cmd + R` / `F5` | Refresh Current Directory |
| `Alt + Left` | Navigate Back in History |
| `Alt + Right` | Navigate Forward in History |
| `Alt + Up` | Navigate Up to Parent Directory |

---

## Project Structure

```
FileManagementSystem/
├── pom.xml                 # Maven build configuration and JavaFX dependencies
├── README.md               # Project documentation and run instructions
├── gemini.md               # Project design specification
└── src/
    ├── filesystem/         # Backend file management service
    │   ├── FileManagerService.java
    │   └── FileSystem.java
    ├── models/             # Core file system domain models
    │   ├── FileSystemItem.java
    │   ├── FileItem.java
    │   └── FolderItem.java
    └── gui/                # JavaFX GUI layer
        ├── FileManagerApp.java        # JavaFX Application entry point
        ├── Launcher.java              # Standard main launcher
        ├── styles.css                 # Dark theme styling
        ├── controller/
        │   └── ExplorerController.java
        ├── model/
        │   ├── ExplorerItem.java
        │   ├── MockExplorerData.java
        │   ├── NavigationHistory.java
        │   └── FileViewMode.java
        ├── util/
        │   └── IconFactory.java
        └── view/
            ├── MainView.java
            ├── MenuBarComponent.java
            ├── NavigationToolBar.java
            ├── ActionToolBar.java
            ├── SidebarView.java
            ├── DirectoryTreeView.java
            ├── FileViewContainer.java
            ├── DetailedTableView.java
            ├── FileListView.java
            ├── TileGridView.java
            ├── GraphVisualizerView.java
            ├── DetailsInspectorView.java
            └── StatusBarComponent.java
```
