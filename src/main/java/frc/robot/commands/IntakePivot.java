package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.IntakeSubsystem;

public class IntakePivot extends Command {

  private IntakeSubsystem mIntakeSubsystem;
  private double mSpeed;

  // Initialization
  public IntakePivot(IntakeSubsystem subsystem, double pivotintakespeedreverse) {
    mIntakeSubsystem = subsystem;
    mSpeed = pivotintakespeedreverse;
    addRequirements(mIntakeSubsystem);
  }

  @Override
  public void initialize() {
  }

  // Starting the pivot motor to turn the motor
  @Override
  public void execute() {
    mIntakeSubsystem.setPivotMotorSetpoint(mSpeed);
  }

  // If the command is finished, return true
  @Override
  public boolean isFinished() {
    return false;
  }

  // When button is unpressed, stop the motor
  @Override
  public void end(boolean interupted) {
    mIntakeSubsystem.setPivotMotorSetpoint(0);
  }
}
