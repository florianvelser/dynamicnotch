package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class DynamicNotch extends JPanel {

    private static final float ANIMATION_DURATION = 500.0f;
    private static final double EASING_MAX_VALUE = 1.0625;
    private static final int TIMER_DELAY = 5;

    private final GeneralPath startPath;
    private final GeneralPath endPath;
    private final GeneralPath currentPath;
    private Timer animationTimer;
    private float progress = 0.0f;

    public DynamicNotch() {
        // Define the start SVG path commands
        startPath = new GeneralPath();
        startPath.moveTo(0.786133, 0);
        startPath.lineTo(13.3834, 0);
        startPath.curveTo(15.992, 0, 18.1068, 2.11476, 18.1068, 4.72344);
        startPath.lineTo(18.1068, 22.0427);
        startPath.curveTo(18.1068, 27.2601, 22.3363, 31.4896, 27.5537, 31.4896);
        startPath.lineTo(196.023, 31.4896);
        startPath.curveTo(200.371, 31.4896, 203.895, 27.965, 203.895, 23.6172);
        startPath.lineTo(203.895, 4.72344);
        startPath.curveTo(203.895, 2.11476, 206.01, 0, 208.619, 0);
        startPath.lineTo(221.215, 0);
        startPath.lineTo(0.786133, 0);
        startPath.closePath();

        // Define the end SVG path commands
        endPath = new GeneralPath();
        endPath.moveTo(2.068, 0);
        endPath.lineTo(35.285, 0);
        endPath.curveTo(42.137, 0, 47.699, 6.562, 47.699, 13.413);
        endPath.lineTo(47.699, 57.267);
        endPath.curveTo(47.699, 72.819, 58.872, 84.942, 74.424, 84.942);
        endPath.lineTo(528.385, 84.942);
        endPath.curveTo(540.083, 84.942, 549.364, 75.661, 549.364, 63.963);
        endPath.lineTo(549.364, 13.413);
        endPath.curveTo(549.364, 6.562, 554.926, 0, 561.778, 0);
        endPath.lineTo(583.932, 0);
        endPath.lineTo(2.068, 0);
        endPath.closePath();

        currentPath = new GeneralPath(startPath);

        // Make the JPanel transparent
        setOpaque(false);
        setPreferredSize(new Dimension(
                (int) Math.ceil(584 * EASING_MAX_VALUE), (int) Math.ceil(85 * EASING_MAX_VALUE)));

        // Mouse listener to start and stop animation
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                startAnimation(startPath, endPath);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                startAnimation(endPath, startPath);
            }
        });
    }

    private void startAnimation(GeneralPath fromPath, GeneralPath toPath) {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        progress = 0.0f;
        long startTime = System.currentTimeMillis();

        animationTimer = new Timer(TIMER_DELAY, e -> {
            long elapsedTime = System.currentTimeMillis() - startTime;
            progress = Math.min(1.0f, elapsedTime / ANIMATION_DURATION);

            // Apply Ease-Out-Back easing function
            float easeOutBack = 1 - (float) Math.pow(1 - progress, 3) * (1 - 3 * progress);
            interpolatePath(fromPath, toPath, easeOutBack);

            repaint();

            if (progress >= 1.0f) {
                animationTimer.stop();
            }
        });

        animationTimer.start();
    }

    private void interpolatePath(GeneralPath fromPath, GeneralPath toPath, float progress) {
        GeneralPath interpolatedPath = new GeneralPath();

        PathIterator fromIterator = fromPath.getPathIterator(null);
        PathIterator toIterator = toPath.getPathIterator(null);

        float[] fromCoords = new float[6];
        float[] toCoords = new float[6];

        boolean started = false;

        while (!fromIterator.isDone() && !toIterator.isDone()) {
            int type = fromIterator.currentSegment(fromCoords);
            toIterator.currentSegment(toCoords);

            switch (type) {
                case PathIterator.SEG_MOVETO:
                    interpolatedPath.moveTo(
                            lerp(fromCoords[0], toCoords[0], progress),
                            lerp(fromCoords[1], toCoords[1], progress));
                    started = true;
                    break;
                case PathIterator.SEG_LINETO:
                    if (started) {
                        interpolatedPath.lineTo(
                                lerp(fromCoords[0], toCoords[0], progress),
                                lerp(fromCoords[1], toCoords[1], progress));
                    }
                    break;
                case PathIterator.SEG_CUBICTO:
                    if (started) {
                        interpolatedPath.curveTo(
                                lerp(fromCoords[0], toCoords[0], progress),
                                lerp(fromCoords[1], toCoords[1], progress),
                                lerp(fromCoords[2], toCoords[2], progress),
                                lerp(fromCoords[3], toCoords[3], progress),
                                lerp(fromCoords[4], toCoords[4], progress),
                                lerp(fromCoords[5], toCoords[5], progress));
                    }
                    break;
                case PathIterator.SEG_CLOSE:
                    interpolatedPath.closePath();
                    started = false; // Reset the started flag for new paths
                    break;
            }

            fromIterator.next();
            toIterator.next();
        }

        currentPath.reset();
        currentPath.append(interpolatedPath, false);
    }

    private float lerp(float start, float end, float t) {
        return start + t * (end - start);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.BLACK);

        // Calculate the bounding box of the current path
        Rectangle2D bounds = currentPath.getBounds2D();
        double width = bounds.getWidth();

        // Calculate the offset for the x Axis to center the path horizontally
        double offsetX = (getWidth() - width) / 2 - bounds.getX();

        // Apply the transformation to center the path
        g2d.translate(offsetX, 0);

        // Draw the current path
        g2d.fill(currentPath);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JDialog dialog = new JDialog((JFrame) null, "Dynamic Notch", false);
            dialog.setType(Window.Type.UTILITY);

            // Create the transparent panel
            DynamicNotch panel = new DynamicNotch();
            dialog.add(panel);

            // Set the frame properties
            dialog.setSize(panel.getPreferredSize());
            dialog.setUndecorated(true);
            dialog.setAlwaysOnTop(true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setBackground(new Color(0, 0, 0, 0)); // Ensure frame background is transparent

            // Center the window on the X-axis of the screen
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension screenSize = toolkit.getScreenSize();
            int x = (screenSize.width - dialog.getWidth()) / 2;
            dialog.setLocation(x, dialog.getY());

            dialog.setVisible(true);
        });
    }
}
