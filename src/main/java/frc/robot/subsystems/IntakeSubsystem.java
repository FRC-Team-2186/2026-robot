
package frc.robot.subsystems;


import edu.wpi.first.wpilibj2.command.SubsystemBase;


import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import frc.robot.Constants;




public class IntakeSubsystem extends SubsystemBase {
   
    public final SparkMax mFuelIntakeMotor = new SparkMax(Constants.FuelIntakeCanID, MotorType.kBrushless);
    public final SparkMax mPivotIntakeMotor = new SparkMax(Constants.PivotIntakeCanID, MotorType.kBrushless);


    public IntakeSubsystem(){
        // var config = new SparkMaxConfig();
        // config.idleMode(IdleMode.kBrake);
    }




   
}






