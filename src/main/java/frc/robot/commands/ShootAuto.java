package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.Constants;
import edu.wpi.first.wpilibj.Timer;
@SuppressWarnings("unused")

public class ShootAuto extends Command {

  ShooterSubsystem mShooter;
  double mSpeed;
  Timer initial;
  // Initialization
  public ShootAuto(ShooterSubsystem pShooter) {
    mShooter = pShooter;
    mSpeed = 7;
    addRequirements(mShooter);
  }

  @Override
  public void initialize() {
    initial = new Timer();
    initial.start();
    System.out.println(initial);
  }

  // Starts both motors at the given speed
  @Override
  public void execute() {
    mShooter.setFlywheelMotorVoltage(mSpeed);
    mShooter.runFeeder(Constants.pos);
  }

  // Stops both motors when the button is let go of
  @Override
  public void end(boolean interrupted) {
    mShooter.setFeederMotorVoltage(0);
    mShooter.setFlywheelMotorVoltage(0);
  }

  @Override
  public boolean isFinished(){

    if (initial.get() > 7.0){
      System.out.println("Finishing Auto");
      return true;
    }
    return false;
  }
}
