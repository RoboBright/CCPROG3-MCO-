package GUI;

import javax.swing.*;
import java.awt.*;
import System.PropertySystem;
import Objects.Property;

/**
 * Dialog for managing environmental rates across property dates.
 * Provides bulk operations for setting rates on individual dates, all dates,
 * date ranges, or randomizing rates. Environmental rates range from 0.80 to 1.20
 * and serve as multipliers affecting the final nightly price.
 */
public class EnvironmentalRatesDialog extends JDialog {

    private PropertySystem system;
    private int propertyIndex;
    private Property property;
    private CalendarGridPanel calendarPanel;

    /**
     * Constructs an EnvironmentalRatesDialog for managing rates.
     * Creates a split-pane interface with calendar view and control panel.
     *
     * @param parent the parent dialog that launched this dialog
     * @param system the PropertySystem containing property data
     * @param propertyIndex the index of the property to manage
     */
    public EnvironmentalRatesDialog(JDialog parent, PropertySystem system, int propertyIndex) {
        super(parent, "Environmental Rates", true);
        this.system = system;
        this.propertyIndex = propertyIndex;
        this.property = system.getProperty(propertyIndex);

        setSize(1100, 700);
        setLocationRelativeTo(parent);

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header showing property name
        JLabel headerLabel = new JLabel("Environmental Rates - " + property.getName(),
                SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Split pane for calendar and controls
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Calendar view on the left side
        calendarPanel = new CalendarGridPanel(property);
        JScrollPane calendarScroll = new JScrollPane(calendarPanel);
        calendarScroll.setBorder(BorderFactory.createTitledBorder("Calendar View (Click dates to edit)"));
        splitPane.setLeftComponent(calendarScroll);

        // Control panel on the right side
        JPanel controlPanel = createControlPanel();
        JScrollPane controlScroll = new JScrollPane(controlPanel);
        controlScroll.setBorder(BorderFactory.createTitledBorder("Bulk Operations"));
        splitPane.setRightComponent(controlScroll);

        // Set divider position to give more space to calendar
        splitPane.setDividerLocation(700);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Close button at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(closeBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Creates the control panel containing all bulk operation options.
     * Includes sections for setting rates by day, all days, range, and randomization.
     *
     * @return a JPanel containing all control sections
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Set rate for one day section
        panel.add(createSectionLabel("Set Rate for One Day"));
        panel.add(createOneDayPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Set rate for all days section
        panel.add(createSectionLabel("Set Rate for All Days"));
        panel.add(createAllDaysPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Set rate for range section
        panel.add(createSectionLabel("Set Rate for Range"));
        panel.add(createRangePanel());
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Randomize section
        panel.add(createSectionLabel("Randomize All Rates"));
        panel.add(createRandomizePanel());

        return panel;
    }

    /**
     * Creates a section header label with bold formatting.
     *
     * @param text the text for the section header
     * @return a JLabel formatted as a section header
     */
    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    /**
     * Creates the panel for setting environmental rate for a single day.
     * Allows user to input a specific day number and desired rate.
     *
     * @return a JPanel containing input fields and apply button
     */
    private JPanel createOneDayPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setMaximumSize(new Dimension(350, 100));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Day number input
        panel.add(new JLabel("Day (1-30):"));
        JTextField dayField = new JTextField();
        panel.add(dayField);

        // Rate input
        panel.add(new JLabel("Rate (0.80-1.20):"));
        JTextField rateField = new JTextField();
        panel.add(rateField);

        // Apply button with validation and update logic
        JButton applyBtn = new JButton("Apply");
        applyBtn.addActionListener(e -> {
            try {
                // Parse user input
                int day = Integer.parseInt(dayField.getText().trim());
                double rate = Double.parseDouble(rateField.getText().trim());

                // Validate day is within range
                if (day < 1 || day > 30) {
                    showError("Day must be between 1 and 30.");
                    return;
                }

                // Validate rate is within range
                if (rate < 0.80 || rate > 1.20) {
                    showError("Rate must be between 0.80 and 1.20.");
                    return;
                }

                // Attempt to set the rate for the specified day
                boolean ok = system.setEnvironmentalRateForDate(propertyIndex, day, rate);

                if (ok) {
                    // Refresh calendar to show updated rate
                    calendarPanel.refreshAll();
                    JOptionPane.showMessageDialog(this, "Rate updated for day " + day);
                    // Clear input fields for next operation
                    dayField.setText("");
                    rateField.setText("");
                } else {
                    showError("Unable to update rate. Date may not be listed.");
                }
            } catch (NumberFormatException ex) {
                // Handle invalid number input
                showError("Please enter valid numbers.");
            }
        });
        panel.add(applyBtn);

        return panel;
    }

    /**
     * Creates the panel for setting environmental rate for all listed days.
     * Applies the same rate to every date in the property.
     *
     * @return a JPanel containing rate input and apply button
     */
    private JPanel createAllDaysPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setMaximumSize(new Dimension(350, 70));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Rate input
        panel.add(new JLabel("Rate (0.80-1.20):"));
        JTextField rateField = new JTextField();
        panel.add(rateField);

        // Apply button with confirmation dialog
        JButton applyBtn = new JButton("Apply to All");
        applyBtn.addActionListener(e -> {
            try {
                // Parse user input
                double rate = Double.parseDouble(rateField.getText().trim());

                // Validate rate is within range
                if (rate < 0.80 || rate > 1.20) {
                    showError("Rate must be between 0.80 and 1.20.");
                    return;
                }

                // Confirm before applying to all dates
                int result = JOptionPane.showConfirmDialog(this,
                        "Apply rate " + rate + " to all listed dates?",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    // Apply rate to all dates in the property
                    boolean ok = system.setEnvironmentalRateForAllDates(propertyIndex, rate);

                    if (ok) {
                        // Refresh calendar to show updated rates
                        calendarPanel.refreshAll();
                        JOptionPane.showMessageDialog(this, "Rate updated for all dates!");
                        rateField.setText("");
                    } else {
                        showError("Unable to update rates.");
                    }
                }
            } catch (NumberFormatException ex) {
                // Handle invalid number input
                showError("Please enter a valid number.");
            }
        });
        panel.add(applyBtn);

        return panel;
    }

    /**
     * Creates the panel for setting environmental rate for a range of days.
     * Allows user to specify start day, end day, and rate to apply.
     *
     * @return a JPanel containing input fields and apply button
     */
    private JPanel createRangePanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setMaximumSize(new Dimension(350, 130));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Start day input
        panel.add(new JLabel("Start Day (1-30):"));
        JTextField startField = new JTextField();
        panel.add(startField);

        // End day input
        panel.add(new JLabel("End Day (1-30):"));
        JTextField endField = new JTextField();
        panel.add(endField);

        // Rate input
        panel.add(new JLabel("Rate (0.80-1.20):"));
        JTextField rateField = new JTextField();
        panel.add(rateField);

        // Apply button with validation
        JButton applyBtn = new JButton("Apply to Range");
        applyBtn.addActionListener(e -> {
            try {
                // Parse user input
                int start = Integer.parseInt(startField.getText().trim());
                int end = Integer.parseInt(endField.getText().trim());
                double rate = Double.parseDouble(rateField.getText().trim());

                // Validate range is valid
                if (start < 1 || end > 30 || start > end) {
                    showError("Invalid range. Ensure 1 ≤ start ≤ end ≤ 30.");
                    return;
                }

                // Validate rate is within range
                if (rate < 0.80 || rate > 1.20) {
                    showError("Rate must be between 0.80 and 1.20.");
                    return;
                }

                // Apply rate to the specified range
                boolean ok = system.setEnvironmentalRateForRange(propertyIndex, start, end, rate);

                if (ok) {
                    // Refresh calendar to show updated rates
                    calendarPanel.refreshAll();
                    JOptionPane.showMessageDialog(this,
                            "Rate updated for days " + start + " to " + end);
                    // Clear input fields
                    startField.setText("");
                    endField.setText("");
                    rateField.setText("");
                } else {
                    showError("Unable to update range.");
                }
            } catch (NumberFormatException ex) {
                // Handle invalid number input
                showError("Please enter valid numbers.");
            }
        });
        panel.add(applyBtn);

        return panel;
    }

    /**
     * Creates the panel for randomizing all environmental rates.
     * Generates random rates between 0.80 and 1.20 for all listed dates.
     *
     * @return a JPanel containing the randomize button
     */
    private JPanel createRandomizePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setMaximumSize(new Dimension(350, 50));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Randomize button with confirmation dialog
        JButton randomizeBtn = new JButton("Randomize All Rates (0.80-1.20)");
        randomizeBtn.addActionListener(e -> {
            // Confirm before randomizing all rates
            int result = JOptionPane.showConfirmDialog(this,
                    "Randomize environmental rates for all listed dates?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                // Apply random rates to all dates
                system.randomizeEnvironmentalRates(propertyIndex);
                // Refresh calendar to show new rates
                calendarPanel.refreshAll();
                JOptionPane.showMessageDialog(this, "All rates randomized!");
            }
        });
        panel.add(randomizeBtn);

        return panel;
    }

    /**
     * Displays an error message dialog with the specified message.
     *
     * @param message the error message to display
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}