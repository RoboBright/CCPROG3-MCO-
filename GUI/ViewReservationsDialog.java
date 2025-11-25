package GUI;

import javax.swing.*;
import java.awt.*;
import System.PropertySystem;
import Objects.Property;
import Objects.Reservation;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Dialog for viewing all reservations for a property.
 */
public class ViewReservationsDialog extends JDialog {
    
    private PropertySystem system;
    private int propertyIndex;
    private Property property;
    
    public ViewReservationsDialog(JDialog parent, PropertySystem system, int propertyIndex) {
        super(parent, "View Reservations", true);
        this.system = system;
        this.propertyIndex = propertyIndex;
        this.property = system.getProperty(propertyIndex);
        
        setSize(700, 600);
        setLocationRelativeTo(parent);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JLabel headerLabel = new JLabel("Reservations - " + property.getName(), 
            SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Reservations list
        ArrayList<Reservation> reservations = system.getReservationsForProperty(propertyIndex);
        
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        
        for (int i = 0; i < reservations.size(); i++) {
            Reservation r = reservations.get(i);
            JPanel resPanel = createReservationPanel(r, i + 1);
            listPanel.add(resPanel);
            listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(closeBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createReservationPanel(Reservation r, int number) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setBackground(new Color(245, 250, 255));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        // Left side - main info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(245, 250, 255));
        
        JLabel numberLabel = new JLabel("Reservation #" + number);
        numberLabel.setFont(new Font("Arial", Font.BOLD, 16));
        numberLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(numberLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JLabel guestLabel = new JLabel("Guest: " + r.getGuestName());
        guestLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        guestLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(guestLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        
        JLabel datesLabel = new JLabel(String.format("Stay: Day %d to Day %d (%d nights)", 
            r.getCheckIn(), r.getCheckOut(), r.getCheckOut() - r.getCheckIn()));
        datesLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        datesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(datesLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Total price
        double total = r.getTotalPrice(property);
        JLabel totalLabel = new JLabel(String.format(Locale.US, "Total: PHP %.2f", total));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(34, 139, 34));
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(totalLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Breakdown
        JLabel breakdownTitle = new JLabel("Nightly Breakdown:");
        breakdownTitle.setFont(new Font("Arial", Font.BOLD, 12));
        breakdownTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(breakdownTitle);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        
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
