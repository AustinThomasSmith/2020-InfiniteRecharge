package com.spartronics4915.frc2020;

import java.util.Set;

import com.spartronics4915.frc2020.commands.*;
import com.spartronics4915.frc2020.subsystems.*;
import com.spartronics4915.lib.util.Logger;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public class RobotContainer
{

    private static class AutoMode
    {
        public final String name;
        public final Command command;

        public AutoMode(String name, Command command)
        {
            this.name = name;
            this.command = command;
        }
    }

    public static final String kAutoOptionsKey = "AutoStrategyOptions";
    public static final String kSelectedAutoModeKey = "AutoStrategy";
    public static final AutoMode kDefaultAutoMode = new AutoMode("All: Do Nothing", new Command()
    {
        @Override
        public Set<Subsystem> getRequirements()
        {
            return null;
        }
    });

    public final AutoMode[] mAutoModes;

    private final Joystick mJoystick;
    private final Joystick mButtonBoard;

    private final Climber mClimber;
    private final Intake mIntake;
    private final Launcher mLauncher;
    private final PanelRotator mPanelRotator;

    /**
     * The container for the robot. Contains subsystems, OI devices, and commands.
     */
    public RobotContainer()
    {
        mClimber = new Climber();
        mIntake = new Intake();
        mLauncher = new Launcher();
        mPanelRotator = new PanelRotator();
        mJoystick = new Joystick(Constants.OI.kJoystickId);
        mButtonBoard = new Joystick(Constants.OI.kButtonBoardId);

        configureJoystickBindings();
        configureButtonBoardBindings();

        mAutoModes = new AutoMode[] {kDefaultAutoMode};
    }

    private void configureJoystickBindings()
    {
        /*
        new JoystickButton(mJoystick, 1).whenPressed(() -> mDrive.driveSlow()).whenReleased(() -> mDrive.driveNormal());
        new JoystickButton(mJoystick, 2).whenHeld(new LauncherCommands.Raise(mLauncher));
        new JoystickButton(mJoystick, 3).whenHeld(new LauncherCommands.Lower(mLauncher));
        new JoystickButton(mJoystick, 4).whenHeld(new LauncherCommands.Left(mLauncher));
        new JoystickButton(mJoystick, 5).whenHeld(new LauncherCommands.Right(mLauncher));
        */

        /* Switch Camera views
        new JoystickButton(mJoystick, 6).whenPressed(
            new InstantCommand(() -> mCamera.switch(Constants.Camera.kFrontId)));
        new JoystickButton(mJoystick, 7).whenPressed(
            new InstantCommand(() -> mCamera.switch(Constants.Camera.kRearId)));
        new JoystickButton(mJoystick, 10).whenPressed(
            new InstantCommand(() -> mCamera.switch(Constants.Camera.kIntakeId)));
        new JoystickButton(mJoystick, 11).whenPressed(
            new InstantCommand(() -> mCamera.switch(Constants.Camera.kTurretId)));
        */
    }

    private void configureButtonBoardBindings()
    {
        /*
        new JoystickButton(mButtonBoard, 0).whenPressed(new IntakeCommands.Intake(mIntake));
        new JoystickButton(mButtonBoard, 1).whenPressed(new IntakeCommands.Stop(mIntake));
        new JoystickButton(mButtonBoard, 2).whileHeld(new IntakeCommands.Unjam(mIntake));
        */

        /*
        new JoystickButton(mButtonBoard, 3).whenPressed(new LauncherCommands.AimLow(mLauncher));
        new JoystickButton(mButtonBoard, 4).whenPressed(new LauncherCommands.Launch(mLauncher));
        new JoystickButton(mButtonBoard, 5).whenPressed(new LauncherCommands.AimHigh(mLauncher));
        */

        new JoystickButton(mButtonBoard, 6).whenPressed(new PanelRotatorCommands.Raise(mPanelRotator));
        new JoystickButton(mButtonBoard, 7).whenPressed(new PanelRotatorCommands.Lower(mPanelRotator));
        new JoystickButton(mButtonBoard, 8).whenPressed(new PanelRotatorCommands.SpinToColor());
        new JoystickButton(mButtonBoard, 9).whenPressed(new PanelRotatorCommands.SpinRotation());

        new JoystickButton(mButtonBoard, 10).whileHeld(new ClimberCommands.Extend(mClimber));
        new JoystickButton(mButtonBoard, 11).whileHeld(new ClimberCommands.Retract(mClimber));
        new JoystickButton(mButtonBoard, 14).whenHeld(new ClimberCommands.WinchPrimary(mClimber)
                .andThen(new ClimberCommands.WinchSecondary(mClimber)));

        /*
        new JoystickButton(mButtonBoard, 15).whenHeld(new TurretRaiseCommand(mLauncher));
        new JoystickButton(mButtonBoard, 16).whenHeld(new TurretLowerCommand(mLauncher));
        new JoystickButton(mButtonBoard, 17).whenHeld(new TurretLeftCommand(mLauncher));
        new JoystickButton(mButtonBoard, 18).whenHeld(new TurretRightCommand(mLauncher));
        */
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand()
    {
        String selectedModeName = SmartDashboard.getString(kSelectedAutoModeKey, "NO SELECTED MODE!!!!");
        Logger.notice("Auto mode name " + selectedModeName);
        for (var mode : mAutoModes)
        {
            if (mode.name.equals(selectedModeName))
            {
                return mode.command;
            }
        }

        Logger.error("AutoModeSelector failed to select auto mode: " + selectedModeName);
        return kDefaultAutoMode.command;
    }
}
