package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SwerveSubsystem;

@SuppressWarnings("unused")
public class DriveTwoMeters extends Command{

  SwerveSubsystem mSwerveSubsystem;
  Timer initial;

  public DriveTwoMeters(SwerveSubsystem pSwerveSubsystem){
    mSwerveSubsystem = pSwerveSubsystem;
    addRequirements(mSwerveSubsystem);
  }

  @Override
  public void initialize(){
    initial = new Timer();
    initial.start();
    mSwerveSubsystem.zeroGyro();
    System.out.println(initial);
  }

  @Override
  public void execute(){
    mSwerveSubsystem.driveRobotOriented(new ChassisSpeeds(1, 0, 0));
  }

  @Override
  public void end(boolean interrupted) {
    mSwerveSubsystem.driveRobotOriented(new ChassisSpeeds(0,0,0));
  }

  @Override
  public boolean isFinished(){

    if (initial.get() > 1.0){
      System.out.println("Finishing Auto");
      return true;
    }
    return false;
  }
  
}
