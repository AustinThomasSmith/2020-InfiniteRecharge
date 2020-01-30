package com.spartronics4915.frc2020.commands;

import javax.xml.namespace.QName;

import com.spartronics4915.frc2020.subsystems.Climber;
import com.spartronics4915.frc2020.Constants;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.StartEndCommand;

public class ClimberCommands
{
    private Climber mClimber;

    ClimberCommands(Climber Climber)
    {
        mClimber = Climber;
    }

    public class Raise extends StartEndCommand
    {
        public Raise(Climber c)
        {
            super(() ->
            {
                c.extend();
            }, () ->
            {
                c.stop();
            }, c);

        }

    }

    public class Retract extends StartEndCommand
    {
        public Retract(Climber c)
        {
            super(() ->
            {
                c.retract();
            }, () ->
            {
                c.stop();
            });
        }
    }

    public class WinchPrimary extends FunctionalCommand
    {
        public WinchPrimary(Climber mClimber)
        {
            super(() ->
            {
            }, () -> mClimber.winch(!Constants.Climber.kStalled), (Boolean b) -> mClimber.stop(),
                    mClimber::isStalled, mClimber);
        }
    }

    public class WinchSecondary extends StartEndCommand
    {
        public WinchSecondary(Climber mClimber)
        {
            super(() -> mClimber.winch(Constants.Climber.kStalled), mClimber::stop, mClimber);
        }
    }
}
