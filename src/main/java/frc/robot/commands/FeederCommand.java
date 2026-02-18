package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterSubsystems;
import frc.robot.Constants;

@SuppressWarnings("unused")
public class FeederCommand extends Command{
  
  ShooterSubsystems shooter;

  //Initialization
  public FeederCommand(ShooterSubsystems shooter){
    this.shooter = shooter;
  }

  @Override
  public void initialize(){}

  //Running the motor at constant value
  @Override
  public void execute() {
    shooter.m3_motor.setVoltage(Constants.ShooterSubsystemConstants.FeederSpeed);
    System.out.println("Running Feeder");
  }

  //Stopping the motor when button lifted up
  @Override
  public void end(boolean interrupted){
    shooter.m3_motor.setVoltage(0);
  }
}