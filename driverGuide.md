# Driver Guide

## Driver Controller (Xbox)
- Driver Station USB index: 0
- A: Zero gyro

## Operator Controller (Xbox)
- Driver Station USB index: 1
- A: Run feeder (manual feed)
- X: Shoot preset (near)
- Y: Shoot preset (mid)
- B: Shoot preset (far)
- Left Trigger: Intake fuel (reverse)
- Right Trigger: Intake fuel (forward)
- Left Bumper: Intake pivot (reverse)
- Right Bumper: Intake pivot (forward)

## Preset Meanings
- Near/Mid/Far are preset shot indices. These should map to increasing distance.
- Update the distance/voltage/RPM tables together when changing presets.
- If you rename presets to field locations, keep the order ascending by distance.

## Shooter Readiness Cues
- Wait until flywheel RPM is within tolerance before feeding.
- The feeder will only run when RPM is close enough to the preset target.
- Watch dashboard indicators:
  - Shooter RPM
  - Shooter/VisionStatus (Target OK / No target)

## Vision Usage
- If vision cannot see the target, the operator controller will pulse rumble.
- Dashboard will show "No target" and a hint to re-aim.
- Re-aim the robot or adjust position until vision locks on again.

## Autonomous Chooser
- Drive 2 Meters Auto: drives forward and stops.
- Shoot Fuel Auto: shoots fuel for a fixed duration.
- Shoot then Drive Auto: shoots, then drives forward.

## Safety Notes
- Avoid running intake rollers if the pivot is in a position that can jam.
- Stop shooter if target is not visible or shots are off-target.
- If the robot behaves unexpectedly, release buttons to cancel commands.

## Commented/Planned Bindings
- Right Stick: Shoot by vision distance (TODO: enable when vision is wired)
- A/B/X/Y: SysId routines (quasistatic/dynamic) - currently commented out
- Right Bumper/Left Bumper: Climb move up/down - currently commented out
