package mycontroller;

import utilities.Coordinate;
import world.WorldSpatial;

public class MyAISteering {

	private MyAIController controller;
	private Coordinate spot;

	public MyAISteering(MyAIController controller) {
		this.controller = controller;
		spot = null;
	}

	/**
	 * Accelerate if the car isn't at top speed.
	 * 
	 * @param topSpeed
	 */
	public void driveForward(float topSpeed) {
		// TODO topSpeed is just CAR_SPEED. Does CAR_SPEED belong in here?
		// Or MyAIController?
		if (controller.getSpeed() < topSpeed) {
			controller.applyForwardAcceleration();
		}
	}

	/**
	 * Rotate left or right until the car is aiming at directionToGo.
	 * When this is first called, we note our current position,
	 * so we don't accidentally move away while rotating.
	 * TODO this function might need some tolerance, because floating point numbers.
	 * 
	 * @param directionToGo
	 */
	public void alignWithDirection(float directionToAlignWith, WorldSpatial.RelativeDirection relativeDirection) {
		if (spot == null) {
			spot = new Coordinate(controller.getPosition());
		}
		if (controller.getAngle() != directionToAlignWith) {
			rotateOnSpot(relativeDirection);
		} else {
			// We've successfully aligned with the angle.
			spot = null;
		}
	}
	
	/**
	 * Rotate on the spot. 
	 * @param spot
	 * @param relativeDirection
	 */
	private void rotateOnSpot(WorldSpatial.RelativeDirection relativeDirection) {
		// Rotate on the spot, somehow not moving off the current
		// position while doing so.
	}


}
