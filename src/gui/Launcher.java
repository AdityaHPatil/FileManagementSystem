package gui;

/**
 * Main launcher entry point that does not extend Application,
 * preventing JavaFX runtime issues in non-modular environments.
 */
public class Launcher {
    public static void main(String[] args) {
        FileManagerApp.main(args);
    }
}
