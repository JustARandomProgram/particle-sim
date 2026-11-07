import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App {
    static Controller controller;
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeLater(() -> {
            controller = new Controller()
                .setInput(new Input())
                .setModel(new Model())
                .setWindow(new Window(900,900));
            controller.init();
        });
    }
}
