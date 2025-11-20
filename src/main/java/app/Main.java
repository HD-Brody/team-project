package app;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppBuilder appBuilder = new AppBuilder();
            JFrame application = appBuilder
                    .addSyllabusUploadView()
                    .addSyllabusUploadUseCase()
                    .build();

            application.pack();
            application.setSize(700, 500);
            application.setLocationRelativeTo(null);
            application.setVisible(true);
        });
    }
}