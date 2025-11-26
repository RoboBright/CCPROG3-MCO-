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
 */
public class BookingMenuGUI extends JDialog {

    private PropertySystem system;

    /**
     * Constructs the BookingMenuGUI dialog with options for reservation management.
     *
     * @param parent the parent frame that launched this dialog
     * @param system the PropertySystem containing property and reservation data
     */
    public BookingMenuGUI(JFrame parent, PropertySystem system) {
        super(parent, "Booking Menu", true);
        this.system = system;

        setSize(600, 450);
        setLocationRelativeTo(parent);

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
        // Back button closes this dialog
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
     * Prompts the user to choose a property and opens the SimulateBookingDialog if a valid property is selected.
     */
    private void simulateBooking() {
        int propertyIndex = selectProperty("Simulate Booking");
        if (propertyIndex < 0) {
            return;
        }
        // Launch the simulation dialog for the chosen property
        SimulateBookingDialog dialog = new SimulateBookingDialog(this, system, propertyIndex);
        dialog.setVisible(true);
    }

    /**
     * Displays all reservations for a selected property in a dialog.
     * If the property has no reservations, an information message is shown instead.
     */
    private void viewReservations() {
        int propertyIndex = selectProperty("View Reservations");
        if (propertyIndex < 0) {
            return;
        }

        ArrayList<Reservation> reservations = system.getReservationsForProperty(propertyIndex);
        if (reservations.isEmpty()) {
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
     * Prompts the user to choose a property and then select a reservation to remove, confirming the action.
     */
    private void removeReservation() {
        int propertyIndex = selectProperty("Remove Reservation");
        if (propertyIndex < 0) {
            return;
        }

        ArrayList<Reservation> reservations = system.getReservationsForProperty(propertyIndex);
        if (reservations.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No reservations to remove.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Build a list of reservation descriptions for selection
        String[] resOptions = new String[reservations.size()];
        for (int i = 0; i < reservations.size(); i++) {
            Reservation r = reservations.get(i);
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
     *
     * @param title the title to display on the selection dialog
     * @return the index of the selected property, or -1 if none was selected (or if cancelled)
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
