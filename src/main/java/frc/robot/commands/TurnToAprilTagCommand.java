package frc.robot.commands;

import java.util.Optional;
import java.util.function.Supplier;

import org.littletonrobotics.junction.Logger;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Vision;
import frc.robot.subsystems.SwerveSubsystem;

public class TurnToAprilTagCommand extends Command {
  private final SwerveSubsystem mSwerveSubsystem;
  private final Vision mVision;
  private final Supplier<Integer> mTargetIDSupplier;

  public TurnToAprilTagCommand(SwerveSubsystem pSwerveSubsystem, Vision pVision, Supplier<Integer> pTargetIDSupplier) {
    mSwerveSubsystem = pSwerveSubsystem;
    mVision = pVision;
    mTargetIDSupplier = pTargetIDSupplier;

    addRequirements(mSwerveSubsystem);
  }

  @Override
  public void execute() {
    int targetID = mTargetIDSupplier.get();
    var target = getTarget(targetID);
    var yaw = target.map(it -> it.getYaw());
    var turnAmount = yaw.map(it -> it * -getP());

    var finalTheta = turnAmount.orElse(0.0);

    Logger.recordOutput("TurnToAprilTag/IsTargetPresent", target.isPresent());
    Logger.recordOutput("TurnToAprilTag/Theta", finalTheta);

    mSwerveSubsystem.driveRobotOrientedDirect(0, 0, finalTheta);
  }

  @Override
  public boolean isFinished() {
    var yaw = getTarget(mTargetIDSupplier.get()).map(it -> it.getYaw()).orElse(0.0);
    return Math.abs(yaw) < 1.5;
  }

  private Optional<PhotonTrackedTarget> getTarget(int pTagID) {
    return mVision.getLatestResult()
        .flatMap(targets -> targets.getTargets().stream().filter(it -> it.getFiducialId() == pTagID).findFirst());
  }

  private double getP() {
    return SmartDashboard.getNumber("TurnToAprilTag/kP", 0.01);
  }
}
