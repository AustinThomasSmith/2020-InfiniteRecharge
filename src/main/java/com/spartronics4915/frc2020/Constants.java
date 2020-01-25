package com.spartronics4915.frc2020;

import edu.wpi.first.wpilibj.util.Units;

public final class Constants {

    public static final class Climber {
        public static final int kLiftMotorId = 5;
        public static final int kWinchMotorId = 6;
        public static final double kExtendSpeed = 1.0;
        public static final double kWinchSpeed = 1.0;
        public static final boolean kStalled = true;
    }
    
    public static final class Indexer {
        public static final int kSpinnerId = -1;
        public static final int kLoaderId = -1;
        public static final int kProxSensorId = -1;
    }   

    public static final class Launcher {
        public static final int kFlywheelMasterID = -1;
        public static final int kFlywheelFollowerID = -1;
        public static final int kAngleAdjusterID = -1;
        public static final int kTurretID = -1;
    }

    public static final class OI {
        public static final int kJoystickId = 0;
        public static final int kButtonBoardId = 1;
    }

    public static final class PanelRotator {
        public static final int kBeamSensorUpID = -1;
        public static final int kBeamSensorDownID = -1;

        public static final int kExtendMotorID = -1;
        public static final int kSpinMotorID = -1;

        public static final double kExtendMotorSpeed = 0.5;
        public static final double kSpinMotorSpeed = 0.5;
    }

    public static final class Drive {
        public static final int kRightDriveMaster = 1;
        public static final int kRightDriveFollower = 2;
        public static final int kLeftDriveMaster = 3;
        public static final int kLeftDriveFollower = 4;

        public static final double kWheelDiameter = Units.inchesToMeters(8);
        public static final double kTrackWidthMeters = 1;
        public static final int kNativeUnitsPerRevolution = 1; //TODO: get ratio

        public static final double kRobotMassKg = 1;
        public static final double kMoi = 1;

        //TODO: characterize
        public static final double kRightS = 1;
        public static final double kRightV = 1;
        public static final double kRightA = 1;
        
        public static final double kLeftS = 1;
        public static final double kLeftV = 1;
        public static final double kLeftA = 1;
    }
}
