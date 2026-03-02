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
    //System.out.println(mSpeed);
    int pos = 0;
    if (mSpeed == Constants.lowVoltage){
      pos = 0;
    } else if (mSpeed == Constants.mediumVoltage){
      pos = 1;
    } else if (mSpeed == Constants.highVoltage){
      pos = 2;
    } else if (mSpeed == Constants.highestVoltage){
      pos = 3;
    }
    //System.out.println(pos);
    mShooter.runFeeder(pos);
  }

  // Stops both motors when the button is let go of
  @Override
  public void end(boolean interrupted) {
    mShooter.setFeederMotorVoltage(0);
    mShooter.setFlywheelMotorVoltage(0);
  }
}
