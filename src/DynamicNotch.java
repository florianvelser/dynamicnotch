package src;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;

public class DynamicNotch extends JPanel {

    private final GeneralPath path;

    public DynamicNotch() {
        // Define the SVG path commands
        path = new GeneralPath();
        path.moveTo(0.786133, 0);
        path.lineTo(13.3834, 0);
        path.curveTo(15.992, 0, 18.1068, 2.11476, 18.1068, 4.72344);
        path.lineTo(18.1068, 22.0427);
        path.curveTo(18.1068, 27.2601, 22.3363, 31.4896, 27.5537, 31.4896);
        path.lineTo(196.023, 31.4896);
        path.curveTo(200.371, 31.4896, 203.895, 27.965, 203.895, 23.6172);
        path.lineTo(203.895, 4.72344);
        path.curveTo(203.895, 2.11476, 206.01, 0, 208.619, 0);
        path.lineTo(221.215, 0);
        path.lineTo(0.786133, 0);
        path.closePath();

        // Make the JPanel transparent
        setOpaque(false);
        setPreferredSize(new Dimension(222, 32)); // Set preferred size for layout purposes
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Ensure the Graphics2D object uses transparent background
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.BLACK);
        g2d.fill(path);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("SVG Viewer");

        // Create the transparent panel
        DynamicNotch panel = new DynamicNotch();
        frame.add(panel);

        // Set the frame properties
        frame.setSize(panel.getPreferredSize());
        frame.setUndecorated(true);
        frame.setAlwaysOnTop(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(new Color(0, 0, 0, 0)); // Ensure frame background is transparent

        // Center the window on the X-axis of the screen
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        frame.setLocation(x, frame.getY());

        frame.setVisible(true);
    }
}
