package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.Constants;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.subsystems.AgitatorSubsystem;
@SuppressWarnings("unused")

public class ShootAuto extends Command {

  ShooterSubsystem mShooter;
  double mSpeed;
  Timer initial;
  AgitatorSubsystem mAgitator;
  // Initialization
  public ShootAuto(ShooterSubsystem pShooter, AgitatorSubsystem pAgitator) {
    mShooter = pShooter;
    mSpeed = 7;
    mAgitator = pAgitator;
    addRequirements(mShooter);
  }

  @Override
  public void initialize() {
    initial = new Timer();
    initial.start();
    //System.out.println(initial);
  }

  // Starts both motors at the given speed
  /*
  @Override
  public void execute() {
    if (initial.get() < 3.0){
      mIntake.setPivotMotorSetpoint(Constants.PivotIntakeSpeedReverse);
    } else {
      mIntake.setPivotMotorSetpoint(0);
    }
    if (initial.get() > 3.0){
      mIntake.setFuelIntakeMotorSetpoint(Constants.fuelIntakeSpeed);
      mShooter.setFlywheelMotorVoltage(mSpeed);
      mShooter.runFeeder(Constants.pos);
    }
  }*/

  @Override
  public void execute() {
    mShooter.setFlywheelMotorVoltage(6);

    if (initial.get() > 2.5){
      mAgitator.setAgitatorMotorSetpoint(.5);
      mShooter.setFeederMotorVoltage(6);
    }
    //mShooter.runFeeder(Constants.kMidShotIndex);
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
      //System.out.println("Finishing Auto");
      return true;
    }
    return false;
  }
}