// I'm bad at naming things. Please come up with a better name...
package com.spartronics4915.frc2020.subsystems;

import com.spartronics4915.frc2020.Constants;
import com.spartronics4915.lib.hardware.motors.SensorModel;
import com.spartronics4915.lib.hardware.motors.SpartronicsMax;
import com.spartronics4915.lib.hardware.motors.SpartronicsMotor;
import com.spartronics4915.lib.hardware.motors.SpartronicsSRX;
import com.spartronics4915.lib.subsystems.SpartronicsSubsystem;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.DigitalInput;

public class PanelRotator extends SpartronicsSubsystem
{
    private final SpartronicsMotor mSpinMotor;
    private final SpartronicsMotor mExtendMotor;

    private final DigitalInput mBeamSensorUp;
    private final DigitalInput mBeamSensorDown;

    private final ColorSensorV3 mColorSensor;

    // TODO: These are essentially random numbers, with the max value based on the
    // images at
    // https://www.andymark.com/products/infinite-recharge-control-panel-stickr
    public int[] mMinimumRed = {200, 0, 0};
    public int[] mMaximumRed = {255, 30, 30};

    // TODO: These are bad and will work in a way that will make you lose, which
    // will be sad
    public int[] mMinimumGreen = {0, 200, 0};
    public int[] mMaximumGreen = {30, 255, 30};

    // TODO: These are bad and will work in a way that will make you lose, which
    // will be sad
    public int[] mMinimumBlue = {0, 200, 200};
    public int[] mMaximumBlue = {30, 255, 255};

    // TODO: These are bad and will work in a way that will make you lose, which
    // will be sad
    public int[] mMinimumYellow = {200, 200, 0};
    public int[] mMaximumYellow = {255, 255, 30};

    public String sensedColor;

    public int red;
    public int green;
    public int blue;

    private final ColorMatch mColorMatcher = new ColorMatch();

    private final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
    private final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
    private final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);
    private final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);

    public PanelRotator()
    {
        mBeamSensorUp = new DigitalInput(Constants.PanelRotator.kBeamSensorUpID);
        mBeamSensorDown = new DigitalInput(Constants.PanelRotator.kBeamSensorDownID);
        mSpinMotor = SpartronicsMax.makeMotor(Constants.PanelRotator.kSpinMotorID,
            SensorModel.fromMultiplier(1));
        mExtendMotor = SpartronicsSRX.makeMotor(Constants.PanelRotator.kExtendMotorID,
            SensorModel.fromMultiplier(1));
        // mSpinMotor = new CANSparkMax(Constants.PanelRotator.kSpinMotorID,
        // MotorType.kBrushless);
        // mExtendMotor = new TalonSRX(Constants.PanelRotator.kExtendMotorID);
        mColorSensor = new ColorSensorV3(I2C.Port.kOnboard);
    }

    /** raises the arm holding the spinner at a set speed*/
    public void raise()
    {
        mExtendMotor.setDutyCycle(Constants.PanelRotator.kExtendMotorSpeed);
    }

    /** lowers the arm holding the spinner at a set speed*/
    public void lower()
    {
        mExtendMotor.setDutyCycle(-Constants.PanelRotator.kExtendMotorSpeed);
    }

    /** gets the color (Red, Blue, Yellow, or Green) through game specific messages that the robot needs to spin to */
    public String getTargetColor()
    {
        return DriverStation.getInstance().getGameSpecificMessage();
    }

    /** */
    public String getRGB()
    {
        int red = mColorSensor.getRed();
        int green = mColorSensor.getGreen();
        int blue = mColorSensor.getBlue();

        String RGB = red + ", " + green + ", " + blue;

        return RGB;
    }

    /** finds what color the color sensor is seeing  (Red, Blue, Yellow, or Green); currently just a placeholder for output */
    public String getActualColor()
    {
        mColorMatcher.addColorMatch(kBlueTarget);
        mColorMatcher.addColorMatch(kGreenTarget);
        mColorMatcher.addColorMatch(kRedTarget);
        mColorMatcher.addColorMatch(kYellowTarget);

        Color detectedColor = mColorSensor.getColor();

        ColorMatchResult match = mColorMatcher.matchClosestColor(detectedColor);

        if (match.color == kBlueTarget) {
            sensedColor = "Blue";
        } else if (match.color == kRedTarget) {
        sensedColor = "Red";
        } else if (match.color == kGreenTarget) {
        sensedColor = "Green";
        } else if (match.color == kYellowTarget) {
        sensedColor = "Yellow";
        } else {
        sensedColor = "Unknown; this shouldn't ever happen, but it did";
        }

        System.out.println(sensedColor);
        return sensedColor;

        // return "method not complete";
    }

    /** sees if the bottom beam sensor is triggered */
    public boolean getBeamSensorDown()
    {
        // TODO: maybe backwards
        return mBeamSensorDown.get();
    }

    /** sees if the top beam sensor is triggered */
    public boolean getBeamSensorUp()
    {
        return mBeamSensorUp.get(); // TODO: maybe backwards
    }

    /** spins the wheel to move the control panel */
    public void spin()
    {
        mSpinMotor.setDutyCycle(Constants.PanelRotator.kSpinMotorSpeed);
    }

    /** get the number of times that the spinning */
    public double getRotations()
    {
        return -1; // TODO: not complete
    }

    /** stops the wheel */
    public void stopSpin()
    {
        mSpinMotor.setDutyCycle(0);
    }

    /** stops the extension motor */
    public void stopExtendMotor()
    {
        mExtendMotor.setDutyCycle(0);
    }

    /** stops the two motors */
    public void stop()
    {
        mSpinMotor.setDutyCycle(0);
        mExtendMotor.setDutyCycle(0);
    }
}
