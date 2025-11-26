package GUI;

import javax.swing.*;
import java.awt.*;
import System.PropertySystem;
import Objects.Property;
import Objects.PropertyType;

/**
 * Dialog for managing a property's settings and details.
 * Provides options to change the property's name, type, base price, manage environmental rates, or remove the property.
 */
public class ManagePropertyDialog extends JDialog {

    private PropertySystem system;
    private int propertyIndex;
    private Property property;
    private JLabel headerLabel;

    /**
     * Constructs a ManagePropertyDialog for a specific property.
     *
     * @param parent        the parent dialog from which this dialog is launched
     * @param system        the PropertySystem managing the properties and business logic
     * @param propertyIndex the index of the property to be managed
     */
    public ManagePropertyDialog(JDialog parent, PropertySystem system, int propertyIndex) {
        super(parent, "Manage Property", true);
        this.system = system;
        this.propertyIndex = propertyIndex;
        this.property = system.getProperty(propertyIndex);

        setSize(600, 550);
        setLocationRelativeTo(parent);

        // Main panel with padding and background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 248, 255));

        // Header label showing the property's name
        headerLabel = new JLabel("Managing: " + property.getName(), SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Panel containing all action buttons
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        buttonPanel.setBackground(new Color(240, 248, 255));

        // Create and add each button with its respective action
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

        // Add all buttons to the button panel
        buttonPanel.add(changeNameBtn);
        buttonPanel.add(changeTypeBtn);
        buttonPanel.add(updatePriceBtn);
        buttonPanel.add(envRatesBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(backBtn);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    /**
     * Creates a JButton with standard formatting for this dialog's menu.
     *
     * @param text the text to display on the button
     * @return a JButton configured with font and cursor style
     */
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Allows the user to change the property's name.
     * Prompts for a new name and updates the property if the name is valid and not a duplicate.
     * Updates the header label to reflect the new name on success.
     */
    private void changeName() {
        // Prompt the user for a new property name, pre-filled with the current name
        String newName = JOptionPane.showInputDialog(this,
                "Enter new property name:",
                property.getName());

        // Only proceed if the user entered a non-empty name (and didn't cancel)
        if (newName != null && !newName.trim().isEmpty()) {
            boolean ok = system.changePropertyName(propertyIndex, newName.trim());

            if (ok) {
                // Refresh the property reference and update UI to show the new name
                property = system.getProperty(propertyIndex);
                // Update the header label text with the new property name
                headerLabel.setText("Managing: " + property.getName());
                JOptionPane.showMessageDialog(this, "Name updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Unable to change name. The name may already exist.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Allows the user to change the property's type.
     * Presents a selection dialog for property types and updates the property type if a selection is made.
     */
    private void changeType() {
        String[] types = {"Eco-Apartment", "Sustainable House", "Green Resort", "Eco-Glamping"};
        // Show a dialog for selecting a new property type, defaulting to the current type
        String selected = (String) JOptionPane.showInputDialog(this,
                "Select new property type:",
                "Change Type",
                JOptionPane.QUESTION_MESSAGE,
                null,
                types,
                property.getType().getDisplayName());

        if (selected != null) {
            // Determine the corresponding type choice number based on the selection
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
                    JOptionPane.showMessageDialog(this,
                            "Unable to update type.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Updates the property's base price if no active reservations exist.
     * Prompts the user for a new price and applies the change if the value is valid (>= 100).
     */
    private void updateBasePrice() {
        // Prevent price update if the property has active reservations
        if (system.hasReservations(propertyIndex)) {
            JOptionPane.showMessageDialog(this,
                    "Cannot update price with active reservations.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ask the user for a new base price (as text input)
        String input = JOptionPane.showInputDialog(this,
                "Enter new base price (minimum 100):",
                "1500");

        if (input != null) {
            try {
                double price = Double.parseDouble(input.trim());

                // Validate the price meets the minimum requirement
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
                    JOptionPane.showMessageDialog(this,
                            "Unable to update base price.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid numeric value for price.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Opens the EnvironmentalRatesDialog to manage the environmental rates for all dates of this property.
     */
    private void manageEnvironmentalRates() {
        EnvironmentalRatesDialog dialog = new EnvironmentalRatesDialog(this, system, propertyIndex);
        dialog.setVisible(true);
    }

    /**
     * Removes the property from the system if there are no active reservations.
     * Prompts for confirmation before deletion and shows a success or error message.
     */
    private void removeProperty() {
        // Do not allow removal if there are active reservations
        if (system.hasReservations(propertyIndex)) {
            JOptionPane.showMessageDialog(this,
                    "Cannot remove property with active reservations.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirm with the user before deleting the property
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this property?\nThis action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            boolean ok = system.removeProperty(propertyIndex);

            if (ok) {
                JOptionPane.showMessageDialog(this, "Property removed successfully!");
                dispose(); // Close the dialog since the property no longer exists
            } else {
                JOptionPane.showMessageDialog(this,
                        "Unable to remove property.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
