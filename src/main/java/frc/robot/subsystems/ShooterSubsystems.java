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

public class ShooterSubsystems extends SubsystemBase {

  private final SparkMax m_motor = new SparkMax(25, MotorType.kBrushless);
  private final SparkMax m2_motor = new SparkMax(22, MotorType.kBrushless);
 
  

  public ShooterSubsystems() {
    System.out.println("Working Shooter");

    SparkMaxConfig configs = new SparkMaxConfig();
    SparkMaxConfig configs2 = new SparkMaxConfig();

    configs.inverted(true);
    configs2.inverted(false);


    m_motor.configure(configs, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);
    m2_motor.configure(configs2, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);
  
  }

  // Method to run the motor at the desired speed from the button press
  public Command runMotor(double velocity) {
    return run(() -> {
      //m_motor.setVoltage(velocity);
      m2_motor.setVoltage(velocity);
    });
  }

  public Command stopMotor() {
    return run(() -> {
      m_motor.setVoltage(0);
      m2_motor.setVoltage(0);
    });
  }

}