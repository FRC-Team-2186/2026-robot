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

  /*
   * Test CAN ID's public static final int FuelIntakeCanID = 10; public static final int PivotIntakeCanID = 22;
   */

  /*
   * public enum MotorTypeProfile { NEO, // Standard NEO brushless motor VORTEX // High-performance NEO Vortex motor }
   * 
   * public static final double kMaxRpm_SparkMaxNeo = 5676.0; // NEO free speed public static final double
   * kMaxRpm_FlexVortex = 6784.0; // Vortex free speed
   * 
   * public static final MotorTypeProfile kMotorTypeProfile = MotorTypeProfile.NEO;
   * 
   * public static final double kMaxRpm_Motor = (kMotorTypeProfile == MotorTypeProfile.NEO) ? kMaxRpm_SparkMaxNeo :
   * kMaxRpm_FlexVortex;
   * 
   * public static final double kFF = 12.0 / kMaxRpm_Motor;
   */

  public static final int FuelIntakeCanID = 24;
  public static final int PivotIntakeCanID = 22;
  public static final int PivotIntakeFollowerCanID = 11;
  
  public static final int pos = 0;

  //public double[] rpm_chart = {0,1649.75,2760.1,3910.5};
  //public double[] rpm_chart = {0,1649.75,3910.5,6350.5};
  public static final double lowRpm = 2760.1;
  public static final double mediumRpm = 3331.5;
  public static final double highRpm = 4190.5;
  public static final double highestRpm = 4895.5;

  public static double[] rpm_chart = {lowRpm,mediumRpm,highRpm,highestRpm};

  public static int BottomPivitSwitchDioChannel = 0;
  public static int TopPivitSwitchDioChannel = 1;

  public static int BottomClimbSwitchDioChannel = 2;
  public static int TopClimbSwitchDioChannel = 3;

  //public double[] voltage_chart = {0,3,5,7};
  //public double[] voltage_chart = {0,3,7,12};
  public static final double lowVoltage = 5;
  public static final double mediumVoltage = 6;
  public static final double highVoltage = 7.5;
  public static final double highestVoltage = 8.75;
  public static double[] voltage_chart = {lowVoltage,mediumVoltage,highVoltage,highestVoltage};
  
  //Inches
  public static double[] distance_chart_inches =  {66,117,135.24,153.5};

  //Feet
  public static double[] distance_chart_feet = {5.5,9.75,11.27,12.791};

  //Meters
  public static double[] distance_chart_meters = {1.6764, 2.9718, 3.435096, 3.8986968};

  public static final int ClimbMotorCanID = 60;

  public static final int kNearShotIndex = 0;
  public static final int kMidShotIndex = 1;
  public static final int kHighShotIndex = 2;
  public static final int kFarShotIndex = 3;
  
  public static final double kFeederToleranceRpm = 50.0;
  public static final double kFeederToleranceRpmFar = 100.0;
  public static final double kFeederLogPeriodSec = 0.25;

  public static final double fuelIntakeSpeed = 0.4;
  public static final double fuelIntakeSpeedReverse = -0.4;
  public static final double PivotIntakeSpeed = .05;
  public static final double PivotIntakeSpeedReverse = -.075;

  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
    public static final int kOperatorControllerPort = 1;
  }

  public static final class ShooterSubsystemConstants {
    public static final double FeederSpeed = 6;

    public static final int kFeederMotorCanId = 28; // SPARK Flex CAN ID
    public static final int kFlywheelMotorCanId = 27; // SPARK Flex CAN ID (Right)
    public static final int kFlywheelFollowerMotorCanId = 23; // SPARK Flex CAN ID (Left)

    /*
     * Test CAN ID's public static final int kFeederMotorCanId = 22; // SPARK Flex CAN ID public static final int
     * kFlywheelMotorCanId = 10; // SPARK Flex CAN ID (Right) public static final int kFlywheelFollowerMotorCanId = 25;
     * // SPARK Flex CAN ID (Left)
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
    public static final double kVortexKv = 0; // rpm/V
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
    public static final double kDrivingMotorReduction = (kDrivingWheelBevelGearTeeth
        * kDrivingWheelFirstStageSpurGearTeeth) / (kDrivingMotorPinionTeeth * kDrivingMotorBevelPinionTeeth);
    public static final double kDriveWheelFreeSpeedRps = (kDrivingMotorFreeSpeedRps * kWheelCircumferenceMeters)
        / kDrivingMotorReduction;
  }
}
