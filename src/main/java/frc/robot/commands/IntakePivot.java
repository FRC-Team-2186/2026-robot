package frc.robot.commands;

import java.util.function.DoubleSupplier;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.IntakeSubsystem;

public class IntakePivot extends Command {

    private IntakeSubsystem mIntakeSubsystem;
    private double mSpeed;

    //Initialization
    public IntakePivot(IntakeSubsystem subsystem, double pivotintakespeedreverse){
        mIntakeSubsystem = subsystem;
        mSpeed = pivotintakespeedreverse;
        addRequirements(mIntakeSubsystem);
    }
   
    @Override
    public void initialize(){
       System.out.println("initialize");
    }

    //Starting the pivot motor to turn the motor
    @Override
    public void execute(){

        System.out.println("executing");
        //System.out.println(mIntakeSubsystem.mPivotIntakeMotor.get());
        //System.out.println(mIntakeSubsystem.mPivotIntakeMotorRight.get());
        mIntakeSubsystem.setPivotMotorSetpoint(mSpeed);
    }

    //If the command is finished, return true
    @Override
    public boolean isFinished(){
        // System.out.println("isFinished");

        return false;
    }

    //When button is unpressed, stop the motor
    @Override
    public void end(boolean interupted){
        System.out.println("end");
        mIntakeSubsystem.setPivotMotorSetpoint(0);
    }
}