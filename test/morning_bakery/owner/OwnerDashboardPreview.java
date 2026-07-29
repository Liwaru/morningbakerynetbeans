package morning_bakery.owner;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

/** Membuat preview off-screen untuk pemeriksaan visual manual. */
public final class OwnerDashboardPreview {

    private OwnerDashboardPreview() {
    }

    public static void main(String[] args) throws Exception {
        OwnerDashboardPanel[] holder = new OwnerDashboardPanel[1];
        SwingUtilities.invokeAndWait(() -> {
            holder[0] = new OwnerDashboardPanel();
            holder[0].setSize(1106, 768);
            layoutTree(holder[0]);
        });
        Thread.sleep(1200);
        SwingUtilities.invokeAndWait(() -> {
            OwnerDashboardPanel panel = holder[0];
            layoutTree(panel);
            BufferedImage image = new BufferedImage(
                    panel.getWidth(),
                    panel.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();
            panel.printAll(graphics);
            graphics.dispose();
            try {
                ImageIO.write(
                        image,
                        "png",
                        new File("build/owner-dashboard-preview.png"));
            } catch (Exception exception) {
                throw new IllegalStateException(exception);
            }
        });
    }

    private static void layoutTree(Container container) {
        container.doLayout();
        for (Component component : container.getComponents()) {
            if (component instanceof Container child) {
                layoutTree(child);
            }
        }
    }
}
