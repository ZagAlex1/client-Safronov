package ru.geekbrains.server.authentication;

public interface AuthenticationService {

    String getUserNameByLoginAndPassword(String login, String password);

    void startAuthentication();
    void endAuthentication();
}
