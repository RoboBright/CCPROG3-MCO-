package GUI;

import javax.swing.*;
import java.awt.*;
import System.PropertySystem;

/**
 * The main window for the Green Property Exchange System GUI.
 * Contains the main menu with buttons to access Property and Booking sections.
 */
public class MainFrame extends JFrame {

    private PropertySystem system;
    private JPanel mainPanel;

    /**
     * Constructs the MainFrame and initializes the GUI components.
     * It also sets up the main menu for property and booking management.
     */
    public MainFrame() {
        // Initialize core system logic
        system = new PropertySystem();

        // Setup frame properties
        setTitle("Green Property Exchange System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create and set up the main panel container
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));

        // Add header panel at the top
        JPanel headerPanel = createHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Add main menu panel at the center
        JPanel menuPanel = createMenuPanel();
        mainPanel.add(menuPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    /**
     * Creates the header panel with a title label.
     *
     * @return the JPanel containing the header content
     */
    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setBackground(new Color(34, 139, 34));
        header.setPreferredSize(new Dimension(getWidth(), 80));

        JLabel titleLabel = new JLabel("🏡 GREEN PROPERTY EXCHANGE SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        header.add(titleLabel);
        return header;
    }

    /**
     * Creates the main menu panel with buttons for navigating to Property and Booking sections,
     * as well as an option to exit the application.
     *
     * @return the JPanel containing the main menu buttons and welcome message
     */
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // Welcome message label
        JLabel welcomeLabel = new JLabel("Welcome! Please select an option:");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(welcomeLabel, gbc);

        // Button to open the Property Menu dialog
        JButton propertyBtn = createStyledButton("Property Menu", new Color(70, 130, 180));
        propertyBtn.addActionListener(e -> openPropertyMenu());
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(propertyBtn, gbc);

        // Button to open the Booking Menu dialog
        JButton bookingBtn = createStyledButton("Booking Menu", new Color(60, 179, 113));
        bookingBtn.addActionListener(e -> openBookingMenu());
        gbc.gridx = 1;
        panel.add(bookingBtn, gbc);

        // Exit button to close the application
        JButton exitBtn = createStyledButton("Exit", new Color(220, 20, 60));
        exitBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to exit?",
                    "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(exitBtn, gbc);

        return panel;
    }

    /**
     * Creates a JButton with standardized style and hover effects.
     *
     * @param text   the text label for the button
     * @param bgColor the background color of the button (base color for hover effect)
     * @return the styled JButton component
     */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(250, 60));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add a hover color effect on the button
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    /**
     * Opens the Property Menu dialog for managing properties.
     * This method invokes the PropertyMenuGUI dialog and displays it.
     */
    private void openPropertyMenu() {
        PropertyMenuGUI propertyMenu = new PropertyMenuGUI(this, system);
        propertyMenu.setVisible(true);
    }

    /**
     * Opens the Booking Menu dialog for managing reservations.
     * This method invokes the BookingMenuGUI dialog and displays it.
     */
    private void openBookingMenu() {
        BookingMenuGUI bookingMenu = new BookingMenuGUI(this, system);
        bookingMenu.setVisible(true);
    }

    /**
     * The entry point of the application, launching the main GUI window.
     *
     * @param args the command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
