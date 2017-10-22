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
    private boolean hasLeftStart;

    public PathFinderExplore(Stack<IPathFinder> pathFinderStack, Sensor sensor) {
        tileTypesToAvoid = new ArrayList<MapTile.Type>();
        tileTypesToAvoid.add(MapTile.Type.WALL);
        tileTypesToAvoid.add(MapTile.Type.TRAP);
        tileTypesToTarget = new ArrayList<MapTile.Type>();
        tileTypesToTarget.add(MapTile.Type.ROAD);
        
        this.sensor = sensor;
        this.start = sensor.getPosition();
        this.pathFinderStack = pathFinderStack;
        this.hasLeftStart = false;
        
        this.trapSections = new ArrayList<HashMap<Coordinate, TrapTile>>();
        this.currentTrapSection = new HashMap<Coordinate, TrapTile>();
    }

    @Override
    public ArrayList<Coordinate> update() {
        trackTraps();
        // TODO comment on why we need this "hasLeftStart" thing.
        if (!hasLeftStart && !start.equals(sensor.getPosition())) {
            hasLeftStart = true;
        }
        if (!sensor.isDirectlyBesideTileOfTypes(tileTypesToAvoid, WorldSpatial.RelativeDirection.LEFT)) {
            System.out.println("Getting adjacent to wall/trap");
            return getToWallTrap();
        } else {
            // We're aligned with a wall, follow it until we come across an obstacle.
            return followWallTrap();
        }
        // Comment about how we just generate one point and chuck it in the array. TODO
    }
    
    private ArrayList<Coordinate> getToUninterruptibleTarget() {
        ArrayList<Coordinate> target = new ArrayList<Coordinate>();
        target.add(this.uninterruptibleTarget);
        return target;
    }

    private ArrayList<Coordinate> getToWallTrap() {
        ArrayList<Coordinate> target = new ArrayList<Coordinate>();
        Coordinate wallCoordinate = sensor.getClosestTileInDirectionOfTypes(WorldSpatial.RelativeDirection.LEFT,
                tileTypesToTarget);
        Coordinate targetCoordinate = sensor.getNearestTileOfTypesNearCoordinate(wallCoordinate, tileTypesToTarget);
        if (targetCoordinate != null) {
            System.out.println("pre target " + targetCoordinate);
            if (targetCoordinate.equals(sensor.getPosition())) {
                // We stop at the target, so move the target forward.
                int[] modMap = WorldSpatial.modMap.get(sensor.getOrientation());
                targetCoordinate = new Coordinate(targetCoordinate.x + modMap[0]*sensor.getVisionAhead(), targetCoordinate.y + modMap[1]*sensor.getVisionAhead());
                targetCoordinate = sensor.getNearestTileOfTypesNearCoordinate(targetCoordinate, tileTypesToTarget);
            }
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
                System.out.println("Getting to the right of upcoming wall");
                Coordinate rightTarget = getToRightOfUpcomingWall(wallInFront);
                target.add(rightTarget);
            }
        }
        return target;
    }
    
    public Coordinate getToRightOfUpcomingWall(Coordinate wallInFront) {
        // For now just use the point beside the upcoming wall so we slow down.
        System.out.println("wall in front: " + wallInFront);
        Coordinate roadNearWall = sensor.getNearestTileOfTypesNearCoordinate(wallInFront, tileTypesToTarget);
        // Using this point, check to the right of it for free road tiles to go to.
        Coordinate rightOfRoadNearWall = roadNearWall;
        for (int i = 0; i <= sensor.getVisionAhead(); i++) {
            int[] rightModMap = WorldSpatial.modMap.get(WorldSpatial.getRightOf(sensor.getOrientation()));
            Coordinate candidate = new Coordinate(roadNearWall.x + (i * rightModMap[0]),
                    roadNearWall.y + (i * rightModMap[1]));
            // If the coordinate to the right of the point near the wall is a road, set the
            // destination to that point.
            if (tileTypesToTarget.contains(sensor.getCurrentView().get(candidate).getType())) {
                rightOfRoadNearWall = candidate;
            }
        }
        return rightOfRoadNearWall;
    }
    
    public void trackTraps() {
        ArrayList<MapTile.Type> traps = new ArrayList<MapTile.Type>();
        traps.add(MapTile.Type.TRAP);
        int[] leftModMap = WorldSpatial.modMap.get(WorldSpatial.getToSideOf(sensor.getOrientation(), WorldSpatial.RelativeDirection.LEFT));
        Coordinate left = new Coordinate(sensor.getPosition().x + leftModMap[0], sensor.getPosition().y + leftModMap[1]);
        // If the tile to our left is part a trap, add it to the current trap section.
        if (sensor.isDirectlyBesideTileOfTypes(traps, WorldSpatial.RelativeDirection.LEFT)) {
            currentTrapSection.put(left, (TrapTile) sensor.getCurrentView().get(left));
        }
        traps.remove(MapTile.Type.TRAP);
        traps.add(MapTile.Type.WALL);
        // If the tile to our left is a wall and we have a non-empty currentTrapSection, add it to the list of all trap sections.
        if (currentTrapSection.size() > 0 && sensor.isDirectlyBesideTileOfTypes(traps, WorldSpatial.RelativeDirection.LEFT)) {
            @SuppressWarnings("unchecked")
            HashMap<Coordinate, TrapTile> clone = (HashMap<Coordinate, TrapTile>) currentTrapSection.clone();
            trapSections.add(clone);
            // We've added the current trap section to the list of all trap sections, empty the current trap section.
            currentTrapSection.clear();
        }
    }

    /**
     * This method allows us to do something before we're done.
     * In this case, we push an Escape pathfinder onto the stack for each trap section in trapSections.
     * Each escape pathfinder takes the appropriate trap section as a constructor argument.
     */
    public boolean isDone() {
        boolean done = start.equals(sensor.getPosition()) && hasLeftStart;
        if (done) {
            for (HashMap<Coordinate, TrapTile> trapSection : trapSections) {
                pathFinderStack.push(new PathFinderEscape(pathFinderStack, sensor, trapSection));
            }
        }
        return done;
    }

}
