package frc.robot.commands;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Radians;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SwerveSubsystem;

public class TurnToPointCommand extends Command {
  private static final TrapezoidProfile.Constraints TURN_CONSTRAINTS = new TrapezoidProfile.Constraints(4, 4);
  private final SwerveSubsystem mSwerveSubsystem;
  private final Translation2d mTargetPosition;

  private final ProfiledPIDController mPidController = new ProfiledPIDController(0.01, 0, 0, TURN_CONSTRAINTS);

  public TurnToPointCommand(SwerveSubsystem pSwerveSubsystem, Translation2d pTargetPosition) {
    mSwerveSubsystem = pSwerveSubsystem;
    mTargetPosition = pTargetPosition;

    mPidController.setTolerance(Math.toRadians(3));
    mPidController.enableContinuousInput(-Math.PI, Math.PI);

    addRequirements(pSwerveSubsystem);
  }

  @Override
  public void initialize() {
    mPidController.reset(mSwerveSubsystem.getHeading().getRadians());
  }

  @Override
  public void execute() {
    var currentPose = mSwerveSubsystem.getPose();
    var delta = currentPose.getTranslation().minus(mTargetPosition);
    var yaw = Radians.of(Math.atan2(delta.getY(), delta.getX()));
    var heading = mSwerveSubsystem.getHeading();

    var theta = mPidController.calculate(heading.getRadians(), yaw.in(Radians));

    Logger.recordOutput("TurnToPoint/CurrentPose", currentPose);
    Logger.recordOutput("TurnToPoint/TargetPosition", mTargetPosition);
    Logger.recordOutput("TurnToPoint/Delta", delta);
    Logger.recordOutput("TurnToPoint/Yaw", yaw.in(Degrees));
    Logger.recordOutput("TurnToPoint/Theta", theta);

    mSwerveSubsystem.driveRobotOrientedDirect(0, 0, theta);
  }

  @Override
  public void end(boolean interrupted) {
    mSwerveSubsystem.driveRobotOrientedDirect(0, 0, 0);
  }

  @Override
  public boolean isFinished() {
    return mPidController.atSetpoint();
  }
}
