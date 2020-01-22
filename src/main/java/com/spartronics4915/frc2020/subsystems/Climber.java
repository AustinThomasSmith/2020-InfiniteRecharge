/**
 * This subsystem has two motors. A NEO using a Spark, while the other is a 775 PRO using a Talon.
 * The NEO motor winches the climber and the 775 PRO extends the climber
 * The four methods used are extend(), winch(), reverse(), and stop()
 */

package com.spartronics4915.frc2020.subsystems;

import com.spartronics4915.frc2020.Constants;
import com.spartronics4915.lib.subsystems.SpartronicsSubsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.TalonSRXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
<<<<<<< HEAD

/**
 * Objective:
 * Subsystem will provide necessary controls/APIs for climbing consistently. Use cases:
 * 1. Accurately hitting the bar in order to climb with upmost efficiancy
 * 2. Describing if the climber is functional or not during the match
 * 3. Not causing any mechanical issues when climbing (battery falling out and the like)
 * 
 * Integrations:
 * 
 */
=======
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

>>>>>>> a511f88a9501da9562967a70b424b6ed9d1a5435
public class Climber extends SpartronicsSubsystem {

    private static TalonSRX mClimber775Pro;
    private static CANSparkMax mClimberNEO;

    public Climber() {
        // Hardware Contructor (Add motors and such here when I get them)
        mClimber775Pro = new TalonSRX(Constants.Climber.kLiftMotorId);
        mClimberNEO = new CANSparkMax(Constants.Climber.kWinchMotorId, MotorType.kBrushless);
    }
<<<<<<< HEAD
    public boolean isWinchFast() {
        //TODO get this method fixed once we understand how the gearbox works
        return true;
    }
    public static void extend() {
        mClimber775PRO.set(ControlMode.PercentOutput, 0.0);
=======

    public void extend() {
        mClimber775Pro.set(ControlMode.PercentOutput, 0.0);
>>>>>>> a511f88a9501da9562967a70b424b6ed9d1a5435
        mClimberNEO.set(0.0);
    }

    public void winch() {
        mClimber775Pro.set(ControlMode.PercentOutput, 0.0);
        mClimberNEO.set(0.0);
    }

<<<<<<< HEAD
    public static void extendReverse() {
        mClimber775PRO.set(ControlMode.PercentOutput, 0.0);
=======
    public void reverse() {
        mClimber775Pro.set(ControlMode.PercentOutput, 0.0);
>>>>>>> a511f88a9501da9562967a70b424b6ed9d1a5435
        mClimberNEO.set(0.0);
    }

    public void stop() {
        mClimber775Pro.set(ControlMode.PercentOutput, 0.0);
        mClimberNEO.set(0.0);
    }

    public static void isWinchStalled() {

    }

}
