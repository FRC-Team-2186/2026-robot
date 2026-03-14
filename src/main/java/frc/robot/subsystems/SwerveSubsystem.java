package frc.robot.subsystems;

import java.io.File;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.units.measure.LinearVelocity;
import static edu.wpi.first.units.Units.DegreesPerSecond;
import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.FeetPerSecond;
import static edu.wpi.first.units.Units.Meter;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.Degrees;
import com.pathplanner.lib.auto.AutoBuilder;
import static edu.wpi.first.units.Units.Radians;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import swervelib.SwerveController;
import swervelib.SwerveDrive;
import swervelib.math.SwerveMath;
import swervelib.parser.SwerveParser;
import swervelib.telemetry.SwerveDriveTelemetry;
import swervelib.telemetry.SwerveDriveTelemetry.TelemetryVerbosity;

@SuppressWarnings("unused")
public class SwerveSubsystem extends SubsystemBase {

  private final SlewRateLimiter xLimiter = new SlewRateLimiter(0.01);
  
  public void drive(double xSpeed, double rotSpeed){
    double x = xLimiter.calculate(xSpeed);
  }

  private static final LinearVelocity MAX_SPEED = FeetPerSecond.of(9);

  private final SwerveDrive mSwerveDrive;

  public SwerveSubsystem(File pConfigDir) {
    boolean isBlueAlliance = DriverStation.getAlliance().map(it -> it == DriverStation.Alliance.Blue).orElse(false);
    Pose2d startingPose = isBlueAlliance
        ? new Pose2d(new Translation2d(Meter.of(1), Meter.of(4)), Rotation2d.fromDegrees(0))
        : new Pose2d(new Translation2d(Meter.of(16), Meter.of(4)), Rotation2d.fromDegrees(180));

    SwerveDriveTelemetry.verbosity = TelemetryVerbosity.HIGH;

    try {
      mSwerveDrive = new SwerveParser(pConfigDir).createSwerveDrive(Constants.DRIVE_MAX_SPEED,
          startingPose);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    // Only enable heading correction while controlling the robot by angle
    mSwerveDrive.setHeadingCorrection(false);

    // Disable cosine compensation while in the simulator
    mSwerveDrive.setCosineCompensator(!SwerveDriveTelemetry.isSimulation);

    // Correct for skew that gets worse as angular velocity increases. Start with a coefficient of 0.1.
    // TODO: Tune me?
    mSwerveDrive.setAngularVelocityCompensation(true, true, 0.1);

    // Periodically push absolute offsets to the encoders
    mSwerveDrive.setModuleEncoderAutoSynchronize(true, 3);

    mSwerveDrive.setMotorIdleMode(false);
  }

  /**
   * This will zero (calibrate) the robot to assume the current position is facing forward
   * <p>
   * If red alliance rotate the robot 180 after the drviebase zero command hi :)
   */
  public void zeroGyroWithAlliance() {
    if (isRedAlliance()) {
      zeroGyro();
      // Set the pose 180 degrees
      resetOdometry(new Pose2d(getPose().getTranslation(), Rotation2d.fromDegrees(180)));
    } else {
      zeroGyro();
    }
  }

  public Command zeroGyroWithAliianceCommand() {
    return runOnce(this::zeroGyroCommand);
  }

  /**
   * Resets the gyro angle to zero and resets odometry to the same position, but facing toward 0.
   */
  public void zeroGyro() {
    mSwerveDrive.zeroGyro();
  }

  public Command zeroGyroCommand() {
    return runOnce(this::zeroGyro);
  }

  /**
   * Resets odometry to the given pose. Gyro angle and module positions do not need to be reset when calling this
   * method. However, if either gyro angle or module position is reset, this must be called in order for odometry to
   * keep working.
   *
   * @param initialHolonomicPose The pose to set the odometry to
   */
  public void resetOdometry(Pose2d initialHolonomicPose) {
    mSwerveDrive.resetOdometry(initialHolonomicPose);
  }

  /**
   * Gets the current pose (position and rotation) of the robot, as reported by odometry.
   *
   * @return The robot's pose
   */
  public Pose2d getPose() {
    return mSwerveDrive.getPose();
  }

  /**
   * Gets the current yaw angle of the robot, as reported by the swerve pose estimator in the underlying drivebase.
   * Note, this is not the raw gyro reading, this may be corrected from calls to resetOdometry().
   *
   * @return The yaw angle
   */
  public Rotation2d getHeading() {
    return getPose().getRotation();
  }

  /**
   * Get the swerve drive kinematics object.
   *
   * @return {@link SwerveDriveKinematics} of the swerve drive.
   */
  public SwerveDriveKinematics getKinematics() {
    return mSwerveDrive.kinematics;
  }

  /**
   * @return The YAGSL Swerve object.
   */
  public SwerveDrive getSwerveDrive() {
    return mSwerveDrive;
  }

  /**
   * Checks if the alliance is red, defaults to false if alliance isn't available.
   *
   * @return true if the red alliance, false if blue. Defaults to false if none is available.
   */
  private boolean isRedAlliance() {
    var alliance = DriverStation.getAlliance();
    return alliance.isPresent() ? alliance.get() == DriverStation.Alliance.Red : false;
  }

  public SwerveController getSwerveController() {
    return mSwerveDrive.getSwerveController();
  }

  public ChassisSpeeds convertOperatorInputToChassisSpeeds(double pTranslationX, double pTranslationY, double pRotateX,
      double pRotateY) {
    var scaled = SwerveMath.cubeTranslation(new Translation2d(pTranslationX, pTranslationY));

    return getSwerveController().getTargetSpeeds(scaled.getX(), scaled.getY(), pRotateX, pRotateY,
        getHeading().getRadians(),Constants.DRIVE_MAX_SPEED);
  }

  public Command driveRobotOrientedCommand(DoubleSupplier pTranslationX, DoubleSupplier pTranslationY,
      DoubleSupplier pAngularRotation) {
    return run(() -> {
      var translation = new Translation2d(pTranslationX.getAsDouble(), pTranslationY.getAsDouble())
          .times(getSwerveDrive().getMaximumChassisVelocity());
      var scaled = SwerveMath.scaleTranslation(translation, 0.8);

      var rotation = Math.pow(pAngularRotation.getAsDouble(), 3) * getSwerveDrive().getMaximumChassisAngularVelocity();

      mSwerveDrive.drive(scaled, rotation, true, false);
    });
  }

  public void driveRobotOriented(ChassisSpeeds velocity){
    mSwerveDrive.drive(velocity);
  }

  public Command driveFieldOrientedCommand(Supplier<ChassisSpeeds> pSpeedsSupplier) {
    return run(() -> {
      mSwerveDrive.driveFieldOriented(pSpeedsSupplier.get());
    });
  }

  public void driveFieldOriented(ChassisSpeeds velocity) {
    mSwerveDrive.driveFieldOriented(velocity);
  }
}
