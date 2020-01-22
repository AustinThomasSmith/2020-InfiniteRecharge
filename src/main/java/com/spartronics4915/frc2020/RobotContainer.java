package com.spartronics4915.frc2020;

import java.util.Set;

import com.spartronics4915.frc2020.commands.*;
import com.spartronics4915.frc2020.subsystems.*;
import com.spartronics4915.lib.util.Logger;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public class RobotContainer {

    private static class AutoMode {
        public final String name;
        public final Command command;

        public AutoMode(String name, Command command) {
            this.name = name;
            this.command = command;
        }
    }

    public static final String kAutoOptionsKey = "AutoStrategyOptions";
    public static final String kSelectedAutoModeKey = "AutoStrategy";
    public static final AutoMode kDefaultAutoMode = new AutoMode("All: Do Nothing", new Command() {
        @Override
        public Set<Subsystem> getRequirements() {
            return null;
        }
    });

    public final AutoMode[] mAutoModes;

    /**
     * The container for the robot. Contains subsystems, OI devices, and commands.
     */
    public RobotContainer() {
        Joystick mJoystick = new Joystick(Constants.OI.kJoystickId);
        Joystick mButtonBoard = new Joystick(Constants.OI.kButtonBoardId);

        Climber mClimber = new Climber();
        Intake mIntake = new Intake();
        Launcher mLauncher = new Launcher();
        PanelRotator mPanelRotator = new PanelRotator();

        configureJoystickBindings();
        configureButtonBoardBindings();
        mAutoModes = new AutoMode[] {kDefaultAutoMode,};
    }

    private void configureJoystickBindings() {
        // new JoystickButton(mJoystick, 1).whenPressed(() -> mDrive.driveSlow()).whenReleased(() -> mDrive.driveNormal());
        new JoystickButton(mJoystick, 2).whenHeld(
            new FunctionalCommand(void, mLauncher::raise, mLauncher::stop, mLauncher::isElevationStalled, mLauncher)); // Duplicate of below
        new JoystickButton(mJoystick, 3).whenHeld(
            new FunctionalCommand(void, mLauncher::lower, mLauncher::stop, mLauncher::isElevationStalled, mLauncher)); // Duplicate of below
        new JoystickButton(mJoystick, 4).whenHeld(
            new FunctionalCommand(void, mLauncher::left, mLauncher::stop, mLauncher::isRotationStalled, mLauncher)); // Duplicate of below
        new JoystickButton(mJoystick, 5).whenHeld(
            new FunctionalCommand(void, mLauncher::right, mLauncher::stop, mLauncher::isRotationStalled, mLauncher)); // Duplicate of below

        /* Switch Camera views
        new JoystickButton(mJoystick, 6).whenPressed();
        new JoystickButton(mJoystick, 7).whenPressed();
        new JoystickButton(mJoystick, 10).whenPressed();
        new JoystickButton(mJoystick, 11).whenPressed();
        */
    }

    private void configureButtonBoardBindings() {
        new JoystickButton(mButtonBoard, 0).whenPressed(
            new StartEndCommand(mIntake::intake, mIntake::stop, mIntake));
        new JoystickButton(mButtonBoard, 1).whenPressed(
            new InstantCommand(mIntake::stop, mIntake));
        new JoystickButton(mButtonBoard, 2).whileHeld( // Will reschedule itself
            new UnjamCommand(mIntake));

        new JoystickButton(mButtonBoard, 3).whenPressed(
            new AimLowCommand(mLauncher));
        new JoystickButton(mButtonBoard, 4).whenPressed(
            new LaunchCommand(mLauncher));
        new JoystickButton(mButtonBoard, 5).whenPressed(
            new AimHighCommand(mLauncher));

        new JoystickButton(mButtonBoard, 6).whenPressed(
            new FunctionalCommand(void, mPanelRotator::raise, mPanelRotator::stop, mPanelRotator::getBeamSensorUp, mPanelRotator)); // Raise the PanMan
        new JoystickButton(mButtonBoard, 7).whenPressed(
            new FunctionalCommand(void, mPanelRotator::lower, mPanelRotator::stop, mPanelRotator::getBeamSensorDown, mPanelRotator)); // Lower the PanMan
        new JoystickButton(mButtonBoard, 8).whenPressed(
            new SpinToColorCommand(mPanelRotator));
        new JoystickButton(mButtonBoard, 9).whenPressed(
            new SpinRotationsCommand(mPanelRotator));

        new JoystickButton(mButtonBoard, 10).whileHeld(
            new StartEndCommand(mClimber::stop, mClimber::extend, mClimber::stop, mClimber)); // Extend the Climber elevator
        new JoystickButton(mButtonBoard, 11).whileHeld(
            new StartEndCommand(mClimber::stop, mClimber::retract, mClimber::stop, mClimber)); // Retract the Climber elevator
        new JoystickButton(mButtonBoard, 14).whenHeld(
            new ClimberWinchCommand(mClimber)); // Consists of a CommandGroup - ClimberWinchPrimary and ClimberWinchSecondary (which will be lambdas)

        new JoystickButton(mButtonBoard, 15).whenHeld(
            new FunctionalCommand(void, mLauncher::raise, mLauncher::stop, mLauncher::isElevationStalled, mLauncher)); // Raises angle of elevation by lowering the hood
        new JoystickButton(mButtonBoard, 16).whenHeld(
            new FunctionalCommand(void, mLauncher::lower, mLauncher::stop, mLauncher::isElevationStalled, mLauncher)); // Lowers the angle of elevation by raising the hood
        new JoystickButton(mButtonBoard, 17).whenHeld(
            new FunctionalCommand(void, mLauncher::left, mLauncher::stop, mLauncher::isRotationStalled, mLauncher)); // Rotates the turret left
        new JoystickButton(mButtonBoard, 18).whenHeld(
            new FunctionalCommand(void, mLauncher::right, mLauncher::stop, mLauncher::isRotationStalled, mLauncher)); // Rotates the turret right
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        String selectedModeName = SmartDashboard.getString(kSelectedAutoModeKey, "NO SELECTED MODE!!!!");
        Logger.notice("Auto mode name " + selectedModeName);
        for (var mode : mAutoModes) {
            if (mode.name.equals(selectedModeName)) {
                return mode.command;
            }
        }

        Logger.error("AutoModeSelector failed to select auto mode: " + selectedModeName);
        return kDefaultAutoMode.command;
    }
}
