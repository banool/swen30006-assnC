package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import tiles.MapTile;
import tiles.TrapTile;
import world.WorldSpatial;
import utilities.Coordinate;

public class Sensor {


    private final Integer VISION_AHEAD = 4;
    private MyAIController controller;
    private Coordinate currentPosition;
    private float angle;
    private WorldSpatial.Direction orientation;
    private HashMap<Coordinate, MapTile> currentView;


    public Sensor(MyAIController controller) {

        this.controller = controller;

    }

    public void update() {
        this.currentView = controller.getView();
        this.currentPosition = new Coordinate(controller.getPosition());
        this.angle = controller.getAngle();
        this.orientation = controller.getOrientation();
    }


    /**
     * Returns whether a tile of a provided class is in the provided direction.
     * Call using, for example, isTileAhead(WorldSpatial.Direction.NORTH, MapTile.class)
     * Or, a version of checkWallAhead -> isTileAhead(orientation, MapTile.Type.WALL);
     * @param orientation
     * @param tileType
     * @return ...
     */
    // TODO, I think this should just return the HashMap<Coordinate, MapTile> that we're used to.
    public boolean isTileAhead(WorldSpatial.Direction orientation, MapTile.Type tileType) {
        LinkedList <MapTile> tilesAhead = getTilesInDirection(orientation);
        while (!tilesAhead.isEmpty()) {
            if (tilesAhead.poll().isType(tileType)) {
                return true;
            }
        }
        return false;
    }


    // TODO, I think this should just return the HashMap<Coordinate, MapTile> that we're used to.
    private LinkedList<MapTile> getTilesInDirection(WorldSpatial.Direction orientation) {
        LinkedList<MapTile> tiles = new LinkedList<MapTile>();
        int[] mod = WorldSpatial.modMap.get(orientation);
        for (int i = 0; i <= VISION_AHEAD; i++) {
            tiles.add(currentView.get(new Coordinate(currentPosition.x + (i * mod[0]),
                    currentPosition.y + (i * mod[1]))));
        }
        return tiles;
    }
    
    public HashMap<Coordinate,MapTile> getTilesInDirection(WorldSpatial.RelativeDirection direction) {
        HashMap<Coordinate, MapTile> tiles = new HashMap<Coordinate, MapTile>();
        // Get the direction to the left.
        int[] mod  = WorldSpatial.modMap.get(WorldSpatial.leftOf.get(orientation));
        if (direction == WorldSpatial.RelativeDirection.RIGHT) {
            // If we needed the right, just go counterclockwise twice more.
            mod = WorldSpatial.modMap.get(WorldSpatial.leftOf.get(orientation));
            mod = WorldSpatial.modMap.get(WorldSpatial.leftOf.get(orientation));
        }
        for (int i = 1; i <= VISION_AHEAD; i++) {
            Coordinate toCheck = new Coordinate(currentPosition.x + (i * mod[0]), currentPosition.y + (i * mod[1]));
            tiles.put(toCheck, currentView.get(toCheck));
        }
        return tiles;
    }
    
    public Coordinate getClosestTileInDirectionOfType(WorldSpatial.RelativeDirection direction, ArrayList<MapTile.Type> tileTypes) {
        HashMap<Coordinate,MapTile> tilesInDirection = getTilesInDirection(direction);
        Coordinate nearest = null;
        for (Map.Entry<Coordinate, MapTile> entry : tilesInDirection.entrySet()) {
            Coordinate key = entry.getKey();
            MapTile value = entry.getValue();
            if (tileTypes.contains(value.getType())) {
                // Initialise the nearest value with the first in the hashmap.
                if (nearest == null) {
                    nearest = key;
                }
                if (key.distance(currentPosition) < nearest.distance(currentPosition)) {
                    nearest = key;
                }
            }
        }
        return nearest;        
    }


    /**
     * Checks if the wall is on the car's left hand side
     * @return boolean true if the wall is on the car's LHS
     */
    public boolean isFollowingWall() {
        return isTileAhead(WorldSpatial.leftOf.get(orientation), MapTile.Type.WALL);
    }
    
    public boolean isBesideTileOfTypes(ArrayList<MapTile.Type> tileTypes) {
        if (tileTypes.contains(currentView.get(new Coordinate(currentPosition.x+1, currentPosition.y)).getType())) {
            return true;
        }
        if (tileTypes.contains(currentView.get(new Coordinate(currentPosition.x-1, currentPosition.y)).getType())) {
            return true;
        }
        if (tileTypes.contains(currentView.get(new Coordinate(currentPosition.x, currentPosition.y+1)).getType())) {
            return true;
        }
        if (tileTypes.contains(currentView.get(new Coordinate(currentPosition.x, currentPosition.y-1)).getType())) {
            return true;
        }
        return false;
    }


    public boolean isWallAhead() {
        return isTileAhead(orientation, MapTile.Type.WALL);
    }
    public boolean isTrapAhead() {
        return isTileAhead(orientation, MapTile.Type.TRAP);
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
                if (key.distance(currentPosition) < nearest.distance(currentPosition)) {
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
    public Coordinate getNearestRoadNearCoordinate(Coordinate coordinate) {
        Coordinate nearest = null;
        for (Map.Entry<Coordinate, MapTile> entry : currentView.entrySet()) {
            Coordinate key = entry.getKey();
            MapTile value = entry.getValue();
            if (value.isType(MapTile.Type.ROAD)) {
                // Initialise the nearest value with the first in the hashmap.
                if (nearest == null) {
                    nearest = key;
                }
                if (key.distance(coordinate) < nearest.distance(coordinate)) {
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


    public Coordinate getPosition() {
        // METHOD STUB
        return new Coordinate(controller.getPosition());
    }

    public float getAngle() {
        // METHOD STUB
        return controller.getAngle();
    }

    public WorldSpatial.Direction getOrientation() {
        // METHOD STUB
        return controller.getOrientation();
    }

    public HashMap<Coordinate, MapTile> getCurrentView() {
        // METHOD STUB
        return new HashMap<Coordinate, MapTile>();
    }



}
