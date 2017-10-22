package pathfinders;

import utilities.Coordinate;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import mycontroller.Sensor;
import tiles.MapTile;
import tiles.TrapTile;

public class PathFinderEscape implements IPathFinder {
    
    private final ArrayList<MapTile.Type> tileTypesToAvoid;
    private final ArrayList<MapTile.Type> tileTypesToTarget;
    
    private Sensor sensor;
    private Coordinate start;
    private Stack<IPathFinder> pathFinderStack;
    HashMap<Coordinate, TrapTile> trapSection;

    public PathFinderEscape(Stack<IPathFinder> pathFinderStack, Sensor sensor, HashMap<Coordinate, TrapTile> trapSection) {
        tileTypesToAvoid = new ArrayList<MapTile.Type>();
        tileTypesToAvoid.add(MapTile.Type.WALL);
        tileTypesToAvoid.add(MapTile.Type.TRAP);
        tileTypesToTarget = new ArrayList<MapTile.Type>();
        tileTypesToTarget.add(MapTile.Type.ROAD);
        
        this.sensor = sensor;
        start = sensor.getPosition();
        this.pathFinderStack = pathFinderStack;
        this.trapSection = trapSection;
    }

    @Override
    public ArrayList<Coordinate> update() {
        if (!sensor.isDirectlyBesideTileOfTypes(tileTypesToAvoid, WorldSpatial.RelativeDirection.LEFT)) {
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
        Coordinate wallCoordinate = sensor.getNearestTileOfTypes(tileTypesToAvoid);
        Coordinate targetCoordinate = sensor.getNearestTileOfTypesNearCoordinate(wallCoordinate, tileTypesToTarget);
        if (targetCoordinate != null) {
            target.add(targetCoordinate);
        } else {
            // If we get here it means that there is no wall within range.
            // Just return the furthest point to the north within vision.
            target.add(sensor.getFurthestPointInDirection(WorldSpatial.Direction.NORTH));
        }
        return target;
    }
    
    private ArrayList<Coordinate> followWallTrap() {
        // We're beside a wall/trap, follow it, avoiding walls and traps in front.
        ArrayList<Coordinate> target = new ArrayList<Coordinate>();
        if (!sensor.isFollowingTileTypes(tileTypesToAvoid)) {
            // We're beside a wall, but not aligned with it.
            // Say that we need to turn left.
            Coordinate closestToSide = sensor.getClosestTileInDirectionOfTypes(WorldSpatial.RelativeDirection.LEFT,
                    tileTypesToTarget);
            if (closestToSide == null) {
                // Turning left would crash us into a wall/trap, turn right instead.
                closestToSide = sensor.getClosestTileInDirectionOfTypes(WorldSpatial.RelativeDirection.RIGHT,
                        tileTypesToTarget);
                // TODO If we're in a corridor, this might just crash into the right wall.
            }
            target.add(closestToSide);
        } else {
            // We're aligned with the wall/trap beside us, just go in the direction we're
            // facing.
            Coordinate wallInFront = sensor.getClosestTileInDirectionOfTypes(sensor.getOrientation(), tileTypesToAvoid);
            if (wallInFront == null) {
                // There's no wall/trap in front in vision, just gun it forward.
                target.add(sensor.getFurthestPointInDirection(sensor.getOrientation()));
            } else {
                target.add(getToRightOfUpcomingWall(wallInFront));
            }
        }
        return target;
    }
    
    public Coordinate getToRightOfUpcomingWall(Coordinate wallInFront) {
        // For now just use the point beside the upcoming wall so we slow down.
        Coordinate roadNearWall = sensor.getNearestTileOfTypesNearCoordinate(wallInFront, tileTypesToTarget);
        // Using this point, check to the right of it for free road tiles to go to.
        Coordinate rightOfRoadNearWall = roadNearWall;
        for (int i = 1; i <= sensor.getVisionAhead(); i++) {
            int[] rightModMap = WorldSpatial.modMap.get(WorldSpatial.getRightOf(sensor.getOrientation()));
            Coordinate candidate = new Coordinate(sensor.getPosition().x + (i * rightModMap[0]),
                    sensor.getPosition().y + (i * rightModMap[1]));
            // If the coordinate to the right of the point near the wall is a road, set the
            // destination to that point.
            if (tileTypesToTarget.contains(sensor.getCurrentView().get(candidate).getType())) {
                rightOfRoadNearWall = candidate;
            }
        }
        return rightOfRoadNearWall;
    }

    /**
     * We've reached the trap section we were going to. 
     * Now we push on an appropriate TrapTraverse PathFinder.
     */
    public boolean isDone() {
        boolean done = (start == sensor.getPosition());
        if (done) {
            pathFinderStack.push(TrapTraverse.getTrapTraverse(pathFinderStack, sensor, trapSection));
        }
        return done;
    }
}
