package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.subsystems.AgitatorSubsystem;

public class MoveAgitator extends Command{
  
  private AgitatorSubsystem m_AgitatorSubsystem;
  Timer initial;

  public MoveAgitator(AgitatorSubsystem p_AgitatorSubsystem){
    m_AgitatorSubsystem = p_AgitatorSubsystem;
  }

  @Override
  public void initialize() {
    initial = new Timer();
    initial.start();
  }

  @Override
  public void execute() {
    if (Math.floor(initial.get()) % 3 == 0){
      m_AgitatorSubsystem.setAgitatorMotorSetpoint(-.5);
    } else{
      m_AgitatorSubsystem.setAgitatorMotorSetpoint(.5);
    }
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
