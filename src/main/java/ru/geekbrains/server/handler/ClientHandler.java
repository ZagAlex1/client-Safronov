package ru.geekbrains.server.handler;

import ru.geekbrains.server.MyServer;
import ru.geekbrains.server.authentication.AuthenticationService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    private static final String AUTHOK_CMD_PREFIX = "/authok"; // + username
    private static final String AUTHERR_CMD_PREFIX = "/autherr"; // + error message
    private static final String CLIENT_MSG_CMD_PREFIX = "/cm"; // + msg
    private static final String SERVER_MSG_CMD_PREFIX = "/sm"; // + smsg
    private static final String PRIVATE_MSG_CMD_PREFIX = "/pm"; // + client + pmsg
    private static final String STOP_SERVER_MSG_CMD_PREFIX = "/stop";
    private static final String END_CLIENT_MSG_CMD_PREFIX = "/end";

    private MyServer myServer;
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private String username;

    public ClientHandler(MyServer myServer, Socket socket) {

        this.myServer = myServer;
        this.clientSocket = socket;
    }

    public void handle() throws IOException {
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());

        new Thread(() -> {
            try {
                authentication();
                readMessage();
            } catch (IOException e) {
                e.printStackTrace();
                myServer.unSubscribe(this);
            }
        }).start();
    }

    private void authentication() throws IOException {
        while (true) {
            String message = in.readUTF();
            if (message.startsWith(AUTH_CMD_PREFIX)) {
                boolean isSuccessAuth = processAuthentication(message);
                if (isSuccessAuth) {
                    break;
                }
            } else {
                out.writeUTF(AUTHERR_CMD_PREFIX + " ???????????? ????????????????????????????");
                System.out.println("?????????????????? ?????????????? ????????????????????????????");
            }
        }
    }

    private boolean processAuthentication(String message) throws IOException {
        String[] parts = message.split("\\s+");
        if (parts.length != 3) {
            out.writeUTF(AUTHERR_CMD_PREFIX + " ???????????? ????????????????????????????");
        }
        String login = parts[1];
        String password = parts[2];

        AuthenticationService auth = myServer.getAuthenticationService();

        username = auth.getUserNameByLoginAndPassword(login, password);

        if (username != null) {

            if (myServer.isUsernameBusy(username)) {
                out.writeUTF(AUTHERR_CMD_PREFIX + " ?????????? ?????? ????????????????????????");
                return false;
            }

            out.writeUTF(AUTHOK_CMD_PREFIX + " " + username);
            myServer.subscribe(this);
            System.out.println("???????????????????????? " + username + " ?????????????????????? ?? ???????? ");
            myServer.broadcastMessage(String.format(">>> %s ?????????????????????????? ?? ????????", username), this, true);
            return true;

        } else {
            out.writeUTF(AUTHERR_CMD_PREFIX + " ?????????? ?????? ???????????? ???? ?????????????????????????? ????????????????????????????????");

            return false;
        }
    }

    private void readMessage() throws IOException {
        while (true) {
            String message = in.readUTF();
            System.out.println("message | " + username + ": " + message);
            if (message.startsWith(STOP_SERVER_MSG_CMD_PREFIX)) {
                System.exit(1);
            } else if (message.startsWith(END_CLIENT_MSG_CMD_PREFIX)) {
                return;
            } else if (message.startsWith(PRIVATE_MSG_CMD_PREFIX)) {
                String[] parts = message.split("\\s+", 3);
                String recipient = parts[1];
                String privateMessage = parts[2];

                myServer.sendPrivateMessage(this, recipient, privateMessage);
            } else {
                myServer.broadcastMessage(message, this);

            }

        }
    }

    public void sendMessage(String sender, String message) throws IOException {
        if (sender != null) {
            out.writeUTF(String.format("%s %s %s", CLIENT_MSG_CMD_PREFIX, sender, message));
        } else {
            out.writeUTF(String.format("%s %s", SERVER_MSG_CMD_PREFIX, message));
        }
    }


    public String getUsername() {
        return username;
    }

}
