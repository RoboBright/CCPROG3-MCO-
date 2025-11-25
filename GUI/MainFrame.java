package GUI;

import javax.swing.*;
import java.awt.*;
import System.PropertySystem;
import Menu.MenuHelper;
import java.util.Scanner;

/**
 * The main window for the Green Property Exchange System GUI.
 * Contains the main menu with buttons to access Property and Booking sections.
 */
public class MainFrame extends JFrame {
    
    private PropertySystem system;
    private MenuHelper helper;
    private JPanel mainPanel;
    
    public MainFrame() {
        // Initialize system
        Scanner scanner = new Scanner(System.in);
        system = new PropertySystem();
        helper = new MenuHelper(scanner);
        
        // Setup frame
        setTitle("Green Property Exchange System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create main panel
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        
        // Create header
        JPanel headerPanel = createHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create menu panel
        JPanel menuPanel = createMenuPanel();
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
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
    
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome! Please select an option:");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(welcomeLabel, gbc);
        
        // Property Menu Button
        JButton propertyBtn = createStyledButton("Property Menu", new Color(70, 130, 180));
        propertyBtn.addActionListener(e -> openPropertyMenu());
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(propertyBtn, gbc);
        
        // Booking Menu Button
        JButton bookingBtn = createStyledButton("Booking Menu", new Color(60, 179, 113));
        bookingBtn.addActionListener(e -> openBookingMenu());
        gbc.gridx = 1;
        panel.add(bookingBtn, gbc);
        
        // Exit Button
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
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(250, 60));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
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
    
    private void openPropertyMenu() {
        PropertyMenuGUI propertyMenu = new PropertyMenuGUI(this, system, helper);
        propertyMenu.setVisible(true);
    }
    
    private void openBookingMenu() {
        BookingMenuGUI bookingMenu = new BookingMenuGUI(this, system, helper);
        bookingMenu.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
