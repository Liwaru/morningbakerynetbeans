package morning_bakery.sales;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.JPanel;

/** Grafik batang custom tanpa dependency chart tambahan. */
public final class SalesChartPanel extends JPanel {

    private static final Color WHITE = new Color(255, 253, 249);
    private static final Color MUTED = new Color(238, 205, 181);
    private static final Color GRID = new Color(255, 255, 255, 35);
    private static final Color TOTAL = new Color(212, 151, 105);
    private static final Color COMPLETED = new Color(255, 239, 214);
    private List<SalesChartItem> items = List.of();

    public SalesChartPanel() {
        setOpaque(false);
    }

    public void setItems(List<SalesChartItem> items) {
        this.items = items == null ? List.of() : List.copyOf(items);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int max = items.stream()
                .mapToInt(SalesChartItem::totalOrders)
                .max().orElse(0);
        if (max == 0) {
            g.setColor(new Color(255, 255, 255, 28));
            int diameter = Math.min(getHeight() - 36, 150);
            g.fillOval(18, (getHeight() - diameter) / 2, diameter, diameter);
            g.setColor(MUTED);
            g.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g.drawString(
                    "Belum ada data penjualan pada periode ini.",
                    diameter + 40,
                    getHeight() / 2);
            g.dispose();
            return;
        }

        int left = 38;
        int top = 14;
        int right = 18;
        int bottom = 34;
        int chartWidth = Math.max(1, getWidth() - left - right);
        int chartHeight = Math.max(1, getHeight() - top - bottom);

        g.setColor(GRID);
        for (int row = 0; row <= 4; row++) {
            int y = top + row * chartHeight / 4;
            g.drawLine(left, y, left + chartWidth, y);
        }

        int count = Math.max(1, items.size());
        double slot = chartWidth / (double) count;
        int barWidth = Math.max(3, (int) Math.min(22, slot * .62));
        int labelStep = Math.max(1, (int) Math.ceil(count / 12.0));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 10));

        for (int index = 0; index < items.size(); index++) {
            SalesChartItem item = items.get(index);
            int x = left + (int) Math.round(index * slot + (slot - barWidth) / 2);
            int totalHeight = item.totalOrders() * chartHeight / max;
            int totalY = top + chartHeight - totalHeight;
            g.setColor(TOTAL);
            g.fillRoundRect(x, totalY, barWidth, totalHeight, 5, 5);

            int completedHeight = item.completedOrders() * chartHeight / max;
            int completedY = top + chartHeight - completedHeight;
            g.setColor(COMPLETED);
            g.fillRoundRect(
                    x + Math.max(1, barWidth / 4),
                    completedY,
                    Math.max(2, barWidth / 2),
                    completedHeight,
                    4, 4);

            if (index % labelStep == 0 || index == items.size() - 1) {
                g.setColor(WHITE);
                String label = item.label();
                int labelWidth = g.getFontMetrics().stringWidth(label);
                g.drawString(
                        label,
                        x + barWidth / 2 - labelWidth / 2,
                        top + chartHeight + 18);
            }
        }

        g.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g.setColor(TOTAL);
        g.fillOval(left, getHeight() - 10, 8, 8);
        g.setColor(WHITE);
        g.drawString("Total Pesanan", left + 12, getHeight() - 2);
        g.setColor(COMPLETED);
        g.fillOval(left + 104, getHeight() - 10, 8, 8);
        g.setColor(WHITE);
        g.drawString("Selesai", left + 116, getHeight() - 2);
        g.dispose();
    }
}
