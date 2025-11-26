package view;

import interface_adapter.welcome.ActionType;
import interface_adapter.welcome.WelcomeController;
import interface_adapter.welcome.WelcomeViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class WelcomeView extends JPanel implements ActionListener, PropertyChangeListener {

    private String viewName = "welcome";

    private WelcomeViewModel welcomeViewModel;
    private WelcomeController welcomeController;

    private JLabel welcomeLabel = new JLabel("Time Til Test");

    private JButton signUpButton;
    private JButton loginButton;

    public WelcomeView(WelcomeViewModel welcomeViewModel) {
        this.welcomeViewModel = welcomeViewModel;
        this.welcomeViewModel.addPropertyChangeListener(this);

        // set padding to look better
        JPanel padding = new JPanel();
        padding.setAlignmentX(CENTER_ALIGNMENT);
        padding.setPreferredSize(new Dimension(500, 0));

        // set label font
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 40));
        welcomeLabel.setAlignmentX(CENTER_ALIGNMENT);

        final JPanel welcomePanel = new JPanel();
        welcomePanel.add(welcomeLabel);
        final JPanel buttons = new JPanel();

        signUpButton = new JButton("Sign Up");
        signUpButton.setAlignmentX(CENTER_ALIGNMENT);
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                welcomeController.execute(ActionType.SIGN_UP);
            }
        });

        loginButton = new JButton("Login");
        loginButton.setAlignmentX(CENTER_ALIGNMENT);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                welcomeController.execute(ActionType.LOGIN);
            }
        });

        buttons.add(signUpButton);
        buttons.add(loginButton);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(padding);
        this.add(welcomePanel);
        this.add(buttons);
    }

    public String getViewName() {
        return viewName;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public void setWelcomeViewController(WelcomeController controller) {
        welcomeController = controller;
    }
}
