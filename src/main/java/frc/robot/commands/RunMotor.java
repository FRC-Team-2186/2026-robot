package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterSubsystems;
import frc.robot.Constants;

@SuppressWarnings("unused")
public class RunMotor extends Command{

  ShooterSubsystems shooter;
  double speed;

  //Initialization
  public RunMotor(ShooterSubsystems shooter, double speed){
    this.shooter = shooter;
    this.speed = speed;
  }

  @Override
  public void initialize(){
  }

  //Starts both motors at the given speed
  @Override
  public void execute() {
    shooter.m_motor.setVoltage(speed);
    shooter.m2_motor.setVoltage(speed);
    System.out.println("Running Motor");  
  }

  //Stops both motors when the button is let go of
  @Override
  public void end(boolean interrupted){
    shooter.m_motor.setVoltage(0);
    shooter.m2_motor.setVoltage(0);
  }
}
