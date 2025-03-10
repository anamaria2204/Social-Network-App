module org.example.retea_socializare {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;

    opens org.example.retea_socializare to javafx.fxml;
    exports org.example.retea_socializare;
}