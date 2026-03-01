package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.Constants;

@SuppressWarnings("unused")
public class RunFlywheel extends Command {

  ShooterSubsystem mShooter;
  int mPos;

  // Initialization
  public RunFlywheel(ShooterSubsystem pShooter, int pPos) {
    mShooter = pShooter;
    mPos = pPos;

    addRequirements(mShooter);
  }

  @Override
  public void initialize() {
  }

  // Starts both motors at the given speed
  @Override
  public void execute() {
    double targetVoltage = mShooter.getTargetVoltageForPos(mPos);
    mShooter.setFlywheelMotorVoltage(targetVoltage);
    mShooter.runFeeder(mPos);
  }

  // Stops both motors when the button is let go of
  @Override
  public void end(boolean interrupted) {
    mShooter.setFeederMotorVoltage(0);
    mShooter.setFlywheelMotorVoltage(0);
  }
}
