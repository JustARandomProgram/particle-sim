import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller {
    public Window window;
    public Input input;
    public Model model;
    private final ScheduledExecutorService master = Executors.newSingleThreadScheduledExecutor();
    private long lastUpdated = System.currentTimeMillis();

    public Controller setWindow(Window newWindow) {
        window = newWindow;
        return this;
    }

    public Controller setInput(Input newInput) {
        input = newInput;
        return this;
    }

    public Controller setModel(Model newModel) {
        model = newModel;
        return this;
    }

    public void init() {
        model.getSettingsMap().put("Constant", 50.0);
        model.getSettingsMap().put("Elasticity", 0.2);
        model.getSettingsMap().put("Terminal Velocity", 100.0);
        model.getSettingsMap().put("Camera Speed", 10.0);

        window.init();
        model.init();
        input.init();

        master.scheduleAtFixedRate(() -> {
            long current = System.currentTimeMillis();
            model.update((current - lastUpdated)/10);
            window.canvas.repaint();
            lastUpdated = current;
        },0,1000/60,TimeUnit.MILLISECONDS);

        window.setVisible(true);
    }
}
