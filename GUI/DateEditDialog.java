package GUI;

import javax.swing.*;
import java.awt.*;
import Objects.Property;
import Objects.Date;

/**
 * Dialog for editing a specific date's price and environmental rate.
 * Allows property managers to modify the base price and environmental impact rate
 * for individual dates, with validation and real-time final price calculation.
 */
public class DateEditDialog extends JDialog {

    private Property property;
    private int day;
    private Date date;
    private boolean updated = false;

    private JTextField priceField;
    private JTextField envRateField;

    /**
     * Constructs a DateEditDialog for editing a specific date.
     *
     * @param parent the parent frame that owns this dialog
     * @param property the property containing this date
     * @param day the day number being edited (1-30)
     * @param date the Date object to edit
     */
    public DateEditDialog(Frame parent, Property property, int day, Date date) {
        super(parent, "Edit Day " + day, true);
        this.property = property;
        this.day = day;
        this.date = date;

        setSize(400, 300);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create the information panel with input fields
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Display day number (read-only)
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(new JLabel("Day:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        infoPanel.add(new JLabel(String.valueOf(day)), gbc);

        // Display reservation status (read-only)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        infoPanel.add(new JLabel("Status:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        String status = date.isAvailable() ? "Available" : "Reserved (Cannot edit)";
        JLabel statusLabel = new JLabel(status);
        if (!date.isAvailable()) {
            statusLabel.setForeground(Color.RED);
        }
        infoPanel.add(statusLabel, gbc);

        // Base price input field (editable only if available)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        infoPanel.add(new JLabel("Base Price (PHP):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        priceField = new JTextField(String.valueOf(date.getPrice()));
        priceField.setEnabled(date.isAvailable());
        infoPanel.add(priceField, gbc);

        // Environmental rate input field (editable only if available)
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        infoPanel.add(new JLabel("Environmental Rate (0.80-1.20):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        envRateField = new JTextField(String.format("%.2f", date.getEnvironmentalRate()));
        envRateField.setEnabled(date.isAvailable());
        infoPanel.add(envRateField, gbc);

        // Display current final price (calculated, read-only)
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        infoPanel.add(new JLabel("Current Final Price:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        double finalPrice = date.getFinalPrice(property.getType().getMultiplier());
        JLabel finalPriceLabel = new JLabel(String.format("PHP %.2f", finalPrice));
        finalPriceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(finalPriceLabel, gbc);

        mainPanel.add(infoPanel, BorderLayout.CENTER);

        // Create button panel with Save and Cancel buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton saveBtn = new JButton("Save");
        saveBtn.setEnabled(date.isAvailable()); // Only enable if date is available
        saveBtn.addActionListener(e -> saveChanges());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Validates and saves the changes made to the date's price and environmental rate.
     * Performs input validation and updates the Date object if all values are valid.
     */
    private void saveChanges() {
        try {
            // Parse input values
            double price = Double.parseDouble(priceField.getText().trim());
            double envRate = Double.parseDouble(envRateField.getText().trim());

            // Validate price is at least 100
            if (price < 100.0) {
                JOptionPane.showMessageDialog(this,
                        "Price cannot be less than 100.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate environmental rate is within allowed range
            if (envRate < 0.80 || envRate > 1.20) {
                JOptionPane.showMessageDialog(this,
                        "Environmental rate must be between 0.80 and 1.20.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Apply the changes to the Date object
            date.setPrice(price);
            date.setEnvironmentalRate(envRate);

            // Mark as updated so the calendar can refresh
            updated = true;
            JOptionPane.showMessageDialog(this,
                    "Date updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException ex) {
            // Handle invalid number input
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Returns whether the date was updated during this dialog session.
     *
     * @return true if changes were saved; false otherwise
     */
    public boolean isUpdated() {
        return updated;
    }
}