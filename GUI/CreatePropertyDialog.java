package GUI;

import javax.swing.*;
import java.awt.*;
import System.PropertySystem;
import Objects.PropertyType;
import java.util.ArrayList;

/**
 * Dialog for creating a new property in the system.
 * Allows users to specify property name, type, and which dates to list.
 * Provides options to list all 30 days or specific date ranges.
 */
public class CreatePropertyDialog extends JDialog {

    private PropertySystem system;
    private JTextField nameField;
    private JComboBox<String> typeCombo;
    private JTextField datesField;
    private JRadioButton specificDatesRadio;
    private JRadioButton allDatesRadio;

    /**
     * Constructs a CreatePropertyDialog for adding a new property.
     *
     * @param parent the parent dialog that launched this creation dialog
     * @param system the PropertySystem where the new property will be added
     */
    public CreatePropertyDialog(JDialog parent, PropertySystem system) {
        super(parent, "Create Property", true);
        this.system = system;

        setSize(500, 400);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Property Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Property Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Property Type
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Property Type:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        String[] types = {"Eco-Apartment", "Sustainable House", "Green Resort", "Eco-Glamping"};
        typeCombo = new JComboBox<>(types);
        formPanel.add(typeCombo, gbc);

        // Date selection
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Dates:"), gbc);

        gbc.gridx = 1;
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        allDatesRadio = new JRadioButton("All (1-30)", true);
        specificDatesRadio = new JRadioButton("Specific dates");
        ButtonGroup group = new ButtonGroup();
        group.add(allDatesRadio);
        group.add(specificDatesRadio);
        radioPanel.add(allDatesRadio);
        radioPanel.add(specificDatesRadio);
        formPanel.add(radioPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Date Numbers:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        datesField = new JTextField("1,2,3,4,5...");
        datesField.setEnabled(false);
        formPanel.add(datesField, gbc);

        // Radio button listener
        allDatesRadio.addActionListener(e -> datesField.setEnabled(false));
        specificDatesRadio.addActionListener(e -> {
            datesField.setEnabled(true);
            datesField.setText("");
        });

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton createBtn = new JButton("Create");
        JButton cancelBtn = new JButton("Cancel");

        createBtn.addActionListener(e -> createProperty());
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(createBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void createProperty() {
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Property name cannot be empty.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (system.propertyNameExists(name)) {
            JOptionPane.showMessageDialog(this, "That name already exists.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PropertyType type = PropertyType.fromChoice(typeCombo.getSelectedIndex() + 1);

        int[] days;
        if (allDatesRadio.isSelected()) {
            days = new int[30];
            for (int i = 0; i < 30; i++) {
                days[i] = i + 1;
            }
        } else {
            String input = datesField.getText().trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter dates.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] parts = input.split(",");
            ArrayList<Integer> dayList = new ArrayList<>();

            for (String part : parts) {
                try {
                    int d = Integer.parseInt(part.trim());
                    if (d >= 1 && d <= 30) {
                        dayList.add(d);
                    }
                } catch (NumberFormatException ignored) {}
            }

            if (dayList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter at least one valid day (1-30).",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            days = new int[dayList.size()];
            for (int i = 0; i < dayList.size(); i++) {
                days[i] = dayList.get(i);
            }
        }

        int index = system.createProperty(name, type, days);

        if (index >= 0) {
            JOptionPane.showMessageDialog(this, "Property created successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Unable to create property.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}