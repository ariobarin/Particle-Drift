# Particle Drift

A 2D LIDAR SLAM (Simultaneous Localization and Mapping) simulator and remote-driving
application written in Java. A robot car drives through an unknown world, fires a
simulated LIDAR sensor, and uses a Monte Carlo particle filter to figure out where it
is while building an occupancy-grid map of its surroundings on the fly. The same UI can
also connect to a real car over Wi-Fi and run the localization against live sensor
data, so the algorithm is exercised in both simulation and hardware.

This project started as a way to understand how robots answer two deceptively hard
questions at once: "where am I?" and "what does the world around me look like?". The
result is a self-contained, visual implementation of the standard SLAM loop (predict,
observe, weight, resample, update the map) that you can watch update frame by frame.

## What it does

- Drives a car through a 2D world using keyboard controls.
- Casts LIDAR rays into a mask image and reports the distance to the nearest wall.
- Runs a particle filter (Monte Carlo localization) that predicts where the car moved,
  scores each particle against the LIDAR readings, and resamples to keep the belief
  concentrated on the true pose.
- Maintains a log-odds occupancy grid that is updated from the estimated pose and
  LIDAR hits, producing a growing map of free and occupied space.
- Renders several live views side by side: the car's point of view, the raw LIDAR
  rays, a fixed world overview, and the occupancy grid being built.
- Talks to a physical robot over a socket. The desktop app sends drive commands and
  receives LIDAR scans from an ESP32 / ESP8266, so the same particle filter runs on
  real hardware.

## The problem it solves

A moving robot with noisy wheel encoders and a rangefinder cannot trust either source
on its own. Dead reckoning drifts, and a single scan only gives distances to walls
without telling the robot which wall it is looking at. Particle-filter SLAM fuses the
two: each particle is a hypothesis about the car's pose, and the LIDAR readings decide
which hypotheses survive. Aggregating the surviving hypotheses' hits into an occupancy
grid yields a coherent map even though no single measurement was trustworthy. Building
this from scratch is the fastest way to learn why the algorithm works, and where it
breaks.

## Key features

- Monte Carlo localization with motion-model prediction, observation weighting,
  normalization, and systematic resampling.
- Log-odds occupancy-grid mapping with clamped probabilities and a growing grid that
  expands as the car explores beyond its initial bounds.
- Ray casting against a per-pixel mask so any PNG can become a world, plus a small map
  format describing width, height, resolution, and start pose.
- A clean view layer that separates world state from rendering, letting the same
  simulation drive multiple panels (POV, LIDAR, overview, map).
- Socket-based hardware bridge (CarSocket / SocketClient) connecting the Java
  front end to embedded firmware.
- A menu-driven GUI for choosing simulation versus real-car mode.

## Tech stack

- Java (AWT / Swing) for the simulation, rendering, and GUI.
- ESP32 and ESP8266 firmware under `Hardware Programming/`, built with PlatformIO,
  that drives the motors and streams LIDAR scans back to the desktop app.
- PNG map masks and a plain-text map descriptor format for worlds.

## Repository layout

- `src/` - the Java application (simulation, particle filter, occupancy grid, views,
  GUI, socket clients).
- `Hardware Programming/main32` and `Hardware Programming/main8266` - PlatformIO
  firmware projects for the two microcontrollers.
- `maps/` - world definitions: a `.txt` descriptor plus a background PNG and a mask
  PNG per map.
- `assets/` - UI icons and images.
- `bin/` - compiled `.class` output.

## How to run

You need a JDK installed. From the repository root:

1. Compile the sources.
   ```
   javac -d bin src/*.java
   ```
2. Launch the menu GUI (it loads maps and assets from the working directory, so run it
   from the repository root).
   ```
   java -cp bin CarGUI
   ```
3. From the menu, choose Simulation to drive against a bundled map, or Real Car
   to connect to a robot running the firmware. In simulation, use the keyboard to drive
   and watch the occupancy-grid view fill in as the car explores.

For the hardware side, open either folder under `Hardware Programming/` in PlatformIO
and flash it to the matching board. Configure the socket endpoint the Java app connects
to so drive commands and LIDAR packets flow between the two.

## Notes

- The particle count, motion noise, log-odds thresholds, and grid resolution are
  constants near the top of `ParticleFilter.java` and `OccupancyGrid.java`; tuning them
  is the easiest way to see how localization accuracy and performance trade off.
- Five sample maps ship in `maps/` (map1 through map5), each with a background and
  a collision mask, so you can try the algorithm on different environments without
  building your own.
- The original roadmap (menu, map loader, map editor, live car mode) is still the
  guiding feature list for the UI.
