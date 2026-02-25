package frc.robot.commands;

import frc.robot.subsystems.ClimbSubsystem;
import java.util.function.DoubleSupplier;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;

public class MoveUp extends Command{
  ClimbSubsystem mClimbSubsystem;
  DoubleSupplier mSpeedProvider;

  //Initialization
  public MoveUp(ClimbSubsystem mClimbSubsystem, DoubleSupplier mSpeedProvider){
    this.mClimbSubsystem = mClimbSubsystem;
    this.mSpeedProvider = mSpeedProvider;
    addRequirements(mClimbSubsystem);
  }

  @Override
  public void initialize(){}

  //Turning on the motor to lower the hook
  @Override
  public void execute(){
    if(mSpeedProvider.getAsDouble() > 0){
        mClimbSubsystem.setClimbMotorSetPoint(mSpeedProvider.getAsDouble());
      }
    
    mClimbSubsystem.setClimbMotorSetPoint(mSpeedProvider.getAsDouble());
    double requestedSpeed = mSpeedProvider.getAsDouble();
    mClimbSubsystem.setClimbMotorSetPoint(requestedSpeed);

  }

  //Stopping when button press is over
  public void end(){
    mClimbSubsystem.setClimbMotorSetPoint(0);
  }

  //Doesn't finish until end
  @Override
  public boolean isFinished(){
    return false;
  }
}