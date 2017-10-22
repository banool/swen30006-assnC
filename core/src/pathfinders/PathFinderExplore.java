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
    private final ArrayList<MapTile.Type> tileTypesToTarget;
    // A single trap section is a HashMap<Coordinate, TrapTile>. 
    // We have multiple of these for each contiguous section of traps.
    private ArrayList<HashMap<Coordinate, TrapTile>> trapSections;
    // This is for keeping track of the current continguous section of traps.
    private HashMap<Coordinate, TrapTile> currentTrapSection;
    
    private Stack<IPathFinder> pathFinderStack;
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
        this.pathFinderStack = pathFinderStack;
    }

    @Override
    public ArrayList<Coordinate> update() {
        trackTraps();
        if (!sensor.isDirectlyBesideTileOfTypes(tileTypesToAvoid)) {
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
        targetCoordinate = sensor.getNearestTileTypesNearCoordinate(targetCoordinate, tileTypesToAvoid);
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
        if (!sensor.isFollowingTileTypes(tileTypesToAvoid)) {
            // We're beside a wall, but not aligned with it.
            // Say that we need to turn left.
            System.out.println("ehhh");
            Coordinate closestToSide = sensor.getClosestTileInDirectionOfTypes(WorldSpatial.RelativeDirection.LEFT,
                    tileTypesToTarget);
            if (closestToSide == null) {
                // Turning left would crash us into a wall/trap, turn right instead.
                closestToSide = sensor.getClosestTileInDirectionOfTypes(WorldSpatial.RelativeDirection.RIGHT,
                        tileTypesToTarget);
                // TODO If we're in a corridor, this might just crash into the right wall.
            }
            System.out.println("???");
            target.add(closestToSide);
        } else {
            // We're aligned with the wall/trap beside us, just go in the direction we're
            // facing.
            Coordinate wallInFront = sensor.getClosestTileInDirectionOfTypes(sensor.getOrientation(), tileTypesToAvoid);
            if (wallInFront == null) {
                // There's no wall/trap in front in vision, just gun it forward.
                target.add(sensor.getFurthestPointInDirection(sensor.getOrientation()));
            } else {
                // For now just use the point beside the upcoming wall so we slow down.
                Coordinate roadNearWall = sensor.getNearestTileTypesNearCoordinate(wallInFront, tileTypesToTarget);
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
                target.add(rightOfRoadNearWall);
            }
        }
        return target;
    }
    
    public void trackTraps() {
        // TODO Add traps to the left to currentTrapSection if they're not broken up by a wall.
        // If we see a wall, we need to "commit" the currentTrapSection to trapSections.
    }

    /**
     * This method allows us to do something before we're done.
     * In this case, we push an Escape pathfinder onto the stack for each trap section in trapSections.
     * Each escape pathfinder takes the appropriate trap section as a constructor argument.
     */
    public boolean isDone() {
        boolean done = (start == sensor.getPosition());
        if (done) {
            for (HashMap<Coordinate, TrapTile> trapSection : trapSections) {
                pathFinderStack.push(new PathFinderEscape(pathFinderStack, sensor, trapSection));
            }
        }
        return done;
    }

}
