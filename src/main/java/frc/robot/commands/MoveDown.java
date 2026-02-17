package frc.robot.commands;

//import frc.robot.Constants.OperatorConstants;
//import frc.robot.commands.Autos;
import frc.robot.subsystems.ClimbSubsystem;
//import frc.robot.subsystems.SwerveSubsystem;
//import swervelib.SwerveDrive;

//import java.io.File;
import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.DigitalInput;
//import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj2.command.Command;
//import edu.wpi.first.wpilibj2.command.Commands;
//import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
//import edu.wpi.first.wpilibj2.command.button.Trigger;


public class MoveDown extends Command{
    ClimbSubsystem mClimbSubsystem;
    DoubleSupplier mSpeedProvider;
    DigitalInput m_bottomlimitSwitch;


    //initializes the variables
    public MoveDown(ClimbSubsystem mClimbSubsystem, DoubleSupplier mSpeedProvider){
        this.mClimbSubsystem = mClimbSubsystem;
        this.mSpeedProvider = mSpeedProvider;
        addRequirements(mClimbSubsystem);
    }


    //zeroes the gyro
    @Override
    public void initialize(){
        //mClimbSubsystem.zeroGyro(); add this line after drivetrain is created
    }


    //the speed in constants is used to lift the hook
    @Override
     public void execute(){
        if(mSpeedProvider.getAsDouble() > 0){
            if(m_bottomlimitSwitch.get()){
                mClimbSubsystem.mClimbMotor.set(0);
            } else {
                mClimbSubsystem.mClimbMotor.set(mSpeedProvider.getAsDouble());
            }
        }
        
        mClimbSubsystem.mClimbMotor.set(mSpeedProvider.getAsDouble());
    }


    //it ends when the speed is zero
     public void end(){
        mClimbSubsystem.mClimbMotor.set(0);
     }

    //never finishes until it ends
    @Override
     public boolean isFinished(){
        return false;
    }
}


