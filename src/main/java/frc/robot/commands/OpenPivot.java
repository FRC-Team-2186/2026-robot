package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.Constants;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.subsystems.PivotSubsystem;
@SuppressWarnings("unused")

public class OpenPivot extends Command{
  PivotSubsystem mPivotSubsystem;
  Timer initial;

  public OpenPivot(PivotSubsystem pPivotSubsystem){
    mPivotSubsystem = pPivotSubsystem;
    addRequirements(mPivotSubsystem);
  }

  @Override
  public void initialize() {
    initial = new Timer();
    initial.start();
    //System.out.println(initial);
  }

  @Override
  public void execute() {
    mPivotSubsystem.setPivotMotorSetpoint(0.1);
  }

  @Override
  public boolean isFinished(){

    if (initial.get() > .5){
      //System.out.println("Finishing Auto");
      return true;
    }
    return false;
  }

  @Override
  public void end(boolean interrupted) {
    mPivotSubsystem.setPivotMotorSetpoint(0);
  }

}

