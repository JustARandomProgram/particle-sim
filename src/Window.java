import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.Timer;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class Window extends JFrame {
    public static enum ViewType {NONE(0),VELOCITY(1),MOMENTUM(2);
        ViewType(int type) {}}

    public final int width, height;
    public Canvas canvas;
    public ViewType viewType = ViewType.NONE;
    public double vectorMulti = 5;

    public JDialog controlPanel;

    public JButton playPauseButton;
    public JButton stepButton;
    public JButton clearButton;
    public JButton viewButton;
    public JButton centerButton;
    public JLabel elasticityLabel;
    public JLabel constantLabel;
    public JSlider elasticitySlider;
    public JTextField constantField;

    public class Canvas extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D)g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, width, height);
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));

            // GRID LINES
            g2d.setColor(new Color(46, 46, 46));
            int currentHundred;
            for (currentHundred = App.model.cameraPos.x; currentHundred <= App.model.cameraPos.x + width; currentHundred++) {
                if (currentHundred % (100) != 0) continue;
                g2d.draw(new Line2D.Double(currentHundred - App.model.cameraPos.x, 0, currentHundred - App.model.cameraPos.x, height));
            }
            for (currentHundred = App.model.cameraPos.y; currentHundred <= App.model.cameraPos.y + height; currentHundred++) {
                if (currentHundred % (100) != 0) continue;
                g2d.draw(new Line2D.Double(0, currentHundred - App.model.cameraPos.y, width, currentHundred - App.model.cameraPos.y));
            }
            double centerCircleRadius = 15;
            g2d.fill(new Ellipse2D.Double(-centerCircleRadius - App.model.cameraPos.x, -centerCircleRadius - App.model.cameraPos.y,2*centerCircleRadius,2*centerCircleRadius));

            // PARTICLES/VISUALS
            for (Model.Particle particle : App.model.particles) {
                g2d.setColor(Color.WHITE);
                g2d.fillOval((int)(particle.x - particle.radius - App.model.cameraPos.x), 
                             (int)(particle.y - particle.radius - App.model.cameraPos.y), 
                             (int)(particle.radius * 2), 
                             (int)(particle.radius * 2));
                if (viewType == ViewType.VELOCITY) {
                    g2d.setColor(Color.BLUE);
                    g2d.draw(new Line2D.Double(particle.x - App.model.cameraPos.x, particle.y - App.model.cameraPos.y, particle.x - App.model.cameraPos.x + particle.vx * vectorMulti, particle.y - App.model.cameraPos.y + particle.vy * vectorMulti));
                } else if (viewType == ViewType.MOMENTUM) {
                    g2d.setColor(Color.GREEN);
                    g2d.draw(new Line2D.Double(particle.x - App.model.cameraPos.x, particle.y - App.model.cameraPos.y, particle.x - App.model.cameraPos.x + particle.vx * particle.mass * vectorMulti, particle.y - App.model.cameraPos.y + particle.vy * particle.mass * vectorMulti));
                }
            }
            if (App.model.settingVel) {
                g2d.setColor(Color.BLUE);
                g2d.draw(new Line2D.Double(App.input.mousePos.x, App.input.mousePos.y, App.model.newParticlePos.x - App.model.cameraPos.x, App.model.newParticlePos.y - App.model.cameraPos.y));
            }

            // MOUSE
            if (App.input.mouseVisible) {
                if (App.model.destroying) {
                    g2d.setColor(new Color(255, 10, 10, 128));
                    g2d.fillOval((int)(App.input.mousePos.x - App.model.mouseRadius), 
                                (int)(App.input.mousePos.y - App.model.mouseRadius), 
                                (int)(App.model.mouseRadius*2.0), 
                                (int)(App.model.mouseRadius*2.0));
                } else {
                    g2d.setColor(new Color(10, 255, 10, 128));
                    g2d.fillOval((int)(App.model.newParticlePos.x - App.model.mouseRadius - App.model.cameraPos.x), 
                                (int)(App.model.newParticlePos.y - App.model.mouseRadius - App.model.cameraPos.y), 
                                (int)(App.model.mouseRadius*2.0), 
                                (int)(App.model.mouseRadius*2.0));
                }
            }
        }
    }

    Window(int width, int height, int fps) {
        this.width = width;
        this.height = height;
        setTitle("Particle Sim");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width,height));
        add(canvas);

        pack();

        // CONTROL PANEL
        controlPanel = new JDialog(this,"Simulation Control Panel");
        controlPanel.setResizable(false);
        controlPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        Font font = new Font("Times New Roman", Font.PLAIN, 20);

        playPauseButton = new JButton("Pause");
        stepButton = new JButton("Step");
        clearButton = new JButton("Clear");
        centerButton = new JButton("Center");
        viewButton = new JButton("View: None");
        elasticityLabel = new JLabel("Elasticity: ");
        elasticitySlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 20);
        constantLabel = new JLabel("Constant: ");
        constantField = new JTextField("50");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        playPauseButton.setPreferredSize(new Dimension(125,50));
        playPauseButton.setFont(font);
        controlPanel.add(playPauseButton,gbc);

        gbc.gridx = 1;
        stepButton.setPreferredSize(new Dimension(50,50));
        stepButton.setFont(font);
        controlPanel.add(stepButton,gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        clearButton.setPreferredSize(new Dimension(125,50));
        clearButton.setFont(font);
        controlPanel.add(clearButton,gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        elasticityLabel.setHorizontalAlignment(JLabel.RIGHT);
        elasticityLabel.setPreferredSize(new Dimension(50,50));
        elasticityLabel.setFont(font);
        controlPanel.add(elasticityLabel,gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        elasticitySlider.setPaintTicks(true);
        elasticitySlider.setMajorTickSpacing(10);
        elasticitySlider.setMinorTickSpacing(5);
        controlPanel.add(elasticitySlider,gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        viewButton.setFont(font);
        controlPanel.add(viewButton,gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        constantLabel.setHorizontalAlignment(JLabel.RIGHT);
        constantLabel.setPreferredSize(new Dimension(50,100));
        constantLabel.setFont(font);
        controlPanel.add(constantLabel,gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        constantField.setFont(font);
        controlPanel.add(constantField,gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        centerButton.setFont(font);
        controlPanel.add(centerButton,gbc);

        controlPanel.pack();

        // UPDATE LOOP
        Timer clock = new Timer((int)(1/fps * 1000.0), e -> {
            App.model.update(1);
            canvas.repaint();
        });
        clock.start();
    }
}
