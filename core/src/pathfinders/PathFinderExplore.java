package pathfinders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import mycontroller.Sensor;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class PathFinderExplore extends PathFinderBasic {

    // A single trap section is a HashMap<Coordinate, TrapTile>. 
    // We have multiple of these for each contiguous section of traps.
    private ArrayList<HashMap<Coordinate, TrapTile>> trapSections;
    // This is for keeping track of the current continguous section of traps.
    private HashMap<Coordinate, TrapTile> currentTrapSection;

    public PathFinderExplore(Stack<IPathFinder> pathFinderStack, Sensor sensor) {
        super(pathFinderStack, sensor);
        
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
            return super.getToWallTrap();
        } else {
            // We're aligned with a wall, follow it until we come across an obstacle.
            return super.followWallTrap();
        }
        // Comment about how we just generate one point and chuck it in the array. TODO
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
        boolean done = sensor.getPosition().equals(start) && hasLeftStart;
        if (done) {
            for (HashMap<Coordinate, TrapTile> trapSection : trapSections) {
                pathFinderStack.push(new PathFinderEscape(pathFinderStack, sensor, trapSection));
            }
        }
        return done;
    }

}
