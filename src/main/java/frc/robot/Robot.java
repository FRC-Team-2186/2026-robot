// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.Optional;

import org.littletonrobotics.junction.LoggedRobot;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.NT4Publisher;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.utils.CommandsLogging;
import swervelib.simulation.ironmaple.simulation.SimulatedArena;
import swervelib.simulation.ironmaple.simulation.seasonspecific.rebuilt2026.RebuiltFuelOnField;

/**
 * The methods in this class are called automatically corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after creating this project, you must also update
 * the Main.java file in the project.
 */
public class Robot extends LoggedRobot {
  private Command m_autonomousCommand;
  private SimulatedArena mSimArena;

  private final RobotContainer m_robotContainer;

  private VisionSubsystem m_visionSubsystem;

  /**
   * This function is run when the robot is first started up and should be used for any initialization code.
   */
  public Robot() {
    // Set up AdvantageKit & friends
    Logger.recordMetadata("ProjectName", "2026-robot");
    Logger.addDataReceiver(new NT4Publisher());

    Logger.start();

    // Set up the command logger.
    // This helps with determining what commands are running and when.
    CommandScheduler.getInstance().onCommandInitialize(CommandsLogging::commandStarted);
    CommandScheduler.getInstance().onCommandFinish(CommandsLogging::commandEnded);
    CommandScheduler.getInstance().onCommandInterrupt(this::commandInterrupted);
    // Instantiate our RobotContainer. This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer();

    // TODO: uncomment -> m_visionSubsystem = new VisionSubsystem(drivetrain::addVisionMeasurement);
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics that you want ran
   * during disabled, autonomous, teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler. This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods. This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();

    // Update the command logger
    CommandsLogging.logRunningCommands();
    CommandsLogging.logRequiredSubsystems();

    // Update the simulation
    if (Robot.isSimulation() && mSimArena != null) {
    }

    Logger.recordOutput("FieldSimulation/RobotPose", m_robotContainer.getSwerveSubsystem().getPose());
    Logger.recordOutput("FieldSimulation/TargetPose",
        m_robotContainer.getSwerveSubsystem().getSwerveDrive().field.getObject("targetPose").getPose());
    
    // Update vision
    vision.periodic();
  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
  }

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      CommandScheduler.getInstance().schedule(m_autonomousCommand);
    }
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
  }

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {
    mSimArena = SimulatedArena.getInstance();
  }

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {
    mSimArena.simulationPeriodic();
  }

  private void commandInterrupted(Command pInterrupted, Optional<Command> pInterrupting) {
    pInterrupting.ifPresent(interrupter -> {
      CommandsLogging.runningInterrupters.put(interrupter, pInterrupted);
    });
    CommandsLogging.commandEnded(pInterrupted);
  }
}
