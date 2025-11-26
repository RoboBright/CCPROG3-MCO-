package GUI;

import javax.swing.*;
import java.awt.*;
import System.PropertySystem;
import Objects.Property;

/**
 * GUI dialog for property management operations.
 * Provides options to create new properties, view property details, or manage existing properties.
 */
public class PropertyMenuGUI extends JDialog {

    private PropertySystem system;

    /**
     * Constructs the PropertyMenuGUI dialog with options for property operations.
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
        // Back button closes this dialog
        JButton backBtn = createMenuButton("Back");
        backBtn.addActionListener(e -> dispose());

        // Add buttons to the panel
        buttonPanel.add(createBtn);
        buttonPanel.add(viewBtn);
        buttonPanel.add(manageBtn);
        buttonPanel.add(backBtn);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    /**
     * Creates a menu button with standard styling for the property menu.
     *
     * @param text the text label of the button
     * @return a JButton styled for the menu
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
     */
    private void openCreateProperty() {
        CreatePropertyDialog dialog = new CreatePropertyDialog(this, system);
        dialog.setVisible(true);
    }

    /**
     * Opens the ViewPropertyDialog to display information about a selected property.
     * It first prompts the user to select a property and then shows the dialog if a valid property is chosen.
     */
    private void openViewProperty() {
        int propertyIndex = selectProperty("View Property");
        if (propertyIndex >= 0) {
            ViewPropertyDialog dialog = new ViewPropertyDialog(this, system, propertyIndex);
            dialog.setVisible(true);
        }
    }

    /**
     * Opens the ManagePropertyDialog to modify settings of a selected property.
     * It first prompts the user to select a property and then shows the management dialog if a valid property is chosen.
     */
    private void openManageProperty() {
        int propertyIndex = selectProperty("Manage Property");
        if (propertyIndex >= 0) {
            ManagePropertyDialog dialog = new ManagePropertyDialog(this, system, propertyIndex);
            dialog.setVisible(true);
        }
    }

    /**
     * Displays a dialog prompting the user to select a property from the system.
     *
     * @param title the title for the selection dialog window
     * @return the index of the selected property, or -1 if the selection was cancelled or none is available
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
