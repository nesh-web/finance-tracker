package org.example.finance_tracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private BorderPane mainPane;
    @FXML private Label balanceLabel;
    @FXML private PieChart expenseChart;
    @FXML private Button btnDashboard;
    @FXML private Button btnAddTransaction;
    @FXML private Button btnHistory;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadRealDatabaseData();
    }

    private void loadRealDatabaseData() {
        String loggedInUser = UserSession.currentUsername;


        double totalBalance = 0.0;
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        String balanceSql = "SELECT SUM(CASE WHEN transaction_type = 'Income' THEN amount ELSE -amount END) AS total_balance FROM transactions WHERE account_username = ?";

        String pieSql = "SELECT category, SUM(amount) AS category_total FROM transactions WHERE transaction_type = 'Expense' AND account_username = ? GROUP BY category";

        try (Connection con = Connect.connection()) {
            if (con == null) return;

            try (PreparedStatement pst1 = con.prepareStatement(balanceSql)) {
                pst1.setString(1, loggedInUser);
                try (ResultSet rs1 = pst1.executeQuery()) {
                    if (rs1.next()) {
                        totalBalance = rs1.getDouble("total_balance");
                        balanceLabel.setText(String.format("Ksh %,.2f", totalBalance));
                    }
                }
            }

            try (PreparedStatement pst2 = con.prepareStatement(pieSql)) {
                pst2.setString(1, loggedInUser);
                try (ResultSet rs2 = pst2.executeQuery()) {
                    while (rs2.next()) {
                        String category = rs2.getString("category");
                        double amount = rs2.getDouble("category_total");
                        pieChartData.add(new PieChart.Data(category, amount));
                    }
                }
            }

            expenseChart.setData(pieChartData);

        } catch (SQLException e) {
            e.printStackTrace();
            balanceLabel.setText("Database Error");
        }
    }

    @FXML
    public void loadAddTransaction(ActionEvent event) {
        try {
            btnDashboard.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-alignment: BASELINE_LEFT; -fx-padding: 10 15; -fx-cursor: hand;");
            btnAddTransaction.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-alignment: BASELINE_LEFT; -fx-padding: 10 15; -fx-cursor: hand;");

            Parent root = FXMLLoader.load(getClass().getResource("Transaction.fxml"));
            mainPane.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadDashboardView(ActionEvent event) {
        try {
            javafx.stage.Stage stage = (javafx.stage.Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadHistoryView(ActionEvent event) {
        try {
            btnDashboard.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-alignment: BASELINE_LEFT; -fx-padding: 10 15; -fx-cursor: hand;");
            btnAddTransaction.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-alignment: BASELINE_LEFT; -fx-padding: 10 15; -fx-cursor: hand;");
            btnHistory.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-alignment: BASELINE_LEFT; -fx-padding: 10 15; -fx-cursor: hand;");

            Parent root = FXMLLoader.load(getClass().getResource("History.fxml"));
            mainPane.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        UserSession.currentUsername = null;

        try {
            javafx.stage.Stage stage = (javafx.stage.Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
            stage.setTitle("FinancePro - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public BorderPane getMainPane() {
        return mainPane;
    }

    public void setMainPane(BorderPane mainPane) {
        this.mainPane = mainPane;
    }
}