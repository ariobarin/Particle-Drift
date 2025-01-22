/*
 * SimulationView.java
 * Ario Barin Ostovary
 * Class for the simulation view
 * Displays the simulation view, has buttons to toggle the different views - world, pov, lidar, and slam
 */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SimulationView extends View {
    // boolean flags for the different views
    private boolean 
    showWorld = false, 
    showPOV = false, 
    showReadings = true, 
    showSLAM = true;

    private final int
    showWorldToggle = KeyEvent.VK_1, 
    showPOVToggle = KeyEvent.VK_2, 
    showReadingsToggle = KeyEvent.VK_3, 
    showSLAMToggle = KeyEvent.VK_4;
    
    private static final BufferedImage worldIcon, povIcon, lidarIcon, slamIcon, backIcon;
    private final int buttonWidth = 50, buttonHeight = 50;
    private final Button[] viewButtons;
    private final Button backButton;

    private final Simulation world;

    static {
        worldIcon = Util.loadImage("assets/world_icon.png");
        povIcon = Util.loadImage("assets/local_icon.png");
        lidarIcon = Util.loadImage("assets/lidar_icon.png");
        slamIcon = Util.loadImage("assets/slam_icon.png");
        backIcon = Util.loadImage("assets/back_icon.png");
    }

    public SimulationView(CarGUIPanel panel, String mapFile) {
        super(panel);
        try {
            this.world = new Simulation(mapFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load simulation", e);
        }
        viewButtons = new Button[4];
        initializeButtons();
        
        // back button
        backButton = new Button(
            () -> 20,
            () -> 20,
            100, 100,
            backIcon, true,
            () -> panel.startSimulationSelecter()
        );
    }

    public void initializeButtons() {
        int iconWidth = 50;
        int iconHeight = 50;
        int totalWidth = iconWidth * 4;

        // world button
        viewButtons[0] = new Button(
            () -> panel.getWidth()/2 - totalWidth/2,
            () -> 10,
            iconWidth, iconHeight, worldIcon, showWorld,
            () -> {
                // toggle world and update button state
                showWorld = !showWorld;
                viewButtons[0].setState(showWorld);
            }
        );

        // pov button
        viewButtons[1] = new Button(
            () -> panel.getWidth()/2 - totalWidth/2 + iconWidth,
            () -> 10,
            iconWidth, iconHeight, povIcon, showPOV,
            () -> {
                // toggle pov and update button state
                showPOV = !showPOV;
                viewButtons[1].setState(showPOV);
            }
        );

        // lidar button
        viewButtons[2] = new Button(
            () -> panel.getWidth()/2 - totalWidth/2 + 2 * iconWidth,
            () -> 10,
            iconWidth, iconHeight, lidarIcon, showReadings,
            () -> {
                // toggle lidar and update button state
                showReadings = !showReadings;
                viewButtons[2].setState(showReadings);
            }
        );

        // slam button
        viewButtons[3] = new Button(
            () -> panel.getWidth()/2 - totalWidth/2 + 3 * iconWidth,
            () -> 10,
            iconWidth, iconHeight, slamIcon, showSLAM,
            () -> {
                // toggle slam and update button state
                showSLAM = !showSLAM;
                viewButtons[3].setState(showSLAM);
            }
        );
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // check if any view button is clicked
        for (Button button : viewButtons) {
            if (button.contains(e.getX(), e.getY())) {
                button.click();
                return;
            }
        }
        if (backButton.contains(e.getX(), e.getY())) {
            backButton.click();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for (Button button : viewButtons) {
            button.setHovered(button.contains(e.getX(), e.getY()));
        }
        backButton.setHovered(backButton.contains(e.getX(), e.getY()));
    }

    private void drawButtons(Graphics g) {
        for (Button button : viewButtons) {
            button.draw(g);
        }
        backButton.draw(g);
    }

    @Override
    public void step(boolean[] keysDown, boolean[] keysPressed) {
        world.update(keysDown);

        // toggle view buttons - have to update the button state (probably could have been done better)
        if (keysPressed[showWorldToggle]) {
            showWorld = !showWorld;
            viewButtons[0].setState(showWorld);
            if (!showWorld && !showPOV && !showReadings && !showSLAM) {
                showWorld = true;
                viewButtons[0].setState(true);
            }
        } else if (keysPressed[showPOVToggle]) {
            showPOV = !showPOV;
            viewButtons[1].setState(showPOV);
            if (!showWorld && !showPOV && !showReadings && !showSLAM) {
                showPOV = true;
                viewButtons[1].setState(true);
            }
        } else if (keysPressed[showReadingsToggle]) {
            showReadings = !showReadings;
            viewButtons[2].setState(showReadings);
            if (!showWorld && !showPOV && !showReadings && !showSLAM) {
                showReadings = true;
                viewButtons[2].setState(true);
            }
        } else if (keysPressed[showSLAMToggle]) {
            showSLAM = !showSLAM;
            viewButtons[3].setState(showSLAM);
            if (!showWorld && !showPOV && !showReadings && !showSLAM) {
                showSLAM = true;
                viewButtons[3].setState(true);
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        int screenWidth = panel.getWidth();
        int screenHeight = panel.getHeight();

        int viewX = 0;
        int viewY = 0;

        int viewWidth = screenWidth;
        int viewHeight = screenHeight;

        // count number of views
        int numViews = 0;
        if (showWorld)
            numViews++;
        if (showPOV)
            numViews++;
        if (showReadings)
            numViews++;
        if (showSLAM)
            numViews++;

        if (numViews == 0) {
            System.out.println("No views to show, how...");
            return;
        }

        if (numViews == 4) {
            viewWidth = screenWidth / 2;
            viewHeight = screenHeight / 2;
        } else {
            viewWidth = screenWidth / numViews;
            viewHeight = screenHeight;
        }

        // Simulation View
        if (showWorld) {
            BufferedImage simView = new BufferedImage(viewWidth, viewHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = simView.createGraphics();
            g2d.setClip(0, 0, viewWidth, viewHeight);
            world.drawPOV(g2d);
            g2d.dispose();

            g.drawImage(simView, viewX, viewY, panel);

            viewX += viewWidth;
        }

        // Lidar View
        if (showReadings) {
            BufferedImage lidarView = new BufferedImage(viewWidth, viewHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2dLidar = lidarView.createGraphics();
            g2dLidar.setClip(0, 0, viewWidth, viewHeight);
            world.drawLidar(g2dLidar);
            g2dLidar.dispose();

            g.drawImage(lidarView, viewX, viewY, panel);

            if (numViews == 4) {
                viewX = 0;
                viewY += viewHeight;
            } else {
                viewX += viewWidth;
            }
        }

        // POV View
        if (showPOV) {
            BufferedImage povView = new BufferedImage(viewWidth, viewHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2dPOV = povView.createGraphics();
            g2dPOV.setClip(0, 0, viewWidth, viewHeight);
            world.drawSimulation(g2dPOV);
            g2dPOV.dispose();

            g.drawImage(povView, viewX, viewY, panel);

            viewX += viewWidth;
        }

        if (showSLAM) {
            BufferedImage occupancyView = new BufferedImage(viewWidth, viewHeight,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2dOccupancy = occupancyView.createGraphics();
            g2dOccupancy.setClip(0, 0, viewWidth, viewHeight);
            world.drawOccupancyGrid(g2dOccupancy);
            g2dOccupancy.dispose();

            g.drawImage(occupancyView, viewX, viewY, panel);
        }

        drawButtons(g);
    }
}