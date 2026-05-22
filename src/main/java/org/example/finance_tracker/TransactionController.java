package org.example.finance_tracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class TransactionController implements Initializable {

    @FXML private ComboBox<String> typeCombo;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private TextField amountField;
    @FXML private DatePicker datePicker;
    @FXML private TextField descField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        typeCombo.getItems().addAll("Income", "Expense");
        datePicker.setValue(LocalDate.now()); // Default to today's date

        typeCombo.setOnAction(e -> {
            categoryCombo.getItems().clear();
            if ("Income".equals(typeCombo.getValue())) {
                categoryCombo.getItems().addAll("Salary", "Business", "Gift", "Other");
            } else {
                categoryCombo.getItems().addAll("Rent", "Groceries", "Transport", "Entertainment", "Utilities", "Other");
            }
        });
    }

    @FXML
    public void handleSave(ActionEvent event) {
        String type = typeCombo.getValue();
        String category = categoryCombo.getValue();
        String amountStr = amountField.getText().trim();
        LocalDate date = datePicker.getValue();
        String desc = descField.getText().trim();

        if (type == null || category == null || amountStr.isEmpty() || date == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Data", "Please fill out all required fields.");
            return;
        }

        double amount = 0;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Format Error", "Amount must be a valid number (e.g., 1500.50).");
            return;
        }

        String loggedInUser = UserSession.currentUsername;

        String sql = "INSERT INTO transactions (account_username, transaction_type, category, amount, transaction_date, description) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = Connect.connection(); PreparedStatement pst = con.prepareStatement(sql)) {
            if (con == null) return;

            pst.setString(1, loggedInUser);
            pst.setString(2, type);
            pst.setString(3, category);
            pst.setDouble(4, amount);
            pst.setDate(5, java.sql.Date.valueOf(date));
            pst.setString(6, desc);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success!", "Transaction saved to database.");
                amountField.clear();
                descField.clear();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save transaction.");
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