package com.spartronics4915.frc2020.subsystems;

import com.spartronics4915.frc2020.Constants;
import com.spartronics4915.lib.subsystems.SpartronicsSubsystem;
import com.spartronics4915.lib.hardware.motors.SpartronicsSRX;
import com.spartronics4915.lib.hardware.motors.SpartronicsMotor;
import com.spartronics4915.lib.hardware.motors.SensorModel;
import com.spartronics4915.lib.hardware.motors.SpartronicsSimulatedMotor;

/**
 * The Intake subsystem takes balls from the playing field and outputs them to
 * storage.
 */
public class Intake extends SpartronicsSubsystem
{
    private SpartronicsMotor mHarvestMotor;

    public Intake()
    {
        mHarvestMotor = SpartronicsSRX.makeMotor(Constants.Intake.kHarvestMotorId,
            SensorModel.fromMultiplier(1));
        if (mHarvestMotor.hadStartupError())
        {
            mHarvestMotor = new SpartronicsSimulatedMotor();
            logInitialized(false);
        }
        else
        {
            logInitialized(true);
        }
    }

    /**
     * Activates the mechanum "vector" wheels in intake
     */
    public void intake()
    {
        mHarvestMotor.setDutyCycle(Constants.Intake.kHarvestSpeed);
    }

    /**
     * Reverses the vector wheel intake
     */
    public void reverse()
    {
        mHarvestMotor.setDutyCycle(Constants.Intake.kEjectSpeed);
    }

    /**
     * Checks to see if a ball is held in the intake chamber
     * with a proximity sensor returning a digital value
     * <p>
     * The style of proximity sensor we use requires MANUAL calibration
     *
     * @return Whether a ball is held
     */
    public boolean isBallHeld()
    {
        // TODO: Implement this functionality with the digital Proximity Sensor
        return false;
    }

    /**
     * Universal stop method
     */
    public void stop()
    {
        mHarvestMotor.setNeutral();
    }
}
