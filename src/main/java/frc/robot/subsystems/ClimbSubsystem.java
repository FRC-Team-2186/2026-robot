package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

@SuppressWarnings("unused")
public class ClimbSubsystem extends SubsystemBase {
  // Initializing a new motor
  private final SparkMax mClimbMotor = new SparkMax(Constants.ClimbMotorCanID, MotorType.kBrushless);
  private final DigitalInput mBottomLimitSwitch = new DigitalInput(Constants.BottomClimbSwitchDioChannel);
  private final DigitalInput mTopLimitSwitch = new DigitalInput(Constants.TopClimbSwitchDioChannel);

  public ClimbSubsystem() {
  }

  public double getClimbMotorSetPoint() {
    return mClimbMotor.getBusVoltage();
  }

  public void setClimbMotorSetPoint(double newClimbMotorSetPoint) {
    mClimbMotor.set(newClimbMotorSetPoint);
  }

  public void setPivotMotorSetpoint(double pValue) {
    if (pValue > 0){
      if (!(atTop() == false)){
        mClimbMotor.set(pValue);
      } else {
        mClimbMotor.set(0);
      }
    }
    if (pValue < 0) {
      if ((atBottom() == false)){
        mClimbMotor.set(pValue);
      } else {
        mClimbMotor.set(0);
      }
    }
    
  }

  public boolean atTop() {
    return mTopLimitSwitch.get();
  }

  public boolean atBottom() {
    return mBottomLimitSwitch.get();
  }

  // Showing the voltage of the climbing motor on the Dashboard
  @Override
  public void periodic() {
    //SmartDashboard.putNumber("Climbing Motor", mClimbMotor.getBusVoltage());
  }
  
}
