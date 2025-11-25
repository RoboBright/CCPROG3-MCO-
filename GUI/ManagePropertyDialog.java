package GUI;

import javax.swing.*;
import java.awt.*;
import System.PropertySystem;
import Menu.MenuHelper;
import Objects.Property;
import Objects.PropertyType;

/**
 * Dialog for managing property settings.
 */
public class ManagePropertyDialog extends JDialog {
    
    private PropertySystem system;
    private int propertyIndex;
    private MenuHelper helper;
    private Property property;
    
    public ManagePropertyDialog(JDialog parent, PropertySystem system, 
                               int propertyIndex, MenuHelper helper) {
        super(parent, "Manage Property", true);
        this.system = system;
        this.propertyIndex = propertyIndex;
        this.helper = helper;
        this.property = system.getProperty(propertyIndex);
        
        setSize(600, 550);
        setLocationRelativeTo(parent);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 248, 255));
        
        // Header
        JLabel headerLabel = new JLabel("Managing: " + property.getName(), SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        buttonPanel.setBackground(new Color(240, 248, 255));
        
        JButton changeNameBtn = createButton("Change Name");
        changeNameBtn.addActionListener(e -> changeName());
        
        JButton changeTypeBtn = createButton("Change Type");
        changeTypeBtn.addActionListener(e -> changeType());
        
        JButton updatePriceBtn = createButton("Update Base Price");
        updatePriceBtn.addActionListener(e -> updateBasePrice());
        
        JButton envRatesBtn = createButton("Environmental Rates");
        envRatesBtn.addActionListener(e -> manageEnvironmentalRates());
        
        JButton removeBtn = createButton("Remove Property");
        removeBtn.setBackground(new Color(220, 20, 60));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.addActionListener(e -> removeProperty());
        
        JButton backBtn = createButton("Back");
        backBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(changeNameBtn);
        buttonPanel.add(changeTypeBtn);
        buttonPanel.add(updatePriceBtn);
        buttonPanel.add(envRatesBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(backBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void changeName() {
        String newName = JOptionPane.showInputDialog(this, 
            "Enter new property name:", 
            property.getName());
        
        if (newName != null && !newName.trim().isEmpty()) {
            boolean ok = system.changePropertyName(propertyIndex, newName.trim());
            
            if (ok) {
                property = system.getProperty(propertyIndex);
                ((JLabel)((JPanel)getContentPane().getComponent(0))
                    .getComponent(0)).setText("Managing: " + property.getName());
                JOptionPane.showMessageDialog(this, "Name updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Unable to change name. Name may already exist.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void changeType() {
        String[] types = {"Eco-Apartment", "Sustainable House", "Green Resort", "Eco-Glamping"};
        String selected = (String) JOptionPane.showInputDialog(this,
            "Select new property type:",
            "Change Type",
            JOptionPane.QUESTION_MESSAGE,
            null,
            types,
            property.getType().getDisplayName());
        
        if (selected != null) {
            int choice = -1;
            for (int i = 0; i < types.length; i++) {
                if (types[i].equals(selected)) {
                    choice = i + 1;
                    break;
                }
            }
            
            if (choice > 0) {
                PropertyType newType = PropertyType.fromChoice(choice);
                boolean ok = system.changePropertyType(propertyIndex, newType);
                
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Type updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Unable to update type.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void updateBasePrice() {
        if (system.hasReservations(propertyIndex)) {
            JOptionPane.showMessageDialog(this, 
                "Cannot update price with active reservations.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String input = JOptionPane.showInputDialog(this, 
            "Enter new base price (minimum 100):", 
            "1500");
        
        if (input != null) {
            try {
                double price = Double.parseDouble(input.trim());
                
                if (price < 100) {
                    JOptionPane.showMessageDialog(this, 
                        "Price must be at least 100.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                boolean ok = system.updateBasePrice(propertyIndex, price);
                
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Base price updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Unable to update base price.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void manageEnvironmentalRates() {
        EnvironmentalRatesDialog dialog = new EnvironmentalRatesDialog(this, system, propertyIndex);
        dialog.setVisible(true);
    }
    
    private void removeProperty() {
        if (system.hasReservations(propertyIndex)) {
            JOptionPane.showMessageDialog(this, 
                "Cannot remove property with active reservations.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this property?\nThis action cannot be undone.",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            boolean ok = system.removeProperty(propertyIndex);
            
            if (ok) {
                JOptionPane.showMessageDialog(this, "Property removed successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Unable to remove property.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
