package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.PivotSubsystem;

public class IntakePivot extends Command {

  private PivotSubsystem mPivotSubsystem;
  private double mSpeed;

  // Initialization
  public IntakePivot(PivotSubsystem pPivotSubsystem, double pivotintakespeedreverse) {
    mPivotSubsystem = pPivotSubsystem;
    mSpeed = pivotintakespeedreverse;
    addRequirements(mPivotSubsystem);
  }

  @Override
  public void initialize() {
  }

  // Starting the pivot motor to turn the motor
  @Override
  public void execute() {
    mPivotSubsystem.setPivotMotorSetpoint(mSpeed);
  }

  // If the command is finished, return true
  @Override
  public boolean isFinished() {
    return false;
  }

  // When button is unpressed, stop the motor
  @Override
  public void end(boolean interupted) {
    mPivotSubsystem.setPivotMotorSetpoint(0);
  }
}
