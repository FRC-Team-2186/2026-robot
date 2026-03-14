package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.Constants;
import frc.robot.subsystems.AgitatorSubsystem;

@SuppressWarnings("unused")
public class RunFlywheel extends Command {

  ShooterSubsystem mShooter;
  int mPos;
  AgitatorSubsystem mAgitator;

  // Initialization
  public RunFlywheel(ShooterSubsystem pShooter, int pPos, AgitatorSubsystem pAgitator) {
    mShooter = pShooter;
    mPos = pPos;
    mAgitator = pAgitator;

    addRequirements(mShooter);
  }

  @Override
  public void initialize() {
  }

  // Starts both motors at the given speed
  @Override
  public void execute() {
    mAgitator.setAgitatorMotorSetpoint(.5);
    mShooter.setFlywheelMotorVoltage(Constants.voltage_chart[mPos]);
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
