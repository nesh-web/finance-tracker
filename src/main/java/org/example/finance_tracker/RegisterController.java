package org.example.finance_tracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmField;

    @FXML
    public void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim().toLowerCase();
        String password = passwordField.getText();
        String confirm = confirmField.getText();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Data", "Please fill in all fields.");
            return;
        }

        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        if (!password.matches(passwordRegex)) {
            showAlert(Alert.AlertType.ERROR, "Weak Password",
                    "Password must be at least 8 characters long, and include an uppercase letter, a lowercase letter, and a number.");
            return;
        }

        if (!password.equals(confirm)) {
            showAlert(Alert.AlertType.ERROR, "Mismatch", "Passwords do not match.");
            return;
        }

        try (Connection con = Connect.connection()) {
            if (con == null) return;

            String checkSql = "SELECT username FROM users WHERE username = ?";
            try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    showAlert(Alert.AlertType.ERROR, "Username Taken", "That username is already in use.");
                    return;
                }
            }

            String insertSql = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (PreparedStatement insertStmt = con.prepareStatement(insertSql)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password);

                int rows = insertStmt.executeUpdate();
                if (rows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success!", "Account created successfully. You can now log in.");
                    loadLogin(event); // Instantly send them back to the login screen
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to register account.");
        }
    }

    @FXML
    public void loadLogin(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("FinancePro - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }
}