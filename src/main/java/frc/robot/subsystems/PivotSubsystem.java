package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import frc.robot.Constants;


@SuppressWarnings("unused")
public class PivotSubsystem extends SubsystemBase {
  private final SparkFlex mPivotIntakeMotor = new SparkFlex(Constants.PivotIntakeCanID, MotorType.kBrushless);

  private final DigitalInput mBottomLimitSwitch = new DigitalInput(Constants.BottomPivitSwitchDioChannel);
  private final DigitalInput mTopLimitSwitch = new DigitalInput(Constants.TopPivitSwitchDioChannel);

  public PivotSubsystem() {
    SparkMaxConfig PivotIntakeConfig = new SparkMaxConfig();
  PivotIntakeConfig.idleMode(IdleMode.kBrake);
    mPivotIntakeMotor.configure(PivotIntakeConfig, ResetMode.kNoResetSafeParameters,
        PersistMode.kNoPersistParameters);
    // var intakeFollowerConfig = new SparkFlexConfig().follow(mPivotIntakeMotor, false);
    // mPivotIntakeMotorRight.configure(intakeFollowerConfig, ResetMode.kResetSafeParameters,
    // PersistMode.kNoPersistParameters);
  }

  public double getPivotMotorSetpoint() {
    return mPivotIntakeMotor.get();
  }

  public void setPivotMotorSetpoint(double pValue) {
    mPivotIntakeMotor.set(pValue);
    // if (pValue > 0){
    //   System.out.println("hi");
    //   if (!(atTop() == false)){
    //     System.out.println("hi2");
    //     mPivotIntakeMotor.set(pValue);
    //   } else {
    //     System.out.println("hi3");
    //     mPivotIntakeMotor.set(0);
    //   }
    // }
    // if (pValue < 0) {
    //   System.out.println("hi4");
    //   if ((atBottom() == false)){
    //     System.out.println("hi5");
    //     mPivotIntakeMotor.set(pValue);
    //   } else {
    //     System.out.println("hi6");
    //     mPivotIntakeMotor.set(0);
    //   }
    // }
    // if (pValue == 0){
    //   mPivotIntakeMotor.set(0);
    // }

    /*
    if (!atBottom() || atTop()){
      mPivotIntakeMotor.set(pValue);
    } else {
      mPivotIntakeMotor.set(0);
    }

    
    */
    
  }

  public boolean atTop() {
    return mTopLimitSwitch.get();
  }

  public boolean atBottom() {
    return mBottomLimitSwitch.get();
  }

  @Override
  public void periodic(){
        SmartDashboard.putBoolean("Lower Pivot Limit Switchy Thiny", mBottomLimitSwitch.get());
    
    SmartDashboard.putBoolean("Top Pivot Limit Switchy Thiny", mTopLimitSwitch.get());
  }
}