package pathfinders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

import mycontroller.Sensor;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class PathFinderExplore implements IPathFinder {

    private final ArrayList<MapTile.Type> tileTypesToAvoid;
    private final ArrayList<MapTile.Type> tileTypesToTarget;
    private ArrayList<HashMap<Coordinate, TrapTile>> trapSections;
    private Sensor sensor;

    private Coordinate start;

    public PathFinderExplore(Stack<IPathFinder> pathFinderStack, Sensor sensor) {
        tileTypesToAvoid = new ArrayList<MapTile.Type>();
        tileTypesToAvoid.add(MapTile.Type.WALL);
        tileTypesToAvoid.add(MapTile.Type.TRAP);
        tileTypesToTarget = new ArrayList<MapTile.Type>();
        tileTypesToTarget.add(MapTile.Type.ROAD);
        
        this.sensor = sensor;
        start = sensor.getPosition();
    }

    @Override
    public ArrayList<Coordinate> update() {
        System.out.println(sensor.getOrientation());
        if (!sensor.isBesideTileOfTypes(tileTypesToAvoid)) {
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
        target.add(sensor.getFurthestPointInDirection(WorldSpatial.Direction.NORTH));
        return target;
    }
    
    private ArrayList<Coordinate> followWallTrap() {
        // We're beside a wall/trap, follow it, avoiding walls and traps in front.
        ArrayList<Coordinate> target = new ArrayList<Coordinate>();
        if (!sensor.isFollowingWall()) {
            // We're beside a wall, but not aligned with it.
            // Say that we need to turn left.
            System.out.println("ehhh");
            Coordinate closestToSide = sensor.getClosestTileInDirectionOfTypes(WorldSpatial.RelativeDirection.LEFT, tileTypesToTarget);
            if (closestToSide == null) {
                // Turning left would crash us into a wall/trap, turn right instead.
                //closestToSide = sensor.getClosestTileInDirectionOfTypes(WorldSpatial.RelativeDirection.RIGHT, tileTypesToTarget);
            }
            System.out.println("???");
            target.add(closestToSide);
        } else {
            // We're aligned with the wall beside us, just go in the direction we're facing.
            Coordinate wallInFront = sensor.getClosestTileInDirectionOfTypes(WorldSpatial.RelativeDirection.LEFT, tileTypesToAvoid);
            if (wallInFront == null) {
                // There's no wall/trap in front in vision, just gun it forward.
                target.add(sensor.getFurthestPointInDirection(sensor.getOrientation()));
            } else {
                // For now just use the point beside the upcoming wall so we slow down.
                target.add(sensor.getNearestRoadNearCoordinate(wallInFront));
            }
        }
        return target;
        // TODO deal with avoiding obstacles.
    }

    @Override
    public boolean isDone() {
        return start == sensor.getPosition();
    }

}
