package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import java.lang.Math;
import static edu.wpi.first.units.Units.Radians;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.Volts;

import com.revrobotics.PersistMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.ResetMode;
import com.revrobotics.config.BaseConfig;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.units.measure.MutAngle;
import edu.wpi.first.units.measure.MutAngularVelocity;
import edu.wpi.first.units.measure.MutVoltage;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.sysid.SysIdRoutineLog;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import edu.wpi.first.wpilibj2.command.Command;

@SuppressWarnings("unused")
public class ShooterSubsystem extends SubsystemBase {
 
  public enum SetPointMode {
    RPM, VOLTAGE
  }

  public enum LookupMode {
    NEAREST, INTERPOLATED
  }

  // Initializing the 3 motors
  private final SparkFlex mLeaderShooterMotor = new SparkFlex(Constants.ShooterSubsystemConstants.kFlywheelMotorCanId,
      MotorType.kBrushless);
  private final SparkFlex mFollowerShooterMotor = new SparkFlex(
      Constants.ShooterSubsystemConstants.kFlywheelFollowerMotorCanId, MotorType.kBrushless);
  private final SparkFlex mFeederShooterMotor = new SparkFlex(Constants.ShooterSubsystemConstants.kFeederMotorCanId,
      MotorType.kBrushless);

  private final RelativeEncoder leaderEncoder = mLeaderShooterMotor.getEncoder();
  private final RelativeEncoder followerEncoder = mFollowerShooterMotor.getEncoder();

  private final SparkClosedLoopController pid = mLeaderShooterMotor.getClosedLoopController();

  private final DoubleLogEntry targetRpmLog = new DoubleLogEntry(DataLogManager.getLog(), "/shooter/target_rpm");
  private final DoubleLogEntry targetVoltageLog = new DoubleLogEntry(DataLogManager.getLog(),
      "/shooter/target_voltage");
  private final DoubleLogEntry leaderRpmLog = new DoubleLogEntry(DataLogManager.getLog(), "/shooter/leader_rpm");
  private final DoubleLogEntry followerRpmLog = new DoubleLogEntry(DataLogManager.getLog(), "/shooter/follower_rpm");

  private final GenericEntry leaderRpmEntry;
  private final GenericEntry followerRpmEntry;

  private double targetRpm = 0.0;
  private double targetVoltage = 0.0;
  private SetPointMode lastSetpointMode = SetPointMode.RPM;
  private double lastLoggedTargetRpm = Double.NaN;
  private double lastLoggedTargetVoltage = Double.NaN;
  private double lastFeederLogTimestamp = Double.NEGATIVE_INFINITY;

  // Mutable holder for unit-safe voltage values, persisted to avoid reallocation.
  private final MutVoltage m_appliedVoltage = Volts.mutable(0);
  // Mutable holder for unit-safe linear distance values, persisted to avoid reallocation.
  private final MutAngle m_angle = Radians.mutable(0);
  // Mutable holder for unit-safe linear velocity values, persisted to avoid reallocation.
  private final MutAngularVelocity m_velocity = RadiansPerSecond.mutable(0);

  private final SysIdRoutine mSysId = new SysIdRoutine(new SysIdRoutine.Config(),
      new SysIdRoutine.Mechanism(mLeaderShooterMotor::setVoltage, this::handleSysIdLog, this));
  
  // Setting the motors to their current settings and overwriting previous configs.
  public ShooterSubsystem() {

    leaderRpmEntry = Shuffleboard.getTab("Shooter").add("Shooter Leader RPM", 0.0).withWidget(BuiltInWidgets.kGraph)
        .getEntry();
    followerRpmEntry = Shuffleboard.getTab("Shooter").add("Shooter Follower RPM", 0.0).withWidget(BuiltInWidgets.kGraph)
        .getEntry();

    //System.out.println("Working Shooter");

    SparkFlexConfig leaderShooterMotorconfig = new SparkFlexConfig();
    SparkFlexConfig followerShooterMotorconfig = new SparkFlexConfig();
    SparkFlexConfig feederMotorConfig = new SparkFlexConfig();

    leaderShooterMotorconfig.idleMode(IdleMode.kBrake);
    leaderShooterMotorconfig.inverted(true);
    followerShooterMotorconfig.idleMode(IdleMode.kBrake).follow(mLeaderShooterMotor, true);
    feederMotorConfig.idleMode(IdleMode.kBrake);
    feederMotorConfig.inverted(true);

    mLeaderShooterMotor.configure(leaderShooterMotorconfig, ResetMode.kNoResetSafeParameters,
        PersistMode.kNoPersistParameters);
    mFollowerShooterMotor.configure(followerShooterMotorconfig, ResetMode.kNoResetSafeParameters,
        PersistMode.kNoPersistParameters);
    mFeederShooterMotor.configure(feederMotorConfig, ResetMode.kNoResetSafeParameters,
        PersistMode.kNoPersistParameters);
  }

  public double getFlywheelMotorVoltage() {
    return mLeaderShooterMotor.getBusVoltage();
  }

  public void setFlywheelMotorVoltage(double motorVoltage) {
    mLeaderShooterMotor.setVoltage(motorVoltage);
  }

  public double getFeederMotorVoltage() {
    return mFeederShooterMotor.getBusVoltage();
  }

  public void setFeederMotorVoltage(double feederSubsystemMotorVoltage) {
    mFeederShooterMotor.setVoltage(feederSubsystemMotorVoltage);
  }

   /**
   * Returns the current vision-estimated distance to the target (meters).
   *
   * Intent:
   * - Placeholder for future vision integration.
   * - ShootByDistance uses this distance to select a preset.
   *
   * Contract:
   * - Return Double.NaN when no valid target/distance is available.
   */

  public double getVisionDistanceMeters() {
    return Double.NaN;
  }

  /**
   * Returns the flywheel voltage for a preset index.
   *
   * Intent:
   * - Single source of truth for voltage lookup by preset.
   * - Keeps open-loop voltage control aligned with distance/RPM tables.
   */
  public double getTargetVoltageForPos(int pos) {
    // assert pos >= 0 && pos < voltage_chart.length : "pos out of range: " + pos;
    if (pos < 0 || pos >= Constants. voltage_chart.length) {
      throw new IllegalArgumentException("pos out of range: " + pos);
    }
    return Constants.voltage_chart[pos];
  }

  /**
   * Returns the target flywheel RPM for a preset index.
   *
   * Intent:
   * - Single source of truth for RPM lookup by preset.
   * - Used by feeder gating and future closed-loop RPM control.
   */
  public double getTargetRpmForPos(int pos) {
    // assert pos >= 0 && pos < rpm_chart.length : "pos out of range: " + pos;
    if (pos < 0 || pos >= Constants.rpm_chart.length) {
      throw new IllegalArgumentException("pos out of range: " + pos);
    }
    return Constants.rpm_chart[pos];
  }

  /**
   * Finds the nearest preset index for a given distance.
   *
   * Intent:
   * - Vision provides distance; we convert to the closest preset index.
   * - Keeps preset index as the central "selector" for voltage/RPM tables.
   */
  public int getPosForDistance(double distanceMeters) {
    int bestIndex = 0;
    double bestError = Double.POSITIVE_INFINITY;
    for (int i = 0; i < Constants.distance_chart_meters.length; i++) {
      double error = Math.abs(Constants.distance_chart_meters[i] - distanceMeters);
      if (error < bestError) {
        bestError = error;
        bestIndex = i;
      }
    }
    return bestIndex;
  }

  /**
   * Returns flywheel voltage from a distance measurement.
   *
   * Behavior:
   * - NEAREST: convert distance -> nearest preset index -> voltage.
   * - INTERPOLATED: linearly interpolate between table entries.
   */
  public double getTargetVoltageForDistance(double distanceMeters, LookupMode mode) {
    if (mode == LookupMode.INTERPOLATED) {
      return interpolateForDistance(distanceMeters, Constants.voltage_chart);
    }
    return getTargetVoltageForPos(getPosForDistance(distanceMeters));
  }

  /**
   * Returns target RPM from a distance measurement.
   *
   * Behavior:
   * - NEAREST: convert distance -> nearest preset index -> RPM.
   * - INTERPOLATED: linearly interpolate between table entries.
   */
  public double getTargetRpmForDistance(double distanceMeters, LookupMode mode) {
    if (mode == LookupMode.INTERPOLATED) {
      return interpolateForDistance(distanceMeters, Constants.rpm_chart);
    }
    return getTargetRpmForPos(getPosForDistance(distanceMeters));
  }

  /**
   * Linear interpolation helper for distance-based tables.
   *
   * Preconditions:
   * - distance_chart and values must be same length.
   * - distance_chart must be sorted ascending (in meters).
   *
   * Behavior:
   * - Clamps to endpoints when distance is outside the table range.
   * - Interpolates between the two bounding points otherwise.
   */
  private double interpolateForDistance(double distanceMeters, double[] values) {
    // assert distance_chart.length == values.length : "chart length mismatch";
    if (Constants.distance_chart_meters.length != values.length) {
      throw new IllegalArgumentException("chart length mismatch");
    }
    if (Constants.distance_chart_meters.length == 0) {
      return 0.0;
    }
    if (Constants.distance_chart_meters.length == 1) {
      return values[0];
    }
    if (distanceMeters <= Constants.distance_chart_meters[0]) {
      return values[0];
    }
    int lastIndex = Constants.distance_chart_meters.length - 1;
    if (distanceMeters >= Constants.distance_chart_meters[lastIndex]) {
      return values[lastIndex];
    }
    int lowerIndex = 0;
    for (int i = 0; i < lastIndex; i++) {
      if (distanceMeters <= Constants.distance_chart_meters[i + 1]) {
        lowerIndex = i;
        break;
      }
    }
    double lowerDistance = Constants.distance_chart_meters[lowerIndex];
    double upperDistance = Constants.distance_chart_meters[lowerIndex + 1];
    double lowerValue = values[lowerIndex];
    double upperValue = values[lowerIndex + 1];
    double distanceSpan = upperDistance - lowerDistance;
    if (distanceSpan <= 0.0) {
      return lowerValue;
    }
    double interpolationRatio = (distanceMeters - lowerDistance) / distanceSpan;
    return lowerValue + (upperValue - lowerValue) * interpolationRatio;
  }

  private void handleSysIdLog(SysIdRoutineLog pLogData) {

  }

  /**
   * Returns a command that will execute a quasistatic test in the given direction.
   *
   * @param direction The direction (forward or reverse) to run the test in
   */
  public Command sysIdQuasistatic(SysIdRoutine.Direction direction) {
    return mSysId.quasistatic(direction);
  }

  /**
   * Returns a command that will execute a dynamic test in the given direction.
   *
   * @param direction The direction (forward or reverse) to run the test in
   */
  public Command sysIdDynamic(SysIdRoutine.Direction direction) {
    return mSysId.dynamic(direction);
  }

  /**
   * Runs the feeder motor when the flywheel is up to speed (RPMs)
   *
   * Intent:
   * - Prevent feeding until the flywheel is at speed.
   * - Allow a looser tolerance for the farthest shot index.
   * - Rate-limit debug output to avoid console spam.
   *
   * Params:
   * - pos: index into rpm_chart[] that selects the target RPM.
   */
  public void runFeeder(int pos) {
    // assert pos >= 0 && pos < rpm_chart.length : "pos out of range: " + pos;
    if (pos < 0 || pos >= Constants.rpm_chart.length) {
      throw new IllegalArgumentException("pos out of range: " + pos);
    }

    // Read the current flywheel RPM from the leader motor’s encoder.
    double currentRpm =
        mLeaderShooterMotor.getEncoder().getVelocity();

    // Look up the target RPM for this position.
    double targetRpm = getTargetRpmForPos(pos);

    // Compute signed error: positive means below target, negative above.
    double error = targetRpm - currentRpm;

    // Use a looser tolerance for the farthest shot index.
    double tolerance =
        (pos == Constants.kFarShotIndex)
            ? Constants.kFeederToleranceRpmFar
            : Constants.kFeederToleranceRpm;
            // Rate-limit debug output to avoid flooding the console.
    double now = Timer.getFPGATimestamp();
    if (now - lastFeederLogTimestamp
        >= Constants.kFeederLogPeriodSec) {
      System.out.println("Shooter RPM error: " + error);
      lastFeederLogTimestamp = now;
    }

    // Only run the feeder once within tolerance.
    if (Math.abs(error) <= tolerance) {
      setFeederMotorVoltage(
          Constants.ShooterSubsystemConstants.FeederSpeed);
    } else {
      setFeederMotorVoltage(0);
    }

  }

  // Shows the voltage of the 3 motors in the dashboard
  @Override
  public void periodic() {
    // SmartDashboard.putNumber("Flywheel (Leader) Applied Voltage",
    //     mLeaderShooterMotor.getAppliedOutput() * mLeaderShooterMotor.getBusVoltage());
    // SmartDashboard.putNumber("Flywheel (Follower)",
    //     mFollowerShooterMotor.getAppliedOutput() * mFollowerShooterMotor.getBusVoltage());
    // SmartDashboard.putNumber("Flywheel RPM", mLeaderShooterMotor.getEncoder().getVelocity());
    // SmartDashboard.putNumber("Feeder", mFeederShooterMotor.getBusVoltage());
  }
}
