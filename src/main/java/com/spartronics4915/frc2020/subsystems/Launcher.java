package com.spartronics4915.frc2020.subsystems;

import com.revrobotics.CANAnalog;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANAnalog.AnalogMode;
import com.spartronics4915.frc2020.Constants;
import com.spartronics4915.frc2020.commands.LauncherCommands;
import com.spartronics4915.lib.hardware.motors.SensorModel;
import com.spartronics4915.lib.hardware.motors.SpartronicsAnalogEncoder;
import com.spartronics4915.lib.hardware.motors.SpartronicsEncoder;
import com.spartronics4915.lib.hardware.motors.SpartronicsMax;
import com.spartronics4915.lib.hardware.motors.SpartronicsMotor;
import com.spartronics4915.lib.hardware.motors.SpartronicsSRX;
import com.spartronics4915.lib.hardware.motors.SpartronicsSimulatedMotor;
import com.spartronics4915.lib.math.twodim.geometry.Rotation2d;
import com.spartronics4915.lib.subsystems.SpartronicsSubsystem;
import com.spartronics4915.lib.util.Interpolable;
import com.spartronics4915.lib.util.InterpolatingDouble;
import com.spartronics4915.lib.util.InterpolatingTreeMap;

import edu.wpi.first.wpilibj.AnalogEncoder;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.AnalogTrigger;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Launcher extends SpartronicsSubsystem
{
    private SpartronicsMotor mFlywheelMasterMotor;
    private SpartronicsEncoder mFlywheelEncoder;
    private SpartronicsMotor mTurretMotor;
    private final Servo mAngleAdjusterMasterServo;
    private final Servo mAngleAdjusterFollowerServo;
    private final SpartronicsAnalogEncoder mTurretEncoder;

    private InterpolatingTreeMap<InterpolatingDouble, LauncherState> table;

    private double targetRPS;
    private Rotation2d targetAngle;

    private SimpleMotorFeedforward mFeedforwardCalculator;
    private final PIDController mTurretPIDController;

    private static class LauncherState implements Interpolable<LauncherState>
    {
        public final Rotation2d hoodAngle;
        public final InterpolatingDouble flywheelSpeedRPS;

        public LauncherState(Rotation2d hoodAngle, InterpolatingDouble speedRPS)
        {
            this.hoodAngle = hoodAngle;
            this.flywheelSpeedRPS = speedRPS;
        }

        @Override
        public LauncherState interpolate(LauncherState endValue, double x)
        {
            return new LauncherState(hoodAngle.interpolate(endValue.hoodAngle, x),
                flywheelSpeedRPS.interpolate(endValue.flywheelSpeedRPS, x));
        }
    }

    public Launcher()
    {
        // ONE NEO for flywheel
        mFlywheelMasterMotor = SpartronicsMax.makeMotor(Constants.Launcher.kFlywheelMasterId);
        if (mFlywheelMasterMotor.hadStartupError())
        {
            mFlywheelMasterMotor = new SpartronicsSimulatedMotor(Constants.Launcher.kFlywheelMasterId);
            logInitialized(false);
        }
        else
        {
            logInitialized(true);
        }
        mFlywheelMasterMotor.setVelocityGains(Constants.Launcher.kP, 0, 0, 0); //ref value is 0.00036
        mFeedforwardCalculator = new SimpleMotorFeedforward(Constants.Launcher.kS,
            Constants.Launcher.kV, Constants.Launcher.kA);
        mFlywheelMasterMotor.setOutputInverted(true);
        mFlywheelEncoder = mFlywheelMasterMotor.getEncoder();

        // One BAG motor for turret
        mTurretMotor = SpartronicsSRX.makeMotor(Constants.Launcher.kTurretId,
            SensorModel.toRadians(360));
        
        if (mTurretMotor.hadStartupError())
        {
            mTurretMotor = new SpartronicsSimulatedMotor(Constants.Launcher.kTurretId);
            logInitialized(false);
        }
        else
        {
            logInitialized(true);
        }
        
        var analogInput = new AnalogInput(Constants.Launcher.kTurretPotentiometerId);
        analogInput.setAverageBits(4);
        mTurretEncoder = new SpartronicsAnalogEncoder(analogInput);
        mTurretEncoder.setDistancePerRotation(1);
        mTurretPIDController = new PIDController(Constants.Launcher.kTurretP, 0, Constants.Launcher.kTurretD);

        // Two Servos for angle adjustement
        mAngleAdjusterMasterServo = new Servo(Constants.Launcher.kAngleAdjusterMasterId);
        mAngleAdjusterFollowerServo = new Servo(Constants.Launcher.kAngleAdjusterFollowerId);

        mFlywheelEncoder = mFlywheelMasterMotor.getEncoder();

        setUpLookupTable(Constants.Launcher.LookupTableSize, Constants.Launcher.DistanceTable,
            Constants.Launcher.AngleTable, Constants.Launcher.RPSTable);

        SmartDashboard.putNumber("Launcher/FlywheelRPS", 0);
    }

    /**
     * call this in execute() method of a command to have the motor constantly run at the target rpm
     */
    public void runFlywheel()
    {
        mFlywheelMasterMotor.setVelocity(targetRPS, mFeedforwardCalculator.calculate(targetRPS / 60.0));
        // System.out.println("Flywheel's current rps is " + getCurrentRPS());
    }


    public void rotateHood()
    {
        mAngleAdjusterMasterServo.setAngle(targetAngle.getDegrees());
        mAngleAdjusterFollowerServo.setAngle(180 - targetAngle.getDegrees());
    }

    /**
     * Rotates turret to a specific angle relative to the home position
     * @param absoluteAngle Angle in degrees you want to turn the turret relative to the home position
     */
    public void turnTurret(Rotation2d absoluteAngle)
    {
        double output = mTurretPIDController.calculate(mTurretEncoder.get(), absoluteAngle.getDegrees());
        mTurretMotor.setDutyCycle(output);
    }

    /**
     * Returns the current angle the turret is facing relative to straight ahead/home position
     * @return Current angle in degrees the turret is facing relative to the home position (forwards)
     */
    public double getTurretDirection()
    {
        return mTurretEncoder.get();
    }

    /**
     * Sets target angle to given angle
     * @param angle Angle in degrees above horizontal you want the angle adjuster to go to
     */
    public void setPitch(double angle)
    {
        if (angle > 30)
        {
            angle = 30;
        }
        targetAngle = Rotation2d.fromDegrees(angle);
    }

    /**
     * Sets target rpm for flywheel to given RPS
     * <p>
     * Does not allow values greater than 90 (currently,
     * refer to Constants.Launcher.kMaxRPS) RPS.
     * @param rpm RPM you want the flywheel to target
     */
    public void setRPS(double rps)
    {
        if (rps > Constants.Launcher.kMaxRPS)
            targetRPS = Constants.Launcher.kMaxRPS;
        else
            targetRPS = rps;
    }

    /**
     * Returns current target angle of angle adjuster
     * @return Angle in degrees above horizontal that the angle adjuster is targeting
     */
    public Rotation2d getTargetPitch()
    {
        return targetAngle;
    }

    /**
     * Returns current target RPS of shooter
     * @return RPS that the flywheel is targeting
     */
    public double getTargetRPS()
    {
        return targetRPS;
    }

    /**
     * Returns current angle of angle adjuster
     * @return Current angle in degrees above horizontal of the angle adjuster
     */
    public double getCurrentPitch()
    {
        // NEED ENC OR POT
        return mAngleAdjusterMasterServo.getPosition();
    }

    /**
     * Returns current RPS of shooter
     * @return The current RPS of the flywheel
     */
    public double getCurrentRPS()
    {
        return mFlywheelEncoder.getVelocity();
    }

    /**
     * Computes and returns angle for angle adjuster based on input distance
     * @param distance Horizontal distance in meters from the shooter to the target
     * @return The angle in degrees above horizontal that is calculated to be necessary to hit the target based off of the input distance
     */
    public Rotation2d calcPitch(double distance)
    {
        Rotation2d angle = table.getInterpolated(new InterpolatingDouble(distance)).hoodAngle;
        return angle;
    }

    /**
     * Computes and returns RPS based on input distance
     * @param distance Horizontal distance in meters from the shooter to the target
     * @return RPS calculated to be necessary to hit the target based of of the input distance
     */
    public double calcRPS(double distance)
    {
        double RPS = table
            .getInterpolated(new InterpolatingDouble(distance)).flywheelSpeedRPS.value;
        return RPS;
    }

    /**
     * Returns whether or not the target is within the range that the turret can rotate to, used by driver
     * @return True if the target is within the turret's range of rotation, else false
     */
    public boolean inFOV()
    {
        boolean inRotationRange = true;
        return inRotationRange;
    }

    /**
     * Returns whether or not the target is within the range that the shooter can shoot, used by driver
     * @return True if the target is within the horizontal distance from the target the shooter is capable of shooting to, else false
     */
    public boolean inRange()
    {
        boolean inRange = true;
        return inRange;
    }

    /**
     * Resets shooter and stops flywheel
     */
    public void reset()
    {
        setRPS(0);
        mFlywheelMasterMotor.setBrakeMode(true);
        setPitch(0);
        turnTurret(Rotation2d.fromDegrees(0));
    }

    public void setUpLookupTable(int size, double[] distances, double[] angles, double[] rps)
    {
        table = new InterpolatingTreeMap<>();
        table.put(new InterpolatingDouble(0.1),
            new LauncherState(Rotation2d.fromDegrees(45.0), new InterpolatingDouble(90.0)));
        for (int k = 0; k < size; k++)
        {
            table.put(new InterpolatingDouble(distances[k]), new LauncherState(
                Rotation2d.fromDegrees(angles[k]), new InterpolatingDouble(rps[k])));
        }
    }

    @Override
    public void periodic()
    {
        dashboardPutNumber("turretAngle", getTurretDirection());
        dashboardPutNumber("currentFlywheelRPS", getCurrentRPS());
        dashboardPutNumber("currentHoodAngle", getCurrentPitch());

    }
}
