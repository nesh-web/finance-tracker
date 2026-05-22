module org.example.finance_tracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens org.example.finance_tracker to javafx.fxml;
    exports org.example.finance_tracker;
}