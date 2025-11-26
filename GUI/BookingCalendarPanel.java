package GUI;

import javax.swing.*;
import java.awt.*;
import Objects.Property;
import Objects.Date;

/**
 * Calendar panel for displaying property availability during booking process.
 * Shows which dates are available, booked, or selected for the current booking.
 * Unlike CalendarGridPanel, this panel is read-only and highlights the selected date range.
 */
public class BookingCalendarPanel extends JPanel {

    private Property property;
    private JButton[][] dayButtons;
    private int selectedCheckIn = -1;
    private int selectedCheckOut = -1;
    private static final int COLUMNS = 7;
    private static final int ROWS = 5;

    /**
     * Constructs a BookingCalendarPanel for the specified property.
     *
     * @param property the property whose availability calendar is displayed
     */
    public BookingCalendarPanel(Property property) {
        this.property = property;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add legend panel at the top
        JPanel legendPanel = createLegend();
        add(legendPanel, BorderLayout.NORTH);

        // Create the calendar grid
        JPanel gridPanel = new JPanel(new GridLayout(ROWS, COLUMNS, 5, 5));
        gridPanel.setBackground(Color.WHITE);
        dayButtons = new JButton[ROWS][COLUMNS];

        // Populate grid with day buttons (days 1-30)
        int day = 1;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                if (day <= 30) {
                    JButton dayButton = createDayButton(day);
                    dayButtons[row][col] = dayButton;
                    gridPanel.add(dayButton);
                    day++;
                } else {
                    // Fill remaining cells with disabled empty buttons
                    JButton emptyButton = new JButton();
                    emptyButton.setEnabled(false);
                    emptyButton.setBackground(Color.LIGHT_GRAY);
                    gridPanel.add(emptyButton);
                }
            }
        }

        add(gridPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the legend panel showing what each color represents.
     *
     * @return a JPanel containing the color legend
     */
    private JPanel createLegend() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);

        panel.add(new JLabel("Legend: "));
        panel.add(createLegendBox(new Color(144, 238, 144), "Reduced (<100%)"));
        panel.add(createLegendBox(Color.WHITE, "Standard (100%)"));
        panel.add(createLegendBox(new Color(255, 255, 153), "Increased (>100%)"));
        panel.add(createLegendBox(new Color(255, 182, 193), "Booked"));
        panel.add(createLegendBox(new Color(173, 216, 230), "Selected"));
        panel.add(createLegendBox(Color.LIGHT_GRAY, "Not Listed"));

        return panel;
    }

    /**
     * Creates a single legend box showing a color and its label.
     *
     * @param color the color to display in the box
     * @param label the text description of what the color represents
     * @return a JPanel containing the colored box and label
     */
    private JPanel createLegendBox(Color color, String label) {
        JPanel box = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        box.setBackground(Color.WHITE);

        // Create colored label with border
        JLabel colorLabel = new JLabel("   ");
        colorLabel.setOpaque(true);
        colorLabel.setBackground(color);
        colorLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        box.add(colorLabel);
        box.add(new JLabel(label));

        return box;
    }

    /**
     * Creates a button representing a single day in the calendar.
     *
     * @param day the day number (1-30)
     * @return a JButton configured to display the day's information
     */
    private JButton createDayButton(int day) {
        Date date = property.getDateByDay(day);
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(100, 70));
        button.setFocusPainted(false);

        // Keep button enabled but make it non-interactive
        button.setEnabled(true);
        button.setFocusable(false);
        // Remove all action listeners to prevent clicks
        button.setBorderPainted(true);

        if (date == null) {
            // Day not listed in property
            button.setText("<html><center>Day " + day + "<br>N/A</center></html>");
            button.setBackground(Color.LIGHT_GRAY);
            button.setForeground(Color.BLACK);
        } else {
            updateButtonDisplay(button, day, date);
        }

        return button;
    }

    /**
     * Updates a day button's display with current price and availability information.
     *
     * @param button the button to update
     * @param day the day number this button represents
     * @param date the Date object containing pricing and availability data
     */
    private void updateButtonDisplay(JButton button, int day, Date date) {
        // Calculate final price including property type multiplier and environmental rate
        double multiplier = property.getType().getMultiplier();
        double finalPrice = date.getFinalPrice(multiplier);
        int percent = (int)(date.getEnvironmentalRate() * 100);

        String status = date.isAvailable() ? "Available" : "BOOKED";

        // Set button text with day number, price, percentage, and status
        button.setText(String.format("<html><center><b>Day %d</b><br>₱%.0f<br>%d%%<br>%s</center></html>",
                day, finalPrice, percent, status));

        // Set text color to black for better visibility
        button.setForeground(Color.BLACK);

        // Determine background color based on selection, booking status, and environmental rate
        if (isInSelectedRange(day)) {
            // Highlight selected dates in light blue
            button.setBackground(new Color(173, 216, 230));
        } else if (!date.isAvailable()) {
            // Booked dates in light red
            button.setBackground(new Color(255, 182, 193));
        } else if (percent < 100) {
            // Reduced environmental impact in light green
            button.setBackground(new Color(144, 238, 144));
        } else if (percent > 100) {
            // Increased environmental impact in light yellow
            button.setBackground(new Color(255, 255, 153));
        } else {
            // Standard rate in white
            button.setBackground(Color.WHITE);
        }
    }

    /**
     * Checks if a given day falls within the currently selected date range.
     *
     * @param day the day to check
     * @return true if the day is in the selected range; false otherwise
     */
    private boolean isInSelectedRange(int day) {
        if (selectedCheckIn < 0 || selectedCheckOut < 0) {
            return false;
        }
        return day >= selectedCheckIn && day < selectedCheckOut;
    }

    /**
     * Sets the selected date range and updates the calendar display.
     * Days within the range will be highlighted in light blue.
     *
     * @param checkIn the check-in day (start of range)
     * @param checkOut the check-out day (end of range, exclusive)
     */
    public void setSelectedRange(int checkIn, int checkOut) {
        this.selectedCheckIn = checkIn;
        this.selectedCheckOut = checkOut;

        // Refresh all day buttons to show the new selection
        refreshAll();
    }

    /**
     * Refreshes the display of all day buttons in the calendar.
     * Called when the selected date range changes.
     */
    private void refreshAll() {
        int day = 1;
        // Iterate through all rows and columns
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