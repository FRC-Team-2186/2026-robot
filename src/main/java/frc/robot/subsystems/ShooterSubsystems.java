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
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import edu.wpi.first.wpilibj2.command.Command;

@SuppressWarnings("unused")
public class ShooterSubsystems extends SubsystemBase {

  //Initializing the 3 motors
  public final SparkMax m_motor = new SparkMax(Constants.ShooterSubsystemConstants.kFlywheelMotorCanId, MotorType.kBrushless);
  public final SparkMax m2_motor = new SparkMax(Constants.ShooterSubsystemConstants.kFlywheelFollowerMotorCanId, MotorType.kBrushless);
  public final SparkMax m3_motor = new SparkMax(Constants.ShooterSubsystemConstants.kFeederMotorCanId, MotorType.kBrushless);

  //Setting the motors to their current settings and overwriting previous configs.
  public ShooterSubsystems() {
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
  }

  //Shows the voltage of the 3 motors in the dashboard
  @Override
  public void periodic() {
    SmartDashboard.putNumber("Motor 1 (Left Shooter)",m_motor.getBusVoltage());
    SmartDashboard.putNumber("Motor 2 (Right Shooter)",m2_motor.getBusVoltage());
    SmartDashboard.putNumber("Motor 3 (Feeder)",m3_motor.getBusVoltage());
  }
}