package frc.robot.commands;

import frc.robot.subsystems.ClimbSubsystem;
import java.util.function.DoubleSupplier;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;

public class MoveUp extends Command {
  ClimbSubsystem mClimbSubsystem;
  DoubleSupplier mSpeedProvider;

  // Initialization
  public MoveUp(ClimbSubsystem pClimbSubsystem, DoubleSupplier pSpeedProvider) {
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
    double requestedSpeed = mSpeedProvider.getAsDouble();
    mClimbSubsystem.setClimbMotorSetPoint(requestedSpeed);
  }

  // Stopping when button press is over
  @Override
  public void end(boolean interrupted) {
    mClimbSubsystem.setClimbMotorSetPoint(0);
  }

  // Doesn't finish until end
  @Override
  public boolean isFinished() {
    return false;
  }
}
