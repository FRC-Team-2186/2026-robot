# Test Plan

This document describes the recommended tests for the current branch changes.

## Scope
- Shooter preset workflow (`pos`-based presets)
- Distance/vision readiness and distance-based shooting
- Operator feedback (rumble + dashboard messages)
- Driver/Operator bindings
- Documentation correctness

## Preconditions
- Robot enabled on a safe test field.
- All controllers connected and in correct USB slots.
- Shooter hardware wired and secured (flywheels guarded).
- Vision system either available or mocked (returning `NaN` when no target).
- Voltage/RPM values may not correspond to the correct distances yet; treat tests as logic validation, not performance validation.

## Controller USB Mapping
- Driver controller: USB index 0
- Operator controller: USB index 1

## Functional Tests

### 0) Table Calibration (Highest Priority)
- Calibrate `distance_chart`, `voltage_chart`, and `rpm_chart` together.
- This is the most important testing task for shooter accuracy.
- Record the field distance and resulting shot performance for each preset.
- Update the tables so each distance has a reliable voltage/RPM pairing.

#### Step-by-Step Calibration Procedure
1. Use the Operator Start button to run `ShooterVoltageTest`.
2. Adjust `Shooter/TestVoltage` on Shuffleboard to set flywheel voltage without redeploying.
3. Choose 3-4 fixed field distances (near/mid/far) and mark them on the field.
4. Set `distance_chart` entries to those exact distances (meters), in ascending order.
5. Start with a safe, low voltage and a conservative RPM for each preset.
6. At the nearest distance:
   - Run the shooter at the current voltage.
   - Observe shot result (short/long/left/right).
   - Adjust voltage (or RPM) until shots are consistently on target.
   - Record the final voltage/RPM for this distance.
7. Repeat step 6 for mid and far distances.
8. Update `voltage_chart` and `rpm_chart` with the tuned values.
9. Re-test all distances with the full table to verify consistency.
10. If using closed-loop later, treat tuned RPM values as the PIDF setpoints.

### 0b) Dashboard Voltage Test (Fast Tuning)
- Use the Operator Start button to run `ShooterVoltageTest`.
- Adjust `Shooter/TestVoltage` on Shuffleboard to tune without redeploying.
- Validate shot behavior and record the voltage for each distance preset.

### 1) Preset Index Flow
- Press Operator X/Y/B for near/mid/far presets.
- Verify flywheel voltage matches `voltage_chart[pos]`.
- Verify feeder does not run until flywheel reaches target RPM tolerance.
- Confirm no exceptions are thrown for valid indices.

### 2) Feeder Gating
- For each preset:
  - Run the flywheel and watch RPM.
  - Confirm feeder starts only when RPM is within tolerance.
  - Confirm feeder stops when RPM drops below tolerance.

### 3) Distance Lookup (Nearest)
- Temporarily set `distance_chart` to known values (e.g., 2, 4, 6 meters).
- Simulate distance values near each preset.
- Confirm `getPosForDistance()` returns the nearest index.

### 4) Distance Lookup (Interpolated)
- Use `getTargetVoltageForDistance(distance, INTERPOLATED)` in a test harness.
- Verify outputs interpolate linearly between two points.
- Confirm clamping at the first/last values when distance is outside range.

### 5) Vision Missing Target Handling
- Force `getVisionDistanceMeters()` to return `NaN`.
- Run `ShootByDistance`:
  - Operator controller should pulse rumble.
  - Dashboard should show `Shooter/VisionStatus = No target`.
  - Dashboard should show `Shooter/VisionHint = Vision lost - re-aim`.
  - Flywheel and feeder voltages should be 0.

### 6) Vision Valid Target Handling
- Force `getVisionDistanceMeters()` to return a valid distance.
- Run `ShootByDistance`:
  - Rumble should stop.
  - Dashboard should show `Shooter/VisionStatus = Target OK`.
  - Dashboard should display the distance in meters.
  - Flywheel voltage and feeder gating should follow the preset.

### 7) Command Lifecycle
- Bind `ShootByDistance` to a button (`.whileTrue`).
- Hold the button, then release:
  - Command should end.
  - Rumble should stop.
  - Shooter motors should stop.

## Regression Checks
- Shooter subsystem still initializes correctly.
- Existing autos run without errors.
- Driver controls (swerve) unaffected.
- Feeder manual control still works (Operator A).

## Documentation Checks
- `driverGuide.md` reflects current bindings and USB indices.
- `branchChanges.md` includes the correct file list and summary.

## Safety Checklist
- Keep flywheels guarded during testing.
- Use low RPM/voltage for first tests.
- Keep robot on blocks for early validation.
- Always have an E-stop available.
