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
public class ShooterSubsystems extends SubsystemBase {

  public enum SetPointMode{
    RPM,
    VOLTAGE
  }

  public enum LookupMode{
    NEAREST,
    INTERPOLATED
  }

  //Initializing the 3 motors
  public final SparkMax m_motor = new SparkMax(Constants.ShooterSubsystemConstants.kFlywheelMotorCanId, MotorType.kBrushless);
  public final SparkMax m2_motor = new SparkMax(Constants.ShooterSubsystemConstants.kFlywheelFollowerMotorCanId, MotorType.kBrushless);
  public final SparkMax m3_motor = new SparkMax(Constants.ShooterSubsystemConstants.kFeederMotorCanId, MotorType.kBrushless);

  private final RelativeEncoder leaderEncoder = m_motor.getEncoder();
  private final RelativeEncoder followerEncoder = m2_motor.getEncoder();
  
  private final SparkClosedLoopController pid = m_motor.getClosedLoopController();

  private final DoubleLogEntry targetRpmLog =
      new DoubleLogEntry(DataLogManager.getLog(), "/shooter/target_rpm");
  private final DoubleLogEntry targetVoltageLog =
      new DoubleLogEntry(DataLogManager.getLog(), "/shooter/target_voltage");
  private final DoubleLogEntry leaderRpmLog =
      new DoubleLogEntry(DataLogManager.getLog(), "/shooter/leader_rpm");
  private final DoubleLogEntry followerRpmLog =
      new DoubleLogEntry(DataLogManager.getLog(), "/shooter/follower_rpm");
  
  private final GenericEntry leaderRpmEntry;
  private final GenericEntry followerRpmEntry;

  private double targetRpm = 0.0;
  private double targetVoltage = 0.0;
  private SetPointMode lastSetpointMode = SetPointMode.RPM;
  private double lastLoggedTargetRpm = Double.NaN;
  private double lastLoggedTargetVoltage = Double.NaN;

  private static final double[] DISTANCE_TABLE_METERS = {
    Constants.kLowDistanceMeters,
    Constants.kMidDistanceMeters,
    Constants.kHighDistanceMeters
  };
  
  private static final double[] RPM_TABLE = {
    Constants.kLowRpm,
    Constants.kMidRpm,
    Constants.kHighRpm
  };
  
  private static final double[] VOLTAGE_TABLE = {
    Constants.kLowVoltage,
    Constants.kMidVoltage,
    Constants.kHighVoltage
  };

  //Setting the motors to their current settings and overwriting previous configs.
  public ShooterSubsystems() {

    leaderRpmEntry =
        Shuffleboard.getTab("Shooter")
            .add("Shooter Leader RPM", 0.0)
            .withWidget(BuiltInWidgets.kGraph)
            .getEntry();
    followerRpmEntry =
        Shuffleboard.getTab("Shooter")
            .add("Shooter Follower RPM", 0.0)
            .withWidget(BuiltInWidgets.kGraph)
            .getEntry();

    System.out.println("Working Shooter");

    SparkMaxConfig configs = new SparkMaxConfig();
    SparkMaxConfig configs2 = new SparkMaxConfig();
    SparkMaxConfig configs3 = new SparkMaxConfig();

    configs.inverted(true);
    configs2.inverted(false);
    configs3.inverted(true);

    m_motor.configure(configs, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);
    m2_motor.configure(configs2, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);
    m3_motor.configure(configs3, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);

    configs.closedLoop.feedForward.kV(Constants.kFF);
    configs2.closedLoop.feedForward.kV(Constants.kFF);
    configs3.closedLoop.feedForward.kV(Constants.kFF);
  }

  //Shows the voltage of the 3 motors in the dashboard
  @Override
  public void periodic() {
    SmartDashboard.putNumber("Motor 1 (Left Shooter)",m_motor.getBusVoltage());
    SmartDashboard.putNumber("Motor 2 (Right Shooter)",m2_motor.getBusVoltage());
    SmartDashboard.putNumber("Motor 3 (Feeder)",m3_motor.getBusVoltage());
  }
}