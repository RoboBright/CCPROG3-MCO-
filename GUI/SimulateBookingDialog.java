package GUI;

import javax.swing.*;
import java.awt.*;
import System.PropertySystem;
import Objects.Property;
import Objects.Reservation;
import java.util.Locale;

/**
 * Dialog for simulating a new booking with calendar view.
 * Displays an interactive calendar showing available and booked dates
 * alongside the booking form for guest information.
 */
public class SimulateBookingDialog extends JDialog {

    private PropertySystem system;
    private int propertyIndex;
    private Property property;
    private JTextField guestField;
    private JSpinner checkInSpinner;
    private JSpinner checkOutSpinner;
    private BookingCalendarPanel calendarPanel;

    /**
     * Constructs a SimulateBookingDialog for creating a new reservation.
     *
     * @param parent the parent dialog that launched this booking dialog
     * @param system the PropertySystem containing property and reservation data
     * @param propertyIndex the index of the property for which to create a booking
     */
    public SimulateBookingDialog(JDialog parent, PropertySystem system, int propertyIndex) {
        super(parent, "Simulate Booking", true);
        this.system = system;
        this.propertyIndex = propertyIndex;
        this.property = system.getProperty(propertyIndex);

        // Set dialog size larger to accommodate calendar
        setSize(1100, 650);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header label showing property name
        JLabel headerLabel = new JLabel("New Booking - " + property.getName(),
                SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Split pane to show calendar and form side by side
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Left side: Calendar view
        calendarPanel = new BookingCalendarPanel(property);
        JScrollPane calendarScroll = new JScrollPane(calendarPanel);
        calendarScroll.setBorder(BorderFactory.createTitledBorder("Property Availability"));
        splitPane.setLeftComponent(calendarScroll);

        // Right side: Booking form
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setBorder(BorderFactory.createTitledBorder("Booking Information"));

        JPanel formPanel = createFormPanel();
        formContainer.add(formPanel, BorderLayout.CENTER);

        // Button panel at bottom of form
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton bookBtn = new JButton("Book");
        bookBtn.setBackground(new Color(34, 139, 34));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.addActionListener(e -> attemptBooking());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(bookBtn);
        buttonPanel.add(cancelBtn);
        formContainer.add(buttonPanel, BorderLayout.SOUTH);

        splitPane.setRightComponent(formContainer);

        // Set divider location to give calendar more space
        splitPane.setDividerLocation(650);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    /**
     * Creates the form panel containing input fields for booking details.
     *
     * @return a JPanel with guest name, check-in, and check-out fields
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Guest name label and text field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Guest Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        guestField = new JTextField(20);
        formPanel.add(guestField, gbc);

        // Check-in day label and spinner
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Check-in Day (1-29):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        SpinnerNumberModel checkInModel = new SpinnerNumberModel(1, 1, 29, 1);
        checkInSpinner = new JSpinner(checkInModel);
        // Add listener to highlight selected dates on calendar
        checkInSpinner.addChangeListener(e -> calendarPanel.setSelectedRange(
                (Integer) checkInSpinner.getValue(),
                (Integer) checkOutSpinner.getValue()));
        formPanel.add(checkInSpinner, gbc);

        // Check-out day label and spinner
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Check-out Day (2-30):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        SpinnerNumberModel checkOutModel = new SpinnerNumberModel(2, 2, 30, 1);
        checkOutSpinner = new JSpinner(checkOutModel);
        // Add listener to highlight selected dates on calendar
        checkOutSpinner.addChangeListener(e -> calendarPanel.setSelectedRange(
                (Integer) checkInSpinner.getValue(),
                (Integer) checkOutSpinner.getValue()));
        formPanel.add(checkOutSpinner, gbc);

        // Info label with booking instructions
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("<html><i>Note: Check-out must be after check-in<br>" +
                "Only available dates (green/white/yellow) can be booked.</i></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        formPanel.add(infoLabel, gbc);

        // Add empty space to push content to top
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        formPanel.add(new JLabel(), gbc);

        return formPanel;
    }

    /**
     * Attempts to create a booking with the provided information.
     * Validates input, checks date availability, creates the reservation,
     * and displays a confirmation message with price breakdown.
     */
    private void attemptBooking() {
        // Get and validate guest name
        String guestName = guestField.getText().trim();

        if (guestName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a guest name.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get check-in and check-out values from spinners
        int checkIn = (Integer) checkInSpinner.getValue();
        int checkOut = (Integer) checkOutSpinner.getValue();

        // Validate that check-out is after check-in
        if (checkOut <= checkIn) {
            JOptionPane.showMessageDialog(this,
                    "Check-out day must be after check-in day.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if all dates in the range are available
        boolean available = system.areDatesAvailable(propertyIndex, checkIn, checkOut);

        if (!available) {
            JOptionPane.showMessageDialog(this,
                    "Some dates in this range are not available.",
                    "Unavailable", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Attempt to create the reservation
        Reservation res = system.addReservation(propertyIndex, guestName, checkIn, checkOut);

        if (res == null) {
            JOptionPane.showMessageDialog(this,
                    "Unable to create reservation.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calculate total price and nightly breakdown
        double total = res.getTotalPrice(property);
        double[] breakdown = res.getBreakdown(property);

        // Build confirmation message with all booking details
        StringBuilder message = new StringBuilder();
        message.append("Reservation successful!\n\n");
        message.append(String.format("Guest: %s\n", guestName));
        message.append(String.format("Check-in: Day %d\n", checkIn));
        message.append(String.format("Check-out: Day %d\n\n", checkOut));
        message.append(String.format(Locale.US, "Total Price: PHP %.2f\n\n", total));
        message.append("Breakdown:\n");

        // Add each night's price to the message
        for (int i = 0; i < breakdown.length; i++) {
            message.append(String.format(Locale.US, "  Night %d: PHP %.2f\n", i + 1, breakdown[i]));
        }

        // Show success dialog with all details
        JOptionPane.showMessageDialog(this,
                message.toString(),
                "Booking Confirmed",
                JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }
}