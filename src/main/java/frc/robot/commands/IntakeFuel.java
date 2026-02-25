package frc.robot.commands;

import java.util.function.DoubleSupplier;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.IntakeSubsystem;

public class IntakeFuel extends Command {

    IntakeSubsystem mIntakeSubsystem;
    double mSpeedProvider;
   
    //Initialization
    public IntakeFuel(IntakeSubsystem subsystem, double fuelintakespeedreverse){
        mIntakeSubsystem = subsystem;
        mSpeedProvider = fuelintakespeedreverse;
        addRequirements(mIntakeSubsystem);
    }

    @Override
    public void initialize(){}
 
    //Starting the motor to run the intake motor
    @Override
    public void execute(){
        System.out.println("executing");
        mIntakeSubsystem.setPivotMotorSetpoint(mSpeedProvider);
    }

    //If the method is finished, return false
    @Override
    public boolean isFinished(){
        return false;
    }

    //When button hold is over, turn off the motor
    @Override
    public void end(boolean interupted){
        System.out.println("end");
        mIntakeSubsystem.setPivotMotorSetpoint(0);
    }
}