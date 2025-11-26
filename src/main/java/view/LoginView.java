package view;

import interface_adapter.login.LoginController;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LoginView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "login";

    private final LoginViewModel loginViewModel;
    private LoginController loginController;

    private final JTextField emailField = new JTextField(30);
    private final JPasswordField passwordField = new JPasswordField(30);
    private final JLabel emailLabel = new JLabel("Email");
    private final JLabel passwordLabel = new JLabel("Password");

    private JButton loginButton;
    private JButton backToWelcomeButton;

    public LoginView(LoginViewModel loginViewModel) {
        this.loginViewModel = loginViewModel;
        this.loginViewModel.addPropertyChangeListener(this);

        // panel setup
        final JPanel padding = new JPanel(); // for better look
        padding.setAlignmentX(CENTER_ALIGNMENT);
        padding.setPreferredSize(new Dimension(30, 0));
        final JPanel emailPanel = new JPanel();
        emailPanel.add(emailLabel);
        emailPanel.add(emailField);
        final JPanel passwordPanel = new JPanel();
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        final JPanel buttons = new JPanel();

        // text field set up
        emailField.setEditable(true);
        emailField.setMaximumSize(new Dimension(500, 30));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setEditable(true);
        passwordField.setMaximumSize(new Dimension(500, 30));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // login button set up
        loginButton = new JButton("Login");
        loginButton.setAlignmentX(CENTER_ALIGNMENT);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final LoginState loginState = loginViewModel.getState();

                loginController.execute(
                        loginState.getEmail(),
                        loginState.getPassword()
                );
            }
        });
        buttons.add(loginButton);

        // back to welcome set up
        backToWelcomeButton = new JButton("Back To Welcome");
        backToWelcomeButton.setAlignmentX(CENTER_ALIGNMENT);
        backToWelcomeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginController.switchToWelcomePage("welcome");
            }
        });
        buttons.add(backToWelcomeButton);

        emailField.getDocument().addDocumentListener(new DocumentListener() {
            private void documentListenerHelper() {
                final LoginState currentState = loginViewModel.getState();
                currentState.setEmail(emailField.getText());
                loginViewModel.setState(currentState);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentListenerHelper();
            }
        });

        passwordField.getDocument().addDocumentListener(new DocumentListener() {

            private void documentListenerHelper() {
                final LoginState currentState = loginViewModel.getState();
                currentState.setPassword(new String(passwordField.getPassword()));
                loginViewModel.setState(currentState);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentListenerHelper();
            }
        });

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(padding);
        this.add(emailPanel);
        this.add(passwordPanel);
        this.add(buttons);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final LoginState state = (LoginState) evt.getNewValue();
        emailField.setText(state.getEmail());

        if (!state.getIsSuccess()) {
            JOptionPane.showMessageDialog(null,
                    state.getErrorMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        else {
            emailField.setText("");
            passwordField.setText("");
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }
}
