package GUI;

import javax.swing.*;
import java.awt.*;
import System.PropertySystem;
import Objects.Property;
import Objects.Reservation;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Dialog for viewing comprehensive property information.
 * Displays property details through multiple tabs including calendar view,
 * high-level statistics, and reservation listings.
 */
public class ViewPropertyDialog extends JDialog {

    private PropertySystem system;
    private int propertyIndex;
    private Property property;
    private CalendarGridPanel calendarPanel;

    /**
     * Constructs a ViewPropertyDialog for displaying property information.
     * Creates a tabbed interface with calendar, info, and reservation views.
     *
     * @param parent the parent dialog that launched this view
     * @param system the PropertySystem containing property data
     * @param propertyIndex the index of the property to display
     */
    public ViewPropertyDialog(JDialog parent, PropertySystem system, int propertyIndex) {
        super(parent, "View Property", true);
        this.system = system;
        this.propertyIndex = propertyIndex;
        this.property = system.getProperty(propertyIndex);

        setSize(1000, 700);
        setLocationRelativeTo(parent);

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header showing property name
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Viewing: " + property.getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed pane for different views of property information
        JTabbedPane tabbedPane = new JTabbedPane();

        // Calendar view tab - interactive grid of all dates
        calendarPanel = new CalendarGridPanel(property);
        JScrollPane calendarScroll = new JScrollPane(calendarPanel);
        tabbedPane.addTab("Calendar Grid", calendarScroll);

        // High-level info tab - summary statistics
        JPanel infoPanel = createHighLevelInfoPanel();
        tabbedPane.addTab("High-Level Info", infoPanel);

        // Reservations tab - list of all bookings
        JPanel reservationsPanel = createReservationsPanel();
        JScrollPane reservationsScroll = new JScrollPane(reservationsPanel);
        tabbedPane.addTab("Reservations", reservationsScroll);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Close button at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(closeBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Creates the high-level information panel displaying property summary.
     * Shows name, type, available dates count, and estimated earnings.
     *
     * @return a JPanel containing formatted property information
     */
    private JPanel createHighLevelInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Define fonts for labels and values
        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font valueFont = new Font("Arial", Font.PLAIN, 16);

        // Property Name row
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Property Name:");
        nameLabel.setFont(labelFont);
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JLabel nameValue = new JLabel(property.getName());
        nameValue.setFont(valueFont);
        panel.add(nameValue, gbc);

        // Property Type row
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel typeLabel = new JLabel("Property Type:");
        typeLabel.setFont(labelFont);
        panel.add(typeLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JLabel typeValue = new JLabel(property.getType().getDisplayName());
        typeValue.setFont(valueFont);
        panel.add(typeValue, gbc);

        // Available Dates count row
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel datesLabel = new JLabel("Available Dates:");
        datesLabel.setFont(labelFont);
        panel.add(datesLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JLabel datesValue = new JLabel(String.valueOf(property.getAvailableDates().length));
        datesValue.setFont(valueFont);
        panel.add(datesValue, gbc);

        // Estimated Earnings row
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        JLabel earningsLabel = new JLabel("Estimated Earnings:");
        earningsLabel.setFont(labelFont);
        panel.add(earningsLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JLabel earningsValue = new JLabel(String.format(Locale.US, "PHP %.2f",
                property.getEstimatedEarnings()));
        earningsValue.setFont(new Font("Arial", Font.BOLD, 18));
        earningsValue.setForeground(new Color(34, 139, 34));
        panel.add(earningsValue, gbc);

        // Add empty space to push content to top
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        panel.add(new JLabel(), gbc);

        return panel;
    }

    /**
     * Creates the reservations panel displaying all bookings for this property.
     * If no reservations exist, displays an informational message.
     *
     * @return a JPanel containing the list of reservations or a "no reservations" message
     */
    private JPanel createReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Get all reservations for this property
        ArrayList<Reservation> reservations = system.getReservationsForProperty(propertyIndex);

        if (reservations.isEmpty()) {
            // Show message when no reservations exist
            JLabel noReservationsLabel = new JLabel("No reservations for this property.",
                    SwingConstants.CENTER);
            noReservationsLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            panel.add(noReservationsLabel, BorderLayout.CENTER);
        } else {
            // Create a vertical list of reservation panels
            JPanel listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

            // Add each reservation as a separate panel
            for (int i = 0; i < reservations.size(); i++) {
                Reservation r = reservations.get(i);
                JPanel resPanel = createReservationPanel(r, i + 1);
                listPanel.add(resPanel);
                listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            panel.add(listPanel, BorderLayout.NORTH);
        }

        return panel;
    }

    /**
     * Creates a panel displaying details for a single reservation.
     * Shows guest name, dates, total price, and nightly breakdown.
     *
     * @param r the Reservation object to display
     * @param number the display number for this reservation (1-based)
     * @return a JPanel containing formatted reservation information
     */
    private JPanel createReservationPanel(Reservation r, int number) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(new Color(245, 245, 245));

        // Create vertical layout for reservation details
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBackground(new Color(245, 245, 245));

        // Calculate total price for this reservation
        double total = r.getTotalPrice(property);

        // Reservation number header
        JLabel numberLabel = new JLabel("Reservation #" + number);
        numberLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(numberLabel);

        // Guest name
        infoPanel.add(new JLabel("Guest: " + r.getGuestName()));

        // Check-in and check-out dates
        infoPanel.add(new JLabel(String.format("Check-in: Day %d | Check-out: Day %d",
                r.getCheckIn(), r.getCheckOut())));

        // Total price
        infoPanel.add(new JLabel(String.format(Locale.US, "Total Price: PHP %.2f", total)));

        // Breakdown of nightly prices
        double[] breakdown = r.getBreakdown(property);
        StringBuilder breakdownText = new StringBuilder("Breakdown: ");
        for (int i = 0; i < breakdown.length; i++) {
            if (i > 0) breakdownText.append(", ");
            breakdownText.append(String.format(Locale.US, "Night %d: PHP %.2f", i + 1, breakdown[i]));
        }
        JLabel breakdownLabel = new JLabel(breakdownText.toString());
        breakdownLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        infoPanel.add(breakdownLabel);

        panel.add(infoPanel, BorderLayout.CENTER);

        return panel;
    }
}