package frc.robot.commands;

import frc.robot.subsystems.ClimbSubsystem;
import java.util.function.DoubleSupplier;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;

public class MoveDown extends Command{
  ClimbSubsystem mClimbSubsystem;
  DoubleSupplier mSpeedProvider;
  DigitalInput m_bottomlimitSwitch;

  //Initialization
  public MoveDown(ClimbSubsystem mClimbSubsystem, DoubleSupplier mSpeedProvider){
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
      if(m_bottomlimitSwitch.get()){
        mClimbSubsystem.setClimbMotorSetPoint(0);
      } else {
        mClimbSubsystem.setClimbMotorSetPoint(mSpeedProvider.getAsDouble());
      }
    }
    mClimbSubsystem.setClimbMotorSetPoint(mSpeedProvider.getAsDouble());
  }

  //Sets the voltage to 0 when the code is finished running
  public void end(){
    mClimbSubsystem.setClimbMotorSetPoint(0);
  }

  //Doesn't until it ends
  @Override
  public boolean isFinished(){
    return false;
  }
}

