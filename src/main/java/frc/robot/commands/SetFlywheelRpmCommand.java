package frc.robot.commands;

import static edu.wpi.first.units.Units.RotationsPerSecond;

import java.util.function.Supplier;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.ShooterSubsystem;

public class SetFlywheelRpmCommand extends Command {
  private final PIDController mPidController = new PIDController(Constants.ShooterSubsystemConstants.kP, 0, 0);
  private final SimpleMotorFeedforward mFeedforward = new SimpleMotorFeedforward(Constants.ShooterSubsystemConstants.kS,
      Constants.ShooterSubsystemConstants.kV, Constants.ShooterSubsystemConstants.kA);
  private final ShooterSubsystem mShooterSubsystem;
  private final Supplier<AngularVelocity> mSpeedProvider;

  public SetFlywheelRpmCommand(ShooterSubsystem pShooterSubsystem, Supplier<AngularVelocity> pSpeedProvider) {
    mShooterSubsystem = pShooterSubsystem;
    mSpeedProvider = pSpeedProvider;

    addRequirements(mShooterSubsystem);
  }

  @Override
  public void initialize() {
    mPidController.reset();
  }

  @Override
  public void execute() {
    var desiredSpeed = mSpeedProvider.get();
    var desiredRps = desiredSpeed.in(RotationsPerSecond);
    var currentRps = mShooterSubsystem.getFlywheelSpeed().in(RotationsPerSecond);

    // The PID and Feedforward controllers both expect rotations per second
    var pidResult = mPidController.calculate(currentRps, desiredRps);
    var feedforward = mFeedforward.calculate(desiredRps);
    var voltage = pidResult + feedforward;

    Logger.recordOutput("Shooter/Desired Velocity", desiredSpeed);
    Logger.recordOutput("Shooter/PID Result", pidResult);
    Logger.recordOutput("Shooter/FeedForward Result", feedforward);
    mShooterSubsystem.setFlywheelMotorVoltage(voltage);
  }
}
