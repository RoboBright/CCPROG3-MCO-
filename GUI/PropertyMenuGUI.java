package GUI;

import javax.swing.*;
import java.awt.*;
import System.PropertySystem;
import Menu.MenuHelper;
import Objects.Property;

/**
 * GUI for property management operations.
 * Allows creating, viewing, and managing properties.
 */
public class PropertyMenuGUI extends JDialog {
    
    private PropertySystem system;
    private MenuHelper helper;
    
    public PropertyMenuGUI(JFrame parent, PropertySystem system, MenuHelper helper) {
        super(parent, "Property Menu", true);
        this.system = system;
        this.helper = helper;
        
        setSize(600, 500);
        setLocationRelativeTo(parent);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        
        // Header
        JLabel headerLabel = new JLabel("Property Management", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Menu buttons
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        buttonPanel.setBackground(new Color(240, 248, 255));
        
        JButton createBtn = createMenuButton("Create Property");
        createBtn.addActionListener(e -> openCreateProperty());
        
        JButton viewBtn = createMenuButton("View Property");
        viewBtn.addActionListener(e -> openViewProperty());
        
        JButton manageBtn = createMenuButton("Manage Property");
        manageBtn.addActionListener(e -> openManageProperty());
        
        JButton backBtn = createMenuButton("Back");
        backBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(createBtn);
        buttonPanel.add(viewBtn);
        buttonPanel.add(manageBtn);
        buttonPanel.add(backBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void openCreateProperty() {
        CreatePropertyDialog dialog = new CreatePropertyDialog(this, system);
        dialog.setVisible(true);
    }
    
    private void openViewProperty() {
        int propertyIndex = selectProperty("View Property");
        if (propertyIndex >= 0) {
            ViewPropertyDialog dialog = new ViewPropertyDialog(this, system, propertyIndex);
            dialog.setVisible(true);
        }
    }
    
    private void openManageProperty() {
        int propertyIndex = selectProperty("Manage Property");
        if (propertyIndex >= 0) {
            ManagePropertyDialog dialog = new ManagePropertyDialog(this, system, propertyIndex, helper);
            dialog.setVisible(true);
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
        
        // Extract index from selection
        String indexStr = selected.substring(0, selected.indexOf(")"));
        return Integer.parseInt(indexStr) - 1;
    }
}
