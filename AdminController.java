package controller;

import model.BookingSummaryData;
import view.BookingSummaryPanel;

public class AdminController {
    private BookingSummaryPanel panel;

    public AdminController(BookingSummaryPanel panel) {
        this.panel = panel;
    }

    public void updateBookingSummary(BookingSummaryData data) {
        panel.setBookingSummaryData(data);
    }
}
