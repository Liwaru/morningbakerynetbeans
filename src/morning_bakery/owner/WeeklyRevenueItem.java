package morning_bakery.owner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

/** Satu baris pendapatan harian dengan progress bar tipis. */
public final class WeeklyRevenueItem extends JPanel {

    private static final Color WHITE = new Color(255, 253, 249);
    private static final Color TRACK = new Color(255, 255, 255, 45);
    private static final Color FILL = new Color(255, 242, 222);

    private final JLabel amountLabel = new JLabel("Rp0", SwingConstants.RIGHT);
    private final RoundedProgressBar progressBar = new RoundedProgressBar();

    public WeeklyRevenueItem(String dayName) {
        super(new GridBagLayout());
        setOpaque(false);

        JLabel dayLabel = new JLabel(dayName);
        dayLabel.setForeground(WHITE);
        dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        amountLabel.setForeground(WHITE);
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(0);
        progressBar.setPreferredSize(new Dimension(0, 9));
        progressBar.setMinimumSize(new Dimension(30, 9));

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 6, 10);
        add(dayLabel, c);

        c.gridx = 1;
        c.weightx = 0;
        c.insets = new Insets(0, 0, 6, 0);
        add(amountLabel, c);

        c.gridy = 1;
        c.gridx = 0;
        c.gridwidth = 2;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 0, 0);
        add(progressBar, c);
    }

    public void setRevenue(String formattedAmount, int percentage) {
        amountLabel.setText(formattedAmount);
        progressBar.setValue(Math.max(0, Math.min(100, percentage)));
    }

    private static final class RoundedProgressBar extends JProgressBar {

        private RoundedProgressBar() {
            setOpaque(false);
            setBorderPainted(false);
            setStringPainted(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int arc = Math.max(6, getHeight());
            g.setColor(TRACK);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            int range = getMaximum() - getMinimum();
            int fillWidth = range == 0
                    ? 0
                    : (int) Math.round(
                            getWidth() * (getValue() - getMinimum()) / (double) range);
            if (fillWidth > 0) {
                g.setColor(FILL);
                g.fillRoundRect(0, 0, fillWidth, getHeight(), arc, arc);
            }
            g.dispose();
        }
    }
}
