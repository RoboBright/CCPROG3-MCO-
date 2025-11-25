package GUI;

import javax.swing.*;
import java.awt.*;
import System.PropertySystem;
import Objects.Property;

/**
 * Dialog for managing environmental rates.
 */
public class EnvironmentalRatesDialog extends JDialog {
    
    private PropertySystem system;
    private int propertyIndex;
    private Property property;
    private CalendarGridPanel calendarPanel;
    
    public EnvironmentalRatesDialog(JDialog parent, PropertySystem system, int propertyIndex) {
        super(parent, "Environmental Rates", true);
        this.system = system;
        this.propertyIndex = propertyIndex;
        this.property = system.getProperty(propertyIndex);
        
        setSize(1100, 700);
        setLocationRelativeTo(parent);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JLabel headerLabel = new JLabel("Environmental Rates - " + property.getName(), 
            SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Split pane for calendar and controls
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // Calendar view
        calendarPanel = new CalendarGridPanel(property);
        JScrollPane calendarScroll = new JScrollPane(calendarPanel);
        calendarScroll.setBorder(BorderFactory.createTitledBorder("Calendar View (Click dates to edit)"));
        splitPane.setLeftComponent(calendarScroll);
        
        // Control panel
        JPanel controlPanel = createControlPanel();
        JScrollPane controlScroll = new JScrollPane(controlPanel);
        controlScroll.setBorder(BorderFactory.createTitledBorder("Bulk Operations"));
        splitPane.setRightComponent(controlScroll);
        
        splitPane.setDividerLocation(700);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(closeBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Set rate for one day
        panel.add(createSectionLabel("Set Rate for One Day"));
        panel.add(createOneDayPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Set rate for all days
        panel.add(createSectionLabel("Set Rate for All Days"));
        panel.add(createAllDaysPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Set rate for range
        panel.add(createSectionLabel("Set Rate for Range"));
        panel.add(createRangePanel());
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Randomize
        panel.add(createSectionLabel("Randomize All Rates"));
        panel.add(createRandomizePanel());
        
        return panel;
    }
    
    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private JPanel createOneDayPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setMaximumSize(new Dimension(350, 100));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(new JLabel("Day (1-30):"));
        JTextField dayField = new JTextField();
        panel.add(dayField);
        
        panel.add(new JLabel("Rate (0.80-1.20):"));
        JTextField rateField = new JTextField();
        panel.add(rateField);
        
        JButton applyBtn = new JButton("Apply");
        applyBtn.addActionListener(e -> {
            try {
                int day = Integer.parseInt(dayField.getText().trim());
                double rate = Double.parseDouble(rateField.getText().trim());
                
                if (day < 1 || day > 30) {
                    showError("Day must be between 1 and 30.");
                    return;
                }
                
                if (rate < 0.80 || rate > 1.20) {
                    showError("Rate must be between 0.80 and 1.20.");
                    return;
                }
                
                boolean ok = system.setEnvironmentalRateForDate(propertyIndex, day, rate);
                
                if (ok) {
                    calendarPanel.refreshAll();
                    JOptionPane.showMessageDialog(this, "Rate updated for day " + day);
                    dayField.setText("");
                    rateField.setText("");
                } else {
                    showError("Unable to update rate. Date may not be listed.");
                }
            } catch (NumberFormatException ex) {
                showError("Please enter valid numbers.");
            }
        });
        panel.add(applyBtn);
        
        return panel;
    }
    
    private JPanel createAllDaysPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setMaximumSize(new Dimension(350, 70));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(new JLabel("Rate (0.80-1.20):"));
        JTextField rateField = new JTextField();
        panel.add(rateField);
        
        JButton applyBtn = new JButton("Apply to All");
        applyBtn.addActionListener(e -> {
            try {
                double rate = Double.parseDouble(rateField.getText().trim());
                
                if (rate < 0.80 || rate > 1.20) {
                    showError("Rate must be between 0.80 and 1.20.");
                    return;
                }
                
                int result = JOptionPane.showConfirmDialog(this,
                    "Apply rate " + rate + " to all listed dates?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION);
                
                if (result == JOptionPane.YES_OPTION) {
                    boolean ok = system.setEnvironmentalRateForAllDates(propertyIndex, rate);
                    
                    if (ok) {
                        calendarPanel.refreshAll();
                        JOptionPane.showMessageDialog(this, "Rate updated for all dates!");
                        rateField.setText("");
                    } else {
                        showError("Unable to update rates.");
                    }
                }
            } catch (NumberFormatException ex) {
                showError("Please enter a valid number.");
            }
        });
        panel.add(applyBtn);
        
        return panel;
    }
    
    private JPanel createRangePanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setMaximumSize(new Dimension(350, 130));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(new JLabel("Start Day (1-30):"));
        JTextField startField = new JTextField();
        panel.add(startField);
        
        panel.add(new JLabel("End Day (1-30):"));
        JTextField endField = new JTextField();
        panel.add(endField);
        
        panel.add(new JLabel("Rate (0.80-1.20):"));
        JTextField rateField = new JTextField();
        panel.add(rateField);
        
        JButton applyBtn = new JButton("Apply to Range");
        applyBtn.addActionListener(e -> {
            try {
                int start = Integer.parseInt(startField.getText().trim());
                int end = Integer.parseInt(endField.getText().trim());
                double rate = Double.parseDouble(rateField.getText().trim());
                
                if (start < 1 || end > 30 || start > end) {
                    showError("Invalid range. Ensure 1 ≤ start ≤ end ≤ 30.");
                    return;
                }
                
                if (rate < 0.80 || rate > 1.20) {
                    showError("Rate must be between 0.80 and 1.20.");
                    return;
                }
                
                boolean ok = system.setEnvironmentalRateForRange(propertyIndex, start, end, rate);
                
                if (ok) {
                    calendarPanel.refreshAll();
                    JOptionPane.showMessageDialog(this, 
                        "Rate updated for days " + start + " to " + end);
                    startField.setText("");
                    endField.setText("");
                    rateField.setText("");
                } else {
                    showError("Unable to update range.");
                }
            } catch (NumberFormatException ex) {
                showError("Please enter valid numbers.");
            }
        });
        panel.add(applyBtn);
        
        return panel;
    }
    
    private JPanel createRandomizePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setMaximumSize(new Dimension(350, 50));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton randomizeBtn = new JButton("Randomize All Rates (0.80-1.20)");
        randomizeBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                "Randomize environmental rates for all listed dates?",
                "Confirm",
                JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                system.randomizeEnvironmentalRates(propertyIndex);
                calendarPanel.refreshAll();
                JOptionPane.showMessageDialog(this, "All rates randomized!");
            }
        });
        panel.add(randomizeBtn);
        
        return panel;
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
