package GUI;

import javax.swing.*;
import java.awt.*;
import System.PropertySystem;
import Objects.Property;
import Objects.Reservation;
import java.util.Locale;

/**
 * Dialog for simulating a booking.
 */
public class SimulateBookingDialog extends JDialog {
    
    private PropertySystem system;
    private int propertyIndex;
    private Property property;
    private JTextField guestField;
    private JSpinner checkInSpinner;
    private JSpinner checkOutSpinner;
    
    public SimulateBookingDialog(JDialog parent, PropertySystem system, int propertyIndex) {
        super(parent, "Simulate Booking", true);
        this.system = system;
        this.propertyIndex = propertyIndex;
        this.property = system.getProperty(propertyIndex);
        
        setSize(500, 400);
        setLocationRelativeTo(parent);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("New Booking - " + property.getName(), 
            SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Guest name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Guest Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        guestField = new JTextField(20);
        formPanel.add(guestField, gbc);
        
        // Check-in
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Check-in Day (1-29):"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        SpinnerNumberModel checkInModel = new SpinnerNumberModel(1, 1, 29, 1);
        checkInSpinner = new JSpinner(checkInModel);
        formPanel.add(checkInSpinner, gbc);
        
        // Check-out
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Check-out Day (2-30):"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        SpinnerNumberModel checkOutModel = new SpinnerNumberModel(2, 2, 30, 1);
        checkOutSpinner = new JSpinner(checkOutModel);
        formPanel.add(checkOutSpinner, gbc);
        
        // Info label
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("<html><i>Note: Check-out must be after check-in</i></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        formPanel.add(infoLabel, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton bookBtn = new JButton("Book");
        bookBtn.setBackground(new Color(34, 139, 34));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.addActionListener(e -> attemptBooking());
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(bookBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void attemptBooking() {
        String guestName = guestField.getText().trim();
        
        if (guestName.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a guest name.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int checkIn = (Integer) checkInSpinner.getValue();
        int checkOut = (Integer) checkOutSpinner.getValue();
        
        if (checkOut <= checkIn) {
            JOptionPane.showMessageDialog(this, 
                "Check-out day must be after check-in day.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check availability
        boolean available = system.areDatesAvailable(propertyIndex, checkIn, checkOut);
        
        if (!available) {
            JOptionPane.showMessageDialog(this, 
                "Some dates in this range are not available.", 
                "Unavailable", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create reservation
        Reservation res = system.addReservation(propertyIndex, guestName, checkIn, checkOut);
        
        if (res == null) {
            JOptionPane.showMessageDialog(this, 
                "Unable to create reservation.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show success with breakdown
        double total = res.getTotalPrice(property);
        double[] breakdown = res.getBreakdown(property);
        
        StringBuilder message = new StringBuilder();
        message.append("Reservation successful!\n\n");
        message.append(String.format("Guest: %s\n", guestName));
        message.append(String.format("Check-in: Day %d\n", checkIn));
        message.append(String.format("Check-out: Day %d\n\n", checkOut));
        message.append(String.format(Locale.US, "Total Price: PHP %.2f\n\n", total));
        message.append("Breakdown:\n");
        
        for (int i = 0; i < breakdown.length; i++) {
            message.append(String.format(Locale.US, "  Night %d: PHP %.2f\n", i + 1, breakdown[i]));
        }
        
        JOptionPane.showMessageDialog(this, 
            message.toString(), 
            "Booking Confirmed", 
            JOptionPane.INFORMATION_MESSAGE);
        
        dispose();
    }
}
