package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import Data.LabWork;
import Data.Coordinates;


public class VisualizationPanel extends JPanel {
    private Map<Integer, LabWork> collection;
    private Integer currentUserId;
    private List<Point> points;
    private Point selectedPoint;
    private Point dragStart;
    private javax.swing.Timer animationTimer;
    private double animationProgress = 0.0;
    private static final int POINT_RADIUS = 5;
    private static final int ANIMATION_DURATION = 500;
    private static final Color[] USER_COLORS = {
        new Color(255, 0, 0),
        new Color(0, 0, 255),
        new Color(0, 128, 0),
        new Color(255, 165, 0),
        new Color(0, 128, 128),
        new Color(128, 0, 0),
        new Color(0, 0, 128)
    };
    private final Map<Integer, Color> userColorMap = new HashMap<>();
    private LabWork selectedLabWork = null;
    private static final int AXIS_PADDING = 20;
    private static final Color AXIS_COLOR = Color.BLACK;
    private static final Color POINT_COLOR = Color.BLUE;
    private static final Color HOVER_COLOR = new Color(255, 0, 0);
    private double minX = -1, maxX = 1, minY = -1, maxY = 1;
    private final Map<Integer, Point> pointsMap = new HashMap<>();
    private Integer hoveredPointId = -1;

    public VisualizationPanel() {
        collection = new TreeMap<>();
        points = new ArrayList<>();
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point click = e.getPoint();
                selectedPoint = findPointAt(click);
                if (selectedPoint != null) {
                    dragStart = e.getPoint();
                    int index = points.indexOf(selectedPoint);
                    if (index >= 0 && index < collection.size()) {
                        selectedLabWork = new ArrayList<>(collection.values()).get(index);
                        showLabWorkInfo(selectedLabWork);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedPoint != null) {
                    updateLabWorkCoordinates(selectedPoint);
                }
                selectedPoint = null;
                dragStart = null;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getPoint());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedPoint != null && dragStart != null) {
                    int dx = e.getX() - dragStart.x;
                    int dy = e.getY() - dragStart.y;
                    selectedPoint.translate(dx, dy);
                    dragStart = e.getPoint();
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMove(e.getPoint());
            }
        });

        animationTimer = new javax.swing.Timer(16, e -> {
            animationProgress += 0.016;
            if (animationProgress >= 1.0) {
                animationProgress = 1.0;
                animationTimer.stop();
            }
            repaint();
        });
    }

    public void setCollection(Map<Integer, LabWork> collection) {
        System.out.println("Setting new collection with " + collection.size() + " elements");
        this.collection = collection;

        System.out.println("Current userColorMap size: " + userColorMap.size());
        updateBounds();
        updatePoints();
        repaint();
    }

    private void updatePoints() {
        pointsMap.clear();
        for (Map.Entry<Integer, LabWork> entry : collection.entrySet()) {
            LabWork labWork = entry.getValue();
            Coordinates coords = labWork.getCoordinates();
            int x = getXFromCoordinate(coords.getX());
            int y = getYFromCoordinate(coords.getY());
            pointsMap.put(entry.getKey(), new Point(x, y));
        }
    }

    private Point findPointAt(Point click) {
        for (Point point : points) {
            if (point.distance(click) <= POINT_RADIUS) {
                return point;
            }
        }
        return null;
    }

    private void updateLabWorkCoordinates(Point point) {
        int index = points.indexOf(point);
        if (index >= 0) {
            List<LabWork> works = new ArrayList<>(collection.values());
            if (index < works.size()) {
                LabWork work = works.get(index);
                if (work.getOwnerId().equals(currentUserId)) {
                    work.setCoordinates(new Coordinates(point.x, (long)point.y));
                    repaint();
                } else {
                    JOptionPane.showMessageDialog(this, "Вы не можете изменять координаты чужого объекта");
                    updatePoints();
                    repaint();
                }
            }
        }
    }

    private void showLabWorkInfo(LabWork work) {
        StringBuilder info = new StringBuilder();
        info.append("ID: ").append(work.getID()).append("\n");
        info.append("Название: ").append(work.getName()).append("\n");
        info.append("Координаты: ").append(work.getCoordinates()).append("\n");
        info.append("Дата создания: ").append(work.getCreationDate()).append("\n");
        info.append("Минимальный балл: ").append(work.getMinimalPoint()).append("\n");
        info.append("Личные качества: ").append(work.getPersonalQualitiesMinimum()).append("\n");
        info.append("Сложность: ").append(work.getDifficulty()).append("\n");
        info.append("Дисциплина: ").append(work.getDiscipline());
        info.append("\nВладелец: ").append(work.getOwnerId());

        JOptionPane.showMessageDialog(this, info.toString(), "Информация об объекте", JOptionPane.INFORMATION_MESSAGE);
    }

    private Color getUserColor(Integer ownerId) {
        if (ownerId == null) {
            System.out.println("Warning: ownerId is null, using gray color");
            return Color.GRAY;
        }
        System.out.println("Getting color for ownerId: " + ownerId);
        Color color = userColorMap.computeIfAbsent(ownerId, k -> {
            int index = Math.abs(ownerId.hashCode()) % USER_COLORS.length;
            System.out.println("Assigning new color at index " + index + " for ownerId: " + ownerId);
            return USER_COLORS[index];
        });
        System.out.println("Returned color: " + color + " for ownerId: " + ownerId);
        return color;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawCoordinateAxes(g2d);

        if (collection != null) {
            System.out.println("Painting collection with " + collection.size() + " elements");
            for (Map.Entry<Integer, LabWork> entry : collection.entrySet()) {
                LabWork labWork = entry.getValue();
                System.out.println("Painting element with ID: " + labWork.getID() + ", ownerId: " + labWork.getOwnerId());
                drawPoint(g2d, entry.getKey(), labWork);
            }
        }

        if (selectedPoint != null) {
            g2d.setColor(new Color(255, 255, 0, 100));
            g2d.fillOval(selectedPoint.x - POINT_RADIUS - 5, selectedPoint.y - POINT_RADIUS - 5,
                        POINT_RADIUS * 2 + 10, POINT_RADIUS * 2 + 10);
        }
    }

    private void drawCoordinateAxes(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();

        Stroke originalStroke = g2d.getStroke();
        Color originalColor = g2d.getColor();
        

        g2d.setColor(AXIS_COLOR);
        g2d.setStroke(new BasicStroke(1.0f));

        g2d.drawLine(width / 2, AXIS_PADDING, width / 2, height - AXIS_PADDING);
        g2d.drawLine(AXIS_PADDING, height / 2, width - AXIS_PADDING, height / 2);

        g2d.setStroke(originalStroke);
        g2d.setColor(originalColor);
    }

    private void updateBounds() {
        if (collection == null || collection.isEmpty()) {
            minX = -1; maxX = 1; minY = -1; maxY = 1;
            return;
        }
        minX = Double.POSITIVE_INFINITY;
        maxX = Double.NEGATIVE_INFINITY;
        minY = Double.POSITIVE_INFINITY;
        maxY = Double.NEGATIVE_INFINITY;
        for (LabWork labWork : collection.values()) {
            Coordinates coords = labWork.getCoordinates();
            if (coords.getX() < minX) minX = coords.getX();
            if (coords.getX() > maxX) maxX = coords.getX();
            if (coords.getY() < minY) minY = coords.getY();
            if (coords.getY() > maxY) maxY = coords.getY();
        }

        double dx = (maxX - minX) * 0.1 + 1e-6;
        double dy = (maxY - minY) * 0.1 + 1e-6;
        minX -= dx; maxX += dx; minY -= dy; maxY += dy;
    }

    private int getXFromCoordinate(double x) {
        int width = getWidth();
        if (maxX == minX) return width / 2;
        return (int) (AXIS_PADDING + (x - minX) / (maxX - minX) * (width - 2 * AXIS_PADDING));
    }

    private int getYFromCoordinate(double y) {
        int height = getHeight();
        if (maxY == minY) return height / 2;

        return (int) (height - AXIS_PADDING - (y - minY) / (maxY - minY) * (height - 2 * AXIS_PADDING));
    }

    private boolean isPointClicked(int clickX, int clickY, int pointX, int pointY) {
        return Math.sqrt(Math.pow(clickX - pointX, 2) + Math.pow(clickY - pointY, 2)) <= POINT_RADIUS;
    }

    private void drawPoint(Graphics2D g2d, int id, LabWork labWork) {
        Coordinates coords = labWork.getCoordinates();
        int x = getXFromCoordinate(coords.getX());
        int y = getYFromCoordinate(coords.getY());

        pointsMap.put(id, new Point(x, y));

        Integer ownerId = labWork.getOwnerId();
        System.out.println("Drawing point for ID: " + id + ", ownerId: " + ownerId);
        Color pointColor = getUserColor(ownerId);
        System.out.println("Assigned color: " + pointColor + " for ownerId: " + ownerId);

        if (id == hoveredPointId) {
            g2d.setColor(HOVER_COLOR);
        } else {
            g2d.setColor(pointColor);
        }
        
        g2d.fillOval(x - POINT_RADIUS, y - POINT_RADIUS, POINT_RADIUS * 2, POINT_RADIUS * 2);
    }

    private void handleMouseClick(Point click) {

        for (Map.Entry<Integer, LabWork> entry : collection.entrySet()) {
            LabWork labWork = entry.getValue();
            Coordinates coords = labWork.getCoordinates();
            int x = getXFromCoordinate(coords.getX());
            int y = getYFromCoordinate(coords.getY());
            
            if (isPointClicked(click.x, click.y, x, y)) {
                showLabWorkInfo(labWork);
                break;
            }
        }
    }

    private void handleMouseMove(Point point) {
        int oldHoveredId = hoveredPointId;
        hoveredPointId = -1;
        
        for (Map.Entry<Integer, Point> entry : pointsMap.entrySet()) {
            if (isPointClicked(point.x, point.y, entry.getValue().x, entry.getValue().y)) {
                hoveredPointId = entry.getKey();
                break;
            }
        }
        
        if (oldHoveredId != hoveredPointId) {
            repaint();
        }
    }
} 