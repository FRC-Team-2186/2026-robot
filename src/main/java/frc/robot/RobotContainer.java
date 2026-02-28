// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.Configs.ShooterSubsystems;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.Autos;
import frc.robot.commands.DriveTwoMeters;
import frc.robot.commands.FeederCommand;
import frc.robot.commands.ShootAuto;
import frc.robot.subsystems.SwerveSubsystem;
import swervelib.SwerveDrive;
import frc.robot.commands.IntakeFuel;
import frc.robot.commands.IntakePivot;
import swervelib.SwerveInputStream;
import java.io.File;

import com.pathplanner.lib.auto.AutoBuilder;

import frc.robot.subsystems.ClimbSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.commands.RunFlywheel;
import frc.robot.commands.MoveUp;
import frc.robot.commands.MoveDown;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a "declarative" paradigm, very
 * little robot logic should actually be handled in the {@link Robot} periodic methods (other than the scheduler calls).
 * Instead, the structure of the robot (including subsystems, commands, and trigger mappings) should be declared here.
 */
@SuppressWarnings("unused")
public class RobotContainer {
  // The robot's subsystems and commands are defined here...

  // Replace with CommandPS4Controller or CommandJoystick if needed
  private CommandXboxController mDriverController = new CommandXboxController(OperatorConstants.kDriverControllerPort);
  private CommandXboxController mOperatorController = new CommandXboxController(
      OperatorConstants.kOperatorControllerPort);

  private final ShooterSubsystem mShooterSubsystem = new ShooterSubsystem();
  private final SwerveSubsystem mSwerveDrive = new SwerveSubsystem(new File(Filesystem.getDeployDirectory(), "swerve"));
  private final IntakeSubsystem mIntakeSubsystem = new IntakeSubsystem();
  private final ClimbSubsystem mClimbSubsystem = new ClimbSubsystem();
    // A simple auto routine that drives forward a specified distance, and then stops.
  private final Command m_drive2meters =
      new DriveTwoMeters(mSwerveDrive);
  // A complex auto routine that drives forward, drops a hatch, and then drives backward.
  private final Command m_shootFuel = new ShootAuto(mShooterSubsystem);

  private final Command m_shootFuelThenDrive = new ShootAuto(mShooterSubsystem).andThen(new DriveTwoMeters(mSwerveDrive));

  // A chooser for autonomous commands
  SendableChooser<Command> m_chooser = new SendableChooser<>();

  SwerveInputStream mDriveFieldOriented = SwerveInputStream.of( //
      mSwerveDrive.getSwerveDrive(), //
      () -> mDriverController.getLeftY() * -1, //
      () -> -mDriverController.getLeftX()) //
      .withControllerRotationAxis(() -> -mDriverController.getRightX()) //
      .deadband(0.1) //
      .scaleTranslation(0.8) //
      .allianceRelativeControl(false);

  SwerveInputStream mDriveRobotOriented = mDriveFieldOriented.copy() //
      .robotRelative(true) //
      .allianceRelativeControl(false);

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    m_chooser.setDefaultOption("Drive 2 Meters Auto", m_drive2meters);
    m_chooser.addOption("Shoot Fuel Auto", m_shootFuel);
    m_chooser.addOption("Shoot then Drive Auto", m_shootFuelThenDrive);

    SmartDashboard.putData("Autonomus", m_chooser);
    // Configure the trigger bindings
    configureBindings();
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary predicate, or via the
   * named factories in {@link edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for
   * {@link CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller PS4}
   * controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight joysticks}.
   */

  private void configureBindings() {
    
    // Moving the robot
    var defaultDriveCommand = mSwerveDrive.driveFieldOrientedCommand(mDriveFieldOriented);

    mSwerveDrive.setDefaultCommand(defaultDriveCommand);

    mDriverController.a().onTrue(mSwerveDrive.zeroGyroCommand());

    // Controlling the Feeder Motor
    mOperatorController.a().whileTrue(new FeederCommand(mShooterSubsystem));

    // Controlling the shooter
    mOperatorController.x().whileTrue(new RunFlywheel(mShooterSubsystem, 3));
    mOperatorController.y().whileTrue(new RunFlywheel(mShooterSubsystem, 7));
    mOperatorController.b().whileTrue(new RunFlywheel(mShooterSubsystem, 12));

    // mOperatorController.a().whileTrue(mShooterSubsystem.sysIdQuasistatic(Direction.kForward));
    // mOperatorController.b().whileTrue(mShooterSubsystem.sysIdQuasistatic(Direction.kReverse));
    // mOperatorController.x().whileTrue(mShooterSubsystem.sysIdDynamic(Direction.kForward));
    // mOperatorController.y().whileTrue(mShooterSubsystem.sysIdDynamic(Direction.kReverse));

    // Controlling the Paddles for Intake
    mOperatorController.leftTrigger().whileTrue(new IntakeFuel(mIntakeSubsystem, Constants.fuelIntakeSpeedReverse));
    mOperatorController.rightTrigger().whileTrue(new IntakeFuel(mIntakeSubsystem, Constants.fuelIntakeSpeed));

    mOperatorController.rightBumper().whileTrue(new MoveUp(mClimbSubsystem,0.5)); 
    mOperatorController.leftBumper().whileTrue(new MoveDown(mClimbSubsystem,-0.5)); 
    // Controlling the Pivot Point for Intake
    //mOperatorController.leftBumper().whileTrue(new IntakePivot(mIntakeSubsystem, Constants.PivotIntakeSpeedReverse));
    //mOperatorController.rightBumper().whileTrue(new IntakePivot(mIntakeSubsystem, Constants.PivotIntakeSpeed));
  }

  /**
   * Use this to pass the autonomous command to the main {@lin\k Robot} class.
   *
   * @return the command to run in autonomous
   */

  public Command getAutonomousCommand() {
    return m_chooser.getSelected();
  }

  public SwerveSubsystem getSwerveSubsystem() {
    return mSwerveDrive;
  }

}

