package gui.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Stack;

/**
 * Manages history stack for backward and forward navigation in the explorer.
 */
public class NavigationHistory {
    private final Stack<ExplorerItem> backStack = new Stack<>();
    private final Stack<ExplorerItem> forwardStack = new Stack<>();
    private final ObjectProperty<ExplorerItem> currentItem = new SimpleObjectProperty<>();

    private final BooleanProperty canGoBack = new SimpleBooleanProperty(false);
    private final BooleanProperty canGoForward = new SimpleBooleanProperty(false);
    private final BooleanProperty canGoUp = new SimpleBooleanProperty(false);

    public NavigationHistory() {
        currentItem.addListener((obs, oldVal, newVal) -> updateProperties());
    }

    /**
     * Navigate to a new directory (pushes current to back stack, clears forward stack).
     */
    public void navigateTo(ExplorerItem item) {
        if (item == null || item.equals(currentItem.get())) {
            return;
        }
        if (currentItem.get() != null) {
            backStack.push(currentItem.get());
        }
        forwardStack.clear();
        currentItem.set(item);
        updateProperties();
    }

    /**
     * Pop previous item from back stack, push current to forward stack.
     */
    public ExplorerItem goBack() {
        if (!backStack.isEmpty()) {
            ExplorerItem prev = backStack.pop();
            if (currentItem.get() != null) {
                forwardStack.push(currentItem.get());
            }
            currentItem.set(prev);
            updateProperties();
            return prev;
        }
        return null;
    }

    /**
     * Pop item from forward stack, push current to back stack.
     */
    public ExplorerItem goForward() {
        if (!forwardStack.isEmpty()) {
            ExplorerItem next = forwardStack.pop();
            if (currentItem.get() != null) {
                backStack.push(currentItem.get());
            }
            currentItem.set(next);
            updateProperties();
            return next;
        }
        return null;
    }

    /**
     * Navigate to the parent directory if one exists.
     */
    public ExplorerItem goUp() {
        ExplorerItem current = currentItem.get();
        if (current != null && current.getParent() != null) {
            navigateTo(current.getParent());
            return current.getParent();
        }
        return null;
    }

    private void updateProperties() {
        canGoBack.set(!backStack.isEmpty());
        canGoForward.set(!forwardStack.isEmpty());
        ExplorerItem current = currentItem.get();
        canGoUp.set(current != null && current.getParent() != null);
    }

    public ObjectProperty<ExplorerItem> currentItemProperty() {
        return currentItem;
    }

    public ExplorerItem getCurrentItem() {
        return currentItem.get();
    }

    public BooleanProperty canGoBackProperty() {
        return canGoBack;
    }

    public boolean canGoBack() {
        return canGoBack.get();
    }

    public BooleanProperty canGoForwardProperty() {
        return canGoForward;
    }

    public boolean canGoForward() {
        return canGoForward.get();
    }

    public BooleanProperty canGoUpProperty() {
        return canGoUp;
    }

    public boolean canGoUp() {
        return canGoUp.get();
    }
}
