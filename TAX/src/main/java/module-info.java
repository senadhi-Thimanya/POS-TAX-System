module com.example.tax {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.tax to javafx.fxml;
    exports com.example.tax;
    exports com.example.tax.controllers;
    opens com.example.tax.controllers to javafx.fxml;
}