package com.spartronics4915.frc2020.commands;

import com.spartronics4915.frc2020.subsystems.Launcher;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;

public class LauncherCommands
{
    /*
     * Command for testing, runs flywheel at a given RPS
     * !DO NOT MAKE THE RPS MORE THAN 90!
     */
    public class ShootBallTest extends CommandBase
    {
        private final Launcher mLauncher;

        // You should only use one subsystem per command. If multiple are needed, use a
        // CommandGroup.
        public ShootBallTest(Launcher Launcher)
        {
            mLauncher = Launcher;
            addRequirements(mLauncher);
        }

        // Called when the command is initially scheduled.
        @Override
        public void initialize()
        {
            mLauncher.setRPS(SmartDashboard.getNumber("Launcher/FlywheelRPS", 0));
        }

        // Called every time the scheduler runs while the command is scheduled.
        @Override
        public void execute()
        {
            mLauncher.runFlywheel();
        }

        // Returns true when the command should end.
        @Override
        public boolean isFinished()
        {
            return false;
        }

        // Called once the command ends or is interrupted.
        @Override
        public void end(boolean interrupted)
        {
            mLauncher.reset();
        }
    }

    public class TurretTest extends CommandBase
    {
        private final Launcher mLauncher;

        // You should only use one subsystem per command. If multiple are needed, use a
        // CommandGroup.
        public TurretTest(Launcher Launcher)
        {
            mLauncher = Launcher;
            addRequirements(mLauncher);
        }

        // Called when the command is initially scheduled.
        @Override
        public void initialize()
        {
        }

        // Called every time the scheduler runs while the command is scheduled.
        @Override
        public void execute()
        {
            SmartDashboard.putNumber("Launcher/TurretDirection", mLauncher.getTurretDirection());
        }

        // Returns true when the command should end.
        @Override
        public boolean isFinished()
        {
            return false;
        }

        // Called once the command ends or is interrupted.
        @Override
        public void end(boolean interrupted)
        {
            mLauncher.reset();
        }
    }

    public class HoodTest extends CommandBase
    {
        private final Launcher mLauncher;

        // You should only use one subsystem per command. If multiple are needed, use a
        // CommandGroup.
        public HoodTest(Launcher Launcher)
        {
            mLauncher = Launcher;
            addRequirements(mLauncher);
        }

        // Called when the command is initially scheduled.
        @Override
        public void initialize()
        {
            mLauncher.setPitch(SmartDashboard.getNumber("Launcher/TurretAimAngle",0));
        }

        // Called every time the scheduler runs while the command is scheduled.
        @Override
        public void execute()
        {
            mLauncher.setPitch(SmartDashboard.getNumber("Launcher/TurretAimAngle",0));
            mLauncher.rotateHood();
        }

        // Returns true when the command should end.
        @Override
        public boolean isFinished()
        {
            return false;
        }

        // Called once the command ends or is interrupted.
        @Override
        public void end(boolean interrupted)
        {
            mLauncher.reset();
        }
    }

    /*
     * Default command of the launcher subsystem, makes the flywheel's target rps 0
     */
    public class LauncherDefaultCommand extends CommandBase
    {
        private final Launcher mLauncher;

        // You should only use one subsystem per command. If multiple are needed, use a
        // CommandGroup.
        public LauncherDefaultCommand(Launcher Launcher)
        {
            mLauncher = Launcher;
            addRequirements(mLauncher);
        }

        // Called when the command is initially scheduled.
        @Override
        public void initialize()
        {
            mLauncher.setRPS(0);
        }

        // Called every time the scheduler runs while the command is scheduled.
        @Override
        public void execute()
        {
            mLauncher.runFlywheel();
        }

        // Returns true when the command should end.
        @Override
        public boolean isFinished()
        {
            return false;
        }

        // Called once the command ends or is interrupted.
        @Override
        public void end(boolean interrupted)
        {
            mLauncher.reset();
        }
    }
}