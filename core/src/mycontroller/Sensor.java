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
import utilities.DirectionUtils;

/**
 * This class is soley responsible for handling how the car and AI reads its
 * surroundings and its current status (direction, velocity, health).
 * 
 * @author Hao Le, Daniel Porteous, David Stern
 * 2017-10-22.
 * Group 17.
 */
public class Sensor {

    public final Integer VISION_AHEAD = 3;

    private MyAIController controller;
    private Coordinate currentPosition;
    private float angle;
    private WorldSpatial.Direction orientation;
    private HashMap<Coordinate, MapTile> currentView;
    private double velocity;
    private Vector2 velocity2;
    private int health;

    public Sensor(MyAIController controller) {
        this.controller = controller;
        this.update();
    }

    public void update() {
        this.currentView = controller.getView();
        this.currentPosition = new Coordinate(controller.getPosition());
        this.angle = controller.getAngle();
        this.orientation = controller.getOrientation();
        this.velocity = (double) controller.getSpeed();
        this.velocity2 = controller.getVelocity();
        this.health = controller.getHealth();
    }
    
    /*************************************************************************
     * Methods for sensing the tiles around the car
     *************************************************************************/

    /**
     * Gets the tiles directly in front of (orientation) of the vehicle
     *
     * @param orientation is the direction the car is facing
     * @return internal representation of the map/tiles (through hashmap of coordinates and tiles)
     */
    private HashMap<Coordinate, MapTile> getTilesInDirection(WorldSpatial.Direction orientation) {
        HashMap<Coordinate, MapTile> tiles = new HashMap<Coordinate, MapTile>();
        int[] mod = DirectionUtils.MOD_MAP.get(orientation);
        for (int i = 1; i <= VISION_AHEAD; i++) {
            Coordinate toCheck = new Coordinate(currentPosition.x + (i * mod[0]), currentPosition.y + (i * mod[1]));
            tiles.put(toCheck, currentView.get(toCheck));
        }
        return tiles;
    }

    /**
     * This method just reroutes RelativeDirection arguments through into the
     * regular Direction based method.
     *
     * @param direction is the relative direction of the car
     * @return internal representation of the map/tiles (through hashmap of coordinates and tiles)
     */
    public HashMap<Coordinate,MapTile> getTilesInDirection(WorldSpatial.RelativeDirection direction) {
        return getTilesInDirection(DirectionUtils.getToSideOf(orientation, direction));
    }

    /**
     * Gets the closest tile of a specified list of types in the given direction.
     *
     * @param orientation the direction that the car is facing
     * @param tileTypes the collection of tiles
     * @return coordinate of the closest tile
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
                if (DirectionUtils.distanceBetweenCoords(key, currentPosition) < DirectionUtils.distanceBetweenCoords(nearest, currentPosition)) {
                    nearest = key;
                }
            }
        }
        return nearest;
    }

    /**
     * This method just acts a RelativeDirection front end for the Direction based version.
     * It does this by converting the RelativeDirection into a Direction based on the current orientation.
     *
     * @param direction is the direction the car is facing
     * @param tileTypes the collection of tiles
     * @return coordinate of the closest tile
     */
    public Coordinate getClosestTileInDirectionOfTypes(WorldSpatial.RelativeDirection direction, ArrayList<MapTile.Type> tileTypes) {
        return getClosestTileInDirectionOfTypes(DirectionUtils.getToSideOf(orientation, direction), tileTypes);      
    }

    /**
     * Checks if the wall is on the car's left hand side.
     * This doesnt just consider the immediate left, but the left within vision.
     * If we wanted to just consider the immediate left, we could use isBesideTileOfTypes.
     *
     * @return boolean true if the wall is on the car's LHS
     */
    public boolean isFollowingTileTypes(ArrayList<MapTile.Type> tileTypes) {
        return getClosestTileInDirectionOfTypes(DirectionUtils.getLeftOf(orientation), tileTypes) != null;
    }
    
    /**
     * Interface into isDirectlyBesideTileOfTypes that checks for all sides (forward, left, right, back).
     *
     * @param tileTypes the collection of tiles
     * @return boolean is true if the car is directly beside a tile in any direction
     */
    public boolean isDirectlyBesideTileOfTypes(ArrayList<MapTile.Type> tileTypes) {
        return isDirectlyBesideTileOfTypes(tileTypes, null);
    }
    
    /**
     * Check if we're directly (within one tile) beside any of the given tile types.
     * We can use a relative direction for this, where it'll just check to that side.
     *
     * @param tileTypes the collection of tiles
     * @param direction the direction we are checking for (null if we want to check all directions)
     * @return boolean is true if the car is directly beside a tile in any direction
     */
    public boolean isDirectlyBesideTileOfTypes(ArrayList<MapTile.Type> tileTypes, WorldSpatial.RelativeDirection direction) {
        int[] modMap = DirectionUtils.MOD_MAP.get(DirectionUtils.getToSideOf(orientation, direction));
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
     * This returns the nearest tile that is one of the given types.
     *
     * @param tileTypes the collection of tiles
     * @return coordinate of the nearest tile of the given type
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
                if (DirectionUtils.distanceBetweenCoords(key, currentPosition) < DirectionUtils.distanceBetweenCoords(nearest, currentPosition)) {
                    nearest = key;
                }
            }
        }
        return nearest;
    }
    
    /**
     * This returns the road tile closest to the given coordinate.
     *
     * @param tileTypes the collection of tiles
     * @return coordinate of the nearest road tile
     */
    public Coordinate getNearestTileOfTypesNearCoordinate(Coordinate coordinate, ArrayList<MapTile.Type> tileTypes) {
        Coordinate nearest = null;
        for (Map.Entry<Coordinate, MapTile> entry : currentView.entrySet()) {
            Coordinate key = entry.getKey();
            MapTile value = entry.getValue();
            if (tileTypes.contains(value.getType())) {
                // Initialise the nearest value with the first in the hashmap.
                if (nearest == null) {
                    nearest = key;
                }
                if (DirectionUtils.distanceBetweenCoords(key, coordinate) < DirectionUtils.distanceBetweenCoords(nearest, coordinate)) {
                    nearest = key;
                }
            }
        }
        return nearest;
    }
    
    /**
     * This returns the tile of the furthest point in a specified direction
     *
     * @param direction specified direction supplied as input
     * @return coordinate of the furthest tile
     */
    public Coordinate getFurthestPointInDirection(WorldSpatial.Direction direction) {
        int newX = currentPosition.x + DirectionUtils.MOD_MAP.get(direction)[0] * VISION_AHEAD;
        int newY = currentPosition.y + DirectionUtils.MOD_MAP.get(direction)[1] * VISION_AHEAD;
        return new Coordinate(newX, newY);
    }

    /*************************************************************************
     * Helper methods
     *************************************************************************/

    /**
     * Gets the current position of the car
     * 
     * @return coordinates of the car's current position
     */
    public Coordinate getPosition() {
        return currentPosition;
    }

    /**
     * Gets the current angle that the car is currently facing
     * 
     * @return angle as a float
     */
    public float getAngle() {
        return this.angle;
    }

    /**
     * Gets the orientation of the car
     * 
     * @return direction relative to the world
     */
    public WorldSpatial.Direction getOrientation() {
        return orientation;
    }

    /**
     * Gets the current view surrounding the car
     * 
     * @return hashmap of the tiles around the car in visible range
     */
    public HashMap<Coordinate, MapTile> getCurrentView() {
        return currentView;
    }
    
    /**
     * Returns the value of how far ahead the car can see
     * 
     * @return integer of how many tiles ahead the car can see
     */
    public int getVisionAhead() {
        return VISION_AHEAD;
    }

    /**
     * Returns the x coordinate of the current position of the car
     * 
     * @return x coordinate of the current position as an integer value
     */
    public int getX() {
        return currentPosition.x;
    }

    /**
     * Returns the y coordinate of the current position of the car
     * 
     * @return y coordinate of the current position as an integer value
     */
    public int getY() {
        return currentPosition.y;
    }

    /**
     * Returns the current velocity of the car
     * 
     * @return current velocity of the car as a double value
     */
    public double getVelocity() {
        return this.velocity;
    }

    /**
     * Returns the current velocity of the car
     * 
     * @return current velocity of the car as a Vector2 value
     */
    public Vector2 getVelocity2() {
        return this.velocity2;
    }

    /**
     * Returns the current health of the car
     * 
     * @return current health of the car as an integer value
     */
    public int getHealth() {
        return health;
    }

}
