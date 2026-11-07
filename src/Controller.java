import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

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
        model.getSettingsMap().put("Constant", 500.0);
        model.getSettingsMap().put("Elasticity", 0.2);
        model.getSettingsMap().put("Terminal Velocity", 1000.0);
        model.getSettingsMap().put("Camera Speed", 10.0);

        window.init();
        model.init();
        input.init(window.canvas.getInputMap(), window.canvas.getActionMap());
        bindListeners();
        bindActions();

        master.scheduleAtFixedRate(() -> {
            long current = System.currentTimeMillis();
            model.update((current - lastUpdated)/10);
            window.canvas.repaint();
            lastUpdated = current;
        },0,1000/60,TimeUnit.MILLISECONDS);

        window.setVisible(true);
    }

    public void bindListeners() {
        window.canvas.addMouseListener(input);
        window.canvas.addMouseMotionListener(input);
        window.canvas.addMouseWheelListener(input);
    }

    public void bindActions() {
        // ACTIONS
        AbstractAction destroyAllAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.destroyAllParticles();
            }
        };
        AbstractAction returnAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.getCameraPos().x = -window.getWidth()/2;
                model.getCameraPos().y = -window.getHeight()/2;
            }
        };
        AbstractAction startLeftAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                input.movementMap().put(KeyEvent.VK_A, true);
            }
        };
        AbstractAction startDownAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                input.movementMap().put(KeyEvent.VK_S, true);
            }
        };
        AbstractAction startUpAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                input.movementMap().put(KeyEvent.VK_W, true);
            }
        };
        AbstractAction startRightAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                input.movementMap().put(KeyEvent.VK_D, true);
            }
        };
        AbstractAction startSpeedUpAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                input.movementMap().put(KeyEvent.VK_SHIFT, true);
            }
        };
        AbstractAction endLeftAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                input.movementMap().put(KeyEvent.VK_A, false);
            }
        };
        AbstractAction endDownAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                input.movementMap().put(KeyEvent.VK_S, false);
            }
        };
        AbstractAction endUpAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                input.movementMap().put(KeyEvent.VK_W, false);
            }
        };
        AbstractAction endRightAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                input.movementMap().put(KeyEvent.VK_D, false);
            }
        };
        AbstractAction endSpeedUpAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                input.movementMap().put(KeyEvent.VK_SHIFT, false);
            }
        };
        AbstractAction pauseAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.paused = !model.paused;
                 if (model.paused) {
                    window.playPauseButton.setText("Play");
                } else {
                    window.playPauseButton.setText("Pause");
                }
            }
        };
        AbstractAction stepAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.stepping = true;
                model.update(1);
                model.stepping = false;
            }
        };
        AbstractAction openKeybindsAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = """
                                A/S/W/D - Movement
                                Shift - Speed Up
                                Space - Pause/Play
                                C - Step
                                X - Clear All Particles
                                Alt + Q - Center to 0, 0
                                V - Toggle Particle View Mode
                                Scroll - Changes particle size/Changes mouse radius
                                L-Click - Creates a particle
                                Drag L-Click - Creates a particle with velocity
                                R-Click - Deletes nearby particles
                                M-Click - Follows a nearby particle
                                / - Opens this menu
                                G - Opens control menu
                                """;
                JOptionPane.showMessageDialog(window, message, "Keybinds", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        AbstractAction openControlPanelAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.controlPanel.setVisible(true);
                if (model.paused) {
                    window.playPauseButton.setText("Play");
                } else {
                    window.playPauseButton.setText("Pause");
                }
            }
        };
        AbstractAction toggleViewAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (window.getViewType()) {
                    case Window.ViewType.NONE:
                        window.setViewType(Window.ViewType.VELOCITY);
                        window.viewButton.setText("View: Vel");
                        break;

                    case Window.ViewType.VELOCITY:
                        window.setViewType(Window.ViewType.MOMENTUM);
                        window.viewButton.setText("View: Mom");
                        break;

                    case Window.ViewType.MOMENTUM:
                        window.setViewType(Window.ViewType.NONE);
                        window.viewButton.setText("View: None");
                        break;
                
                    default:
                        break;
                }
            }
        };

        // BUTTON BINDINGS
        window.playPauseButton.addActionListener(e -> {pauseAction.actionPerformed(e);});
        window.stepButton.addActionListener(e -> {stepAction.actionPerformed(e);});
        window.clearButton.addActionListener(e -> {destroyAllAction.actionPerformed(e);});
        window.viewButton.addActionListener(e -> {toggleViewAction.actionPerformed(e);});
        window.centerButton.addActionListener(e -> {returnAction.actionPerformed(e);});
        window.elasticitySlider.addChangeListener(e -> {
            model.getSettingsMap().put("Elasticity", window.elasticitySlider.getValue()/100.0);
        });
        window.constantField.addActionListener(e -> {
            try {
                model.getSettingsMap().put("Constant", Double.valueOf(window.constantField.getText()));
            } catch (NumberFormatException ex) {
                window.constantField.setText(String.valueOf(model.getSettingsMap().get("Constant")));
                JOptionPane.showMessageDialog(window, "Invalid Argument", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        window.cameraSpeedField.addActionListener(e -> {
            try {
                double value = Double.valueOf(window.cameraSpeedField.getText());
                if (value <= 0) {
                    throw new IllegalArgumentException();
                }
                model.getSettingsMap().put("Camera Speed", value);
            } catch (NumberFormatException ex) {
                window.cameraSpeedField.setText(String.valueOf(model.getSettingsMap().get("Camera Speed")));
                JOptionPane.showMessageDialog(window, "Invalid Argument", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                window.cameraSpeedField.setText(String.valueOf(model.getSettingsMap().get("Camera Speed")));
                JOptionPane.showMessageDialog(window, "Invalid Argument: Number cannot be negative or zero", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ACTION MAP
        window.canvas.getActionMap().put("destroy all particles", destroyAllAction);
        window.canvas.getActionMap().put("return", returnAction);
        window.canvas.getActionMap().put("start left", startLeftAction);
        window.canvas.getActionMap().put("start down", startDownAction);
        window.canvas.getActionMap().put("start up", startUpAction);
        window.canvas.getActionMap().put("start right", startRightAction);
        window.canvas.getActionMap().put("start speed up", startSpeedUpAction);
        window.canvas.getActionMap().put("end left", endLeftAction);
        window.canvas.getActionMap().put("end down", endDownAction);
        window.canvas.getActionMap().put("end up", endUpAction);
        window.canvas.getActionMap().put("end right", endRightAction);
        window.canvas.getActionMap().put("end speed up", endSpeedUpAction);
        window.canvas.getActionMap().put("pause", pauseAction);
        window.canvas.getActionMap().put("step", stepAction);
        window.canvas.getActionMap().put("toggle view", toggleViewAction);
        window.canvas.getActionMap().put("open keybinds", openKeybindsAction);
        window.canvas.getActionMap().put("open control panel", openControlPanelAction);
    }
}
