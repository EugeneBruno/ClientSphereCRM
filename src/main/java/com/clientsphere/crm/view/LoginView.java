package com.clientsphere.crm.view;

import com.clientsphere.crm.model.User;
import com.clientsphere.crm.repository.UserRepository;
import com.clientsphere.crm.util.CurrentUser;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView {

    private final BorderPane root;
    private final UserRepository userRepository;

    private TextField emailField;
    private PasswordField passwordField;
    private Label messageLabel;

    public LoginView() {
        root = new BorderPane();
        userRepository = new UserRepository();

        createView();
    }

    private void createView() {

        Label title = new Label("ClientSphere CRM");
        title.setStyle(
                "-fx-font-size: 30px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0F172A;"
        );

        Label subtitle = new Label("Sign in to your account");
        subtitle.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-text-fill: #64748B;"
        );

        Label emailLabel = new Label("Email");
        emailLabel.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #334155;"
        );

        emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setMaxWidth(Double.MAX_VALUE);
        emailField.setPrefHeight(42);

        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #334155;"
        );

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setMaxWidth(Double.MAX_VALUE);
        passwordField.setPrefHeight(42);

        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #DC2626;"
        );

        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(44);
        loginButton.setStyle(
                "-fx-background-color: #2563EB;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );

        Button registerButton = new Button("Create an account");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setPrefHeight(42);
        registerButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: #CBD5E1;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-text-fill: #334155;" +
                        "-fx-font-size: 14px;" +
                        "-fx-cursor: hand;"
        );

        loginButton.setOnAction(event -> handleLogin());

        registerButton.setOnAction(event -> {
            RegisterView registerView = new RegisterView();

            Stage stage = (Stage) root.getScene().getWindow();

            stage.setScene(
                    new Scene(
                            registerView.getRoot(),
                            900,
                            600
                    )
            );

            stage.setTitle("ClientSphere CRM - Create Account");
        });

        VBox form = new VBox(10);
        form.setMaxWidth(420);
        form.setPadding(new Insets(35));
        form.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 14px;" +
                        "-fx-border-color: #E2E8F0;" +
                        "-fx-border-radius: 14px;"
        );

        form.getChildren().addAll(
                title,
                subtitle,
                new VBox(5, emailLabel, emailField),
                new VBox(5, passwordLabel, passwordField),
                messageLabel,
                loginButton,
                registerButton
        );

        VBox container = new VBox(form);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(25));

        root.setCenter(container);

        root.setStyle(
                "-fx-background-color: #F8FAFC;"
        );
    }

    private void handleLogin() {

        String email = emailField.getText().trim();
        String password = passwordField.getText();

        messageLabel.setText("");

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText(
                    "Please enter your email and password."
            );
            return;
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {
            messageLabel.setText(
                    "Invalid email or password."
            );
            return;
        }

        if (!user.isActive()) {
            messageLabel.setText(
                    "This account has been deactivated."
            );
            return;
        }

        if (!user.getPassword().equals(password)) {
            messageLabel.setText(
                    "Invalid email or password."
            );
            return;
        }

        CurrentUser.login(user);

        openMainApplication();
    }

    private void openMainApplication() {

        MainLayout mainLayout = new MainLayout();

        Stage stage = (Stage) root.getScene().getWindow();

        Scene scene = new Scene(
                mainLayout.getRoot(),
                1200,
                750
        );

        stage.setScene(scene);
        stage.setTitle("ClientSphere CRM");
        stage.setMaximized(true);
    }

    public BorderPane getRoot() {
        return root;
    }
}