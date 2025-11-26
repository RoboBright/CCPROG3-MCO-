package GUI;

import javax.swing.*;
import java.awt.*;
import System.PropertySystem;
import Objects.Property;
import Objects.Reservation;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Dialog for viewing all reservations for a specific property.
 * Displays each reservation with guest details, dates, pricing, and nightly breakdown.
 * Provides a scrollable list when multiple reservations exist.
 */
public class ViewReservationsDialog extends JDialog {

    private PropertySystem system;
    private int propertyIndex;
    private Property property;

    /**
     * Constructs a ViewReservationsDialog for displaying all property bookings.
     * Creates a scrollable list of reservation panels with detailed information.
     *
     * @param parent the parent dialog that launched this view
     * @param system the PropertySystem containing reservation data
     * @param propertyIndex the index of the property whose reservations to display
     */
    public ViewReservationsDialog(JDialog parent, PropertySystem system, int propertyIndex) {
        super(parent, "View Reservations", true);
        this.system = system;
        this.propertyIndex = propertyIndex;
        this.property = system.getProperty(propertyIndex);

        setSize(700, 600);
        setLocationRelativeTo(parent);

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header showing property name
        JLabel headerLabel = new JLabel("Reservations - " + property.getName(),
                SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Get all reservations for this property
        ArrayList<Reservation> reservations = system.getReservationsForProperty(propertyIndex);

        // Create vertical list panel to hold reservation panels
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        // Add each reservation as a separate panel with spacing
        for (int i = 0; i < reservations.size(); i++) {
            Reservation r = reservations.get(i);
            JPanel resPanel = createReservationPanel(r, i + 1);
            listPanel.add(resPanel);
            listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // Wrap list in scrollable pane for many reservations
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Close button at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(closeBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Creates a panel displaying detailed information for a single reservation.
     * Includes guest name, dates, total price, and per-night breakdown.
     *
     * @param r the Reservation object to display
     * @param number the display number for this reservation (1-based index)
     * @return a JPanel containing formatted reservation information
     */
    private JPanel createReservationPanel(Reservation r, int number) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setBackground(new Color(245, 250, 255));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Left side - main info displayed vertically
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(245, 250, 255));

        // Reservation number header
        JLabel numberLabel = new JLabel("Reservation #" + number);
        numberLabel.setFont(new Font("Arial", Font.BOLD, 16));
        numberLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(numberLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Guest name
        JLabel guestLabel = new JLabel("Guest: " + r.getGuestName());
        guestLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        guestLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(guestLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 3)));

        // Stay dates with number of nights
        JLabel datesLabel = new JLabel(String.format("Stay: Day %d to Day %d (%d nights)",
                r.getCheckIn(), r.getCheckOut(), r.getCheckOut() - r.getCheckIn()));
        datesLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        datesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(datesLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Total price prominently displayed
        double total = r.getTotalPrice(property);
        JLabel totalLabel = new JLabel(String.format(Locale.US, "Total: PHP %.2f", total));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(34, 139, 34));
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(totalLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Breakdown section header
        JLabel breakdownTitle = new JLabel("Nightly Breakdown:");
        breakdownTitle.setFont(new Font("Arial", Font.BOLD, 12));
        breakdownTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(breakdownTitle);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 3)));

        // List each night's price with corresponding day
        double[] breakdown = r.getBreakdown(property);
        for (int i = 0; i < breakdown.length; i++) {
            JLabel nightLabel = new JLabel(String.format(Locale.US,
                    "  • Night %d (Day %d): PHP %.2f",
                    i + 1, r.getCheckIn() + i, breakdown[i]));
            nightLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            nightLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(nightLabel);
        }

        panel.add(infoPanel, BorderLayout.CENTER);

        return panel;
    }
}