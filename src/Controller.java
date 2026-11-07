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
                App.controller.model.destroyAllParticles();
            }
        };
        AbstractAction returnAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                App.controller.model.getCameraPos().x = -App.controller.window.getWidth()/2;
                App.controller.model.getCameraPos().y = -App.controller.window.getHeight()/2;
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
                App.controller.model.paused = !App.controller.model.paused;
                 if (App.controller.model.paused) {
                    App.controller.window.playPauseButton.setText("Play");
                } else {
                    App.controller.window.playPauseButton.setText("Pause");
                }
            }
        };
        AbstractAction stepAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                App.controller.model.stepping = true;
                App.controller.model.update(1);
                App.controller.model.stepping = false;
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
                JOptionPane.showMessageDialog(App.controller.window, message, "Keybinds", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        AbstractAction openControlPanelAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                App.controller.window.controlPanel.setVisible(true);
                if (App.controller.model.paused) {
                    App.controller.window.playPauseButton.setText("Play");
                } else {
                    App.controller.window.playPauseButton.setText("Pause");
                }
            }
        };
        AbstractAction toggleViewAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (App.controller.window.getViewType()) {
                    case Window.ViewType.NONE:
                        App.controller.window.setViewType(Window.ViewType.VELOCITY);
                        App.controller.window.viewButton.setText("View: Vel");
                        break;

                    case Window.ViewType.VELOCITY:
                        App.controller.window.setViewType(Window.ViewType.MOMENTUM);
                        App.controller.window.viewButton.setText("View: Mom");
                        break;

                    case Window.ViewType.MOMENTUM:
                        App.controller.window.setViewType(Window.ViewType.NONE);
                        App.controller.window.viewButton.setText("View: None");
                        break;
                
                    default:
                        break;
                }
            }
        };

        // BUTTON BINDINGS
        App.controller.window.playPauseButton.addActionListener(e -> {pauseAction.actionPerformed(e);});
        App.controller.window.stepButton.addActionListener(e -> {stepAction.actionPerformed(e);});
        App.controller.window.clearButton.addActionListener(e -> {destroyAllAction.actionPerformed(e);});
        App.controller.window.viewButton.addActionListener(e -> {toggleViewAction.actionPerformed(e);});
        App.controller.window.centerButton.addActionListener(e -> {returnAction.actionPerformed(e);});
        App.controller.window.elasticitySlider.addChangeListener(e -> {
            App.controller.model.getSettingsMap().put("Elasticity", App.controller.window.elasticitySlider.getValue()/100.0);
        });
        App.controller.window.constantField.addActionListener(e -> {
            try {
                App.controller.model.getSettingsMap().put("Constant", Double.valueOf(App.controller.window.constantField.getText()));
            } catch (NumberFormatException ex) {
                App.controller.window.constantField.setText(String.valueOf(App.controller.model.getSettingsMap().get("Constant")));
                JOptionPane.showMessageDialog(App.controller.window, "Invalid Argument", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        App.controller.window.cameraSpeedField.addActionListener(e -> {
            try {
                double value = Double.valueOf(App.controller.window.cameraSpeedField.getText());
                if (value <= 0) {
                    throw new IllegalArgumentException();
                }
                App.controller.model.getSettingsMap().put("Camera Speed", value);
            } catch (NumberFormatException ex) {
                App.controller.window.cameraSpeedField.setText(String.valueOf(App.controller.model.getSettingsMap().get("Camera Speed")));
                JOptionPane.showMessageDialog(App.controller.window, "Invalid Argument", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                App.controller.window.cameraSpeedField.setText(String.valueOf(App.controller.model.getSettingsMap().get("Camera Speed")));
                JOptionPane.showMessageDialog(App.controller.window, "Invalid Argument: Number cannot be negative or zero", "Error", JOptionPane.ERROR_MESSAGE);
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
