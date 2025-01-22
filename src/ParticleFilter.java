/*
 * ParticleFilter.java
 * Ario Barin Ostovary
 * Class for the particle filter
 * Uses particles to represent a possible position and a possible map
 * Uses the particles to estimate the position and map
 * Monte Carlo localization
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ParticleFilter {
    private List<Particle> particles;
    private final OccupancyGrid occupancyGrid;

    private static final int RESOLUTION = 5;

    private static final int STARTING_WIDTH = 100;
    private static final int STARTING_HEIGHT = 100;

    private static final double INITIAL_POSITION_STDDEV = 50.0;
    private static final double INITIAL_BEARING_STDDEV = 0.0;

    private static final int NUM_PARTICLES_START = 1;
    private static final int NUM_PARTICLES_END = 1;
    private static final int DEFAULT_RESOLUTION = RESOLUTION;

    private static final int RESAMPLE_COUNT_DOWN = 4;
    private int particleFilterCountDown;

    public ParticleFilter() {
        this.occupancyGrid = new OccupancyGrid(STARTING_WIDTH, STARTING_HEIGHT, DEFAULT_RESOLUTION);

        particles = new ArrayList<>();
        MyDirectedPoint pose = new MyDirectedPoint(0, 0, 0);
        for (int i = 0; i < NUM_PARTICLES_START; i++) {
            particles.add(new Particle(pose, occupancyGrid));
        }
        particleFilterCountDown = 0;
    }

    public ParticleFilter(OccupancyGrid occupancyGrid) {
        this.occupancyGrid = occupancyGrid;
        resetParticles();
    }

    private void resetParticles() {
        this.particles = new ArrayList<>();
        // Get grid dimensions
        int width = occupancyGrid.getWidth();
        int height = occupancyGrid.getHeight();

        double worldWidth = occupancyGrid.gridToWorldX(width);
        double worldHeight = occupancyGrid.gridToWorldY(height);

        for (int i = 0; i < NUM_PARTICLES_START; i++) {
            // Generate uniform random positions within the grid bounds
            // double x = Util.randomDouble(-worldWidth, worldWidth);
            // double y = Util.randomDouble(-worldHeight, worldHeight);
            // double angle = Util.randomDouble(0, 2 * Math.PI); // Random angle between 0 and 2π

            double x = 0;
            double y = 0;
            double angle = 0;

            MyDirectedPoint noisyPose = new MyDirectedPoint(x, y, angle);
            particles.add(new Particle(noisyPose, occupancyGrid));
        }

        particleFilterCountDown = RESAMPLE_COUNT_DOWN;
    }

    public void predict(double speed, double rotation) {
        for (Particle particle : particles) {
            particle.updatePose(speed, rotation);
        }
    }

    public void updateWeights(List<MyVector> lidarReadings) {
        // update the weights of the particles
        for (Particle particle : particles) {
            particle.updateWeight(lidarReadings);
        }

        // // normalize the weights
        double sumWeights = 0.0;
        for (Particle particle : particles) {
            sumWeights += particle.getWeight();
        }
        for (Particle particle : particles) {
            particle.setWeight(particle.getWeight() / sumWeights);
        }
    }

    public void resample(int numParticles) {
        List<Particle> newParticles = new ArrayList<>();

        // Calculate cumulative weights
        double[] cumulativeWeights = new double[particles.size()];
        cumulativeWeights[0] = particles.get(0).getWeight();
        for (int i = 1; i < particles.size(); i++) {
            cumulativeWeights[i] = cumulativeWeights[i - 1] + particles.get(i).getWeight();
        }

        // Resample particles
        for (int i = 0; i < numParticles; i++) {
            double randomValue = Util.randomDouble(0, 1);

            // Binary search to find the particle
            int left = 0;
            int right = particles.size() - 1;

            while (left < right) {
                int mid = (left + right) / 2;
                if (cumulativeWeights[mid] < randomValue) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }

            // Add a copy of the selected particle
            newParticles.add(particles.get(left).copy());
        }

        // Replace old particles with new ones
        particles = newParticles;
    }

    public MyDirectedPoint getEstimatedPosition() {
        // return the position of the particle with the highest weight
        Particle bestParticle = particles.get(0);
        double bestWeight = bestParticle.getWeight();
        for (Particle particle : particles) {
            if (particle.getWeight() > bestWeight) {
                bestWeight = particle.getWeight();
                bestParticle = particle;
            }
        }
        // if the best particle is not good enough, resample (NAN)
        if (Double.isNaN(bestWeight)) {
            // resetParticles();
        }
        return bestParticle.getPose();
    }

    public OccupancyGrid getOccupancyGrid() {
        return occupancyGrid;
    }

    public void updateGrid(List<MyVector> lidarReadings) {
        MyDirectedPoint pose = getEstimatedPosition();
        occupancyGrid.updateGrid(pose, lidarReadings);
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public void update(double speed, double rotation, List<MyVector> lidarReadings) {
        predict(speed, rotation);
        updateWeights(lidarReadings);

        // Calculate number of particles based on linear interpolation
        int numParticles;
        if (particleFilterCountDown > 0) {
            double progress = (double) particleFilterCountDown / RESAMPLE_COUNT_DOWN;
            numParticles = (int) (NUM_PARTICLES_END + (NUM_PARTICLES_START - NUM_PARTICLES_END) * progress);
            particleFilterCountDown--;
        } else {
            numParticles = NUM_PARTICLES_END;
        }
        resample(numParticles);

        updateGrid(lidarReadings);
    }

    public Color getColor(double probability) {
        if (probability > 0.7) {
            return Color.BLACK; // Occupied
        } else if (probability < 0.3) {
            return Color.WHITE; // Free
        } else {
            return new Color((int) (255 * probability), (int) (255 * probability), (int) (255 * probability));
        }
    }

    public void draw(Graphics g) {
        Rectangle panel = g.getClipBounds();
        int panelWidth = panel.width;
        int panelHeight = panel.height;

        // draw the best grid
        OccupancyGrid bestGrid = getOccupancyGrid();
        int width = bestGrid.getWidth();
        int height = bestGrid.getHeight();

        // Create a new buffered image for this frame
        BufferedImage bufferedWorld = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D bufferedGraphics = bufferedWorld.createGraphics();

        // Copy the occupancy grid image
        bufferedGraphics.drawImage(occupancyGrid.getImage(), 0, 0, null);

        // Draw particles on the buffered image
        int radius = 5;
        for (Particle particle : particles) {
            double logWeight = particle.getWeight();

            double probability = Util.logitToProb(logWeight);
            int scaledValue = (int) Math.round(probability * 16777215);
            scaledValue = Math.min(Math.max(scaledValue, 0), 16777215);

            Color particleColor = new Color(scaledValue);
            bufferedGraphics.setColor(particleColor);

            MyDirectedPoint gridPose = particle.getGridPose();

            bufferedGraphics.drawOval(
                    (int) gridPose.getX() - radius,
                    (int) gridPose.getY() - radius,
                    2 * radius,
                    2 * radius);

            MyDirectedPoint end = gridPose.copy();
            end.move(radius * 3);
            bufferedGraphics.drawLine(
                    (int) gridPose.getX(),
                    (int) gridPose.getY(),
                    (int) end.getX(),
                    (int) end.getY());
        }

        bufferedGraphics.dispose();

        // Draw the final buffered image to the panel
        g.drawImage(bufferedWorld, 0, 0, panelWidth, panelHeight, null);
    }
}