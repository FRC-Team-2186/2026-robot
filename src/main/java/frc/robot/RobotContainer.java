// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.Configs.ShooterSubsystems;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.Autos;
import frc.robot.commands.DriveTwoMetersBack;
import frc.robot.commands.FeederCommand;
import frc.robot.commands.ShootAuto;
import frc.robot.commands.OpenPivot;
import frc.robot.subsystems.SwerveSubsystem;
import swervelib.SwerveDrive;
import frc.robot.commands.IntakeFuel;
import frc.robot.commands.IntakePivot;
import swervelib.SwerveInputStream;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.RPM;

import java.io.File;
import frc.robot.commands.rotateRobotAuto;

import com.fasterxml.jackson.databind.util.Named;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.config.PIDConstants;

import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.subsystems.ClimbSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.PivotSubsystem;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.commands.TurnToAprilTagCommand;
import frc.robot.commands.TurnToHeading;
import frc.robot.commands.TurnToPointCommand;
import frc.robot.commands.RunFlywheel;
import frc.robot.commands.MoveUp;
import frc.robot.commands.MoveDown;
import frc.robot.commands.ShooterVoltageTest;
import frc.robot.commands.DriveTwoMetersForward;
import frc.robot.commands.DriveAndIntake;
import frc.robot.commands.MoveAgitator;
import frc.robot.subsystems.AgitatorSubsystem;
import frc.robot.commands.AutoMoveDown;
import frc.robot.commands.AutoMoveUp;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a "declarative" paradigm, very
 * little robot logic should actually be handled in the {@link Robot} periodic methods (other than the scheduler calls).
 * Instead, the structure of the robot (including subsystems, commands, and trigger mappings) should be declared here.
 */
@SuppressWarnings("unused")
public class RobotContainer {
    // The robot's subsystems and commands are defined here...
    private static final Translation2d BLUE_ALLIANCE_HUB = new Translation2d(Meters.of(4.632), Meters.of(4.030));

    // Replace with CommandPS4Controller or CommandJoystick if needed
    private CommandXboxController mDriverController = new CommandXboxController(
            OperatorConstants.kDriverControllerPort);
    private CommandXboxController mOperatorController = new CommandXboxController(
            OperatorConstants.kOperatorControllerPort);

    // A chooser for autonomous commands
    SendableChooser<Command> m_chooser = new SendableChooser<>();
    private SendableChooser<Command> autoChooser;
    
        // PathPlanner chooser (created in constructor to avoid running AutoBuilder too early
        // during field initialization which can cause runtime errors in simulation)
        // ShuffleboardTab autoTab = Shuffleboard.getTab("Auto Tab");
    
        private final ShooterSubsystem mShooterSubsystem = new ShooterSubsystem();
        private final SwerveSubsystem mSwerveDrive = new SwerveSubsystem(
                new File(Filesystem.getDeployDirectory(), "swerve"));
        private final IntakeSubsystem mIntakeSubsystem = new IntakeSubsystem();
        private final ClimbSubsystem mClimbSubsystem = new ClimbSubsystem();
        private final PivotSubsystem mPivotSubsystem = new PivotSubsystem();
        //private final AgitatorSubsystem mMoveAgitator = new AgitatorSubsystem();
    
        // A simple auto routine that drives forward a specified distance, and then stops.
        private final Command m_drive2meters = new DriveTwoMetersBack(mSwerveDrive);

        private final Command m_moveClimb = new AutoMoveUp(mClimbSubsystem, 0.5)
                .andThen(new DriveTwoMetersBack(mSwerveDrive))
                .andThen(new DriveTwoMetersBack(mSwerveDrive))
                .andThen(new AutoMoveDown(mClimbSubsystem, -0.5));

        private final Command m_moveAutoUp = new AutoMoveUp(mClimbSubsystem, 0.5);
    
        private final Vision mVision = new Vision(mSwerveDrive::updateOdometryFromVision);
    
        private final Command m_push = new AutoMoveUp(mClimbSubsystem, 0.5);
    
        private final Command m_pull = new MoveDown(mClimbSubsystem, -0.5);
    
        private final Command m_drive2metersForward = new DriveTwoMetersForward(mSwerveDrive);
    
        private final Command m_dropPivot = new OpenPivot(mPivotSubsystem);
        // A complex auto routine that drives forward, drops a hatch, and then drives backward.
        //private final Command m_shootFuelOnly = new ShootAuto(mShooterSubsystem, mMoveAgitator);
    
        private final Command m_intakeFuel = new IntakeFuel(mIntakeSubsystem, Constants.fuelIntakeSpeed);
    
        private final Command m_shootFuel = new OpenPivot(mPivotSubsystem)
                .andThen(new ShootAuto(mShooterSubsystem));
                
        private final Command m_rotateRobotLeft = new rotateRobotAuto(mSwerveDrive, 90);
        private final Command m_rotateRobotRight = new rotateRobotAuto(mSwerveDrive, -90);
        private final Command m_DriveAndIntake = new DriveAndIntake(mSwerveDrive, mIntakeSubsystem);
    
        private final Command m_shootFuelThenDrive = new DriveTwoMetersBack(mSwerveDrive)
                .andThen(new OpenPivot(mPivotSubsystem))
                .andThen(new ShootAuto(mShooterSubsystem));

        public final Command m_hugLeftWall = new OpenPivot(mPivotSubsystem)
                .andThen(new ShootAuto(mShooterSubsystem))
                .andThen(new rotateRobotAuto(mSwerveDrive, (Math.PI / 2)))
                .andThen(new DriveAndIntake(mSwerveDrive, mIntakeSubsystem))
                .andThen(new DriveAndIntake(mSwerveDrive, mIntakeSubsystem))
                .andThen(new rotateRobotAuto(mSwerveDrive, -(Math.PI / 2)));

        public final Command m_hugRightWall = new OpenPivot(mPivotSubsystem)
                .andThen(new ShootAuto(mShooterSubsystem))
                .andThen(new rotateRobotAuto(mSwerveDrive, -(Math.PI / 2)))
                .andThen(new DriveAndIntake(mSwerveDrive, mIntakeSubsystem))
                .andThen(new DriveAndIntake(mSwerveDrive, mIntakeSubsystem))
                .andThen(new rotateRobotAuto(mSwerveDrive, (Math.PI / 2)));

        private final Command m_driveThenShootFuel = new OpenPivot(mPivotSubsystem)
                .andThen(new DriveTwoMetersBack(mSwerveDrive)).andThen(new ShootAuto(mShooterSubsystem));
        private final Command m_driveRotateLeftShoot = new OpenPivot(mPivotSubsystem)
                .andThen(new DriveTwoMetersBack(mSwerveDrive)).andThen(new rotateRobotAuto(mSwerveDrive, (Math.PI / 2)))
                .andThen(new ShootAuto(mShooterSubsystem));
        private final Command m_driveRotateRightShoot = new OpenPivot(mPivotSubsystem)
                .andThen(new DriveTwoMetersBack(mSwerveDrive)).andThen(new rotateRobotAuto(mSwerveDrive, -(Math.PI / 2)))
                .andThen(new ShootAuto(mShooterSubsystem));

        private final Command m_shootDriveRightUseWhenLeft = new OpenPivot(mPivotSubsystem)
                .andThen(new ShootAuto(mShooterSubsystem))
                .andThen(new rotateRobotAuto(mSwerveDrive, -(Math.PI / 2)))
                .andThen(new DriveTwoMetersBack(mSwerveDrive));

        private final Command m_shootDriveLeftUseWhenRight = new OpenPivot(mPivotSubsystem)
                .andThen(new ShootAuto(mShooterSubsystem))
                .andThen(new rotateRobotAuto(mSwerveDrive, (Math.PI / 2)))
                .andThen(new DriveTwoMetersBack(mSwerveDrive));
    
        private final Command m_veryComplexMovementAutoLeft = new DriveTwoMetersBack(mSwerveDrive)
                .andThen(new rotateRobotAuto(mSwerveDrive, -(Math.PI / 2))).andThen(new OpenPivot(mPivotSubsystem))
                .andThen(new ShootAuto(mShooterSubsystem))
                .andThen(new rotateRobotAuto(mSwerveDrive, -(Math.PI / 2))).andThen(new DriveTwoMetersBack(mSwerveDrive))
                .andThen(new rotateRobotAuto(mSwerveDrive, -(Math.PI / 3)))
                .andThen(new rotateRobotAuto(mSwerveDrive, (Math.PI))).andThen(new DriveTwoMetersForward(mSwerveDrive));
    
        private final Command m_veryComplexMovementAutoRight = new DriveTwoMetersBack(mSwerveDrive)
                .andThen(new rotateRobotAuto(mSwerveDrive, (Math.PI / 2))).andThen(new OpenPivot(mPivotSubsystem))
                .andThen(new ShootAuto(mShooterSubsystem))
                .andThen(new rotateRobotAuto(mSwerveDrive, (Math.PI / 2))).andThen(new DriveTwoMetersBack(mSwerveDrive))
                .andThen(new rotateRobotAuto(mSwerveDrive, (Math.PI / 3)))
                .andThen(new rotateRobotAuto(mSwerveDrive, (Math.PI))).andThen(new DriveTwoMetersForward(mSwerveDrive));
    
        private final Command m_DoNotRunThisUnlesYouWantNoRobotAliveLeft = new DriveTwoMetersBack(mSwerveDrive)
                .andThen(new rotateRobotAuto(mSwerveDrive, -(Math.PI / 2))).andThen(new OpenPivot(mPivotSubsystem))
                .andThen(new ShootAuto(mShooterSubsystem))
                .andThen(new rotateRobotAuto(mSwerveDrive, -(Math.PI / 2))).andThen(new DriveTwoMetersBack(mSwerveDrive))
                .andThen(new rotateRobotAuto(mSwerveDrive, -(Math.PI / 3)))
                .andThen(new rotateRobotAuto(mSwerveDrive, (Math.PI))).andThen(new DriveTwoMetersForward(mSwerveDrive))
                .andThen(new rotateRobotAuto(mSwerveDrive, (Math.PI / 3)))
                .andThen(new DriveAndIntake(mSwerveDrive, mIntakeSubsystem))
                .andThen(new rotateRobotAuto(mSwerveDrive, (Math.PI)))
                .andThen(new DriveAndIntake(mSwerveDrive, mIntakeSubsystem))
                .andThen(new rotateRobotAuto(mSwerveDrive, -(Math.PI / 3)))
                .andThen(new DriveAndIntake(mSwerveDrive, mIntakeSubsystem))
                .andThen(new rotateRobotAuto(mSwerveDrive, (Math.PI / 3))).andThen(new DriveTwoMetersForward(mSwerveDrive))
                .andThen(new rotateRobotAuto(mSwerveDrive, (Math.PI / 2)))
                .andThen(new ShootAuto(mShooterSubsystem));
    
        SwerveInputStream mDriveFieldOriented = SwerveInputStream.of( //
                mSwerveDrive.getSwerveDrive(), //
                () -> mDriverController.getLeftY() * -1, //
                () -> -mDriverController.getLeftX()) //
                .withControllerRotationAxis(() -> -mDriverController.getRightX()) //
                .deadband(0.1) //
                .scaleTranslation(0.6) //
                .allianceRelativeControl(false);
    
        SwerveInputStream mDriveRobotOriented = mDriveFieldOriented.copy() //
                .robotRelative(true) //
                .allianceRelativeControl(false);
    
        // The container for the robot. Contains subsystems, OI devices, and commands.
        public RobotContainer() {
            // m_chooser.setDefaultOption("Drive 2 Meters Auto back", m_drive2meters);
            // m_chooser.setDefaultOption("Drive 2 Meters forward", m_drive2metersForward);
            // m_chooser.addOption("Shoot Fuel Auto", m_shootFuel);
            // m_chooser.addOption("Drive Auto then Shoot", m_driveThenShootFuel);
            // m_chooser.addOption("Drive, Rotate Left, then Shoot", m_driveRotateLeftShoot);
            // m_chooser.addOption("Drive, Rotate Right, then Shoot", m_driveRotateRightShoot);
            // // DONT USE v
            // m_chooser.addOption("Shoot then Drive Auto", m_shootFuelThenDrive);
            // m_chooser.addOption("Lots of stuff Left (Dont run)", m_veryComplexMovementAutoLeft);
            // m_chooser.addOption("Lots of stuff Right (Dont run)", m_veryComplexMovementAutoRight);
            // m_chooser.addOption("DO NOT RUN ISTG (left Edition)", m_DoNotRunThisUnlesYouWantNoRobotAliveLeft);
    
            // In RobotContainer.java constructor
            // Build the PathPlanner auto chooser here (after subsystems are constructed)
    
            NamedCommands.registerCommand("m_push", new MoveUp(mClimbSubsystem, 0.5));
            NamedCommands.registerCommand("m_pull", new MoveDown(mClimbSubsystem, -0.5));
            NamedCommands.registerCommand("m_shootFuelOnly", new ShootAuto(mShooterSubsystem));
            NamedCommands.registerCommand("m_intakeFuel", new IntakeFuel(mIntakeSubsystem, Constants.fuelIntakeSpeed));
            NamedCommands.registerCommand("m_dropPivot", new OpenPivot(mPivotSubsystem));
    
            autoChooser = AutoBuilder.buildAutoChooser();

        autoChooser.setDefaultOption("Shoot Fuel Auto (Both)", m_shootFuel);
        autoChooser.addOption("Shoot Turn Right Go Center (Right)",m_hugRightWall);
        autoChooser.addOption("Shoot Turn Left Go Center (Left)", m_hugLeftWall);
        autoChooser.addOption("Shoot then Drive Auto (Center)", m_shootFuelThenDrive);
        autoChooser.addOption("Blue Left go Center Return Wide Shoot (Left)", new PathPlannerAuto("ShootBackLeftBlueWide"));
        autoChooser.addOption("Blue Right go center Return Wide Shoot (Right)", new PathPlannerAuto("ShootBackRightBlueWide"));
        autoChooser.addOption("Red Left go center Return Wide Shoot (Left)", new PathPlannerAuto("ShootMoveLeftRedWide"));
        autoChooser.addOption("Red Right go center Return Wide Shoot (Right)", new PathPlannerAuto("ShootMoveRightRedWide"));
        autoChooser.addOption("Shoot then turn Right Auto (Left)", m_shootDriveRightUseWhenLeft);
        autoChooser.addOption("Shoot then turn Left Auto (Right)", m_shootDriveLeftUseWhenRight); 
        autoChooser.addOption("Drive 2 Meters Auto back (DONT RUN)", m_drive2meters);
        autoChooser.addOption("Drive 2 Meters forward (DONT RUN)", m_drive2metersForward);
        autoChooser.addOption("Drive Auto then Shoot (DONT RUN)", m_driveThenShootFuel);
        autoChooser.addOption("Drive, Rotate Left, then Shoot (DONT RUN)", m_driveRotateLeftShoot);
        autoChooser.addOption("Drive, Rotate Right, then Shoot (DONT RUN)", m_driveRotateRightShoot);
        autoChooser.addOption("Lots of stuff Left (DONT RUN)", m_veryComplexMovementAutoLeft);
        autoChooser.addOption("Lots of stuff Right (DONT RUN)", m_veryComplexMovementAutoLeft);
        autoChooser.addOption("DO NOT RUN ISTG (left Edition) (DONT RUN)", m_DoNotRunThisUnlesYouWantNoRobotAliveLeft);
        autoChooser.addOption("Blue Left to Blue Right (DONT RUN)", new PathPlannerAuto("ShootCircleLeftBlue"));
        autoChooser.addOption("Blue Right to Blue Left (DONT RUN)", new PathPlannerAuto("ShootCircleRightBlue"));
        autoChooser.addOption("Climber (DONT RUN)", new PathPlannerAuto("TotallynotClimbing"));
        
        
        SmartDashboard.putData("Autonomous", autoChooser);
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
        // mOperatorController.a().whileTrue(new FeederCommand(mShooterSubsystem));
        // Driver and Operator E-Stop for testing

        // Controlling the shooter
        /*mOperatorController.a()
                .whileTrue(mShooterSubsystem.setFlywheelRpmCommand(RPM.of(2950))
                        .alongWith(Commands.runEnd(() -> mMoveAgitator.setAgitatorMotorSetpoint(-0.675),
                                () -> mMoveAgitator.setAgitatorMotorSetpoint(0), mMoveAgitator)));*/

        mOperatorController.a().whileTrue(new RunFlywheel(mShooterSubsystem, Constants.kNearShotIndex));
        //mOperatorController.x().whileTrue(new RunFlywheel(mShooterSubsystem, Constants.kMidShotIndex));

        //mOperatorController.y().whileTrue(new RunFlywheel(mShooterSubsystem, Constants.kLaVoltage));

        mOperatorController.x().whileTrue(mShooterSubsystem.setFlywheelRpmCommand(RPM.of(3000)));
        mOperatorController.b().whileTrue(new FeederCommand(mShooterSubsystem));

        mDriverController.rightBumper().whileTrue(new TurnToAprilTagCommand(mSwerveDrive, mVision, () -> 26));
        // mOperatorController.y().whileTrue(new RunFlywheel(mShooterSubsystem, Constants.kHighShotIndex,
        // mMoveAgitator));
        // mOperatorController.b().whileTrue(new RunFlywheel(mShooterSubsystem, Constants.kFarShotIndex,
        // mMoveAgitator));

        // mOperatorController.a().whileTrue(new FeederCommand(mShooterSubsystem));

        // Driver and Operator E-Stop for testing

        // Controlling the shooter
        // mOperatorController.a().whileTrue(new RunFlywheel(mShooterSubsystem, Constants.kNearShotIndex));
        // mOperatorController.x().whileTrue(new RunFlywheel(mShooterSubsystem, Constants.kMidShotIndex));
        // mOperatorController.y().whileTrue(new RunFlywheel(mShooterSubsystem, Constants.kHighShotIndex));
        // mOperatorController.b().whileTrue(new RunFlywheel(mShooterSubsystem, Constants.kFarShotIndex));
        // mOperatorController.a().onTrue(mShooterSubsystem.setFlywheelRpmCommand(RPM.of(3250)));

        // mOperatorController.a().whileTrue(new RunFlywheel(mShooterSubsystem, Constants.lowVoltage));
        // mOperatorController.x().whileTrue(new RunFlywheel(mShooterSubsystem, Constants.mediumVoltage));
        // mOperatorController.y().whileTrue(new RunFlywheel(mShooterSubsystem, Constants.highVoltage));
        // mOperatorController.b().whileTrue(new RunFlywheel(mShooterSubsystem, Constants.highestVoltage));

        // mOperatorController.a().whileTrue(mShooterSubsystem.sysIdQuasistatic(Direction.kForward));
        // mOperatorController.b().whileTrue(mShooterSubsystem.sysIdQuasistatic(Direction.kReverse));
        // mOperatorController.x().whileTrue(mShooterSubsystem.sysIdDynamic(Direction.kForward));
        // mOperatorController.y().whileTrue(mShooterSubsystem.sysIdDynamic(Direction.kReverse));

        // mOperatorController.rightStick().whileTrue(new ShootByDistance(mShooterSubsystem, mOperatorController));

        // Controlling the Paddles for Intake
        mOperatorController.leftTrigger().whileTrue(new IntakeFuel(mIntakeSubsystem, Constants.fuelIntakeSpeedReverse));
        mOperatorController.rightTrigger().whileTrue(new IntakeFuel(mIntakeSubsystem, Constants.fuelIntakeSpeed));

        mOperatorController.povUp().whileTrue(new MoveUp(mClimbSubsystem, 0.5));
        mOperatorController.povDown().whileTrue(new MoveDown(mClimbSubsystem, -0.5));
        // Controlling the Pivot Point for Intake
        mOperatorController.leftBumper().whileTrue(new IntakePivot(mPivotSubsystem, Constants.PivotIntakeSpeedReverse));
        mOperatorController.rightBumper().whileTrue(new IntakePivot(mPivotSubsystem, Constants.PivotIntakeSpeed));
    }

    /**
     * Use this to pass the autonomous command to the main {@lin\k Robot} class.
     *
     * @return the command to run in autonomous
     */

    public Command getAutonomousCommand() {
        return autoChooser.getSelected();
        // return m_chooser.getSelected();
    }

    public SwerveSubsystem getSwerveSubsystem() {
        return mSwerveDrive;
    }

    public Vision getVision() {
        return mVision;
    }

}
