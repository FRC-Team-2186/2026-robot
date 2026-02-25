package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class ClimbSubsystem extends SubsystemBase {
  // Initializing a new motor
  private final SparkMax mClimbMotor = new SparkMax(Constants.ClimbMotorCanID, MotorType.kBrushless);

  public ClimbSubsystem() {
  }

  public double getClimbMotorSetPoint() {
    return mClimbMotor.getBusVoltage();
  }

  public void setClimbMotorSetPoint(double newClimbMotorSetPoint) {
    mClimbMotor.setVoltage(newClimbMotorSetPoint);
  }

  // Showing the voltage of the climbing motor on the Dashboard
  @Override
  public void periodic() {
    SmartDashboard.putNumber("Climbing Motor", mClimbMotor.getBusVoltage());
  }
}
