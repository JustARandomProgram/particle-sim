import java.awt.Point;
import java.awt.event.*;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

public class Input {
    private Point mousePos = new Point(0, 0);
    private Boolean mouseVisible = false;
    private int currentButton = MouseEvent.NOBUTTON;
    private HashMap<Integer,Boolean> movementMap = new HashMap<>();

    // GETTERS

    public Point getMousePos() {
        return mousePos;
    }

    public Boolean isMouseVisible() {
        return mouseVisible;
    }

    public HashMap<Integer,Boolean> movementMap() {
        return movementMap;
    }

    // MOUSE INPUT

    public class MouseInput implements MouseMotionListener, MouseListener, MouseWheelListener {

        @Override
        public void mouseClicked(MouseEvent e) {}
        
        @Override
        public void mousePressed(MouseEvent e) {
            currentButton = e.getButton();
            if (currentButton == MouseEvent.BUTTON1) {
                App.model.settingVel = true;
            } else if (currentButton == MouseEvent.BUTTON3) {
                App.model.destroying = true;
            }
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            currentButton = MouseEvent.NOBUTTON;
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (App.model.getSelectedParticle() != null) {
                    App.model.getInitialVelocity().x += App.model.getSelectedParticle().vx;
                    App.model.getInitialVelocity().y += App.model.getSelectedParticle().vy;
                }
                App.model.createParticle(App.model.getNewParticlePos().x, App.model.getNewParticlePos().y, App.model.getInitialVelocity().x, App.model.getInitialVelocity().y);
                App.model.settingVel = false;
                App.model.getInitialVelocity().x = 0;
                App.model.getInitialVelocity().y = 0;
                App.model.getNewParticlePos().x = mousePos.x + App.model.getCameraPos().x;
                App.model.getNewParticlePos().y = mousePos.y + App.model.getCameraPos().y;
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                App.model.getNewParticlePos().x = mousePos.x + App.model.getCameraPos().x;
                App.model.getNewParticlePos().y = mousePos.y + App.model.getCameraPos().y;
                App.model.destroying = false;
            } else if (e.getButton() == MouseEvent.BUTTON2) {
                App.model.selectClosestParticle();
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            mouseVisible = true;
        }

        @Override
        public void mouseExited(MouseEvent e) {
            mouseVisible = false;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            mousePos = e.getPoint();
            if (currentButton == MouseEvent.BUTTON1) {
                App.model.calculateInitialVel();
            } else if (currentButton == MouseEvent.BUTTON3) {
                App.model.attemptToDestroyNearbyParticles();
            } else if (e.getButton() == MouseEvent.BUTTON2) {
                App.model.getNewParticlePos().x = mousePos.x;
                App.model.getNewParticlePos().y = mousePos.y;
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            mousePos = e.getPoint();
            updateNewParticlePos();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (e.isControlDown()) {
                App.model.setMouseRadius(App.model.getMouseRadius() - e.getPreciseWheelRotation() * 5);
            } else {
                App.model.setMouseRadius(App.model.getMouseRadius() - e.getPreciseWheelRotation());
            }
            if (App.model.getMouseRadius() < 0.1) App.model.setMouseRadius(0.1);
        }

    }

    public void updateNewParticlePos() {
        App.model.getNewParticlePos().x = mousePos.x + App.model.getCameraPos().x;
        App.model.getNewParticlePos().y = mousePos.y + App.model.getCameraPos().y;
    }

    Input() {
        InputMap inputMap = App.window.canvas.getInputMap();
        ActionMap actionMap = App.window.canvas.getActionMap();
        MouseInput mouseInput = new MouseInput();
        App.window.canvas.addMouseListener(mouseInput);
        App.window.canvas.addMouseMotionListener(mouseInput);
        App.window.canvas.addMouseWheelListener(mouseInput);
        
        // MOVEMENT MAP
        movementMap.put(KeyEvent.VK_SHIFT, false);
        movementMap.put(KeyEvent.VK_A, false);
        movementMap.put(KeyEvent.VK_S, false);
        movementMap.put(KeyEvent.VK_W, false);
        movementMap.put(KeyEvent.VK_D, false);

        // KEYBINDINGS
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X,0), "destroy all particles");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,0), "pause");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C,0), "step");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V,0), "toggle view");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,0), "start left");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,0), "start down");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W,0), "start up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,0), "start right");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.SHIFT_DOWN_MASK), "start left");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.SHIFT_DOWN_MASK), "start down");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.SHIFT_DOWN_MASK), "start up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.SHIFT_DOWN_MASK), "start right");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,0,true), "end left");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,0,true), "end down");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W,0,true), "end up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,0,true), "end right");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.SHIFT_DOWN_MASK,true), "end left");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.SHIFT_DOWN_MASK,true), "end down");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.SHIFT_DOWN_MASK,true), "end up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.SHIFT_DOWN_MASK,true), "end right");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT,InputEvent.SHIFT_DOWN_MASK), "start speed up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT,0, true), "end speed up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_DOWN_MASK), "return");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0),"open keybinds");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, 0),"open control panel");

        // ACTIONS
        AbstractAction destroyAllAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                App.model.destroyAllParticles();
            }
        };
        AbstractAction returnAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                App.model.getCameraPos().x = -App.window.getWidth()/2;
                App.model.getCameraPos().y = -App.window.getHeight()/2;
            }
        };
        AbstractAction startLeftAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movementMap.put(KeyEvent.VK_A, true);
            }
        };
        AbstractAction startDownAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movementMap.put(KeyEvent.VK_S, true);
            }
        };
        AbstractAction startUpAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movementMap.put(KeyEvent.VK_W, true);
            }
        };
        AbstractAction startRightAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movementMap.put(KeyEvent.VK_D, true);
            }
        };
        AbstractAction startSpeedUpAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movementMap.put(KeyEvent.VK_SHIFT, true);
            }
        };
        AbstractAction endLeftAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movementMap.put(KeyEvent.VK_A, false);
            }
        };
        AbstractAction endDownAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movementMap.put(KeyEvent.VK_S, false);
            }
        };
        AbstractAction endUpAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movementMap.put(KeyEvent.VK_W, false);
            }
        };
        AbstractAction endRightAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movementMap.put(KeyEvent.VK_D, false);
            }
        };
        AbstractAction endSpeedUpAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movementMap.put(KeyEvent.VK_SHIFT, false);
            }
        };
        AbstractAction pauseAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                App.model.paused = !App.model.paused;
                 if (App.model.paused) {
                    App.window.playPauseButton.setText("Play");
                } else {
                    App.window.playPauseButton.setText("Pause");
                }
            }
        };
        AbstractAction stepAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                App.model.stepping = true;
                App.model.update(1);
                App.model.stepping = false;
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
                JOptionPane.showMessageDialog(App.window, message, "Keybinds", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        AbstractAction openControlPanelAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                App.window.controlPanel.setVisible(true);
                if (App.model.paused) {
                    App.window.playPauseButton.setText("Play");
                } else {
                    App.window.playPauseButton.setText("Pause");
                }
            }
        };
        AbstractAction toggleViewAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (App.window.getViewType()) {
                    case Window.ViewType.NONE:
                        App.window.setViewType(Window.ViewType.VELOCITY);
                        App.window.viewButton.setText("View: Vel");
                        break;

                    case Window.ViewType.VELOCITY:
                        App.window.setViewType(Window.ViewType.MOMENTUM);
                        App.window.viewButton.setText("View: Mom");
                        break;

                    case Window.ViewType.MOMENTUM:
                        App.window.setViewType(Window.ViewType.NONE);
                        App.window.viewButton.setText("View: None");
                        break;
                
                    default:
                        break;
                }
            }
        };

        // BUTTON BINDINGS
        App.window.playPauseButton.addActionListener(e -> {pauseAction.actionPerformed(e);});
        App.window.stepButton.addActionListener(e -> {stepAction.actionPerformed(e);});
        App.window.clearButton.addActionListener(e -> {destroyAllAction.actionPerformed(e);});
        App.window.viewButton.addActionListener(e -> {toggleViewAction.actionPerformed(e);});
        App.window.centerButton.addActionListener(e -> {returnAction.actionPerformed(e);});
        App.window.elasticitySlider.addChangeListener(e -> {
            App.model.getSettingsMap().put("Elasticity", App.window.elasticitySlider.getValue()/100.0);
        });
        App.window.constantField.addActionListener(e -> {
            try {
                App.model.getSettingsMap().put("Constant", Double.valueOf(App.window.constantField.getText()));
            } catch (NumberFormatException ex) {
                App.window.constantField.setText(String.valueOf(App.model.getSettingsMap().get("Constant")));
                JOptionPane.showMessageDialog(App.window, "Invalid Argument", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        App.window.cameraSpeedField.addActionListener(e -> {
            try {
                double value = Double.valueOf(App.window.cameraSpeedField.getText());
                if (value <= 0) {
                    throw new IllegalArgumentException();
                }
                App.model.getSettingsMap().put("Camera Speed", value);
            } catch (NumberFormatException ex) {
                App.window.cameraSpeedField.setText(String.valueOf(App.model.getSettingsMap().get("Camera Speed")));
                JOptionPane.showMessageDialog(App.window, "Invalid Argument", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                App.window.cameraSpeedField.setText(String.valueOf(App.model.getSettingsMap().get("Camera Speed")));
                JOptionPane.showMessageDialog(App.window, "Invalid Argument: Number cannot be negative or zero", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ACTION MAP
        actionMap.put("destroy all particles", destroyAllAction);
        actionMap.put("return", returnAction);
        actionMap.put("start left", startLeftAction);
        actionMap.put("start down", startDownAction);
        actionMap.put("start up", startUpAction);
        actionMap.put("start right", startRightAction);
        actionMap.put("start speed up", startSpeedUpAction);
        actionMap.put("end left", endLeftAction);
        actionMap.put("end down", endDownAction);
        actionMap.put("end up", endUpAction);
        actionMap.put("end right", endRightAction);
        actionMap.put("end speed up", endSpeedUpAction);
        actionMap.put("pause", pauseAction);
        actionMap.put("step", stepAction);
        actionMap.put("toggle view", toggleViewAction);
        actionMap.put("open keybinds", openKeybindsAction);
        actionMap.put("open control panel", openControlPanelAction);
    }
}
