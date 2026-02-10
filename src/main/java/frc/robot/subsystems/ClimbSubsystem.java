//needs to stop once reaching a limit switch
package frc.robot.subsystems;

import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.Autos;
import frc.robot.subsystems.SwerveSubsystem;
import swervelib.SwerveDrive;

import java.io.File;

import com.ctre.phoenix6.sim.TalonFXSimState.MotorType;
import com.google.flatbuffers.Constants;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

//sets the motor as the climb motor
public class ClimbSubsystem extends SubsystemBase{
    public final SparkMax mClimbMotor = newSparkMax(Constants.ClimbMotorCanID, MotorType.kBrushless);

    public ClimbSubsystem(){
    }
}