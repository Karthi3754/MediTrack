import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;
import org.json.*;

public class MedicineManagementSystem extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JTextField usernameField, passwordField, nameField, quantityField, priceField, searchField, chatInputField;
    private JTable commonMedicineTable;
    private DefaultTableModel commonTableModel;
    private TableRowSorter<TableModel> commonTableSorter;
    private JTextArea chatOutputArea;
    private JButton backButton;
    private Font labelFont = new Font("Arial", Font.BOLD, 16);
    private Font buttonFont = new Font("Arial", Font.BOLD, 14);
    private Color buttonColor = new Color(0, 102, 204);
    private Image backgroundImage;

    public MedicineManagementSystem() {
        setTitle("Medicine Management System");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        backgroundImage = new ImageIcon("bg.jpg").getImage(); // Ensure the image exists

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        add(cardPanel);

        initCommonTable();

        createLoginPage();
        createAdminHomePage();
        createAddMedicinePage();
        createDeleteMedicinePage();
        createUserPage();
        createUserAvailabilityPage();

        cardLayout.show(cardPanel, "login");

        setVisible(true);
    }

    private void initCommonTable() {
        String[] columnNames = { "Name", "Quantity", "Price" };
        commonTableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        commonMedicineTable = new JTable(commonTableModel);
        commonTableSorter = new TableRowSorter<>(commonTableModel);
        commonMedicineTable.setRowSorter(commonTableSorter);
    }

    private void createLoginPage() {
        JPanel loginPanel = new BackgroundPanel(backgroundImage);
        loginPanel.setLayout(new GridBagLayout());

        JLabel userLabel = createStyledLabel("Username:");
        JLabel passLabel = createStyledLabel("Password:");

        usernameField = createStyledTextField(15);
        passwordField = createStyledTextField(15);

        JButton loginButton = createStyledButton("Admin Login");
        JButton userButton = createStyledButton("Continue as User");

        loginButton.addActionListener(e -> {
            String user = usernameField.getText();
            String pass = passwordField.getText();
            if (user.equals("admin") && pass.equals("admin123")) {
                cardLayout.show(cardPanel, "adminHome");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid admin credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        userButton.addActionListener(e -> cardLayout.show(cardPanel, "user"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0;
        loginPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        loginPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginPanel.add(loginButton, gbc);

        gbc.gridy = 3;
        loginPanel.add(userButton, gbc);

        cardPanel.add(loginPanel, "login");
    }

    private void createAdminHomePage() {
        JPanel adminPanel = new BackgroundPanel(backgroundImage);
        adminPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setForeground(new Color(0, 102, 204));

        JButton addButton = createStyledButton("Add Medicine");
        JButton deleteButton = createStyledButton("Delete Medicine");
        JButton logoutButton = createStyledButton("Logout");

        addButton.addActionListener(e -> cardLayout.show(cardPanel, "addMedicine"));
        deleteButton.addActionListener(e -> cardLayout.show(cardPanel, "deleteMedicine"));
        logoutButton.addActionListener(e -> cardLayout.show(cardPanel, "login"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(logoutButton);

        adminPanel.add(titleLabel, BorderLayout.NORTH);
        adminPanel.add(buttonPanel, BorderLayout.CENTER);

        cardPanel.add(adminPanel, "adminHome");
    }
    private void createAddMedicinePage() {
        JPanel addPanel = new BackgroundPanel(backgroundImage);
        addPanel.setLayout(new GridBagLayout());

        JLabel nameLabel = createStyledLabel("Medicine Name:");
        JLabel quantityLabel = createStyledLabel("Quantity:");
        JLabel priceLabel = createStyledLabel("Price:");

        nameField = createStyledTextField(15);
        quantityField = createStyledTextField(15);
        priceField = createStyledTextField(15);

        JButton addButton = createStyledButton("Add");
        JButton backButton = createStyledButton("Back");

        addButton.addActionListener(e -> addMedicine());
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "adminHome"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        addPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        addPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        addPanel.add(quantityLabel, gbc);
        gbc.gridx = 1;
        addPanel.add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        addPanel.add(priceLabel, gbc);
        gbc.gridx = 1;
        addPanel.add(priceField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        addPanel.add(addButton, gbc);

        gbc.gridy = 4;
        addPanel.add(backButton, gbc);

        cardPanel.add(addPanel, "addMedicine");
    }

    private void createDeleteMedicinePage() {
        JPanel deletePanel = new BackgroundPanel(backgroundImage);
        deletePanel.setLayout(new GridBagLayout());

        JLabel nameLabel = createStyledLabel("Medicine Name:");
        JLabel quantityLabel = createStyledLabel("Quantity to Delete:");

        JTextField delNameField = createStyledTextField(15);
        JTextField delQuantityField = createStyledTextField(15);

        JButton deleteButton = createStyledButton("Delete");
        JButton backButton = createStyledButton("Back");

        deleteButton.addActionListener(e -> {
            String name = delNameField.getText();
            int quantity;
            try {
                quantity = Integer.parseInt(delQuantityField.getText());
                deleteMedicine(name, quantity);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity entered.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> cardLayout.show(cardPanel, "adminHome"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        deletePanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        deletePanel.add(delNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        deletePanel.add(quantityLabel, gbc);
        gbc.gridx = 1;
        deletePanel.add(delQuantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        deletePanel.add(deleteButton, gbc);

        gbc.gridy = 3;
        deletePanel.add(backButton, gbc);

        cardPanel.add(deletePanel, "deleteMedicine");
    }

    private void createUserPage() {
        JPanel userPanel = new BackgroundPanel(backgroundImage);
        userPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Welcome User");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setForeground(new Color(0, 102, 204));

        JButton availabilityButton = createStyledButton("Check Medicine Availability");
        JButton chatButton = createStyledButton("Ask Chatbot");
        JButton backButton = createStyledButton("Back");

        availabilityButton.addActionListener(e -> {
            loadMedicines();
            cardLayout.show(cardPanel, "userAvailability");
        });

        chatButton.addActionListener(e -> {
            chatInputField.setText("");
            chatOutputArea.setText("");
            cardLayout.show(cardPanel, "chatbot");
        });

        backButton.addActionListener(e -> cardLayout.show(cardPanel, "login"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(availabilityButton);
        buttonPanel.add(chatButton);
        buttonPanel.add(backButton);

        userPanel.add(titleLabel, BorderLayout.NORTH);
        userPanel.add(buttonPanel, BorderLayout.CENTER);

        cardPanel.add(userPanel, "user");
        createChatbotPage(); // called here to ensure fields are initialized
    }

    private void createChatbotPage() {
        JPanel chatbotPanel = new BackgroundPanel(backgroundImage);
        chatbotPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Ask your Medicine Chatbot");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setForeground(new Color(0, 102, 204));

        chatOutputArea = new JTextArea(10, 40);
        chatOutputArea.setEditable(false);
        chatOutputArea.setLineWrap(true);
        chatOutputArea.setWrapStyleWord(true);
        JScrollPane outputScroll = new JScrollPane(chatOutputArea);

        chatInputField = new JTextField(30);
        JButton sendButton = createStyledButton("Send");
        JButton backButton = createStyledButton("Back");

        sendButton.addActionListener(e -> {
            String userText = chatInputField.getText().trim();
            if (!userText.isEmpty()) {
                chatOutputArea.append("You: " + userText + "\n");
                String response = getBotResponse(userText);
                chatOutputArea.append("Bot: " + response + "\n\n");
                chatInputField.setText("");
            }
        });

        backButton.addActionListener(e -> cardLayout.show(cardPanel, "user"));

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setOpaque(false);
        inputPanel.add(chatInputField);
        inputPanel.add(sendButton);
        inputPanel.add(backButton);

        chatbotPanel.add(titleLabel, BorderLayout.NORTH);
        chatbotPanel.add(outputScroll, BorderLayout.CENTER);
        chatbotPanel.add(inputPanel, BorderLayout.SOUTH);

        cardPanel.add(chatbotPanel, "chatbot");
    }

    private String getBotResponse(String prompt) {
        String apiKey = "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"; // Replace with your API key

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        JSONObject messageObj = new JSONObject();
        messageObj.put("role", "user");
        messageObj.put("content", prompt);

        JSONObject body = new JSONObject();
        body.put("model", "gpt-3.5-turbo");
        body.put("messages", new JSONArray().put(messageObj));

        Request request = new Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .post(RequestBody.create(body.toString(), mediaType))
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("Content-Type", "application/json")
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
            System.out.println("HTTP Error: " + response.code());
            System.out.println("Error body: " + response.body().string());
            return "Error: Unable to connect to the chatbot service. Please try again later.";
        }

            JSONObject json = new JSONObject(response.body().string());
            JSONArray choices = json.getJSONArray("choices");

            if (choices.length() > 0) {
                return choices.getJSONObject(0).getJSONObject("message").getString("content").trim();
            } else {
                return "Sorry, I couldn't understand your question.";
            }
        } catch (IOException e) {
            return "There was a problem contacting the chatbot service. Please try again later.";
        }
    }

    private void createUserAvailabilityPage() {
        JPanel availabilityPanel = new BackgroundPanel(backgroundImage);
        availabilityPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Medicine Availability (User View)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setForeground(new Color(0, 102, 204));

        searchField = createStyledTextField(20);
        JLabel searchLabel = createStyledLabel("Search Medicine:");
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setOpaque(false);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        JScrollPane scrollPane = new JScrollPane(commonMedicineTable);

        backButton = createStyledButton("Back");
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "user"));

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { search(); }
            public void removeUpdate(DocumentEvent e) { search(); }
            public void insertUpdate(DocumentEvent e) { search(); }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);

        availabilityPanel.add(titleLabel, BorderLayout.NORTH);
        availabilityPanel.add(searchPanel, BorderLayout.NORTH);
        availabilityPanel.add(scrollPane, BorderLayout.CENTER);
        availabilityPanel.add(buttonPanel, BorderLayout.SOUTH);

        cardPanel.add(availabilityPanel, "userAvailability");
    }

    private void addMedicine() {
        String name = nameField.getText();
        int quantity;
        double price;

        try {
            quantity = Integer.parseInt(quantityField.getText());
            price = Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for quantity and price.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO Medicines (name, quantity, price) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setInt(2, quantity);
            stmt.setDouble(3, price);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Medicine added successfully.");
            loadMedicines();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding medicine: " + ex.getMessage());
        }
    }

    private void deleteMedicine(String name, int quantity) {
        try (Connection conn = DBConnection.getConnection()) {
            String checkQuery = "SELECT quantity FROM Medicines WHERE name = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, name);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int currentQuantity = rs.getInt("quantity");
                if (currentQuantity >= quantity) {
                    String updateQuery = "UPDATE Medicines SET quantity = quantity - ? WHERE name = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setInt(1, quantity);
                    updateStmt.setString(2, name);
                    int rowsUpdated = updateStmt.executeUpdate();

                    if (rowsUpdated > 0) {
                        if (currentQuantity - quantity == 0) {
                            String deleteQuery = "DELETE FROM Medicines WHERE name = ?";
                            PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
                            deleteStmt.setString(1, name);
                            deleteStmt.executeUpdate();
                            JOptionPane.showMessageDialog(this, "Medicine deleted as quantity became 0.");
                        } else {
                            JOptionPane.showMessageDialog(this, "Medicine quantity updated successfully.");
                        }
                        loadMedicines();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Not enough quantity available.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Medicine not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating medicine quantity: " + ex.getMessage());
        }
    }

    private void loadMedicines() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM Medicines ORDER BY name";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            commonTableModel.setRowCount(0);
            while (rs.next()) {
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                commonTableModel.addRow(new Object[]{name, quantity, price});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading medicines: " + ex.getMessage());
        }
    }

    private void search() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            commonTableSorter.setRowFilter(null);
        } else {
            commonTableSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(buttonFont);
        button.setBackground(buttonColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(labelFont);
        return label;
    }

    private JTextField createStyledTextField(int columns) {
        JTextField textField = new JTextField(columns);
        styleTextField(textField);
        return textField;
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            textField.getBorder(),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    private class EnterKeyListener extends KeyAdapter {
        private final JComponent nextComponent;

        EnterKeyListener(JComponent nextComponent) {
            this.nextComponent = nextComponent;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                nextComponent.requestFocusInWindow();
            }
        }
    }

    class BackgroundPanel extends JPanel {
        private final Image backgroundImage;

        public BackgroundPanel(Image backgroundImage) {
            this.backgroundImage = backgroundImage;
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MedicineManagementSystem::new);
    }
}