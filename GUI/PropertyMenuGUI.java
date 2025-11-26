package GUI;

import javax.swing.*;
import java.awt.*;
import System.PropertySystem;
import Objects.Property;

/**
 * GUI dialog for property management operations.
 * Provides options to create new properties, view property details, or manage existing properties.
 * Acts as the main navigation hub for all property-related functionality.
 */
public class PropertyMenuGUI extends JDialog {

    private PropertySystem system;

    /**
     * Constructs the PropertyMenuGUI dialog with options for property operations.
     * Creates a modal dialog with buttons for creating, viewing, and managing properties.
     *
     * @param parent the parent frame that launched this dialog
     * @param system the PropertySystem containing property data and operations
     */
    public PropertyMenuGUI(JFrame parent, PropertySystem system) {
        super(parent, "Property Menu", true);
        this.system = system;

        setSize(600, 500);
        setLocationRelativeTo(parent);

        // Main panel for the dialog
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));

        // Header label at the top
        JLabel headerLabel = new JLabel("Property Management", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Panel with menu buttons
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        buttonPanel.setBackground(new Color(240, 248, 255));

        // Create Property button opens a dialog to create a new property
        JButton createBtn = createMenuButton("Create Property");
        createBtn.addActionListener(e -> openCreateProperty());

        // View Property button allows viewing details of a selected property
        JButton viewBtn = createMenuButton("View Property");
        viewBtn.addActionListener(e -> openViewProperty());

        // Manage Property button allows managing settings of a selected property
        JButton manageBtn = createMenuButton("Manage Property");
        manageBtn.addActionListener(e -> openManageProperty());

        // Back button closes this dialog and returns to main menu
        JButton backBtn = createMenuButton("Back");
        backBtn.addActionListener(e -> dispose());

        // Add buttons to the panel in order
        buttonPanel.add(createBtn);
        buttonPanel.add(viewBtn);
        buttonPanel.add(manageBtn);
        buttonPanel.add(backBtn);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    /**
     * Creates a menu button with standard styling for the property menu.
     * All buttons share consistent appearance and behavior.
     *
     * @param text the text label of the button
     * @return a JButton styled for the menu with blue background and white text
     */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Opens the CreatePropertyDialog to add a new property.
     * Launches a modal dialog for entering property details.
     */
    private void openCreateProperty() {
        CreatePropertyDialog dialog = new CreatePropertyDialog(this, system);
        dialog.setVisible(true);
    }

    /**
     * Opens the ViewPropertyDialog to display information about a selected property.
     * First prompts the user to select a property, then shows detailed information.
     * If no property is selected or available, the operation is cancelled.
     */
    private void openViewProperty() {
        // Prompt user to select a property
        int propertyIndex = selectProperty("View Property");

        // Only proceed if a valid property was selected
        if (propertyIndex >= 0) {
            ViewPropertyDialog dialog = new ViewPropertyDialog(this, system, propertyIndex);
            dialog.setVisible(true);
        }
    }

    /**
     * Opens the ManagePropertyDialog to modify settings of a selected property.
     * First prompts the user to select a property, then shows management options.
     * If no property is selected or available, the operation is cancelled.
     */
    private void openManageProperty() {
        // Prompt user to select a property
        int propertyIndex = selectProperty("Manage Property");

        // Only proceed if a valid property was selected
        if (propertyIndex >= 0) {
            ManagePropertyDialog dialog = new ManagePropertyDialog(this, system, propertyIndex);
            dialog.setVisible(true);
        }
    }

    /**
     * Displays a dialog prompting the user to select a property from the system.
     * Shows a dropdown list of all properties with their names and types.
     *
     * @param title the title for the selection dialog window
     * @return the index of the selected property, or -1 if cancelled or no properties exist
     */
    private int selectProperty(String title) {
        int count = system.getPropertyCount();

        // If there are no properties, inform the user and return -1
        if (count == 0) {
            JOptionPane.showMessageDialog(this, "No properties found.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return -1;
        }

        // Build an array of property descriptions for user selection
        String[] propertyNames = new String[count];
        for (int i = 0; i < count; i++) {
            Property p = system.getProperty(i);
            // Format: "1) Property Name (Property Type)"
            propertyNames[i] = (i + 1) + ") " + p.getName() + " (" + p.getType().getDisplayName() + ")";
        }

        // Show a dialog with a dropdown to select a property
        String selected = (String) JOptionPane.showInputDialog(this,
                "Select a property:",
                title,
                JOptionPane.QUESTION_MESSAGE,
                null,
                propertyNames,
                propertyNames[0]);

        // If the user closed the dialog or clicked cancel, selected will be null
        if (selected == null) {
            return -1;
        }

        // Extract the numeric index from the selected string (e.g., "1) PropertyName ..." -> 1)
        String indexStr = selected.substring(0, selected.indexOf(")"));
        return Integer.parseInt(indexStr) - 1;
    }
}