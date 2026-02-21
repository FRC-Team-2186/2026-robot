
package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import frc.robot.Constants;

public class IntakeSubsystem extends SubsystemBase {
  //Initialization
  public final SparkMax mFuelIntakeMotor = new SparkMax(Constants.FuelIntakeCanID, MotorType.kBrushless);
  public final SparkMax mPivotIntakeMotor = new SparkMax(Constants.PivotIntakeCanID, MotorType.kBrushless);

  public IntakeSubsystem(){}

  //Shows the values of pivot and fuel in the Dashboard
  @Override
  public void periodic() {
    SmartDashboard.putNumber("Fuel Intake Motor",mFuelIntakeMotor.getBusVoltage());
    SmartDashboard.putNumber("Pivot Intake Motor",mPivotIntakeMotor.getBusVoltage());
  }
}