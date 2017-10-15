package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

/**
 * Holds the sensor data. This could easily be extended
 * to hold additional information (for example, whether
 * the car is braking / accelerating).
 * 
 * Currently we make a new SensorData for each update call
 * so all the attributes are set to final. As such we're
 * not using getters and settings.
 * 
 * TODO talk about this in the design rationale.
 * 
 * @author daniel
 *
 */
public class SensorData {
	public final HashMap<Coordinate, MapTile> CURRENT_VIEW;
	public final float SPEED;
	public final float ANGLE;
	public final int HEALTH;
	public final WorldSpatial.RelativeDirection TURNING;

	public SensorData(MyAIController controller) {
		CURRENT_VIEW = controller.getView();
		SPEED = controller.getSpeed();
		ANGLE = controller.getAngle();
		HEALTH = controller.getHealth();
		TURNING = controller.getTurningDirection();
	}
}
