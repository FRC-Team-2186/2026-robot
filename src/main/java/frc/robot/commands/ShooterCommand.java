package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterSubsystems;
import frc.robot.Constants;

@SuppressWarnings("unused")
public class ShooterCommand extends Command{
  
  ShooterSubsystems shooter;

  public ShooterCommand(ShooterSubsystems shooter){
    this.shooter = shooter;
  }

  @Override
  public void initialize(){

  }

  @Override
  public void execute() {
    shooter.m3_motor.setVoltage(Constants.ShooterSubsystemConstants.FeederSpeed);
  }

  @Override
  public void end(boolean interrupted){
    shooter.m3_motor.setVoltage(0);
  }

}
