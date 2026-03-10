package frc.robot.commands;

import frc.robot.subsystems.ClimbSubsystem;
import java.util.function.DoubleSupplier;
import edu.wpi.first.wpilibj2.command.Command;

public class MoveDown extends Command {
  ClimbSubsystem mClimbSubsystem;
  double mSpeedProvider;

  // Initialization
  public MoveDown(ClimbSubsystem pClimbSubsystem, double pSpeedProvider) {
    mClimbSubsystem = pClimbSubsystem;
    mSpeedProvider = pSpeedProvider;
    addRequirements(mClimbSubsystem);
  }

  @Override
  public void initialize() {
  }

  // Turning on the motor to lower the hook
  @Override
  public void execute() {
    mClimbSubsystem.setClimbMotorSetPoint(mSpeedProvider);
  }

  // Sets the voltage to 0 when the code is finished running
  @Override
  public void end(boolean interrupted) {
    mClimbSubsystem.setClimbMotorSetPoint(0);
  }

  // Doesn't until it ends
  @Override
  public boolean isFinished() {
    return false;
  }
}
