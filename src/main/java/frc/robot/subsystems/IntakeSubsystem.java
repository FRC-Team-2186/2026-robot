
package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkMax;
import frc.robot.Constants;

public class IntakeSubsystem extends SubsystemBase {
  //Initialization
  private final SparkMax mFuelIntakeMotor = new SparkMax(Constants.FuelIntakeCanID, MotorType.kBrushless);
  private final SparkFlex mPivotIntakeMotor = new SparkFlex(Constants.PivotIntakeCanID, MotorType.kBrushless);
  //private final SparkFlex mPivotIntakeMotorRight = new SparkFlex(Constants.PivotIntakeFollowerCanID, MotorType.kBrushless);

  public IntakeSubsystem() {
    //var intakeFollowerConfig = new SparkFlexConfig().follow(mPivotIntakeMotor, false);
    //mPivotIntakeMotorRight.configure(intakeFollowerConfig, ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);
  }

  public double getPivotMotorSetpoint() {
    return mPivotIntakeMotor.get();
  }

  public void setPivotMotorSetpoint(double pValue) {
    mPivotIntakeMotor.set(pValue);
    // mPivotIntakeMotorRight.set(-pValue);
  }

  public double getFuelIntakeMotorSetpoint() {
    return mFuelIntakeMotor.get();
  }

  public void setFuelIntakeMotorSetpoint(double pValue) {
    mFuelIntakeMotor.set(pValue);
  }

  //Shows the values of pivot and fuel in the Dashboard
  @Override
  public void periodic() {
    SmartDashboard.putNumber("Fuel Intake Motor",mFuelIntakeMotor.getBusVoltage());
    SmartDashboard.putNumber("Pivot Intake Motor",mPivotIntakeMotor.getBusVoltage());
    //SmartDashboard.putNumber("Pivot 2 Intake Motor",mPivotIntakeMotorRight.getBusVoltage());
  }
}