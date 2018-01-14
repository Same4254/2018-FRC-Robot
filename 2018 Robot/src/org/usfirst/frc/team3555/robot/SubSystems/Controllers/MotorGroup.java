package org.usfirst.frc.team3555.robot.SubSystems.Controllers;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

public class MotorGroup {
	/**
	 * All the different ways to interpret joystick input
	 */
	public static enum DriveMode {
		ArcadeDrive, TankDrive;
	}
	
	/**
	 * Controllers for the two sides
	 */
	private CANTalon left, right;
	
	/**
	 * What control mode to use for interpreting joystick input
	 */
	private DriveMode currentDriveMode;
	
	/**
	 * Variables to scale the motor output
	 * Cannot go lower than the minimum
	 */
	private double scaleFactor, scaleFactorMinimum;
	
	public MotorGroup(int idLeft, int idRight) {
		left = new CANTalon(idLeft);
		right = new CANTalon(idRight);
		
		currentDriveMode = DriveMode.ArcadeDrive;
	}

	//***************************** Delegate Methods For The CANTalons *****************************  
	
	/**
	 * Update the controllers to their setPoints. 
	 * Call talon.set(double, double) or talon.set(double) at some point to change their setPoints. 
	 */
	public void update() {
		left.update();
		right.update();
	}
	
	/**
	 * Check the enabled state of the two motor controllers 
	 * 
	 * @return -> Whether or not the controllers are enabled
	 */
	public boolean isEnabled() { return left.isEnabled(); }
	
	/**
	 * Change the enabled state of the two motor controllers
	 * 
	 * @param enabled -> Dictates whether or not these controllers are enabled
	 */
	public void setEnabled(boolean enabled) {
		left.setEnabled(enabled);
		right.setEnabled(enabled);
	}
	
	/**
	 * Dictates the setPoint of each side. 
	 * Be sure to check the inversion of these controllers -> we really don't want to break any gear boxes here... 
	 * If the inversion has been set properly (invertLeft(boolean), inverRight(boolean)) neither of these should be negative. 
	 * 
	 * @param leftSetPoint -> SetPoint for the left side controller
	 * @param rightSetPoint -> SetPoint for the right side controller
	 */
	public void set(double leftSetPoint, double rightSetPoint) {
		left.set(leftSetPoint);
		right.set(rightSetPoint);
	}

	/**
	 * Dictates the setPoint for both motor controllers. 
	 * The setPoint is literal! This will not invert automatically! Call one of the invert methods! 
	 * 
	 * @param setPoint -> SetPoint of both controllers
	 */
	public void set(double setPoint) {
		set(setPoint, setPoint);
	}

	/**
	 * Configurate the amount of native units each rotation of the sensor is equivalent to
	 * 
	 * @param sensorUnitsPerRotation -> Amount of native units per rotation
	 */
	public void setSensorUnitsPerRotation(int sensorUnitsPerRotation) {
		left.setSensorUnitsPerRotation(sensorUnitsPerRotation);
		right.setSensorUnitsPerRotation(sensorUnitsPerRotation);
	}
	
	/**
	 * Change the controlMode of the controllers. 
	 * 
	 * @param controlMode -> How the controllers will interpret their SetPoint
	 */
	public void setControlMode(ControlMode controlMode) {
		left.setControlMode(controlMode);
		right.setControlMode(controlMode);
	}
	
	/**
	 * Dictates the sensor that both of the controllers will listen to.
	 * 
	 * @param feedBackDevice -> Sensor for both of the controllers
	 */
	public void setFeedBackDevice(FeedbackDevice feedbackDevice) {
		left.setFeedbackDevice(feedbackDevice);
		right.setFeedbackDevice(feedbackDevice);
	}
	
	/**
	 * Determine the velocity of the left side's controller in terms of rpm. 
	 * 
	 * @return -> Velocity of the left side controller's motor in terms of rpm
	 */
	public double getLeftVelocity() { return left.getVelocityRPM(); }
	
	/**
	 * Determine the velocity of the right side's controller in terms of rpm. 
	 * 
	 * @return -> Velocity of the right side controller's motor in terms of rpm
	 */
	public double getRightVelocity() { return right.getVelocityRPM(); }
	
	/**
	 * Determine the position of the right side's controller in terms of rotations. 
	 * 
	 * @return -> Position of the right side controller's motor in terms of rotations
	 */
	public double getLeftPosition() { return left.getPositionRotations(); }
	
	/**
	 * Changes both controllers to coast mode. 
	 * Coast by default. 
	 */
	public void setCoast() {
		left.setCoast();
		right.setCoast();
	}
	
	/**
	 * Changes both controllers to brake mode. 
	 * Coast by default.
	 */
	public void setBrake() { 
		left.setBrake();
		right.setBrake();
	}
	
	/**
	 * Invert the direction of both controllers from what they currently are. 
	 */
	public void invert() {
		left.setInverted(!left.getInverted());
		right.setInverted(!right.getInverted());
	}
	
	/**
	 * Invert the left side controller's output. 
	 * @param inverted -> Dictates whether it should be inverted
	 */
	public void invertLeft(boolean inverted) { left.setInverted(inverted); }

	/**
	 * Invert the right side controller's output. 
	 * @param inverted -> Dictates whether it should be inverted
	 */
	public void invertRight(boolean inverted) { right.setInverted(inverted); }
	
	/**
	 * Set the PID values of the Left Side controller
	 * 
	 * @param p -> Proportional value
	 * @param i -> Integral value
	 * @param d -> Derivative value
	 */
	public void setLeftPID(double p, double i, double d) { left.setPID(p, i, d); }
	
	/**
	 * Set the PIDF values of the Left Side controller
	 * 
	 * @param p -> Proportional value
	 * @param i -> Integral value
	 * @param d -> Derivative value
	 * @param f -> Feed Forward value 
	*/
	public void setLeftPIDF(double p, double i, double d, double f) { left.setPIDF(p, i, d, f); }
	
	/**
	 * Set the PID values of the Right Side controller
	 * 
	 * @param p -> Proportional value
	 * @param i -> Integral value
	 * @param d -> Derivative value
	 */
	public void setRightPID(double p, double i, double d) { right.setPID(p, i, d); }
	
	/**
	 * Set the PIDF values of the Right Side controller
	 * 
	 * @param p -> Proportional value
	 * @param i -> Integral value
	 * @param d -> Derivative value
	 * @param f -> Feed Forward value 
	*/
	public void setRightPIDF(double p, double i, double d, double f) { right.setPIDF(p, i, d, f); }
	
	//***************************** Interpret Data *****************************

	/**
	 * Interpret the x and y axis positions to create an arcade drive control scheme  
	 * Be sure to invert the motor properly to avoid breaking a gearbox or two
	 * 
	 * @param xAxis -> Value of the xAxis, for instance the position of the joystick on the xAxis
	 * @param yAxis -> Value of the yxAxis, for instance the position of the joystick on the yAxis
	 */
	public void arcadeDrive(double xAxis, double yAxis) {
		double leftSpeed = yAxis + xAxis;
    	double rightSpeed = yAxis - xAxis;
    	
    	set(leftSpeed * scaleFactor, rightSpeed * scaleFactor);
	}
	
	/**
	 * Interpret y axis positions to create a tank drive control scheme  
	 * Be sure to invert the motor properly to avoid breaking a gearbox or two
	 * 
	 * @param leftYAxis -> Value of the left y axis, for instance the position of the left joystick on the yAxis
	 * @param rightYAxis -> Value of the right y axis, for instance the position of the right joystick on the yAxis
	 */
	public void tankDrive(double leftYAxis, double rightYAxis) {
		double leftSpeed = leftYAxis;
		double rightSpeed = rightYAxis;
		
		set(leftSpeed * scaleFactor, rightSpeed * scaleFactor);
	}
	
	/**
	 * Dictate the factor to scale the motor output by. This is on a scale of 0 -> 1
	 * @param scaleFactor -> Amount to multiply motor output by
	 */
	public void setScaleFactor(double scaleFactor) {
		if(scaleFactor < scaleFactorMinimum)
			this.scaleFactor = scaleFactorMinimum;
		this.scaleFactor = scaleFactor; 
	}
	
	/**
	 * Dictate how low the factor can go before being capped
	 * @param minimum -> How low the scale factor can go
	 */
	public void setScaleFactorMinimum(double minimum) { this.scaleFactorMinimum = minimum; }
	
	/**
	 * @return -> The current drive mode that is stored
	 */
	public DriveMode getCurrentDriveMode() { return currentDriveMode; }
	
	/**
	 * @param driveMode -> Drive Mode to store
	 */
	public void setDriveMode(DriveMode driveMode) { this.currentDriveMode = driveMode; }
	
	//***************************** Getters *****************************
	
	/**
	 * Retrieve the left motor controller
	 * 
	 * @return -> The motor controller that controls the left side
	 */
	public CANTalon getLeftController() { return left; }
	
	/**
	 * Retrieve the right motor controller
	 * 
	 * @return -> The motor controller that controls the right side
	 */
	public CANTalon getRightcontroller() { return right; }
}
