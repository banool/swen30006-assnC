package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;

/**
 * Holds the sensor data. This could easily be extended
 * to hold additional information (for example, whether
 * the car is braking / accelerating).
 * 
 * Currently we make a new SensorData for each update call
 * so all the attributes are set to final.
 * 
 * TODO talk about this in the design rationale.
 * 
 * @author daniel
 *
 */
public class SensorData {
	private final HashMap<Coordinate, MapTile> currentView;
	private final float speed;
	private final float angle;
	private final int health;

	public SensorData(HashMap<Coordinate, MapTile> currentView, float speed, float angle, int health) {
		this.currentView = currentView;
		this.speed = speed;
		this.angle = angle;
		this.health = health;
		/* TODO add a bool for isFollowingWall, and an
		 * appropriate method for getting this info. */
	}

    /**
     * @return the currentView
     */
    public HashMap<Coordinate, MapTile> getCurrentView() {
        return currentView;
    }

    /**
     * @return the speed
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * @return the angle
     */
    public float getAngle() {
        return angle;
    }

    /**
     * @return the health
     */
    public int getHealth() {
        return health;
    }
}
