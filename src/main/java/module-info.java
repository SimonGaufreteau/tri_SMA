module com.example.tri_sma {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;

    opens com.tri_sma to javafx.fxml;
    exports com.tri_sma;
}