import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App {
    static Model model;
    static Window window;
    static Input input;
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeLater(() -> {
            window = new Window(900,900,60);
            model = new Model();
            model.settings.put("Constant", 50.0);
            model.settings.put("Elasticity", 0.2);
            model.settings.put("Terminal Velocity", 100.0);
            model.settings.put("Camera Speed", 10.0);
            input = new Input();
            window.setVisible(true);

        });
    }
}
