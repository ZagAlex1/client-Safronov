module ru.geekbrains.client {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.geekbrains.client to javafx.fxml;
    exports ru.geekbrains.client;
}