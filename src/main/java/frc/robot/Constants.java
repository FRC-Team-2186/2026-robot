// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import static edu.wpi.first.units.Units.FeetPerSecond;
import static edu.wpi.first.units.Units.MetersPerSecond;
import edu.wpi.first.units.LinearVelocityUnit;
import edu.wpi.first.units.measure.LinearVelocity;
import edu.wpi.first.units.measure.Velocity;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean constants. This
 * class should not be used for any other purpose. All constants should be declared globally (i.e. public static). Do
 * not put anything functional in this class.
 *
 * <p>
 * It is advised to statically import this class (or one of its inner classes) wherever the constants are needed, to
 * reduce verbosity.
 */
@SuppressWarnings("unused")
public final class Constants {
  public static final LinearVelocity DRIVE_MAX_SPEED = FeetPerSecond.of(12);

  /* Test CAN ID's
  public static  final int FuelIntakeCanID = 10;
  public static final int PivotIntakeCanID = 22;
  */
  
  public static  final int FuelIntakeCanID = 24;
  public static final int PivotIntakeCanID = 11;

  public static final int ClimbMotorCanID = 0;

  public static final double IntakeSpeed = 0.5;

  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
    public static final int kOperatorControllerPort = 1                                                                             ;
  }

  public static final class ShooterSubsystemConstants {
    public static final double FeederSpeed = 6;
    
    public static final int kFeederMotorCanId = 28;    // SPARK Flex CAN ID
    public static final int kFlywheelMotorCanId = 27;  // SPARK Flex CAN ID (Right)
    public static final int kFlywheelFollowerMotorCanId = 23;  // SPARK Flex CAN ID (Left)

  /* Test CAN ID's 
    public static final int kFeederMotorCanId = 22;    // SPARK Flex CAN ID
    public static final int kFlywheelMotorCanId = 10;  // SPARK Flex CAN ID (Right)
    public static final int kFlywheelFollowerMotorCanId = 25;  // SPARK Flex CAN ID (Left)
    */

    public static final class FeederSetpoints {
      public static final double kFeed = 0;
    }

    public static final class FlywheelSetpoints {
      public static final double kShootRpm = 0;
      public static final double kVelocityTolerance = 0;
    }
  }

  public static final class NeoMotorConstants {
    public static final double kFreeSpeedRpm = 0;
    public static final double kVortexKv = 0;   // rpm/V
  }

  public static final class ModuleConstants {
    // The MAXSwerve module can be configured with one of three pinion gears: 12T,
    // 13T, or 14T. This changes the drive speed of the module (a pinion gear with
    // more teeth will result in a robot that drives faster).
    public static final int kDrivingMotorPinionTeeth = 0;

    // Calculations required for driving motor conversion factors and feed forward
    public static final double kDrivingMotorFreeSpeedRps = NeoMotorConstants.kFreeSpeedRpm / 60;
    public static final double kWheelDiameterMeters = Units.inchesToMeters(0);
    public static final double kWheelCircumferenceMeters = kWheelDiameterMeters * Math.PI;
    // 45 teeth on the wheel's bevel gear, 22 teeth on the first-stage spur gear, 15
    // teeth on the bevel pinion
    public static final double kDrivingWheelBevelGearTeeth = 0;
    public static final double kDrivingWheelFirstStageSpurGearTeeth = 0;
    public static final double kDrivingMotorBevelPinionTeeth = 0;
    public static final double kDrivingMotorReduction = (kDrivingWheelBevelGearTeeth * kDrivingWheelFirstStageSpurGearTeeth)
        / (kDrivingMotorPinionTeeth * kDrivingMotorBevelPinionTeeth);
    public static final double kDriveWheelFreeSpeedRps = (kDrivingMotorFreeSpeedRps * kWheelCircumferenceMeters)
        / kDrivingMotorReduction;
  }
}