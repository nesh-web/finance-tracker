package org.example.finance_tracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class HistoryController implements Initializable {

    @FXML private TableView<Transaction> historyTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadHistoryData();
    }

    private void loadHistoryData() {
        String loggedInUser = UserSession.currentUsername;


        ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM transactions WHERE account_username = ? ORDER BY transaction_date DESC";

        try (Connection con = Connect.connection(); PreparedStatement pst = con.prepareStatement(sql)) {
            if (con == null) return;

            pst.setString(1, loggedInUser);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String date = rs.getDate("transaction_date").toString();
                    String type = rs.getString("transaction_type");
                    String category = rs.getString("category");
                    double amount = rs.getDouble("amount");
                    String desc = rs.getString("description");

                    if (desc == null) desc = "";

                    transactionList.add(new Transaction(date, type, category, amount, desc));
                }
            }

            historyTable.setItems(transactionList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleExport(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Transaction History");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("Date,Type,Category,Amount,Description");

                for (Transaction t : historyTable.getItems()) {

                    String safeDesc = t.getDescription() != null ? t.getDescription().replace("\"", "\"\"") : "";

                    writer.printf("%s,%s,%s,%.2f,\"%s\"\n",
                            t.getDate(),
                            t.getType(),
                            t.getCategory(),
                            t.getAmount(),
                            safeDesc
                    );
                }

                showAlert(Alert.AlertType.INFORMATION, "Export Successful", "Your history was safely exported to: " + file.getName());

            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Export Failed", "A system error occurred while trying to save the file.");
            }
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