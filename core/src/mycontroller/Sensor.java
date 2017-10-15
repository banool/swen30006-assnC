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

    private static Map<WorldSpatial.Direction,int[]> modmap = new HashMap<WorldSpatial.Direction,int[]>() {{
        put(WorldSpatial.Direction.EAST, new int[] {1, 0});
        put(WorldSpatial.Direction.NORTH, new int[] {0, 1});
        put(WorldSpatial.Direction.WEST, new int[] {-1, 0});
        put(WorldSpatial.Direction.SOUTH, new int[] {0, -1});
    }};

    private static Map<WorldSpatial.Direction,WorldSpatial.Direction> isLeftOf
            = new HashMap<WorldSpatial.Direction,WorldSpatial.Direction>() {{
        put(WorldSpatial.Direction.EAST, WorldSpatial.Direction.NORTH);
        put(WorldSpatial.Direction.NORTH, WorldSpatial.Direction.WEST);
        put(WorldSpatial.Direction.WEST, WorldSpatial.Direction.SOUTH);
        put(WorldSpatial.Direction.SOUTH, WorldSpatial.Direction.EAST);
    }};


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
    public boolean isTileAhead(WorldSpatial.Direction orientation, MapTile.Type tileType) {
        LinkedList <MapTile> tilesAhead = getTilesInDirection(orientation);
        while (!tilesAhead.isEmpty()) {
            if (tilesAhead.poll().isType(tileType)) {
                return true;
            }
        }
        return false;
    }


    private LinkedList<MapTile> getTilesInDirection(WorldSpatial.Direction orientation) {
        LinkedList<MapTile> tiles = new LinkedList<MapTile>();
        int[] mod = modmap.get(orientation);
        for (int i = 0; i <= VISION_AHEAD; i++) {
            tiles.add(currentView.get(new Coordinate(currentPosition.x + (i * mod[0]),
                    currentPosition.y + (i * mod[1]))));
        }
        return tiles;
    }


    /**
     * Checks if the wall is on the car's left hand side
     * @return boolean true if the wall is on the car's LHS
     */
    public boolean isFollowingWall() {
        return isTileAhead(isLeftOf.get(orientation), MapTile.Type.WALL);
    }


    public boolean isWallAhead() {
        return isTileAhead(orientation, MapTile.Type.WALL);
    }
    public boolean isTrapAhead() {
        return isTileAhead(orientation, MapTile.Type.TRAP);
    }


    public Coordinate getPosition() {
        // METHOD STUB
        return new Coordinate(0, 0);
    }

    public float getAngle() {
        // METHOD STUB
        return (float) 0.0;
    }

    public WorldSpatial.Direction getOrientation() {
        // METHOD STUB
        return WorldSpatial.Direction.EAST;
    }

    public HashMap<Coordinate, MapTile> getCurrentView() {
        // METHOD STUB
        return new HashMap<Coordinate, MapTile>();
    }



}
