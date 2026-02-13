package frc.robot.commands;


import java.util.function.DoubleSupplier;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.IntakeSubsystem;


public class IntakePivot extends Command {


    private IntakeSubsystem mIntakeSubsystem;
    private DoubleSupplier mSpeedProvider;


    public IntakePivot(IntakeSubsystem subsystem, DoubleSupplier speed){
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
        mIntakeSubsystem.mPivotIntakeMotor.set(mSpeedProvider.getAsDouble());
    }


    @Override
    public boolean isFinished(){
        return false;
    }


    @Override
    public void end(boolean interupted){
        // System.out.println("end");
        mIntakeSubsystem.mPivotIntakeMotor.set(0);
   
}
}





