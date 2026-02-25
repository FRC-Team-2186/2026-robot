package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.Constants;

@SuppressWarnings("unused")
public class RunFlywheel extends Command {

  ShooterSubsystem mShooter;
  double mSpeed;

  // Initialization
  public RunFlywheel(ShooterSubsystem pShooter, double pSpeed) {
    mShooter = pShooter;
    mSpeed = pSpeed;

    addRequirements(mShooter);
  }

  @Override
  public void initialize() {
  }

  // Starts both motors at the given speed
  @Override
  public void execute() {
    mShooter.setFlywheelMotorVoltage(mSpeed);
  }

  // Stops both motors when the button is let go of
  @Override
  public void end(boolean interrupted) {
    mShooter.setFlywheelMotorVoltage(0);
  }
}
