import model.BookingSummaryData;
import view.BookingSummaryPanel;
import controller.AdminController;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class AdminDashboardDemo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Admin Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 300);
            frame.setLayout(new BorderLayout());

            BookingSummaryPanel summaryPanel = new BookingSummaryPanel();
            AdminController controller = new AdminController(summaryPanel);

            BookingSummaryData data = new BookingSummaryData(
                    "Inception",
                    "2025-10-25 | 7:30 PM",
                    Arrays.asList("A1", "A2", "A3"),
                    450.0
            );

            controller.updateBookingSummary(data);

            frame.add(summaryPanel, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}
