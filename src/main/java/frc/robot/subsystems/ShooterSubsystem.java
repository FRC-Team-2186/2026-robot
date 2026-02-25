package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
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
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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

  // Setting the motors to their current settings and overwriting previous configs.
  public ShooterSubsystem() {

    leaderRpmEntry = Shuffleboard.getTab("Shooter").add("Shooter Leader RPM", 0.0).withWidget(BuiltInWidgets.kGraph)
        .getEntry();
    followerRpmEntry = Shuffleboard.getTab("Shooter").add("Shooter Follower RPM", 0.0).withWidget(BuiltInWidgets.kGraph)
        .getEntry();

    System.out.println("Working Shooter");

    SparkMaxConfig leaderShooterMotorconfig = new SparkMaxConfig();
    SparkMaxConfig followerShooterMotorconfig = new SparkMaxConfig();

    leaderShooterMotorconfig.idleMode(IdleMode.kCoast);
    followerShooterMotorconfig.idleMode(IdleMode.kCoast).follow(mLeaderShooterMotor, true);

    mLeaderShooterMotor.configure(leaderShooterMotorconfig, ResetMode.kNoResetSafeParameters,
        PersistMode.kNoPersistParameters);
    mFollowerShooterMotor.configure(followerShooterMotorconfig, ResetMode.kNoResetSafeParameters,
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
