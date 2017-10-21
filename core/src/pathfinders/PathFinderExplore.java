package pathfinders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import mycontroller.Sensor;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class PathFinderExplore implements IPathFinder {

    private final ArrayList<MapTile.Type> tileTypesToAvoid;
    private ArrayList<HashMap<Coordinate, TrapTile>> trapSections;
    private Sensor sensor;

    private Coordinate start;

    public PathFinderExplore(Stack<IPathFinder> pathFinderStack, Sensor sensor) {
        tileTypesToAvoid = new ArrayList<MapTile.Type>();
        tileTypesToAvoid.add(MapTile.Type.WALL);
        tileTypesToAvoid.add(MapTile.Type.TRAP);
        
        this.sensor = sensor;
        start = sensor.getPosition();
    }

    @Override
    public ArrayList<Coordinate> update() {
        System.out.println(sensor.getOrientation());
        if (!sensor.isFollowingWall()) {
            System.out.println("Getting adjacent to wall/trap");
            return getToWallTrap();
        } else {
            // We're aligned with a wall, follow it until we come across an obstacle.
            return followWallTrap();
        }
        // Comment about how we just generate one point and chuck it in the array. TODO
    }

    private ArrayList<Coordinate> getToWallTrap() {
        ArrayList<Coordinate> target = new ArrayList<Coordinate>();
        Coordinate targetCoordinate = sensor.getNearestTileOfTypes(tileTypesToAvoid);
        targetCoordinate = sensor.getNearestRoadNearCoordinate(targetCoordinate);
        if (targetCoordinate != null) {
            target.add(targetCoordinate);
            return target;
        }
        // If we get here it means that there is no wall within range.
        // Just return the furthest point to the north within vision.
        target.add(sensor.getFurtherPointInDirection(WorldSpatial.Direction.NORTH));
        return target;
    }
    
    private ArrayList<Coordinate> followWallTrap() {
        // We're aligned with a wall/trap, follow it, avoiding walls and traps in front.
        ArrayList<Coordinate> target = new ArrayList<Coordinate>();
        target.add(sensor.getFurtherPointInDirection(sensor.getOrientation()));
        return target;
        // TODO deal with avoiding obstacles.
    }

    @Override
    public boolean isDone() {
        return start == sensor.getPosition();
    }

}
