package frc.robot.commands;

import org.littletonrobotics.junction.Logger;
import org.photonvision.PhotonUtils;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.Vision;
import frc.robot.subsystems.SwerveSubsystem;

public class TurnToAprilTag extends Command {
  private final SwerveSubsystem mSwerve;
  private final Vision mVision;
  private final int mDesiredID;

  public TurnToAprilTag(SwerveSubsystem pSwerve, Vision pVision, int pDesiredID) {
    mSwerve = pSwerve;
    mVision = pVision;
    mDesiredID = pDesiredID;

    addRequirements(mSwerve);
  }

  @Override
  public void execute() {
    var currentResultOpt = mVision.getLatestResult();
    if (!currentResultOpt.isPresent()) {
      return;
    }

    var currentResult = currentResultOpt.get();
    if (!currentResult.hasTargets()) {
      return;
    }

    var desiredTargetOpt = currentResult.getTargets().stream().filter(it -> it.getFiducialId() == mDesiredID)
        .findFirst();
    if (!desiredTargetOpt.isPresent()) {
      return;
    }

    var desiredTarget = desiredTargetOpt.get();
    var targetFieldPoseOpt = mVision.getPoseEstimator().getFieldTags().getTagPose(desiredTarget.getFiducialId());
    if (!targetFieldPoseOpt.isPresent()) {
      return;
    }

    var targetFieldPose = targetFieldPoseOpt.get();
    var robotPose = PhotonUtils.estimateFieldToRobotAprilTag(desiredTarget.getBestCameraToTarget(), targetFieldPose,
        Vision.ROBOT_TO_CAMERA.inverse());
    var requiredYaw = PhotonUtils.getYawToPose(robotPose.toPose2d(), targetFieldPose.toPose2d());

    Logger.recordOutput("TurnToAprilTag/RequiredYaw", requiredYaw);

    var turnAmount = requiredYaw.getDegrees() * Constants.AIM_SERVO_PID_P;
    Logger.recordOutput("TurnToAprilTag/TurnAmount", turnAmount);

    mSwerve.driveRobotOrientedDirect(0, 0, turnAmount);
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
