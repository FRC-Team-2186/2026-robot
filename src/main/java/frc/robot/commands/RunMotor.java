package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.Constants;

@SuppressWarnings("unused")
public class RunMotor extends Command{

  ShooterSubsystem shooter;
  double speed;

  //Initialization
  public RunMotor(ShooterSubsystem shooter, double speed){
    this.shooter = shooter;
    this.speed = speed;
  }

  @Override
  public void initialize(){
  }

  //Starts both motors at the given speed
  @Override
  public void execute() {
    shooter.setFlywheelMotorVoltage(speed);
  }

  //Stops both motors when the button is let go of
  @Override
  public void end(boolean interrupted){
    shooter.setFlywheelMotorVoltage(0);
  }
}
