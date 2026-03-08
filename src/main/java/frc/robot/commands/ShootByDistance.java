package frc.robot.commands;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.ShooterSubsystem;

@SuppressWarnings("unused")
public class ShootByDistance extends Command {
  private final ShooterSubsystem mShooter;
  private final CommandXboxController mOperatorController;
  private static final double kRumblePulsePeriodSec = 0.6;
  private static final double kRumblePulseOnSec = 0.2;

  /**
   * Command that shoots using vision distance as the input.
   *
   * Flow:
   * 1) Read distance from vision (meters).
   * 2) Convert distance -> nearest preset index (pos).
   * 3) Use pos to look up voltage + RPM targets.
   * 4) Spin flywheel and gate feeder using the same pos.
   *
   * This keeps the preset index as the single source of truth for
   * distance/voltage/RPM and makes PIDF or interpolation easy to add later.
   */
  public ShootByDistance(ShooterSubsystem shooter, CommandXboxController operatorController) {
    mShooter = shooter;
    mOperatorController = operatorController;
    addRequirements(mShooter);
  }

  /**
   * Executes the distance-based shooting loop.
   *
   * Steps:
   * - Read vision distance in meters. If no target is found, distance should be NaN.
   * - When distance is NaN, show a dashboard warning and rumble the operator controller.
   * - Convert distance -> nearest preset index.
   * - Use preset index to command flywheel voltage.
   * - Gate the feeder based on RPM tolerance for that preset.
   */
  @Override
  public void execute() {
    // Vision gives distance in meters to the target.
    double distanceMeters = mShooter.getVisionDistanceMeters();
    if (Double.isNaN(distanceMeters)) {
      SmartDashboard.putString("Shooter/VisionStatus", "No target");
      SmartDashboard.putNumber("Shooter/VisionDistanceMeters", Double.NaN);
      SmartDashboard.putString("Shooter/VisionHint", "Vision lost - re-aim");
      double now = Timer.getFPGATimestamp();
      double pulsePhase = now % kRumblePulsePeriodSec;
      double rumbleLevel = (pulsePhase < kRumblePulseOnSec) ? 1.0 : 0.0;
      mOperatorController.getHID().setRumble(GenericHID.RumbleType.kBothRumble, rumbleLevel);
      mShooter.setFeederMotorVoltage(0);
      mShooter.setFlywheelMotorVoltage(0);
      return;
    }
    SmartDashboard.putString("Shooter/VisionStatus", "Target OK");
    SmartDashboard.putNumber("Shooter/VisionDistanceMeters", distanceMeters);
    SmartDashboard.putString("Shooter/VisionHint", "");
    mOperatorController.getHID().setRumble(GenericHID.RumbleType.kBothRumble, 0.0);
    // Convert distance -> nearest preset index.
    int pos = mShooter.getPosForDistance(distanceMeters);
    // Use the same preset index to get the voltage.
    double targetVoltage = mShooter.getTargetVoltageForPos(pos);
    // Spin the flywheel at the preset voltage.
    mShooter.setFlywheelMotorVoltage(targetVoltage);
    // Only run feeder when RPM is within tolerance for this preset.
    mShooter.runFeeder(pos);
  }

  /**
   * Stops the shooter motors when the command ends.
   *
   * Note:
   * - When bound with `.whileTrue(...)`, releasing the button cancels the command
   *   and calls `end(true)`.
   */
  @Override
  public void end(boolean interrupted) {
    // Stop everything when command ends or is interrupted.
    mOperatorController.getHID().setRumble(GenericHID.RumbleType.kBothRumble, 0.0);
    mShooter.setFeederMotorVoltage(0);
    mShooter.setFlywheelMotorVoltage(0);
  }
}