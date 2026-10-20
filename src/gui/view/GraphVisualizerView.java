package gui.view;

import gui.controller.ExplorerController;
import gui.model.ExplorerItem;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Obsidian-inspired interactive graph visualizer displaying files and folders as a force-directed node graph.
 */
public class GraphVisualizerView extends StackPane {
    private final ExplorerController controller;
    private final Canvas canvas;
    private final List<GraphNode> nodes = new ArrayList<>();
    private GraphNode draggedNode = null;
    private GraphNode hoveredNode = null;
    private AnimationTimer timer;
    private ExplorerItem currentFolder;

    public static class GraphNode {
        public ExplorerItem item;
        public double x, y;
        public double vx, vy;
        public double radius;
        public Color color;
        public boolean isCenter;

        public GraphNode(ExplorerItem item, double x, double y, double radius, Color color, boolean isCenter) {
            this.item = item;
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.color = color;
            this.isCenter = isCenter;
        }
    }

    public GraphVisualizerView(ExplorerController controller) {
        this.controller = controller;
        this.setStyle("-fx-background-color: #121214;");

        canvas = new Canvas(800, 600);
        // Bind canvas size to stackpane size
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());

        Label title = new Label("Obsidian-Style Graph View (Click or Drag nodes, Double-click folders)");
        title.setStyle("-fx-text-fill: #71717a; -fx-font-size: 11px; -fx-padding: 8;");
        StackPane.setAlignment(title, Pos.TOP_LEFT);

        this.getChildren().addAll(canvas, title);

        setupMouseInteraction();
        startSimulation();
    }

    public void displayGraph(ExplorerItem folder) {
        this.currentFolder = folder;
        nodes.clear();
        if (folder == null) return;

        double cx = Math.max(200, canvas.getWidth() / 2.0);
        double cy = Math.max(200, canvas.getHeight() / 2.0);

        // Center Node (Current Directory)
        GraphNode centerNode = new GraphNode(folder, cx, cy, 18, Color.web("#F59E0B"), true);
        nodes.add(centerNode);

        List<ExplorerItem> children = folder.getChildren();
        int count = children.size();
        Random rng = new Random(42);

        for (int i = 0; i < count; i++) {
            ExplorerItem child = children.get(i);
            double angle = (2 * Math.PI * i) / Math.max(1, count) + (rng.nextDouble() * 0.2);
            double distance = 110 + rng.nextDouble() * 90;
            double nx = cx + Math.cos(angle) * distance;
            double ny = cy + Math.sin(angle) * distance;

            Color nodeColor;
            double radius;
            if (child.isDirectory()) {
                nodeColor = Color.web("#FBBF24");
                radius = 12;
            } else {
                switch (child.getFileCategory()) {
                    case "code": nodeColor = Color.web("#38BDF8"); break;
                    case "pdf": nodeColor = Color.web("#F87171"); break;
                    case "image": nodeColor = Color.web("#A78BFA"); break;
                    case "doc": nodeColor = Color.web("#60A5FA"); break;
                    case "sheet": nodeColor = Color.web("#34D399"); break;
                    default: nodeColor = Color.web("#94A3B8"); break;
                }
                radius = 8;
            }

            nodes.add(new GraphNode(child, nx, ny, radius, nodeColor, false));
        }
    }

    private void setupMouseInteraction() {
        canvas.setOnMousePressed(e -> {
            for (GraphNode node : nodes) {
                double dx = node.x - e.getX();
                double dy = node.y - e.getY();
                if (Math.hypot(dx, dy) <= node.radius + 6) {
                    draggedNode = node;
                    controller.onSelectionChanged(List.of(node.item));
                    if (e.getClickCount() == 2 && node.item.isDirectory() && !node.isCenter) {
                        controller.navigateTo(node.item);
                    }
                    break;
                }
            }
        });

        canvas.setOnMouseDragged(e -> {
            if (draggedNode != null) {
                draggedNode.x = e.getX();
                draggedNode.y = e.getY();
                draggedNode.vx = 0;
                draggedNode.vy = 0;
            }
        });

        canvas.setOnMouseReleased(e -> draggedNode = null);

        canvas.setOnMouseMoved(e -> {
            hoveredNode = null;
            for (GraphNode node : nodes) {
                if (Math.hypot(node.x - e.getX(), node.y - e.getY()) <= node.radius + 4) {
                    hoveredNode = node;
                    break;
                }
            }
        });
    }

    private void startSimulation() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updatePhysics();
                render();
            }
        };
        timer.start();
    }

    private void updatePhysics() {
        if (nodes.isEmpty()) return;
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        if (width <= 0 || height <= 0) return;

        GraphNode center = nodes.get(0);
        if (draggedNode != center) {
            center.x = width / 2.0;
            center.y = height / 2.0;
        }

        // Repulsion between nodes
        for (int i = 0; i < nodes.size(); i++) {
            GraphNode n1 = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++) {
                GraphNode n2 = nodes.get(j);
                double dx = n2.x - n1.x;
                double dy = n2.y - n1.y;
                double dist = Math.hypot(dx, dy);
                if (dist > 1) {
                    double force = 2800.0 / (dist * dist);
                    double fx = (dx / dist) * force;
                    double fy = (dy / dist) * force;
                    if (n1 != draggedNode && !n1.isCenter) { n1.vx -= fx; n1.vy -= fy; }
                    if (n2 != draggedNode && !n2.isCenter) { n2.vx += fx; n2.vy += fy; }
                }
            }
        }

        // Attraction to center node (spring force)
        for (int i = 1; i < nodes.size(); i++) {
            GraphNode n = nodes.get(i);
            if (n == draggedNode) continue;
            double dx = center.x - n.x;
            double dy = center.y - n.y;
            double dist = Math.hypot(dx, dy);
            double springForce = (dist - 140) * 0.015;
            n.vx += (dx / dist) * springForce;
            n.vy += (dy / dist) * springForce;

            // Damping
            n.vx *= 0.85;
            n.vy *= 0.85;

            n.x += n.vx;
            n.y += n.vy;

            // Bounds constrain
            n.x = Math.max(30, Math.min(width - 30, n.x));
            n.y = Math.max(30, Math.min(height - 30, n.y));
        }
    }

    private void render() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        gc.clearRect(0, 0, width, height);

        if (nodes.isEmpty()) return;

        GraphNode center = nodes.get(0);

        // Draw connections
        gc.setLineWidth(1.2);
        for (int i = 1; i < nodes.size(); i++) {
            GraphNode n = nodes.get(i);
            gc.setStroke(Color.rgb(60, 65, 80, 0.65));
            gc.strokeLine(center.x, center.y, n.x, n.y);
        }

        // Draw Nodes
        for (GraphNode node : nodes) {
            // Glow effect for hovered or center node
            if (node == hoveredNode || node.isCenter) {
                gc.setFill(node.color.deriveColor(0, 1, 1, 0.25));
                gc.fillOval(node.x - node.radius - 5, node.y - node.radius - 5, (node.radius + 5) * 2, (node.radius + 5) * 2);
            }

            gc.setFill(node.color);
            gc.fillOval(node.x - node.radius, node.y - node.radius, node.radius * 2, node.radius * 2);

            // Node Label
            gc.setFill(node == hoveredNode ? Color.WHITE : Color.web("#CBD5E1"));
            gc.setFont(javafx.scene.text.Font.font(node.isCenter ? 12 : 10));
            gc.fillText(node.item.getName(), node.x + node.radius + 4, node.y + 4);
        }
    }
}
