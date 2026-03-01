package frc.robot.commands;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterSubsystem;

@SuppressWarnings("unused")
public class ShooterVoltageTest extends Command {
  private static final String kTabName = "Shooter";
  private static final String kVoltageEntryName = "Shooter/TestVoltage";
  private static final double kDefaultTestVoltage = 0.0;

  private final ShooterSubsystem mShooter;
  private final int mPos;
  private final GenericEntry mTestVoltageEntry;

  /**
   * Test command that drives the flywheel with a dashboard-supplied voltage.
   *
   * Intent:
   * - Allow quick tuning of flywheel voltage without redeploying code.
   * - Use a preset index for feeder gating consistency.
   */
  public ShooterVoltageTest(ShooterSubsystem shooter, int pos) {
    mShooter = shooter;
    mPos = pos;
    mTestVoltageEntry = Shuffleboard.getTab(kTabName)
        .add(kVoltageEntryName, kDefaultTestVoltage)
        .getEntry();
    addRequirements(mShooter);
  }

  @Override
  public void execute() {
    double testVoltage = mTestVoltageEntry.getDouble(kDefaultTestVoltage);
    mShooter.setFlywheelMotorVoltage(testVoltage);
    mShooter.runFeeder(mPos);
  }

  @Override
  public void end(boolean interrupted) {
    mShooter.setFeederMotorVoltage(0);
    mShooter.setFlywheelMotorVoltage(0);
  }
}
