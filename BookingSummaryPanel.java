package view;

import model.BookingSummaryData;
import javax.swing.*;
import java.awt.*;

public class BookingSummaryPanel extends JPanel {

    private JLabel lblMovieTitle, lblShowtime, lblSeats, lblTotalPrice;

    public BookingSummaryPanel() {
        setLayout(new GridLayout(4, 1, 10, 10));
        setBorder(BorderFactory.createTitledBorder("Booking Summary"));
        setBackground(new Color(245, 245, 245));

        lblMovieTitle = new JLabel();
        lblShowtime = new JLabel();
        lblSeats = new JLabel();
        lblTotalPrice = new JLabel();

        Font font = new Font("SansSerif", Font.PLAIN, 16);
        lblMovieTitle.setFont(font);
        lblShowtime.setFont(font);
        lblSeats.setFont(font);
        lblTotalPrice.setFont(font);

        add(lblMovieTitle);
        add(lblShowtime);
        add(lblSeats);
        add(lblTotalPrice);
    }

    public void setBookingSummaryData(BookingSummaryData data) {
        lblMovieTitle.setText("🎬 Movie: " + data.getMovieTitle());
        lblShowtime.setText("🕒 Showtime: " + data.getShowtime());
        lblSeats.setText("💺 Seats: " + String.join(", ", data.getSeatLabels()));
        lblTotalPrice.setText("💰 Total Price: ₹" + data.getTotalPrice());
    }
}
