package morning_bakery.owner;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/** Panel rounded dan gradient reusable untuk seluruh halaman Owner. */
public class RoundedGradientPanel extends JPanel {

    private final Color startColor;
    private final Color endColor;
    private final int cornerRadius;

    public RoundedGradientPanel(
            LayoutManager layout,
            Color startColor,
            Color endColor,
            int cornerRadius) {
        super(layout);
        this.startColor = startColor;
        this.endColor = endColor;
        this.cornerRadius = cornerRadius;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(new GradientPaint(
                0, 0, startColor, getWidth(), getHeight(), endColor));
        g.fillRoundRect(
                0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        g.setColor(new Color(255, 255, 255, 25));
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(
                0, 0, getWidth() - 1, getHeight() - 1,
                cornerRadius, cornerRadius);
        g.dispose();
        super.paintComponent(graphics);
    }
}
