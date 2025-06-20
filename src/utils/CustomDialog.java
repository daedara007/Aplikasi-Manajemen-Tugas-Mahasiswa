package utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomDialog {
    // Colors matching the application theme
    private static final Color HEADER_COLOR = new Color(150, 50, 200);
    private static final Color BODY_COLOR = new Color(255, 200, 255);
    private static final Color SUCCESS_COLOR = new Color(100, 200, 100);
    private static final Color ERROR_COLOR = new Color(255, 100, 0);
    private static final Color CONFIRM_COLOR = new Color(255, 255, 0);
    private static final Color YES_BUTTON_COLOR = new Color(100, 200, 100);
    private static final Color NO_BUTTON_COLOR = new Color(220, 50, 50);
    private static final Color OK_BUTTON_COLOR = new Color(150, 50, 200);
    private static final Color STATUS_BUTTON_COLOR = new Color(150, 50, 200);
    
    public static void showSuccess(Component parent, String message) {
        showCustomDialog(parent, "Sukses", message, SUCCESS_COLOR, " ", new String[]{"OK"}, YES_BUTTON_COLOR);
    }
    
    public static void showError(Component parent, String message) {
        showCustomDialog(parent, "Kesalahan", message, ERROR_COLOR, "X", new String[]{"OK"}, OK_BUTTON_COLOR);
    }
    
    public static boolean showConfirm(Component parent, String message) {
        int result = showCustomDialog(parent, "Konfirmasi", message, CONFIRM_COLOR, "?", new String[]{"Tidak", "Ya"}, null);
        return result == 1; // Ya button
    }
    
    public static String showStatusDialog(Component parent, String message, String[] statusOptions) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parent);
        
        final String[] result = {null};
        
        // Main panel with rounded corners
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BODY_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(HEADER_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                // Fill bottom part to make it rectangular
                g2.fillRect(0, getHeight() - 15, getWidth(), 15);
                g2.dispose();
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 15));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        // Icon and title
        JLabel iconLabel = new JLabel("?");
        iconLabel.setFont(new Font("Arial", Font.BOLD, 36));
        iconLabel.setForeground(CONFIRM_COLOR);
        
        JLabel titleLabel = new JLabel("Ubah Status");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(CONFIRM_COLOR);
        
        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);
        
        // Message panel
        JPanel messagePanel = new JPanel();
        messagePanel.setOpaque(false);
        messagePanel.setLayout(new BorderLayout());
        messagePanel.setBorder(new EmptyBorder(20, 40, 10, 40));
        
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        
        // Status options panel
        JPanel statusPanel = new JPanel();
        statusPanel.setOpaque(false);
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBorder(new EmptyBorder(10, 40, 20, 40));
        
        for (String status : statusOptions) {
            JButton statusButton = new JButton(status) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    Color bgColor = STATUS_BUTTON_COLOR;
                    if (getModel().isPressed()) {
                        bgColor = bgColor.darker();
                    } else if (getModel().isRollover()) {
                        bgColor = bgColor.brighter();
                    }
                    
                    g2.setColor(bgColor);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    
                    // Draw text
                    g2.setColor(Color.WHITE);
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = (getHeight() + fm.getAscent()) / 2 - 2;
                    g2.drawString(getText(), x, y);
                    
                    g2.dispose();
                }
            };
            
            statusButton.setPreferredSize(new Dimension(300, 40));
            statusButton.setMaximumSize(new Dimension(300, 40));
            statusButton.setFont(new Font("Arial", Font.BOLD, 14));
            statusButton.setFocusPainted(false);
            statusButton.setBorderPainted(false);
            statusButton.setContentAreaFilled(false);
            statusButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            statusButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            statusButton.addActionListener(e -> {
                result[0] = status;
                dialog.dispose();
            });
            
            statusPanel.add(statusButton);
            statusPanel.add(Box.createVerticalStrut(10));
        }
        
        // Cancel button
        JButton cancelButton = new JButton("Batal") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bgColor = NO_BUTTON_COLOR;
                if (getModel().isPressed()) {
                    bgColor = bgColor.darker();
                } else if (getModel().isRollover()) {
                    bgColor = bgColor.brighter();
                }
                
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Draw text
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
        };
        
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setMaximumSize(new Dimension(100, 35));
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setContentAreaFilled(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        statusPanel.add(Box.createVerticalStrut(10));
        statusPanel.add(cancelButton);
        
        // Assemble dialog
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(messagePanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
        
        return result[0];
    }
    
    private static int showCustomDialog(Component parent, String title, String message, Color iconColor, String iconText, String[] buttonTexts, Color singleButtonColor) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), true);
        dialog.setUndecorated(true);
        dialog.setSize(500, 250);
        dialog.setLocationRelativeTo(parent);
        
        // Main panel with rounded corners
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BODY_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(HEADER_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                // Fill bottom part to make it rectangular
                g2.fillRect(0, getHeight() - 15, getWidth(), 15);
                g2.dispose();
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 15));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        // Icon label
        JLabel iconLabel = new JLabel(iconText);
        iconLabel.setFont(new Font("Arial", Font.BOLD, 36));
        iconLabel.setForeground(iconColor);
        
        // Title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(iconColor);
        
        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);
        
        // Message panel
        JPanel messagePanel = new JPanel();
        messagePanel.setOpaque(false);
        messagePanel.setLayout(new BorderLayout());
        messagePanel.setBorder(new EmptyBorder( 0, 40, 0, 40));
        
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setOpaque(false);
        
        final int[] result = {-1};
        
        for (int i = 0; i < buttonTexts.length; i++) {
            final int index = i;
            String buttonText = buttonTexts[i];
            
            Color buttonColor;
            if (buttonTexts.length == 1) {
                buttonColor = singleButtonColor;
            } else {
                buttonColor = buttonText.equals("Ya") ? YES_BUTTON_COLOR : NO_BUTTON_COLOR;
            }
            
            JButton button = new JButton(buttonText) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    Color bgColor = buttonColor;
                    if (getModel().isPressed()) {
                        bgColor = bgColor.darker();
                    } else if (getModel().isRollover()) {
                        bgColor = bgColor.brighter();
                    }
                    
                    g2.setColor(bgColor);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    
                    // Draw text
                    g2.setColor(Color.BLACK);
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = (getHeight() + fm.getAscent()) / 2 - 2;
                    g2.drawString(getText(), x, y);
                    
                    g2.dispose();
                }
            };
            
            button.setPreferredSize(new Dimension(100, 40));
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            button.addActionListener(e -> {
                result[0] = index;
                dialog.dispose();
            });
            
            buttonPanel.add(button);
        }
        
        // Assemble dialog
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(messagePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
        
        return result[0];
    }
}
