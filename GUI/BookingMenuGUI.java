package GUI;

import javax.swing.*;
import java.awt.*;
import System.PropertySystem;
import Menu.MenuHelper;
import Objects.Property;
import Objects.Reservation;
import java.util.ArrayList;

/**
 * GUI for booking management operations.
 */
public class BookingMenuGUI extends JDialog {
    
    private PropertySystem system;
    private MenuHelper helper;
    
    public BookingMenuGUI(JFrame parent, PropertySystem system, MenuHelper helper) {
        super(parent, "Booking Menu", true);
        this.system = system;
        this.helper = helper;
        
        setSize(600, 450);
        setLocationRelativeTo(parent);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        
        // Header
        JLabel headerLabel = new JLabel("Booking Management", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Menu buttons
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        buttonPanel.setBackground(new Color(240, 248, 255));
        
        JButton simulateBtn = createMenuButton("Simulate Booking");
        simulateBtn.addActionListener(e -> simulateBooking());
        
        JButton viewBtn = createMenuButton("View Reservations");
        viewBtn.addActionListener(e -> viewReservations());
        
        JButton removeBtn = createMenuButton("Remove Reservation");
        removeBtn.addActionListener(e -> removeReservation());
        
        JButton backBtn = createMenuButton("Back");
        backBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(simulateBtn);
        buttonPanel.add(viewBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(backBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(60, 179, 113));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void simulateBooking() {
        int propertyIndex = selectProperty("Simulate Booking");
        if (propertyIndex < 0) return;
        
        SimulateBookingDialog dialog = new SimulateBookingDialog(this, system, propertyIndex);
        dialog.setVisible(true);
    }
    
    private void viewReservations() {
        int propertyIndex = selectProperty("View Reservations");
        if (propertyIndex < 0) return;
        
        ArrayList<Reservation> reservations = system.getReservationsForProperty(propertyIndex);
        
        if (reservations.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No reservations for this property.", 
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        ViewReservationsDialog dialog = new ViewReservationsDialog(this, system, propertyIndex);
        dialog.setVisible(true);
    }
    
    private void removeReservation() {
        int propertyIndex = selectProperty("Remove Reservation");
        if (propertyIndex < 0) return;
        
        ArrayList<Reservation> reservations = system.getReservationsForProperty(propertyIndex);
        
        if (reservations.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No reservations to remove.", 
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] resStrings = new String[reservations.size()];
        for (int i = 0; i < reservations.size(); i++) {
            Reservation r = reservations.get(i);
            resStrings[i] = String.format("%d) %s | Day %d to %d", 
                i + 1, r.getGuestName(), r.getCheckIn(), r.getCheckOut());
        }
        
        String selected = (String) JOptionPane.showInputDialog(this,
            "Select reservation to remove:",
            "Remove Reservation",
            JOptionPane.QUESTION_MESSAGE,
            null,
            resStrings,
            resStrings[0]);
        
        if (selected != null) {
            int resIndex = Integer.parseInt(selected.substring(0, selected.indexOf(")"))) - 1;
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove this reservation?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = system.removeReservation(propertyIndex, resIndex);
                
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Reservation removed successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Unable to remove reservation.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private int selectProperty(String title) {
        int count = system.getPropertyCount();
        
        if (count == 0) {
            JOptionPane.showMessageDialog(this, "No properties found.", 
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return -1;
        }
        
        String[] propertyNames = new String[count];
        for (int i = 0; i < count; i++) {
            Property p = system.getProperty(i);
            propertyNames[i] = (i + 1) + ") " + p.getName() + " (" + p.getType().getDisplayName() + ")";
        }
        
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
        
        String indexStr = selected.substring(0, selected.indexOf(")"));
        return Integer.parseInt(indexStr) - 1;
    }
}
