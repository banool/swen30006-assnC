package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import world.WorldSpatial;
import utilities.Coordinate;

/**
 * Responsibilities of this class include:
 * - Reporting what surrounds the car.
 * - Telling the controller when we encounter a trap.
 *
 * @author daniel
 */
public class MyAISensor {

    private MyAIController controller;

    public MyAISensor(MyAIController controller) {
        this.controller = controller;
    }

    public SensorData update() {
        HashMap<Coordinate, MapTile> currentView = controller.getView();
        float speed = controller.getSpeed();
        float angle = controller.getAngle();
        int health = controller.getHealth();
        return new SensorData(currentView, speed, angle, health);
    }

    /**
     * @return If there is a wall in front, how far away it is. Otherwise -1.
     */
    public float wallInFront() {
        // TODO
        return -1.0f;
    }

    public float directionOfNearestWall() {
        // TODO
        return -1.0f;
    }

    /**
     * With the default settings, this will return 4 MapTiles.
     * You can use this to check the tiles to the left or right of the car.
     */
    public ArrayList<MapTile> tilesOnSide(WorldSpatial.RelativeDirection side) {
        // TODO
        ArrayList<MapTile> out = new ArrayList<MapTile>();
        return out;
    }

    /**
     * @param distance
     * @return Return true if the car is within "distance" of any wall.
     * "distance" is probably just the max vision range of the car (4 by default).
     */
    public boolean nearWall(float distance) {
        // TODO
        return true;
    }

    /**
     * This might not be necessary, but I'll leave it here.
     *
     * @param side
     * @return Return true if the car is within "distance" of the wall on "side".
     */
    public boolean huggingWall(WorldSpatial.RelativeDirection side, float distance) {
        // TODO
        return true;
    }

    /**
     * @param side
     * @return Return true if the car is aligned with the wall on "side".
     */
    public boolean alignedWithWall(WorldSpatial.RelativeDirection side) {
        // TODO
        return true;
    }

    /**
     * @return Return the direction of the nearest wall, or -1 if there isn't one nearby.
     * Do this by checking currentView.
     */
    public float getDirectionOfNearestWall() {
        // TODO
        return -1.0f;
    }

}
