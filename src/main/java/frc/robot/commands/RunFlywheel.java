package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.Constants;
import frc.robot.subsystems.AgitatorSubsystem;
import edu.wpi.first.wpilibj.Timer;

@SuppressWarnings("unused")
public class RunFlywheel extends Command {

  ShooterSubsystem mShooter;
  int mPos;
  AgitatorSubsystem mAgitator;
  Timer initial;

  // Initialization
  public RunFlywheel(ShooterSubsystem pShooter, int pPos, AgitatorSubsystem pAgitator) {
    mShooter = pShooter;
    mPos = pPos;
    mAgitator = pAgitator;
    addRequirements(mShooter);
  }

  @Override
  public void initialize() {
    initial = new Timer();
    initial.start();
  }

  // Starts both motors at the given speed
  @Override
  public void execute() {
    mAgitator.setAgitatorMotorSetpoint(.4);

    double val = initial.get();
    double currentTime = Math.floor(val);

    if ((val - currentTime) > .9){
      //mShooter.setFeederMotorVoltage(0);
      mShooter.setFlywheelMotorVoltage(Constants.voltage_chart[mPos] + 1);
    } else {
      mShooter.setFlywheelMotorVoltage(Constants.voltage_chart[mPos]);
      //mShooter.runFeeder(mPos);
    }

    mShooter.runFeeder(mPos);
  }

  // Stops both motors when the button is let go of
  @Override
  public void end(boolean interrupted) {
    mAgitator.setAgitatorMotorSetpoint(0);
    mShooter.setFeederMotorVoltage(0);
    mShooter.setFlywheelMotorVoltage(0);
  }
}
