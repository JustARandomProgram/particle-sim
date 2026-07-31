import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.SwingWorker;

public class Model {
    public LinkedList<Particle> particles = new LinkedList<>();
    public HashMap<String,Double> settings = new HashMap<>();
    public Random random = new Random();

    public Boolean paused = false;
    public Boolean stepping = false;
    public Boolean settingVel = false;
    public Boolean destroying = false;
    
    public double mouseRadius = 10;
    public Point cameraPos = new Point(-App.window.width/2, -App.window.height/2);
    public Particle selectedParticle;
    public Point newParticlePos = new Point(0, 0);
    public Point initialVel = new Point(0, 0);
    
    public class Particle {
        public double x, y;
        public double vx, vy;
        
        final public double radius;
        final public double mass;
        
        public Particle(double x, double y, double vx, double vy, double radius) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.radius = radius;

            this.mass = radius*0.5;

            particles.add(this);
        }
        
        public Particle(double x, double y, double radius) {
            this(x, y, 0, 0, radius);
        }

        public Particle(double x, double y, double vx, double vy) {
            this(x, y, vx, vy, 5);
        }
        
        public Particle(double x, double y) {
            this(x, y, 0, 0, 5);
        }

        public void update(double delta) {
            double fx = 0, fy = 0;
            for (Particle particle : particles) {
                if (particle == this) continue;
                double dx = (particle.x - this.x), dy = (particle.y - this.y);
                double distance = Math.sqrt(dx*dx + dy*dy);
                if (distance == 0) {
                    distance = 0.0001;
                    double randomAngle = Math.toRadians(random.nextDouble(0, 359));
                    dx = Math.cos(randomAngle) * 0.0001;
                    dy = Math.sin(randomAngle) * 0.0001;
                }
                double nx = dx/distance, ny = dy/distance;
                if ((this.radius + particle.radius) >= distance) {
                    double relVX = this.vx - particle.vx;
                    double relVY = this.vy - particle.vy;
                    double relVelNorm = relVX*nx + relVY*ny;
                    double elasticity = settings.get("Elasticity");
                    double impulse = (1 + elasticity) * relVelNorm / (1/this.mass + 1/particle.mass);
                    
                    this.vx -= impulse / this.mass * nx;
                    this.vy -= impulse / this.mass * ny;
                    particle.vx += impulse / particle.mass * nx;
                    particle.vy += impulse / particle.mass * ny;
                    
                    for (int i = 0; i < 3; i++) {
                        double overlap = (this.radius + particle.radius - distance) / 2;
                        this.x -= overlap * nx;
                        this.y -= overlap * ny;
                        particle.x += overlap * nx;
                        particle.y += overlap * ny;
                    }
                }
                double force = settings.get("Constant") * this.mass * particle.mass / (distance * distance);
                fx += nx * force;
                fy += ny * force;
            }
            this.vx += (fx/this.mass) * delta;
            this.vy += (fy/this.mass) * delta;
            if (Math.abs(this.vx) > settings.get("Terminal Velocity")) this.vx = settings.get("Terminal Velocity") * Math.signum(this.vx);
            if (Math.abs(this.vy) > settings.get("Terminal Velocity")) this.vy = settings.get("Terminal Velocity") * Math.signum(this.vy);
            this.x += this.vx * delta;
            this.y += this.vy * delta;
        }
        
        @Override
        public String toString() {
            return "[" + this.x + "," + this.y + ":" + this.radius + "]";
        }
    }

    //CALCULATIONS

    public void calculateInitialVel() {
        initialVel.x = (int)((App.input.mousePos.x - newParticlePos.x + cameraPos.x) / App.window.vectorMulti);
        initialVel.y = (int)((App.input.mousePos.y - newParticlePos.y + cameraPos.y) / App.window.vectorMulti);
    }

    //UPDATE

    public void update(double delta) {
        if (!inParticleList(selectedParticle)) {
            selectedParticle = null;
            updateCameraPos(delta);
        } else {
            cameraPos.x = (int)(selectedParticle.x - 450.0);
            cameraPos.y = (int)(selectedParticle.y - 450.0);
        }
        if (settingVel) {
            calculateInitialVel();
        } else {
            App.input.updateNewParticlePos();
        }
        if (!paused || stepping) {
            SwingWorker<Void,Void> attractionWorker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    for (Particle particle : particles) {
                        particle.update(delta);
                    }
                    return null;
                }  
            };
            attractionWorker.execute();
        }
    }
    
    public void updateCameraPos(double delta) {
        double speedMultiplier = App.input.movementMap.get(KeyEvent.VK_SHIFT) ? 3 : 1;
        if (App.input.movementMap.get(KeyEvent.VK_A)) {
            cameraPos.x -= App.model.settings.get("Camera Speed") * speedMultiplier;
        }
        if (App.input.movementMap.get(KeyEvent.VK_S)) {
            cameraPos.y += App.model.settings.get("Camera Speed") * speedMultiplier;
        }
        if (App.input.movementMap.get(KeyEvent.VK_W)) {
            cameraPos.y -= App.model.settings.get("Camera Speed") * speedMultiplier;
        }
        if (App.input.movementMap.get(KeyEvent.VK_D)) {
            cameraPos.x += App.model.settings.get("Camera Speed") * speedMultiplier;
        }
    }
    
    //PARTICLE STUFF

    public void createParticle(double x, double y, double vx, double vy) {
        new Particle(x, y, vx, vy, this.mouseRadius);
    }

    public void createParticle(double x, double y) {
        createParticle(x, y,0,0);
    }
    
    public void attemptToDestroyNearbyParticles() {
        particles.removeIf(particle -> {
            double dx = particle.x - (App.input.mousePos.x + cameraPos.x);
            double dy = particle.y - (App.input.mousePos.y + cameraPos.y);
            double distance = Math.sqrt(dx*dx + dy*dy);
            return (distance - particle.radius) < mouseRadius;
        });
    }

    public Boolean inParticleList(Particle particle) {
        for (Particle other : particles) {
            if (other == particle) {
                return true;
            }
        }
        return false;
    }

    public void selectClosestParticle() {
        Particle closestParticle = null;
        double closestDistance = Double.POSITIVE_INFINITY;
        for (Particle particle : particles) {
            double dx = particle.x - (App.input.mousePos.x + cameraPos.x);
            double dy = particle.y - (App.input.mousePos.y + cameraPos.y);
            double distance = Math.sqrt(dx*dx + dy*dy);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestParticle = particle;
            }
        }
        if (closestParticle != null && closestDistance <= closestParticle.radius) {
            selectedParticle = closestParticle;
        } else if (closestParticle != null && closestDistance > closestParticle.radius){
            selectedParticle = null;
        }
    }
}
