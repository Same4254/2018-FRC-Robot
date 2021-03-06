package org.usfirst.frc.team3555.robot.SubSystems;

import org.usfirst.frc.team3555.robot.Autonomous.Action;
import org.usfirst.frc.team3555.robot.SubSystems.Controllers.CurvedJoystick;
import org.usfirst.frc.team3555.robot.SubSystems.Controllers.CurvedXboxController;
import org.usfirst.frc.team3555.robot.SubSystems.Controllers.MotorGroup;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import edu.wpi.first.wpilibj.GenericHID.Hand;

public class DriveTrain extends SubSystem {
	private MotorGroup mainGroup;//Back
	private MotorGroup slaves;//Front 
	
	private CurvedXboxController controller;
	private CurvedJoystick joyLeft, joyRight;
	
	//Data
	private double wheelCircumference; // <- meters
	private double wheelRadius; // <- meters
	private double distanceBetweenWheels; // <- meters
	
	public DriveTrain() {
		//Input init
		controller = new CurvedXboxController(1);
//		joyLeft = new CurvedJoystick(2);
//		joyRight = new CurvedJoystick(3);
		
		//Init groups to hold the talons
		mainGroup = new MotorGroup(43, 44);
		slaves = new MotorGroup(41, 42);
		
//		leftRear = new CANTalon(43);
//		leftFront = new CANTalon(41); 
//		
//		rightRear = new CANTalon(44);
//		rightFront = new CANTalon(42);
		
		mainGroup.setControlMode(ControlMode.PercentOutput);
		mainGroup.setFeedBackDevice(FeedbackDevice.QuadEncoder);
		
		mainGroup.setSensorUnitsPerRotation(360);

		//Set PID constants
		mainGroup.setLeftPIDF(.85, 0.01, .2, 0);
		mainGroup.setRightPIDF(.85, 0.01, .2, 0);
		
		//Make sure that the right side has the same positive direction as the left side
		mainGroup.negateRightSetPoint(true);
		mainGroup.setScaleFactorMinimum(.3);//Scale factor to slow down the speed when more precise movemnt is desired
		
		//Follow the main group
		slaves.setControlMode(ControlMode.Follower);
		slaves.set(mainGroup.getLeftController().getDeviceID(), mainGroup.getRightController().getDeviceID());// Follow the main controllers
		
		//Enable the drives
		mainGroup.setEnabled(true);
		slaves.setEnabled(true);
		
		//Math Data for auto
		wheelRadius = 0.0762;
		wheelCircumference = 2 * Math.PI * wheelRadius;
		distanceBetweenWheels = .51;
	}
	
	/**
	 * Interpret the driver input, and make sure the slave drives are up to date with their master controllers
	 */
	@Override
	public void teleopUpdate() {
		interpretController();
		
//		mainGroup.update();
		slaves.update();
	}
	
	/**
	 * Interpret the controller input to how it would be in a video game
	 */
	private void interpretController() {
		if(controller.getBButtonReleased())
			swapFront();
		
		double forward = 0;
		double backward = 0;
		
//		forward = controller.getCurvedTrigger(Hand.kRight);
//		backward = controller.getCurvedTrigger(Hand.kLeft);
		
		forward = controller.getRawTrigger(Hand.kRight);
		backward = controller.getRawTrigger(Hand.kLeft);
		
		forward -= backward;//Total Forward Power

		//Separate into the two sides
		double rightForward = forward;
		double leftForward = forward;
		
		//Turn Power
		double rightTurn = 0;
		double leftTurn = 0;
		
		double stickValue = controller.getRawX(Hand.kLeft) / 2.0;
		if(stickValue > 0) {
			rightTurn = stickValue;
			rightForward *= 1 - rightTurn;
//			rightForward *= 1 - (rightTurn * 1.5);
		} else if(stickValue < 0) {
			leftTurn = stickValue * -1;
			leftForward *= 1 - leftTurn;
//			leftForward *= 1 - (leftTurn * 1.5);
		}
		
		//Reverse 
		if(forward < 0) {
			rightTurn *= -1;
			leftTurn *=-1;
		}
			
//		System.out.println(forward);
		
		//Apply power
		mainGroup.set(leftForward + rightTurn, rightForward + leftTurn);
	}
	
	/**
	 * Control Robot with a single joystick in an arcade fashion
	 */
	private void interpretArcadeDrive() {
		double leftSpeed = 0;
    	double rightSpeed = 0;
    	
    	leftSpeed = joyRight.getCurvedY() + joyRight.getCurvedX();
    	rightSpeed = -joyRight.getCurvedY() + joyRight.getCurvedX();

    	mainGroup.set(leftSpeed, rightSpeed);
	}
	
	/**
	 * Control Robot with both joysticks in a tank fashion
	 */
	private void interpretTankDrive() {
		double leftSpeed = 0;
    	double rightSpeed = 0;
    	
    	leftSpeed = joyLeft.getCurvedY();
		rightSpeed = joyRight.getCurvedY();
    	
		mainGroup.set(leftSpeed, rightSpeed);
	}
	
	/**
	 * Swap which direction is forward
	 * This is used for when the robot is turned around, so that the driver can change the forward direction and not think about how to drive backwards
	 */
	private void swapFront() {
		mainGroup.swap();
		mainGroup.invert();
		slaves.swap();
		slaves.invert();
	}
	
	//***************************** Actions ***********************************//
	
	/**
	 * Create an action object to drive the robot at certain rpm on each side for a certain amount of seconds. 
	 * Battery / Signal light is the front.
	 * 
	 * @param speedLeft - Speed in RPM
	 * @param speedRight - Speed in RPM
	 * @param seconds - Seconds for this to take (Keep this reasonable)
	 * @return - The Action object to be used in autonomous
	 */
	public Action getAutoDriveAction(double speedLeft, double speedRight, double seconds) {
		return new Action(() -> {//Start
			mainGroup.setControlMode(ControlMode.Velocity);

			mainGroup.set(speedLeft, speedRight);
			slaves.update();
			
//			leftRear.set(-speedLeft);
//			rightRear.set(speedRight);
//			leftFront.set(leftRear.getDeviceID());
//			rightFront.set(rightRear.getDeviceID());
		}, (startTime) -> {//Update
			if(System.currentTimeMillis() >= (seconds * 1000) + startTime)
				return true;
			return false;
		}, () -> {//Clean Up
//			leftRear.set(0);
//			rightRear.set(0);
//			leftFront.set(leftRear.getDeviceID());
//			rightFront.set(rightRear.getDeviceID());
			
			mainGroup.set(0);
			slaves.update();
			mainGroup.setControlMode(ControlMode.PercentOutput);
			
//			leftRear.setControlMode(ControlMode.PercentOutput);
//			rightRear.setControlMode(ControlMode.PercentOutput);
//			leftRear.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
//			rightRear.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		});
	}
		
	/**
	 * Creates an action object that will drive each side their designated distance in the allocated time.
	 * 
	 * @param metersLeftSide - Meters for the left side to drive 
	 * @param metersRightSide - Meters for the right side to drive
	 * @param seconds - Seconds for this to be completed in (Keep this reasonable)
	 * @return - The action object that can be added to the queue
	 */
	public Action drive(double metersLeftSide, double metersRightSide, double seconds) {
		double metersPerSecondLeft = metersLeftSide / seconds;
		double metersPerSecondRight = metersRightSide / seconds;
		
		double rpmLeft = (metersPerSecondLeft / wheelRadius) * (60 / (Math.PI * 2)); 
		double rpmRight = (metersPerSecondRight / wheelRadius) * (60 / (Math.PI * 2)); 
		
		return getAutoDriveAction(rpmLeft, rpmRight, seconds);
	}
	
	/**
	 * Creates an action object that will drive both sides the designated distance in the allocated time
	 * 
	 * @param meters - Distance in meters for both sides to drive
	 * @param seconds - Seconds that this is to be done in
	 * @return - The Action object that can be added to the autonomous queue
	 */
	public Action drive(double meters, double seconds) { return drive(meters, meters, seconds); }
	
	/**
	 * Turn a certain degrees to the right.
	 * This will only turn the left wheel, the right will remain stationary.
	 * 
	 * @param degrees - Degrees to turn to the right
	 * @param seconds - Seconds that this action should take
	 * @return - Action object to be added to the autonomous queue
	 */
	public Action turnLeftDegrees(double degrees, double seconds) { return turnLeftRadians(Math.toRadians(degrees), seconds); }
	
	/**
	 * Turn a certain radians to the right.
	 * This will only turn the left wheel, the right will remain stationary.
	 * 
	 * @param radians - Radians to turn to the right
	 * @param seconds - Seconds that this action should take
	 * @return - Action object to be added to the autonomous queue
	 */
	public Action turnLeftRadians(double radians, double seconds) {
		double distance = radians * distanceBetweenWheels;
		return drive(distance, 0, seconds);
	}

	/**
	 * Turn a certain degrees to the right.
	 * This will turn both wheels in opposite directions, causing it to turn on a point (roughly)
	 * 
	 * @param degrees - Degrees to turn to the right
	 * @param seconds - Seconds that this action should take
	 * @return - Action object to be added to the autonomous queue
	 */
	public Action turnLeftOnDimeDegrees(double degrees, double seconds) { return turnLeftOnDimeRadians(Math.toRadians(degrees), seconds); }

	/**
	 * Turn a certain radians to the right.
	 * This will turn both wheels in opposite directions, causing it to turn on a point (roughly)
	 * 
	 * @param radians - Radians to turn to the right
	 * @param seconds - Seconds that this action should take
	 * @return - Action object to be added to the autonomous queue
	 */
	public Action turnLeftOnDimeRadians(double radians, double seconds) {
		double distance = radians * distanceBetweenWheels;
		return drive(distance, -distance);
	}
	
	/**
	 * Turn a certain degrees to the left.
	 * This will only turn the right wheel, the left will remain stationary.
	 * 
	 * @param degrees - Degrees to turn to the left
	 * @param seconds - Seconds that this action should take
	 * @return - Action object to be added to the autonomous queue
	 */
	public Action turnRightDegrees(double degrees, double seconds) { return turnRightRadians(Math.toRadians(degrees), seconds); }
	
	/**
	 * Turn a certain radians to the left.
	 * This will only turn the right wheel, the left will remain stationary.
	 * 
	 * @param radians - Radians to turn to the left
	 * @param seconds - Seconds that this action should take
	 * @return - Action object to be added to the autonomous queue
	 */
	public Action turnRightRadians(double radians, double seconds) {
		double distance = radians * distanceBetweenWheels;
		return drive(0, distance, seconds);
	}
	
	/**
	 * Turn a certain degrees to the Left.
	 * This will turn both wheels in opposite directions, causing it to turn on a point (roughly)
	 * 
	 * @param degrees - Degrees to turn to the left
	 * @param seconds - Seconds that this action should take
	 * @return - Action object to be added to the autonomous queue
	 */
	public Action turnRightOnDimeDegrees(double degrees, double seconds) { return turnRightOnDimeRadians(Math.toRadians(degrees), seconds); }
	
	/**
	 * Turn a certain radians to the Left.
	 * This will turn both wheels in opposite directions, causing it to turn on a point (roughly)
	 * 
	 * @param radians - Radians to turn to the left
	 * @param seconds - Seconds that this action should take
	 * @return - Action object to be added to the autonomous queue
	 */
	public Action turnRightOnDimeRadians(double radians, double seconds) {
		double distance = radians * distanceBetweenWheels;
		return drive(-distance, distance);
	}
	
	/**
	 * Spins the robot on a dime, rotations in terms of the amount of time to turn the robot 360 degrees (2pi radians)
	 * 
	 * @param rotations - Amount of full spins of the robot
	 * @param seconds - Seconds for this action to complete
	 * @return - Action object to be added to the autonomous queue
	 */
	public Action spinRight(double rotations, double seconds) {
		return turnRightOnDimeDegrees(rotations * 360, seconds);
	}
	
	/**
	 * Spins the robot on a dime, rotations in terms of the amount of time to turn the robot 360 degrees (2pi radians)
	 * 
	 * @param rotations - Amount of full spins of the robot
	 * @param seconds - Seconds for this action to complete
	 * @return - Action object to be added to the autonomous queue
	 */
	public Action spinLeft(double rotations, double seconds) {
		return turnLeftOnDimeDegrees(rotations * 360, seconds);
	}
	
	/**
	 * Drive the robot in terms of wheel rotations
	 * 
	 * @param rotationsLeft - Amount rotations of the left wheels
	 * @param rotationsRight - Amount of rotations of the right wheels
	 * @param seconds - Seconds for this Action to complete
	 * @return - Action object to be added to the autonomous queue
	 */
	public Action driveRotations(double rotationsLeft, double rotationsRight, double seconds) { 
		return drive(wheelCircumference * rotationsLeft, wheelCircumference * rotationsRight, seconds); 
	}
	
	/**
	 * Drive the robot in terms of wheel rotations
	 * 
	 * @param rotations - Amount of rotations for all wheels to turn
	 * @param seconds - Seconds for this Action to complete
	 * @return - Action object to be added to the autonomous queue
	 */
	public Action driveRotations(double rotations, double seconds) { return drive(wheelCircumference * rotations, wheelCircumference * rotations, seconds); }
}
