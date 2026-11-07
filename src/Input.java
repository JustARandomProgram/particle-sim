import java.awt.Point;
import java.awt.event.*;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

public class Input implements MouseMotionListener, MouseListener, MouseWheelListener {
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
    @Override
    public void mouseClicked(MouseEvent e) {}
    
    @Override
    public void mousePressed(MouseEvent e) {
        currentButton = e.getButton();
        if (currentButton == MouseEvent.BUTTON1) {
            App.controller.model.settingVel = true;
        } else if (currentButton == MouseEvent.BUTTON3) {
            App.controller.model.destroying = true;
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        currentButton = MouseEvent.NOBUTTON;
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (App.controller.model.getSelectedParticle() != null) {
                App.controller.model.getInitialVelocity().x += App.controller.model.getSelectedParticle().vx;
                App.controller.model.getInitialVelocity().y += App.controller.model.getSelectedParticle().vy;
            }
            App.controller.model.createParticle(App.controller.model.getNewParticlePos().x, App.controller.model.getNewParticlePos().y, App.controller.model.getInitialVelocity().x, App.controller.model.getInitialVelocity().y);
            App.controller.model.settingVel = false;
            App.controller.model.getInitialVelocity().x = 0;
            App.controller.model.getInitialVelocity().y = 0;
            App.controller.model.getNewParticlePos().x = mousePos.x + App.controller.model.getCameraPos().x;
            App.controller.model.getNewParticlePos().y = mousePos.y + App.controller.model.getCameraPos().y;
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            App.controller.model.getNewParticlePos().x = mousePos.x + App.controller.model.getCameraPos().x;
            App.controller.model.getNewParticlePos().y = mousePos.y + App.controller.model.getCameraPos().y;
            App.controller.model.destroying = false;
        } else if (e.getButton() == MouseEvent.BUTTON2) {
            App.controller.model.selectClosestParticle();
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
            App.controller.model.calculateInitialVel();
        } else if (currentButton == MouseEvent.BUTTON3) {
            App.controller.model.attemptToDestroyNearbyParticles();
        } else if (e.getButton() == MouseEvent.BUTTON2) {
            App.controller.model.getNewParticlePos().x = mousePos.x;
            App.controller.model.getNewParticlePos().y = mousePos.y;
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
            App.controller.model.setMouseRadius(App.controller.model.getMouseRadius() - e.getPreciseWheelRotation() * 5);
        } else {
            App.controller.model.setMouseRadius(App.controller.model.getMouseRadius() - e.getPreciseWheelRotation());
        }
        if (App.controller.model.getMouseRadius() < 0.1) App.controller.model.setMouseRadius(0.1);
    }

    public void updateNewParticlePos() {
        App.controller.model.getNewParticlePos().x = mousePos.x + App.controller.model.getCameraPos().x;
        App.controller.model.getNewParticlePos().y = mousePos.y + App.controller.model.getCameraPos().y;
    }

    public void init(InputMap inputMap, ActionMap actionMap) {
        
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
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.CTRL_DOWN_MASK), "start left");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_DOWN_MASK), "start down");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.CTRL_DOWN_MASK), "start up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.CTRL_DOWN_MASK), "start right");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.SHIFT_DOWN_MASK|InputEvent.CTRL_DOWN_MASK), "start left");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.SHIFT_DOWN_MASK|InputEvent.CTRL_DOWN_MASK), "start down");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.SHIFT_DOWN_MASK|InputEvent.CTRL_DOWN_MASK), "start up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.SHIFT_DOWN_MASK|InputEvent.CTRL_DOWN_MASK), "start right");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.ALT_DOWN_MASK), "start left");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.ALT_DOWN_MASK), "start down");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.ALT_DOWN_MASK), "start up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.ALT_DOWN_MASK), "start right");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.SHIFT_DOWN_MASK|InputEvent.ALT_DOWN_MASK), "start left");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.SHIFT_DOWN_MASK|InputEvent.ALT_DOWN_MASK), "start down");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.SHIFT_DOWN_MASK|InputEvent.ALT_DOWN_MASK), "start up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.SHIFT_DOWN_MASK|InputEvent.ALT_DOWN_MASK), "start right");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.CTRL_DOWN_MASK|InputEvent.ALT_DOWN_MASK), "start left");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_DOWN_MASK|InputEvent.ALT_DOWN_MASK), "start down");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.CTRL_DOWN_MASK|InputEvent.ALT_DOWN_MASK), "start up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.CTRL_DOWN_MASK|InputEvent.ALT_DOWN_MASK), "start right");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.SHIFT_DOWN_MASK|InputEvent.CTRL_DOWN_MASK|InputEvent.ALT_DOWN_MASK), "start left");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.SHIFT_DOWN_MASK|InputEvent.CTRL_DOWN_MASK|InputEvent.ALT_DOWN_MASK), "start down");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.SHIFT_DOWN_MASK|InputEvent.CTRL_DOWN_MASK|InputEvent.ALT_DOWN_MASK), "start up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.SHIFT_DOWN_MASK|InputEvent.CTRL_DOWN_MASK|InputEvent.ALT_DOWN_MASK), "start right");

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,0,true), "end left");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,0,true), "end down");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W,0,true), "end up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,0,true), "end right");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.SHIFT_DOWN_MASK,true), "end left");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.SHIFT_DOWN_MASK,true), "end down");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.SHIFT_DOWN_MASK,true), "end up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.SHIFT_DOWN_MASK,true), "end right");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.CTRL_DOWN_MASK,true), "end left");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_DOWN_MASK,true), "end down");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.CTRL_DOWN_MASK,true), "end up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.CTRL_DOWN_MASK,true), "end right");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.SHIFT_DOWN_MASK|InputEvent.CTRL_DOWN_MASK,true), "end left");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.SHIFT_DOWN_MASK|InputEvent.CTRL_DOWN_MASK,true), "end down");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.SHIFT_DOWN_MASK|InputEvent.CTRL_DOWN_MASK,true), "end up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.SHIFT_DOWN_MASK|InputEvent.CTRL_DOWN_MASK,true), "end right");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.ALT_DOWN_MASK,true), "end left");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.ALT_DOWN_MASK,true), "end down");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.ALT_DOWN_MASK,true), "end up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.ALT_DOWN_MASK,true), "end right");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.SHIFT_DOWN_MASK|InputEvent.ALT_DOWN_MASK,true), "end left");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.SHIFT_DOWN_MASK|InputEvent.ALT_DOWN_MASK,true), "end down");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.SHIFT_DOWN_MASK|InputEvent.ALT_DOWN_MASK,true), "end up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.SHIFT_DOWN_MASK|InputEvent.ALT_DOWN_MASK,true), "end right");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.CTRL_DOWN_MASK|InputEvent.ALT_DOWN_MASK,true), "end left");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_DOWN_MASK|InputEvent.ALT_DOWN_MASK,true), "end down");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.CTRL_DOWN_MASK|InputEvent.ALT_DOWN_MASK,true), "end up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.CTRL_DOWN_MASK|InputEvent.ALT_DOWN_MASK,true), "end right");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.SHIFT_DOWN_MASK|InputEvent.CTRL_DOWN_MASK|InputEvent.ALT_DOWN_MASK,true), "end left");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.SHIFT_DOWN_MASK|InputEvent.CTRL_DOWN_MASK|InputEvent.ALT_DOWN_MASK,true), "end down");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.SHIFT_DOWN_MASK|InputEvent.CTRL_DOWN_MASK|InputEvent.ALT_DOWN_MASK,true), "end up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,InputEvent.SHIFT_DOWN_MASK|InputEvent.CTRL_DOWN_MASK|InputEvent.ALT_DOWN_MASK,true), "end right");

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT,InputEvent.SHIFT_DOWN_MASK), "start speed up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT,InputEvent.SHIFT_DOWN_MASK|InputEvent.ALT_DOWN_MASK), "start speed up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT,InputEvent.SHIFT_DOWN_MASK|InputEvent.CTRL_DOWN_MASK), "start speed up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT,InputEvent.SHIFT_DOWN_MASK|InputEvent.ALT_DOWN_MASK|InputEvent.CTRL_DOWN_MASK), "start speed up");

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT,0, true), "end speed up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT,InputEvent.ALT_DOWN_MASK, true), "end speed up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT,InputEvent.CTRL_DOWN_MASK, true), "end speed up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT,InputEvent.ALT_DOWN_MASK|InputEvent.CTRL_DOWN_MASK, true), "end speed up");

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_DOWN_MASK), "return");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0),"open keybinds");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, 0),"open control panel");
    }
}
