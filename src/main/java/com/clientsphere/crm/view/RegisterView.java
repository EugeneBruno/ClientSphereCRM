package com.clientsphere.crm.view;

import com.clientsphere.crm.model.User;
import com.clientsphere.crm.repository.UserRepository;

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

public class RegisterView {

    private final BorderPane root;
    private final UserRepository userRepository;

    private TextField firstNameField;
    private TextField lastNameField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private Label messageLabel;

    public RegisterView() {
        root = new BorderPane();
        userRepository = new UserRepository();

        createView();
    }

    private void createView() {

        Label title = new Label("Create Account");
        title.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0F172A;"
        );

        Label subtitle = new Label(
                "Create your ClientSphere CRM account"
        );
        subtitle.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-text-fill: #64748B;"
        );

        Label firstNameLabel = createFieldLabel("First Name");
        firstNameField = createTextField(
                "Enter your first name"
        );

        Label lastNameLabel = createFieldLabel("Last Name");
        lastNameField = createTextField(
                "Enter your last name"
        );

        Label emailLabel = createFieldLabel("Email");
        emailField = createTextField(
                "Enter your email"
        );

        Label passwordLabel = createFieldLabel("Password");
        passwordField = new PasswordField();
        passwordField.setPromptText("Create a password");
        passwordField.setPrefHeight(42);
        passwordField.setMaxWidth(Double.MAX_VALUE);

        Label confirmPasswordLabel =
                createFieldLabel("Confirm Password");

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText(
                "Confirm your password"
        );
        confirmPasswordField.setPrefHeight(42);
        confirmPasswordField.setMaxWidth(Double.MAX_VALUE);

        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #DC2626;"
        );

        Button createAccountButton =
                new Button("Create Account");

        createAccountButton.setMaxWidth(
                Double.MAX_VALUE
        );

        createAccountButton.setPrefHeight(44);

        createAccountButton.setStyle(
                "-fx-background-color: #2563EB;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );

        Button backToLoginButton =
                new Button("Back to Login");

        backToLoginButton.setMaxWidth(
                Double.MAX_VALUE
        );

        backToLoginButton.setPrefHeight(42);

        backToLoginButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: #CBD5E1;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-text-fill: #334155;" +
                        "-fx-font-size: 14px;" +
                        "-fx-cursor: hand;"
        );

        createAccountButton.setOnAction(
                event -> handleRegistration()
        );

        backToLoginButton.setOnAction(
                event -> openLogin()
        );

        VBox form = new VBox(9);

        form.setMaxWidth(450);
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

                new VBox(
                        5,
                        firstNameLabel,
                        firstNameField
                ),

                new VBox(
                        5,
                        lastNameLabel,
                        lastNameField
                ),

                new VBox(
                        5,
                        emailLabel,
                        emailField
                ),

                new VBox(
                        5,
                        passwordLabel,
                        passwordField
                ),

                new VBox(
                        5,
                        confirmPasswordLabel,
                        confirmPasswordField
                ),

                messageLabel,
                createAccountButton,
                backToLoginButton
        );

        VBox container = new VBox(form);

        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(25));

        root.setCenter(container);

        root.setStyle(
                "-fx-background-color: #F8FAFC;"
        );
    }

    private Label createFieldLabel(String text) {

        Label label = new Label(text);

        label.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #334155;"
        );

        return label;
    }

    private TextField createTextField(String prompt) {

        TextField field = new TextField();

        field.setPromptText(prompt);
        field.setPrefHeight(42);
        field.setMaxWidth(Double.MAX_VALUE);

        return field;
    }

    private void handleRegistration() {

        String firstName =
                firstNameField.getText().trim();

        String lastName =
                lastNameField.getText().trim();

        String email =
                emailField.getText().trim();

        String password =
                passwordField.getText();

        String confirmPassword =
                confirmPasswordField.getText();

        messageLabel.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #DC2626;"
        );

        messageLabel.setText("");

        if (
                firstName.isEmpty() ||
                        lastName.isEmpty() ||
                        email.isEmpty() ||
                        password.isEmpty() ||
                        confirmPassword.isEmpty()
        ) {
            messageLabel.setText(
                    "Please fill in all fields."
            );
            return;
        }

        if (!email.contains("@")) {
            messageLabel.setText(
                    "Please enter a valid email address."
            );
            return;
        }

        if (password.length() < 6) {
            messageLabel.setText(
                    "Password must be at least 6 characters."
            );
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText(
                    "Passwords do not match."
            );
            return;
        }

        if (userRepository.emailExists(email)) {
            messageLabel.setText(
                    "An account with this email already exists."
            );
            return;
        }

        /*
         * Every account created through normal registration
         * automatically becomes a USER.
         *
         * Admin accounts will be created separately.
         */
        User user = new User(
                firstName,
                lastName,
                email,
                password
        );

        userRepository.create(user);

        messageLabel.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #16A34A;"
        );

        messageLabel.setText(
                "Account created successfully. " +
                        "You can now log in."
        );

        clearFields();
    }

    private void clearFields() {

        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    private void openLogin() {

        LoginView loginView = new LoginView();

        Stage stage =
                (Stage) root.getScene().getWindow();

        stage.setScene(
                new Scene(
                        loginView.getRoot(),
                        900,
                        600
                )
        );

        stage.setTitle(
                "ClientSphere CRM - Login"
        );
    }

    public BorderPane getRoot() {
        return root;
    }
}