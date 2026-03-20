package frc.robot.commands;

import static edu.wpi.first.units.Units.Radians;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SwerveSubsystem;

public class TurnToHeading extends Command {
  private final SwerveSubsystem mSwerveSubsystem;
  private final Angle mTargetHeading;
  private final PIDController mPidController = new PIDController(0.675, 0, 0);

  public TurnToHeading(SwerveSubsystem pSwerveSubsystem, Angle pTargetHeading) {
    mSwerveSubsystem = pSwerveSubsystem;
    mTargetHeading = pTargetHeading;

    addRequirements(mSwerveSubsystem);

    mPidController.enableContinuousInput(-Math.PI, Math.PI);
    mPidController.setTolerance(Math.toRadians(2.5));
  }

  @Override
  public void initialize() {
    mPidController.reset();
    mPidController.setSetpoint(MathUtil.angleModulus(mTargetHeading.in(Radians)));
  }

  @Override
  public void execute() {
    var currentHeading = MathUtil.angleModulus(mSwerveSubsystem.getHeading().getRadians());
    var pidResult = mPidController.calculate(currentHeading, MathUtil.angleModulus(mTargetHeading.in(Radians)));

    Logger.recordOutput("TurnToHeading/PID", pidResult);

    mSwerveSubsystem.driveRobotOrientedDirect(0, 0, pidResult);
  }

  @Override
  public boolean isFinished() {
    return mPidController.atSetpoint();
  }
}
