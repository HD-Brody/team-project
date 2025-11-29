package view;

import interface_adapter.sign_up.SignUpController;
import interface_adapter.sign_up.SignUpState;
import interface_adapter.sign_up.SignUpViewModel;
import view.components.ViewConstants;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class SignUpView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "signup";

    private final SignUpViewModel signUpViewModel;
    private SignUpController signUpController;

    private final JTextField emailField = new JTextField(30);
    private final JTextField nameField = new JTextField(30);
    private final JPasswordField passwordField = new JPasswordField(30);
    private final JLabel emailLabel = new JLabel("Email");
    private final JLabel nameLabel = new JLabel("Nickname");
    private final JLabel passwordLabel = new JLabel("Password");

    private JButton signUpButton;
    private JButton backToWelcomeButton;

    public SignUpView(SignUpViewModel signUpViewModel) {
        this.signUpViewModel = signUpViewModel;
        this.signUpViewModel.addPropertyChangeListener(this);

        // panel setup
        final JPanel padding = new JPanel(); // for better look
        padding.setAlignmentX(CENTER_ALIGNMENT);
        padding.setPreferredSize(new Dimension(30, 0));
        final JPanel emailPanel = new JPanel();
        emailPanel.add(emailLabel);
        emailPanel.add(emailField);
        final JPanel namePanel = new JPanel();
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        final JPanel passwordPanel = new JPanel();
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        final JPanel buttons = new JPanel();

        // label view setup
        emailLabel.setFont(ViewConstants.LABEL_FONT);
        nameLabel.setFont(ViewConstants.LABEL_FONT);
        passwordLabel.setFont(ViewConstants.LABEL_FONT);

        // text field setup
        emailField.setEditable(true);
        emailField.setMaximumSize(ViewConstants.TEXT_FIELD_SIZE);
        emailField.setPreferredSize(ViewConstants.TEXT_FIELD_SIZE);
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setEditable(true);
        passwordField.setMaximumSize(ViewConstants.TEXT_FIELD_SIZE);
        passwordField.setPreferredSize(ViewConstants.TEXT_FIELD_SIZE);
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField.setEditable(true);
        nameField.setMaximumSize(ViewConstants.TEXT_FIELD_SIZE);
        nameField.setPreferredSize(ViewConstants.TEXT_FIELD_SIZE);
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // sign up button setup
        signUpButton = new JButton("Sign Up");
        signUpButton.setAlignmentX(CENTER_ALIGNMENT);
        signUpButton.setBackground(ViewConstants.BLUE);
        signUpButton.setForeground(Color.WHITE);
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final SignUpState state = signUpViewModel.getState();
                signUpController.execute(
                        state.getEmail(),
                        state.getPassword(),
                        state.getName()
                );
            }
        });
        buttons.add(signUpButton);

        // back to welcome button setup
        backToWelcomeButton = new JButton("Back To Welcome");
        backToWelcomeButton.setAlignmentX(CENTER_ALIGNMENT);
        backToWelcomeButton.setBackground(ViewConstants.BLUE);
        backToWelcomeButton.setForeground(Color.WHITE);
        backToWelcomeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signUpController.switchView();
            }
        });
        buttons.add(backToWelcomeButton);

        emailField.getDocument().addDocumentListener(new DocumentListener() {
            private void documentListenerHelper() {
                final SignUpState currentState = signUpViewModel.getState();
                currentState.setEmail(emailField.getText());
                signUpViewModel.setState(currentState);
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
                final SignUpState currentState = signUpViewModel.getState();
                currentState.setPassword(new String(passwordField.getPassword()));
                signUpViewModel.setState(currentState);
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

        nameField.getDocument().addDocumentListener(new DocumentListener() {
            private void documentListenerHelper() {
                final SignUpState currentState = signUpViewModel.getState();
                currentState.setName(nameField.getText());
                signUpViewModel.setState(currentState);
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
        this.add(namePanel);
        this.add(passwordPanel);
        this.add(buttons);
    }

    public String getViewName() {
        return viewName;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final SignUpState state = (SignUpState) evt.getNewValue();
        emailField.setText(state.getEmail());
        nameField.setText(state.getName());
        passwordField.setText(state.getPassword());

        if (!state.getIsSuccess()) {
            JOptionPane.showMessageDialog(null,
                    state.getErrorMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        else {
            JOptionPane.showMessageDialog(null,
                    "Sign up Successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            emailField.setText("");
            nameField.setText("");
            passwordField.setText("");
        }
    }

    public void setSignUpController(SignUpController controller) {
        signUpController = controller;
    }
}
