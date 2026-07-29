package morning_bakery.owner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

/** Card statistik dengan ukuran dan tipografi konsisten. */
public final class StatCard extends RoundedGradientPanel {

    private static final Color WHITE = new Color(255, 253, 249);
    private static final Color MUTED = new Color(245, 218, 195);
    private static final Color CARD_START = new Color(132, 79, 47);
    private static final Color CARD_END = new Color(67, 31, 22);

    private final JLabel valueLabel = new JLabel();

    public StatCard(String title, String initialValue) {
        super(new BorderLayout(0, 5), CARD_START, CARD_END, 12);
        setBorder(BorderFactory.createEmptyBorder(14, 16, 13, 16));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(MUTED);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        valueLabel.setText(initialValue);
        valueLabel.setForeground(WHITE);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 25));

        add(titleLabel, BorderLayout.NORTH);
        add(valueLabel, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}
