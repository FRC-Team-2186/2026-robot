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
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

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

  //public double[] rpm_chart = {0,1649.75,2760.1,3910.5};
  public double[] rpm_chart = {0,1649.75,3910.5,6350.5};
  
  //public double[] voltage_chart = {0,3,5,7};
  public double[] voltage_chart = {0,3,7,12};
  
  public double[] distance_chart =  {0,0,0,0};

  public enum SetPointMode {
    RPM, VOLTAGE
  }

  public enum LookupMode {
    NEAREST, INTERPOLATED
  }

  // Initializing the 3 motors
  private final SparkMax mLeaderShooterMotor = new SparkMax(Constants.ShooterSubsystemConstants.kFlywheelMotorCanId,
      MotorType.kBrushless);
  private final SparkMax mFollowerShooterMotor = new SparkMax(
      Constants.ShooterSubsystemConstants.kFlywheelFollowerMotorCanId, MotorType.kBrushless);
  private final SparkMax mFeederShooterMotor = new SparkMax(Constants.ShooterSubsystemConstants.kFeederMotorCanId,
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

  private static final double[] DISTANCE_TABLE_METERS = { Constants.kLowDistanceMeters, Constants.kMidDistanceMeters,
      Constants.kHighDistanceMeters };

  private static final double[] RPM_TABLE = { Constants.kLowRpm, Constants.kMidRpm, Constants.kHighRpm };

  private static final double[] VOLTAGE_TABLE = { Constants.kLowVoltage, Constants.kMidVoltage,
      Constants.kHighVoltage };

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

    System.out.println("Working Shooter");

    SparkMaxConfig leaderShooterMotorconfig = new SparkMaxConfig();
    SparkMaxConfig followerShooterMotorconfig = new SparkMaxConfig();
    SparkMaxConfig feederMotorConfig = new SparkMaxConfig();

    leaderShooterMotorconfig.idleMode(IdleMode.kCoast);
    leaderShooterMotorconfig.inverted(true);
    followerShooterMotorconfig.idleMode(IdleMode.kCoast).follow(mLeaderShooterMotor, true);
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

  public void runFeeder(int pos){
    System.out.println(rpm_chart[pos] - mLeaderShooterMotor.getEncoder().getVelocity());
    System.out.println((rpm_chart[pos] - mLeaderShooterMotor.getEncoder().getVelocity() <= 50) & (rpm_chart[pos] - mLeaderShooterMotor.getEncoder().getVelocity() >= 50));

    
    if (Math.abs(rpm_chart[pos] - mLeaderShooterMotor.getEncoder().getVelocity()) <= 50){
      setFeederMotorVoltage(Constants.ShooterSubsystemConstants.FeederSpeed);
    } else if ((pos == 3) && (Math.abs(rpm_chart[pos] - mLeaderShooterMotor.getEncoder().getVelocity()) <= 100)){
      setFeederMotorVoltage(Constants.ShooterSubsystemConstants.FeederSpeed);
    } else {
      setFeederMotorVoltage(0);
    }

  }

  // Shows the voltage of the 3 motors in the dashboard
  @Override
  public void periodic() {
    SmartDashboard.putNumber("Flywheel (Leader) Applied Voltage",
        mLeaderShooterMotor.getAppliedOutput() * mLeaderShooterMotor.getBusVoltage());
    SmartDashboard.putNumber("Flywheel (Follower)",
        mFollowerShooterMotor.getAppliedOutput() * mFollowerShooterMotor.getBusVoltage());
    SmartDashboard.putNumber("Flywheel RPM", mLeaderShooterMotor.getEncoder().getVelocity());
    SmartDashboard.putNumber("Feeder", mFeederShooterMotor.getBusVoltage());
  }
}
