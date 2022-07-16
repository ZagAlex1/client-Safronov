module ru.geekbrains.client {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.geekbrains.client to javafx.fxml;
    exports ru.geekbrains.client;
    exports ru.geekbrains.client.controllers;
    opens ru.geekbrains.client.controllers to javafx.fxml;
}