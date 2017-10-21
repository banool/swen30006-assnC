package world;

import java.util.HashMap;
import java.util.Map;

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
    
    public static Map<WorldSpatial.Direction,int[]> modMap = new HashMap<WorldSpatial.Direction,int[]>() {{
        put(WorldSpatial.Direction.EAST, new int[] {1, 0});
        put(WorldSpatial.Direction.NORTH, new int[] {0, 1});
        put(WorldSpatial.Direction.WEST, new int[] {-1, 0});
        put(WorldSpatial.Direction.SOUTH, new int[] {0, -1});
    }};

    public static Map<WorldSpatial.Direction,WorldSpatial.Direction> leftOf
            = new HashMap<WorldSpatial.Direction,WorldSpatial.Direction>() {{
        put(WorldSpatial.Direction.EAST, WorldSpatial.Direction.NORTH);
        put(WorldSpatial.Direction.NORTH, WorldSpatial.Direction.WEST);
        put(WorldSpatial.Direction.WEST, WorldSpatial.Direction.SOUTH);
        put(WorldSpatial.Direction.SOUTH, WorldSpatial.Direction.EAST);
    }};
    
    
}
