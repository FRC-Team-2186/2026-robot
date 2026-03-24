package frc.robot.commands;

import frc.robot.subsystems.ClimbSubsystem;
import java.util.function.DoubleSupplier;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.Timer;

public class AutoMoveUp extends Command{
  
  ClimbSubsystem mClimbSubsystem;
  double mSpeedProvider;
  Timer initial;
    
  public AutoMoveUp(ClimbSubsystem pClimbSubsystem, double pSpeedProvider) {
      mClimbSubsystem = pClimbSubsystem;
      mSpeedProvider = pSpeedProvider;
      addRequirements(mClimbSubsystem);
  }

  @Override
  public void initialize() {
    initial = new Timer();
    initial.start();
    //System.out.println(initial);
  }

    @Override
  public void execute() {
    mClimbSubsystem.setClimbMotorSetPoint(mSpeedProvider);
  }

  public void end(boolean interrupted) {
    mClimbSubsystem.setClimbMotorSetPoint(0);
  }

  @Override
  public boolean isFinished() {
    if (initial.get() > 7.0){
    //System.out.println("Finishing Auto");
    return true;
    }
    return false;
  }

}
