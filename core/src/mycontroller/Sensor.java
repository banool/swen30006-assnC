/**
 * Changes had to be made to the implementation of Sensor most notably the attributes and methods.
 * This was due to the realisation that what was originally planned in the design simply doesn't
 * assist to achieve what is needed with the Sensor class.
 */

package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;
import tiles.MapTile;
import world.WorldSpatial;
import utilities.Coordinate;

public class Sensor {

    public final Integer VISION_AHEAD = 3;

    private MyAIController controller;
    private Coordinate currentPosition;
    private float angle;
    private WorldSpatial.Direction orientation;
    private HashMap<Coordinate, MapTile> currentView;
    private double velocity;
    private Vector2 velocity2;

    public Sensor(MyAIController controller) {
        this.controller = controller;
    }

    public void update() {
        this.currentView = controller.getView();
        this.currentPosition = new Coordinate(controller.getPosition());
        this.angle = controller.getAngle();
        this.orientation = controller.getOrientation();
        this.velocity = (double) controller.getSpeed();
        this.velocity2 = controller.getVelocity();
    }
    
    /*************************************************************************
     * Methods for sensing the tiles around the car
     *************************************************************************/

    /**
     * Gets the tiles directly in front of (orientation) of the vehicle
     * @param orientation
     * @return
     */
    private HashMap<Coordinate, MapTile> getTilesInDirection(WorldSpatial.Direction orientation) {
        HashMap<Coordinate, MapTile> tiles = new HashMap<Coordinate, MapTile>();
        int[] mod = WorldSpatial.modMap.get(orientation);
        for (int i = 1; i <= VISION_AHEAD; i++) {
            Coordinate toCheck = new Coordinate(currentPosition.x + (i * mod[0]), currentPosition.y + (i * mod[1]));
            tiles.put(toCheck, currentView.get(toCheck));
        }
        return tiles;
    }
    
    /**
     * This method just reroutes RelativeDirection arguments through into the
     * regular Direction based method.
     * @param direction
     * @return
     */
    public HashMap<Coordinate,MapTile> getTilesInDirection(WorldSpatial.RelativeDirection direction) {
        return getTilesInDirection(WorldSpatial.getToSideOf(orientation, direction));
    }
    
    /**
     * Gets the closest tile of a specified list of types in the given direction.
     * @param direction
     * @param tileTypes
     * @return
     */
    public Coordinate getClosestTileInDirectionOfTypes(WorldSpatial.Direction orientation, ArrayList<MapTile.Type> tileTypes) {
        HashMap<Coordinate,MapTile> tilesInDirection = getTilesInDirection(orientation);
        // Loop through the tiles in the given direction and find the closest of the specified type.
        Coordinate nearest = null;
        for (Map.Entry<Coordinate, MapTile> entry : tilesInDirection.entrySet()) {
            Coordinate key = entry.getKey();
            MapTile value = entry.getValue();
            if (tileTypes.contains(value.getType())) {
                // Initialise the nearest value with the first in the hashmap.
                if (nearest == null) {
                    nearest = key;
                }
                if (key.distanceFrom(currentPosition) < nearest.distanceFrom(currentPosition)) {
                    nearest = key;
                }
            }
        }
        return nearest;
    }
    
    /**
     * This method just acts a RelativeDirection front end for the Direction based version.
     * It does this by converting the RelativeDirection into a Direction based on the current orientation.
     * @param direction
     * @param tileTypes
     * @return
     */
    public Coordinate getClosestTileInDirectionOfTypes(WorldSpatial.RelativeDirection direction, ArrayList<MapTile.Type> tileTypes) {
        return getClosestTileInDirectionOfTypes(WorldSpatial.getToSideOf(orientation, direction), tileTypes);      
    }


    /**
     * Checks if the wall is on the car's left hand side
     * NOTE TODO, this doesnt just consider the immediate left, but the left within vision.
     * If we wanted to just consider the immediate left, we could use isBesideTileOfTypes.
     * @return boolean true if the wall is on the car's LHS
     */
    public boolean isFollowingTileTypes(ArrayList<MapTile.Type> tileTypes) {
        return getClosestTileInDirectionOfTypes(WorldSpatial.getLeftOf(orientation), tileTypes) != null;
    }
    
    /**
     * Interface into isDirectlyBesideTileOfTypes that checks for all sides (forward, left, right, back).
     * @param tileTypes
     * @return
     */
    public boolean isDirectlyBesideTileOfTypes(ArrayList<MapTile.Type> tileTypes) {
        return isDirectlyBesideTileOfTypes(tileTypes, null);
    }
    
    public boolean isDirectlyBesideTileOfTypes(ArrayList<MapTile.Type> tileTypes, WorldSpatial.RelativeDirection direction) {
        int[] modMap = WorldSpatial.modMap.get(WorldSpatial.getToSideOf(orientation, direction));
        if (direction == null || modMap[0] == 1) {
            if (tileTypes.contains(currentView.get(new Coordinate(currentPosition.x+1, currentPosition.y)).getType())) {
                return true;
            }
        }
        if (direction == null || modMap[0] == -1) {
            if (tileTypes.contains(currentView.get(new Coordinate(currentPosition.x-1, currentPosition.y)).getType())) {
                return true;
            }
        }
        if (direction == null || modMap[1] == 1) {
            if (tileTypes.contains(currentView.get(new Coordinate(currentPosition.x, currentPosition.y+1)).getType())) {
                return true;
            }
        }
        if (direction == null || modMap[1] == -1) {
            if (tileTypes.contains(currentView.get(new Coordinate(currentPosition.x, currentPosition.y-1)).getType())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * TODO comment. This returns the nearest tile that is one of the given types.
     * @param tileTypes
     * @return
     */
    public Coordinate getNearestTileOfTypes(ArrayList<MapTile.Type> tileTypes) {
        Coordinate nearest = null;
        for (Map.Entry<Coordinate, MapTile> entry : currentView.entrySet()) {
            Coordinate key = entry.getKey();
            MapTile value = entry.getValue();
            if (tileTypes.contains(value.getType())) {
                // Initialise the nearest value with the first in the hashmap.
                if (nearest == null) {
                    nearest = key;
                }
                if (key.distanceFrom(currentPosition) < nearest.distanceFrom(currentPosition)) {
                    nearest = key;
                }
            }
        }
        return nearest;
    }
    
    /**
     * This returns the road tile closest to the given coordinate.
     * @param tileTypes
     * @return
     */
    public Coordinate getNearestTileTypesNearCoordinate(Coordinate coordinate, ArrayList<MapTile.Type> tileTypes) {
        Coordinate nearest = null;
        for (Map.Entry<Coordinate, MapTile> entry : currentView.entrySet()) {
            Coordinate key = entry.getKey();
            MapTile value = entry.getValue();
            if (tileTypes.contains(value.getType())) {
                // Initialise the nearest value with the first in the hashmap.
                if (nearest == null) {
                    nearest = key;
                }
                if (key.distanceFrom(coordinate) < nearest.distanceFrom(coordinate)) {
                    nearest = key;
                }
            }
        }
        return nearest;
    }
    
    public Coordinate getFurthestPointInDirection(WorldSpatial.Direction direction) {
        int newX = currentPosition.x + WorldSpatial.modMap.get(direction)[0] * VISION_AHEAD;
        int newY = currentPosition.y + WorldSpatial.modMap.get(direction)[1] * VISION_AHEAD;
        return new Coordinate(newX, newY);
    }

    /*************************************************************************
     * Helper methods
     *************************************************************************/

    public Coordinate getPosition() {
        return currentPosition;
    }

    public float getAngle() {
        return this.angle;
    }

    public WorldSpatial.Direction getOrientation() {
        return orientation;
    }

    public HashMap<Coordinate, MapTile> getCurrentView() {
        return currentView;
    }
    
    public int getVisionAhead() {
        return VISION_AHEAD;
    }

    public int getX() {
        return currentPosition.x;
    }

    public int getY() {
        return currentPosition.y;
    }

    public double getVelocity() {
        return this.velocity;
    }

    public Vector2 getVelocity2() {
        return this.velocity2;
    }

}
