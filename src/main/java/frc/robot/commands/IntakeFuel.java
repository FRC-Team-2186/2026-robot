package frc.robot.commands;


import java.util.function.DoubleSupplier;


import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.IntakeSubsystem;


public class IntakeFuel extends Command {


    IntakeSubsystem mIntakeSubsystem;
    DoubleSupplier mSpeedProvider;
   
    public IntakeFuel(IntakeSubsystem subsystem, DoubleSupplier speed){
        mIntakeSubsystem = subsystem;
        mSpeedProvider = speed;
        addRequirements(mIntakeSubsystem);
    }


    @Override
    public void initialize(){
        // System.out.println("initialize");
    }


    @Override
    public void execute(){
        // System.out.println("executing");
        mIntakeSubsystem.mFuelIntakeMotor.set(mSpeedProvider.getAsDouble());
    }


   


    @Override
    public boolean isFinished(){
        return false;
    }


    @Override
    public void end(boolean interupted){
        // System.out.println("end");
        mIntakeSubsystem.mFuelIntakeMotor.set(0);
    }
}





