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

//Joystick Button

@SuppressWarnings("unused")
public class ShooterSubsystems extends SubsystemBase {

  /*
  private final SparkMax m_motor = new SparkMax(27, MotorType.kBrushless);
  private final SparkMax m2_motor = new SparkMax(28, MotorType.kBrushless);
  private final SparkMax m3_motor = new SparkMax(23, MotorType.kBrushless);
  */

  private final SparkMax m_motor = new SparkMax(Constants.ShooterSubsystemConstants.kFlywheelMotorCanId, MotorType.kBrushless);
  private final SparkMax m2_motor = new SparkMax(Constants.ShooterSubsystemConstants.kFlywheelFollowerMotorCanId, MotorType.kBrushless);
  public final SparkMax m3_motor = new SparkMax(Constants.ShooterSubsystemConstants.kFeederMotorCanId, MotorType.kBrushless);
  
  public static boolean isFeederWorking = false;

  public static void madeFeederTrue(){
    isFeederWorking = true;
  }

  public static void madeFeederFalse(){
    isFeederWorking = false;
  }
  

  public ShooterSubsystems() {
    System.out.println("Working Shooter");

    SparkMaxConfig configs = new SparkMaxConfig();
    SparkMaxConfig configs2 = new SparkMaxConfig();
    SparkMaxConfig configs3 = new SparkMaxConfig();

    configs.inverted(true);
    configs2.inverted(false);
    configs3.inverted(false);


    m_motor.configure(configs, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);
    m2_motor.configure(configs2, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);
    m3_motor.configure(configs3, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);
  
  }

  // Method to run the motor at the desired speed from the button press
  public Command runMotor(double velocity) {
    return run(() -> {
      m_motor.setVoltage(velocity);
      m2_motor.setVoltage(velocity);
    });
  }

  public Command stopMotor() {
    return run(() -> {
      m_motor.setVoltage(0);
      m2_motor.setVoltage(0);
    });
  }

  /*
  public Command checkFeeder() {
    if (isFeederWorking == false){
      madeFeederTrue();
      System.out.println("Running");
      System.out.println(isFeederWorking);
      return runFeeder();
    } else {
      madeFeederFalse();
      System.out.println("Stopping");
      System.out.println(isFeederWorking);
      return stopFeeder();
    }
  }

  
  public Command runFeeder() {
    return run(() -> {
      m3_motor.setVoltage(6);
    });
  }

  public void end(boolean interrupted){
    m3_motor.setVoltage(0);
  }

  public Command stopFeeder() {
    return run(() -> {
      m3_motor.setVoltage(0);
    });
  }
  */

}