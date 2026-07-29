package morning_bakery.owner;

import java.awt.Component;
import java.awt.Container;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/** Smoke test sederhana yang dapat dijalankan tanpa framework test tambahan. */
public final class OwnerDashboardDAOSmokeTest {

    private OwnerDashboardDAOSmokeTest() {
    }

    public static void main(String[] args) throws Exception {
        OwnerDashboardData data
                = new OwnerDashboardDAO().loadDashboardData();

        if (data.todayRevenue() == null
                || data.todayAverageTransaction() == null
                || data.todayBestSellingMenu() == null) {
            throw new AssertionError("Nilai ringkasan tidak boleh null.");
        }
        for (DayOfWeek day : DayOfWeek.values()) {
            if (!data.currentWeekRevenue().containsKey(day)) {
                throw new AssertionError(
                        "Pendapatan mingguan tidak memiliki " + day);
            }
        }

        SwingUtilities.invokeAndWait(() -> {
            OwnerDashboardPanel panel = new OwnerDashboardPanel();
            panel.setSize(1100, 720);
            panel.doLayout();
            List<JScrollPane> scrollPanes = new ArrayList<>();
            collectScrollPanes(panel, scrollPanes);
            if (scrollPanes.size() != 1) {
                throw new AssertionError(
                        "Dashboard harus memiliki tepat satu JScrollPane.");
            }
            if (scrollPanes.get(0).getHorizontalScrollBarPolicy()
                    != JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) {
                throw new AssertionError(
                        "Scrollbar horizontal dashboard harus dinonaktifkan.");
            }
        });
        System.out.println("OwnerDashboardDAO OK: " + data);
    }

    private static void collectScrollPanes(
            Container parent, List<JScrollPane> result) {
        for (Component component : parent.getComponents()) {
            if (component instanceof JScrollPane scrollPane) {
                result.add(scrollPane);
            }
            if (component instanceof Container child) {
                collectScrollPanes(child, result);
            }
        }
    }
}
