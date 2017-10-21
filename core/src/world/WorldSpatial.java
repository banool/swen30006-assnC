package world;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Defines the degrees that you can refer to if you want to create your
 * own code for turning the car.
 */
public class WorldSpatial {

    public enum Direction {EAST, WEST, SOUTH, NORTH}

    /* TODO do we want forward and backwards too? */
    public static enum RelativeDirection {
        LEFT, RIGHT
    };

    public final static int EAST_DEGREE_MIN = 0;
    public final static int EAST_DEGREE_MAX = 360;
    public final static int NORTH_DEGREE = 90;
    public final static int WEST_DEGREE = 180;
    public final static int SOUTH_DEGREE = 270;
    
    // TODO rename this to MOD_MAP
    public static final Map<WorldSpatial.Direction,int[]> modMap = new HashMap<WorldSpatial.Direction,int[]>() {{
        put(WorldSpatial.Direction.EAST, new int[] {1, 0});
        put(WorldSpatial.Direction.NORTH, new int[] {0, 1});
        put(WorldSpatial.Direction.WEST, new int[] {-1, 0});
        put(WorldSpatial.Direction.SOUTH, new int[] {0, -1});
    }};

    // TODO rename this to LEFT_OF. should this be private and we use a method? probs not
    public static final Map<WorldSpatial.Direction,WorldSpatial.Direction> leftOf
            = new HashMap<WorldSpatial.Direction,WorldSpatial.Direction>() {{
        put(WorldSpatial.Direction.EAST, WorldSpatial.Direction.NORTH);
        put(WorldSpatial.Direction.NORTH, WorldSpatial.Direction.WEST);
        put(WorldSpatial.Direction.WEST, WorldSpatial.Direction.SOUTH);
        put(WorldSpatial.Direction.SOUTH, WorldSpatial.Direction.EAST);
    }};
    
    /**
     * TODO comment. Method for getting the counterclockwise orientation from the given orientation.
     * @return
     */
    public static Direction getLeftOf(Direction orientation) {
        return leftOf.get(orientation);
    }
    
    /**
     * TODO comment. Method for getting the clockwise orientation from the given orientation.
     * We iterate through the leftOf map and get the key corresponding to the given value.
     * @return
     */
    public static Direction getRightOf(Direction orientation) {
        for (Entry<Direction, Direction> entry : leftOf.entrySet()) {
            if (orientation.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        System.out.println("never");
        return null;
    }
    
    /**
     * Given an orientation and a relative direction, return the direction either clockwise
     * or counterclockwise of that given direction, based on the relative direction.
     * @param orientation
     * @param direction
     * @return
     */
    public static Direction getToSideOf(Direction orientation, RelativeDirection direction) {
        if (direction == WorldSpatial.RelativeDirection.LEFT) {
            return getLeftOf(orientation);
        } else {
            return getRightOf(orientation);
        }
    }
    
    
}
