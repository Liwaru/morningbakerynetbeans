package morning_bakery.sales;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/** Render off-screen untuk pemeriksaan layout Laporan Penjualan. */
public final class SalesReportPreview {

    private SalesReportPreview() {
    }

    public static void main(String[] args) throws Exception {
        LaporanPenjualanPanel[] holder = new LaporanPenjualanPanel[1];
        SwingUtilities.invokeAndWait(() -> {
            holder[0] = new LaporanPenjualanPanel();
            holder[0].setSize(1106, 768);
            layoutTree(holder[0]);
        });
        Thread.sleep(1500);
        SwingUtilities.invokeAndWait(() -> {
            LaporanPenjualanPanel panel = holder[0];
            layoutTree(panel);
            render(panel, "build/sales-report-preview.png");
            JScrollPane pageScroll = findScrollPane(panel);
            if (pageScroll != null) {
                pageScroll.getVerticalScrollBar().setValue(
                        pageScroll.getVerticalScrollBar().getMaximum());
                layoutTree(panel);
                render(panel, "build/sales-report-preview-bottom.png");
            }
        });
    }

    private static void render(Container panel, String target) {
        BufferedImage image = new BufferedImage(
                panel.getWidth(),
                panel.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        panel.printAll(graphics);
        graphics.dispose();
        try {
            ImageIO.write(image, "png", new File(target));
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static JScrollPane findScrollPane(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JScrollPane scrollPane) {
                return scrollPane;
            }
            if (component instanceof Container child) {
                JScrollPane found = findScrollPane(child);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
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
