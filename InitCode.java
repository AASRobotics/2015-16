/**
This is code created by max boender - Revision tracking in Github
BASIC CODE - RUNS SIMPLE CONTROLL OF ROBOTIC THRU TANK DRIVE
*/

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * TeleOp Mode
 * <p>
 * Enables control of the robot via the gamepad
 */
public class TankDrive extends OpMode {
    // TETRIX VALUES.
    final static double ARM_MIN_RANGE  = 0.20;
    final static double ARM_MAX_RANGE  = 0.90;
    final static double CLAW_MIN_RANGE  = 0.20;
    final static double CLAW_MAX_RANGE  = 0.7;

	// position of the arm servo.
	double armPosition;

	// amount to change the arm servo position.
	double armDelta = 0.1;

	// position of the claw servo
	double clawPosition;

	// amount to change the claw servo position by
	double clawDelta = 0.1;

	DcMotor motorRight;
	DcMotor motorLeft;
  DcMotor motorArm;
	Servo claw;
	Servo arm;

	/**
	 * Constructor
	 */
	public TankDrive() {

	}

	/*
	 * Code to run when the op mode is first enabled goes here
	 * INITIAL SETUP CONFIG CODE
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
	 */
	@Override
	public void init() {
		motorRight = hardwareMap.dcMotor.get("motor_2");
		motorLeft = hardwareMap.dcMotor.get("motor_1");
    motorArm = hardwareMap.dcMotor.get("motor_3")
		motorLeft.setDirection(DcMotor.Direction.REVERSE);

		arm = hardwareMap.servo.get("servo_1");
		claw = hardwareMap.servo.get("servo_6");

		// assign the starting position of the wrist and claw
		armPosition = 0.2;
		clawPosition = 0.2;
	}

	/*
	 * This method will be called repeatedly in a loop
	 *
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
	 */
	@Override
	public void loop() {

		/*
		 * Gamepad 1
		 *
		 * Gamepad 1 controls the motors via the left stick, and it controls the
		 * wrist/claw via the a,b, x, y buttons
		 */

        // tank drive
        // note that if y equal -1 then joystick is pushed all of the way forward.
        float left = -gamepad1.left_stick_y;
        float right = -gamepad1.right_stick_y;

		// clip the right/left values so that the values never exceed +/- 1
		right = Range.clip(right, -1, 1);
		left = Range.clip(left, -1, 1);

		// scale the joystick value to make it easier to control
		// the robot more precisely at slower speeds.
		right = (float)scaleInput(right);
		left =  (float)scaleInput(left);

		// write the values to the motors
		motorRight.setPower(right);
		motorLeft.setPower(left);

		// update the position of the arm.
		if (gamepad1.a) {
			// if the A button is pushed on gamepad1, increment the position of
			// the arm servo.
			armPosition += armDelta;
		}

		if (gamepad1.y) {
			// if the Y button is pushed on gamepad1, decrease the position of
			// the arm servo.
			armPosition -= armDelta;
		}

        // update the position of the claw
        if (gamepad1.left_bumper) {
            clawPosition += clawDelta;
        }

        if (gamepad1.left_trigger > 0.25) {
            clawPosition -= clawDelta;
        }

        if (gamepad1.b) {
            clawPosition -= clawDelta;
        }

		// update the position of the claw
		if (gamepad1.x) {
			clawPosition += clawDelta;
		}

		if (gamepad1.b) {
			clawPosition -= clawDelta;
		}

		// clip the position values so that they never exceed their allowed range.
		armPosition = Range.clip(armPosition, ARM_MIN_RANGE, ARM_MAX_RANGE);
		clawPosition = Range.clip(clawPosition, CLAW_MIN_RANGE, CLAW_MAX_RANGE);

		// write position values to the wrist and claw servo
		arm.setPosition(armPosition);
		claw.setPosition(clawPosition);

		telemetry.addData("Text", "*** Robot Data***");
        telemetry.addData("arm", "arm:  " + String.format("%.2f", armPosition));
        telemetry.addData("claw", "claw:  " + String.format("%.2f", clawPosition));
		telemetry.addData("left tgt pwr",  "left  pwr: " + String.format("%.2f", left));
		telemetry.addData("right tgt pwr", "right pwr: " + String.format("%.2f", right));
	}

	/*
	 * Code to run when the op mode is first disabled goes here
	 *
   * CLEANUP
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
	 */
	@Override
	public void stop() {

	}

	/*
	 * Scales Joystick values
	 */
	double scaleInput(double dVal)  {
		double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
				0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

		// get the corresponding index for the scaleInput array.
		int index = (int) (dVal * 16.0);

		// index should be positive.
		if (index < 0) {
			index = -index;
		}

		// index cannot exceed size of array minus 1.
		if (index > 16) {
			index = 16;
		}

		// get value from the array.
		double dScale = 0.0;
		if (dVal < 0) {
			dScale = -scaleArray[index];
		} else {
			dScale = scaleArray[index];
		}

		// return scaled value.
		return dScale;
	}

}
