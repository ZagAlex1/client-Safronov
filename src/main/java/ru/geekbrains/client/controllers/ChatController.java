package ru.geekbrains.client.controllers;


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.geekbrains.client.models.Network;

public class ChatController {
    @FXML
    private ListView<String> userList;

    @FXML
    private Label userNameTitle;

    @FXML
    private TextArea chatHistory;

    @FXML
    private TextField inputField;

    @FXML
    private Button sendButton;

    @FXML
    public void initialize() {
        userList.setItems(FXCollections.observableArrayList("Тимофей", "Дмитрий", "Диана", "Роман"));
        sendButton.setOnAction(event -> sendMessage());
        inputField.setOnAction(event -> sendMessage());
    }

    private Network network;

    public void setNetwork(Network network) {
        this.network = network;
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        inputField.clear();

        if (message.isBlank()) {
            return;
        }

        network.sendMessage(message);

//        appendMessage(message);
    }

    public void appendMessage(String message) {
        chatHistory.appendText(message);
        chatHistory.appendText(System.lineSeparator());
    }


}