# Branch Changes and Improvements

## Files Changed in This Branch
- README.md
- driverGuide.md
- src/main/java/frc/robot/Constants.java
- src/main/java/frc/robot/Robot.java
- src/main/java/frc/robot/RobotContainer.java
- src/main/java/frc/robot/commands/DriveTwoMeters.java
- src/main/java/frc/robot/commands/IntakeFuel.java
- src/main/java/frc/robot/commands/MoveDown.java
- src/main/java/frc/robot/commands/MoveUp.java
- src/main/java/frc/robot/commands/RunFlywheel.java
- src/main/java/frc/robot/commands/ShootAuto.java
- src/main/java/frc/robot/commands/ShootByDistance.java
- src/main/java/frc/robot/subsystems/ClimbSubsystem.java
- src/main/java/frc/robot/subsystems/IntakeSubsystem.java
- src/main/java/frc/robot/subsystems/ShooterSubsystem.java
- src/main/java/frc/robot/subsystems/SwerveSubsystem.java

This document summarizes the changes and improvements made in the `Dave_Reviews` branch.

## Guiding Idea
We centered the shooter workflow around a single preset index (`pos`) as the primary selector for distance, voltage, and RPM.
Using one primary selector keeps the tables aligned, avoids fragile conversions, and makes future vision/PIDF upgrades fit cleanly.

## Shooter Architecture and Presets
- Standardized shooter presets around a single `pos` index shared by distance, voltage, and RPM tables.
- Added named shot index constants: `kNearShotIndex`, `kMidShotIndex`, `kFarShotIndex`.
- Removed the older `Constants.pos` to avoid duplicate/ambiguous preset definitions.
- `RunFlywheel` now takes a preset index and uses centralized lookup helpers (no voltage -> index mapping).
- Added feeder tolerance and logging constants to make tuning explicit and consistent.

## Distance and Vision Readiness
- Added distance-based lookup helpers in `ShooterSubsystem`:
  - `getPosForDistance(distanceMeters)`
  - `getTargetVoltageForDistance(distanceMeters, mode)`
  - `getTargetRpmForDistance(distanceMeters, mode)`
- Implemented linear interpolation for distance-based tables with clamping at endpoints.
- Added comments requiring distance/voltage/RPM charts to remain in ascending order.
- Added `getVisionDistanceMeters()` stub returning `NaN` when no target is available.

## New Command: ShootByDistance
- Created `ShootByDistance` command that:
  - Reads vision distance.
  - Selects a preset index from distance.
  - Commands flywheel voltage and feeder gating using the preset.
- Added dense, step-by-step comments and method headers to explain the flow.
- Added operator feedback when vision target is missing:
  - Dashboard status + hint.
  - Pulsing operator controller rumble.
  - Shooter motors stopped until a valid target returns.
- Added a commented-out binding in `RobotContainer` (TODO for vision wiring).

## Hardware Alignment
- Updated shooter motor/controller usage to `SparkFlex` and `SparkFlexConfig` to match Vortex hardware.

## Documentation
- Added `driverGuide.md` with:
  - Driver/Operator bindings and USB indices.
  - Preset meanings and usage notes.
  - Vision usage and readiness cues.
  - Autonomous chooser descriptions.
  - Safety notes.
- Added `testPlan.md` with a calibration procedure and dashboard-driven voltage tuning steps.

## Code Quality Improvements
- Centralized lookup methods with clear headers for maintainability.
- Removed magic numbers from button bindings in favor of named constants.
- Added clear comments and guard checks for invalid preset usage.
- Ensured variable names are descriptive in new logic.
