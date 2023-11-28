// Eithar Alhazmi
// 2105616
package loginframe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class LoginFrame extends JFrame implements ActionListener {

    // Container for GUI components
    Container container = getContentPane();

    // Labels and input fields
    JLabel titleLabel = new JLabel("Login");
    JLabel userLabel = new JLabel("USERNAME");
    JLabel passwordLabel = new JLabel("PASSWORD");
    JTextField userTextField = new JTextField();
    JPasswordField passwordField = new JPasswordField();

    // Buttons and checkbox
    JButton loginButton = new JButton("LOGIN");
    JCheckBox showPassword = new JCheckBox("Show Password");
    JButton registerButton1 = new JButton("REGISTER");

    LoginFrame() {
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        addActionEvent();
    }

    public void setLayoutManager() {
        // Use null layout for precise component positioning
        container.setLayout(null);
    }

    public void setLocationAndSize() {
        // Set the positions and sizes of components
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
        titleLabel.setBounds(150, 60, 120, 30);
        titleLabel.setForeground(Color.RED);
        userLabel.setBounds(50, 150, 100, 30);
        passwordLabel.setBounds(50, 220, 100, 30);
        userTextField.setBounds(150, 150, 150, 30);
        passwordField.setBounds(150, 220, 150, 30);
        showPassword.setBounds(150, 250, 150, 30);
        loginButton.setBounds(50, 300, 100, 30);
        registerButton1.setBounds(200, 300, 100, 30);
    }

    public void addComponentsToContainer() {
        // Add components to the container
        container.add(titleLabel);
        container.add(userLabel);
        container.add(passwordLabel);
        container.add(userTextField);
        container.add(passwordField);
        container.add(showPassword);
        container.add(loginButton);
        container.add(registerButton1);
    }

    public void addActionEvent() {
        // Attach action listeners to buttons and checkbox
        loginButton.addActionListener(this);
        showPassword.addActionListener(this);
        registerButton1.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            // Handle login button click
            String username = userTextField.getText();
            String password = new String(passwordField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                // Show an error message for empty fields
                JOptionPane.showMessageDialog(this, "Please enter a username and password.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (isUserValid(username, password)) {
                // If login is successful, show a success message and open the chat client
                JOptionPane.showMessageDialog(this, "Login Successful");
                openChatClient();
            } else {
                // Show an error message for invalid credentials
                JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == showPassword) {
            // Toggle password visibility
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('*');
            }
        } else if (e.getSource() == registerButton1) {
            // Handle registration button click
            RegistrationFrame registrationFrame = new RegistrationFrame(this);
            registrationFrame.setTitle("Registration Form");
            registrationFrame.setVisible(true);
            registrationFrame.setBounds(10, 5, 400, 500);
            registrationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            registrationFrame.setResizable(true);
        }
    }

    public void openChatClient() {
        // Open the chat client
        ITClinet chatClient = new ITClinet();
        this.dispose(); // Close the login frame
    }

    public boolean isUserValid(String username, String password) {
        // Check if the user credentials are valid
        JSONArray users = readUserData();
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            if (user.getString("username").equals(username) && user.getString("password").equals(password)) {
                return true;
            }
        }
        return false;
    }

    public JSONArray readUserData() {
        // Read user data from a JSON file
        JSONArray users = new JSONArray();
        try {
            File file = new File("users.json");
            if (file.exists()) {
                FileReader reader = new FileReader(file);
                JSONTokener jsonTokener = new JSONTokener(reader);
                users = new JSONArray(jsonTokener);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void writeUserData(JSONArray users) {
        // Write user data to a JSON file
        try {
            FileWriter writer = new FileWriter("users.json");
            writer.write(users.toString(4)); // Use 4 as an argument to pretty-print JSON
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] a) {
        // Create and display the login frame
        LoginFrame frame = new LoginFrame();
        frame.setTitle("Login Form");
        frame.setVisible(true);
        frame.setBounds(10, 5, 400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
    }
}

class RegistrationFrame extends JFrame implements ActionListener {

    private final LoginFrame loginFrame; // Reference to the LoginFrame instance

    Container container = getContentPane();
    JLabel titleLabel = new JLabel("Register");
    JLabel userLabel = new JLabel("USERNAME");
    JLabel passwordLabel = new JLabel("PASSWORD");
    JTextField userTextField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton registerButton2 = new JButton("REGISTER");

    public RegistrationFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame; // Initialize the reference to the LoginFrame
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        registerButton2.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton2) {
            // Handle registration button click
            String username = userTextField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                // Show an error message for empty fields
                JOptionPane.showMessageDialog(this, "Please enter a username and password.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (isUsernameTaken(username)) {
                // Show an error message if the username is already taken
                JOptionPane.showMessageDialog(this, "Username is already taken.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!isPasswordValid(password)) {
                // Show an error message for an invalid password
                String errorMessage = "<html> <center> Password does not meet the complexity requirements. <br>" + 
                         "It should contain at least 8 characters <br> "
                        + "including uppercase, lowercase, and a digit. <br> <br> "
                        + "Please try again. </center> </html>";
                JOptionPane.showMessageDialog(this,errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // Read existing user data from the LoginFrame
                JSONArray users = loginFrame.readUserData();

                // Create a new user and save it to the JSON file
                JSONObject newUser = new JSONObject();
                newUser.put("username", username);
                newUser.put("password", password);
                // Add the new user to the array
                users.put(newUser);
                // Write the updated user data back to the JSON file
                loginFrame.writeUserData(users);

                // Show a success message and close the registration frame
                JOptionPane.showMessageDialog(this, "Registration Successful");
                this.dispose();
            }
        }
    }

    private boolean isPasswordValid(String password) {
        // Check if the password meets complexity requirements
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        return password.matches(regex);
    }

    private boolean isUsernameTaken(String username) {
        // Check if the username is already in use
        JSONArray users = loginFrame.readUserData();
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            if (user.getString("username").equals(username)) {
                return true;
            }
        }
        return false;
    }

    public void setLayoutManager() {
        // Use null layout for precise component positioning
        container.setLayout(null);
    }

    public void setLocationAndSize() {
        // Set the positions and sizes of components
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
        titleLabel.setBounds(150, 60, 120, 30);
        titleLabel.setForeground(Color.RED);
        userLabel.setBounds(50, 150, 100, 30);
        passwordLabel.setBounds(50, 220, 100, 30);
        userTextField.setBounds(150, 150, 150, 30);
        passwordField.setBounds(150, 220, 150, 30);
        registerButton2.setBounds(125, 300, 100, 30);
    }

    public void addComponentsToContainer() {
        // Add components to the container
        container.add(titleLabel);
        container.add(userLabel);
        container.add(passwordLabel);
        container.add(userTextField);
        container.add(passwordField);
        container.add(registerButton2);
    }
}
