package GUI;

import javax.swing.*;
import java.awt.*;
import System.PropertySystem;
import Objects.Property;
import Objects.Reservation;
import java.util.ArrayList;

/**
 * GUI dialog for booking management operations.
 * Provides options to simulate a booking, view reservations, or remove a reservation.
 * Serves as the main navigation hub for all booking-related functionality.
 */
public class BookingMenuGUI extends JDialog {

    private PropertySystem system;

    /**
     * Constructs the BookingMenuGUI dialog with options for reservation management.
     * Creates a modal dialog with buttons for booking operations.
     *
     * @param parent the parent frame that launched this dialog
     * @param system the PropertySystem containing property and reservation data
     */
    public BookingMenuGUI(JFrame parent, PropertySystem system) {
        super(parent, "Booking Menu", true);
        this.system = system;

        setSize(600, 450);
        setLocationRelativeTo(parent);

        // Main panel with background color
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));

        // Header label at the top
        JLabel headerLabel = new JLabel("Booking Management", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Panel for menu buttons
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        buttonPanel.setBackground(new Color(240, 248, 255));

        // Simulate Booking button opens a dialog to create a new reservation for a property
        JButton simulateBtn = createMenuButton("Simulate Booking");
        simulateBtn.addActionListener(e -> simulateBooking());

        // View Reservations button opens a dialog listing reservations for a property
        JButton viewBtn = createMenuButton("View Reservations");
        viewBtn.addActionListener(e -> viewReservations());

        // Remove Reservation button allows deleting a selected reservation
        JButton removeBtn = createMenuButton("Remove Reservation");
        removeBtn.addActionListener(e -> removeReservation());

        // Back button closes this dialog and returns to main menu
        JButton backBtn = createMenuButton("Back");
        backBtn.addActionListener(e -> dispose());

        // Add all buttons to the button panel
        buttonPanel.add(simulateBtn);
        buttonPanel.add(viewBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(backBtn);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    /**
     * Creates a menu button with standard styling for the booking menu.
     * All buttons share consistent appearance with green background.
     *
     * @param text the text label of the button
     * @return a JButton styled for this menu
     */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(60, 179, 113));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Initiates the process of simulating a new booking (reservation).
     * Prompts the user to choose a property and opens SimulateBookingDialog.
     * If no property is selected, the operation is cancelled.
     */
    private void simulateBooking() {
        // Prompt user to select a property for booking
        int propertyIndex = selectProperty("Simulate Booking");

        // Only proceed if a valid property was selected
        if (propertyIndex < 0) {
            return;
        }

        // Launch the simulation dialog for the chosen property
        SimulateBookingDialog dialog = new SimulateBookingDialog(this, system, propertyIndex);
        dialog.setVisible(true);
    }

    /**
     * Displays all reservations for a selected property in a dialog.
     * If the property has no reservations, an information message is shown.
     * Prompts user to select a property first.
     */
    private void viewReservations() {
        // Prompt user to select a property
        int propertyIndex = selectProperty("View Reservations");

        // Only proceed if a valid property was selected
        if (propertyIndex < 0) {
            return;
        }

        // Get all reservations for the selected property
        ArrayList<Reservation> reservations = system.getReservationsForProperty(propertyIndex);

        if (reservations.isEmpty()) {
            // Show message if no reservations exist
            JOptionPane.showMessageDialog(this,
                    "No reservations for this property.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Open the dialog to view reservations of the selected property
        ViewReservationsDialog dialog = new ViewReservationsDialog(this, system, propertyIndex);
        dialog.setVisible(true);
    }

    /**
     * Removes a reservation from a selected property.
     * Prompts user to choose a property, then select a reservation to remove.
     * Requires confirmation before deletion as this action cannot be undone.
     */
    private void removeReservation() {
        // Prompt user to select a property
        int propertyIndex = selectProperty("Remove Reservation");

        // Only proceed if a valid property was selected
        if (propertyIndex < 0) {
            return;
        }

        // Get all reservations for the selected property
        ArrayList<Reservation> reservations = system.getReservationsForProperty(propertyIndex);

        if (reservations.isEmpty()) {
            // Show message if no reservations exist to remove
            JOptionPane.showMessageDialog(this,
                    "No reservations to remove.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Build a list of reservation descriptions for selection
        String[] resOptions = new String[reservations.size()];
        for (int i = 0; i < reservations.size(); i++) {
            Reservation r = reservations.get(i);
            // Format: "1) Guest Name | Day X to Y"
            resOptions[i] = String.format("%d) %s | Day %d to %d",
                    i + 1, r.getGuestName(), r.getCheckIn(), r.getCheckOut());
        }

        // Prompt the user to select which reservation to remove
        String selected = (String) JOptionPane.showInputDialog(this,
                "Select reservation to remove:",
                "Remove Reservation",
                JOptionPane.QUESTION_MESSAGE,
                null,
                resOptions,
                resOptions[0]);

        if (selected != null) {
            // Parse the selected string to get the reservation index
            int resIndex = Integer.parseInt(selected.substring(0, selected.indexOf(")"))) - 1;

            // Confirm removal of the selected reservation
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to remove this reservation?",
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Attempt to remove the reservation
                boolean ok = system.removeReservation(propertyIndex, resIndex);

                if (ok) {
                    JOptionPane.showMessageDialog(this, "Reservation removed successfully!");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Unable to remove reservation.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Displays a dialog to select a property from the system.
     * Shows a dropdown list of all properties with names and types.
     *
     * @param title the title to display on the selection dialog
     * @return the index of the selected property, or -1 if cancelled or none available
     */
    private int selectProperty(String title) {
        int count = system.getPropertyCount();

        // If there are no properties, inform the user and abort selection
        if (count == 0) {
            JOptionPane.showMessageDialog(this, "No properties found.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return -1;
        }

        // Prepare an array of property options with name and type
        String[] propertyNames = new String[count];
        for (int i = 0; i < count; i++) {
            Property p = system.getProperty(i);
            // Format: "1) Property Name (Property Type)"
            propertyNames[i] = (i + 1) + ") " + p.getName() + " (" + p.getType().getDisplayName() + ")";
        }

        // Show a dialog prompting the user to select one of the properties
        String selected = (String) JOptionPane.showInputDialog(this,
                "Select a property:",
                title,
                JOptionPane.QUESTION_MESSAGE,
                null,
                propertyNames,
                propertyNames[0]);

        if (selected == null) {
            return -1;
        }

        // Extract the index number from the selected option string
        String indexStr = selected.substring(0, selected.indexOf(")"));
        return Integer.parseInt(indexStr) - 1;
    }
}