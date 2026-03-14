package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.AgitatorSubsystem;

public class MoveAgitator extends Command{
  
  private AgitatorSubsystem m_AgitatorSubsystem;

  public MoveAgitator(AgitatorSubsystem p_AgitatorSubsystem){
    m_AgitatorSubsystem = p_AgitatorSubsystem;
  }

  @Override
  public void initialize() {
  }

  @Override
  public void execute() {
    m_AgitatorSubsystem.setAgitatorMotorSetpoint(.5);
  }

  @Override
  public boolean isFinished() {
    return false;
  }

  @Override
  public void end(boolean interupted) {
    m_AgitatorSubsystem.setAgitatorMotorSetpoint(0);
  }

}
