package view.swing;

import app.AppBuilder;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Standalone launcher for the grade calculation Swing frame.
 */
public final class GradeCalculationMain {

    private GradeCalculationMain() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppBuilder builder = new AppBuilder();
            JFrame frame = builder.buildGradeCalculationFrame();
            frame.setVisible(true);
        });
    }
}
