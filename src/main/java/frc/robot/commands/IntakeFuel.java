package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.IntakeSubsystem;

public class IntakeFuel extends Command {

  IntakeSubsystem mIntakeSubsystem;
  double mSpeed;

  // Initialization
  public IntakeFuel(IntakeSubsystem subsystem, double pSpeed) {
    mIntakeSubsystem = subsystem;
    mSpeed = pSpeed;
    addRequirements(mIntakeSubsystem);
  }

  @Override
  public void initialize() {
  }

  // Starting the motor to run the intake motor
  @Override
  public void execute() {
    mIntakeSubsystem.setFuelIntakeMotorSetpoint(mSpeed);
  }

  // If the method is finished, return false
  @Override
  public boolean isFinished() {
    return false;
  }

  // When button hobuild is over, turn off the motor
  @Override
  public void end(boolean interupted) {
    //System.out.println("end");
    mIntakeSubsystem.setFuelIntakeMotorSetpoint(0);
  }
}
