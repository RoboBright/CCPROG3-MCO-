package GUI;

import javax.swing.*;
import java.awt.*;
import Objects.Property;
import Objects.Date;

/**
 * Interactive calendar grid panel that displays property dates
 * and allows editing of individual date information.
 */
public class CalendarGridPanel extends JPanel {
    
    private Property property;
    private JButton[][] dayButtons;
    private static final int COLUMNS = 7;
    private static final int ROWS = 5;
    
    public CalendarGridPanel(Property property) {
        this.property = property;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Legend
        JPanel legendPanel = createLegend();
        add(legendPanel, BorderLayout.NORTH);
        
        // Calendar grid
        JPanel gridPanel = new JPanel(new GridLayout(ROWS, COLUMNS, 5, 5));
        gridPanel.setBackground(Color.WHITE);
        dayButtons = new JButton[ROWS][COLUMNS];
        
        int day = 1;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                if (day <= 30) {
                    JButton dayButton = createDayButton(day);
                    dayButtons[row][col] = dayButton;
                    gridPanel.add(dayButton);
                    day++;
                } else {
                    JButton emptyButton = new JButton();
                    emptyButton.setEnabled(false);
                    emptyButton.setBackground(Color.LIGHT_GRAY);
                    gridPanel.add(emptyButton);
                }
            }
        }
        
        add(gridPanel, BorderLayout.CENTER);
    }
    
    private JPanel createLegend() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        
        panel.add(new JLabel("Legend: "));
        panel.add(createLegendBox(new Color(144, 238, 144), "Reduced (<100%)"));
        panel.add(createLegendBox(Color.WHITE, "Standard (100%)"));
        panel.add(createLegendBox(new Color(255, 255, 153), "Increased (>100%)"));
        panel.add(createLegendBox(new Color(255, 182, 193), "Booked"));
        panel.add(createLegendBox(Color.LIGHT_GRAY, "Not Listed"));
        
        return panel;
    }
    
    private JPanel createLegendBox(Color color, String label) {
        JPanel box = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        box.setBackground(Color.WHITE);
        
        JLabel colorLabel = new JLabel("   ");
        colorLabel.setOpaque(true);
        colorLabel.setBackground(color);
        colorLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        box.add(colorLabel);
        box.add(new JLabel(label));
        
        return box;
    }
    
    private JButton createDayButton(int day) {
        Date date = property.getDateByDay(day);
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(120, 80));
        button.setFocusPainted(false);
        
        if (date == null) {
            button.setText("<html><center>Day " + day + "<br>N/A</center></html>");
            button.setBackground(Color.LIGHT_GRAY);
            button.setEnabled(false);
        } else {
            updateButtonDisplay(button, day, date);
            button.addActionListener(e -> editDate(day));
        }
        
        return button;
    }
    
    private void updateButtonDisplay(JButton button, int day, Date date) {
        double multiplier = property.getType().getMultiplier();
        double finalPrice = date.getFinalPrice(multiplier);
        int percent = (int)(date.getEnvironmentalRate() * 100);
        
        String status = date.isAvailable() ? "Available" : "BOOKED";
        
        button.setText(String.format("<html><center><b>Day %d</b><br>₱%.0f<br>%d%%<br>%s</center></html>", 
            day, finalPrice, percent, status));
        
        // Set background color based on status and environmental rate
        if (!date.isAvailable()) {
            button.setBackground(new Color(255, 182, 193)); // Light red for booked
        } else if (percent < 100) {
            button.setBackground(new Color(144, 238, 144)); // Light green
        } else if (percent > 100) {
            button.setBackground(new Color(255, 255, 153)); // Light yellow
        } else {
            button.setBackground(Color.WHITE);
        }
        
        button.setEnabled(true);
    }
    
    private void editDate(int day) {
        Date date = property.getDateByDay(day);
        if (date == null) return;
        
        DateEditDialog dialog = new DateEditDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            property, day, date);
        dialog.setVisible(true);
        
        if (dialog.isUpdated()) {
            // Refresh button display
            refreshDay(day);
        }
    }
    
    private void refreshDay(int day) {
        int row = (day - 1) / COLUMNS;
        int col = (day - 1) % COLUMNS;
        
        if (row < ROWS && col < COLUMNS) {
            JButton button = dayButtons[row][col];
            Date date = property.getDateByDay(day);
            if (date != null) {
                updateButtonDisplay(button, day, date);
            }
        }
    }
    
    public void refreshAll() {
        int day = 1;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                if (day <= 30) {
                    JButton button = dayButtons[row][col];
                    Date date = property.getDateByDay(day);
                    if (date != null) {
                        updateButtonDisplay(button, day, date);
                    }
                    day++;
                }
            }
        }
    }
}
