package pathfinders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import mycontroller.Sensor;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import utilities.DirectionUtils;
import world.WorldSpatial;

/**
 * This IPathFinder is responsible for exploring a room, a section of the map
 * enclosed by traps. For each trap section (a contiguous section of traps) that
 * it finds, it pushes a PathFinderEscape onto the stack that will get the car
 * adjacent to the trap section. It explores the room by hugging the left wall,
 * avoiding traps by hugging to the left of those too. Its job is done once it
 * gets back to where it started, upon which it gets popped off the stack and
 * the PathFinderEscape on the top of takes over.
 * 
 * @author Hao Le, Daniel Porteous, David Stern
 * 2017-10-22.
 * Group 17.
 */
public class PathFinderExplore extends PathFinderBasic {

    /**
     * A single trap section is a HashMap<Coordinate, TrapTile>. We have multiple of
     * these for each contiguous section of traps.
     */
    private ArrayList<HashMap<Coordinate, TrapTile>> trapSections;
    /** This is for keeping track of the current continguous section of traps. */
    private HashMap<Coordinate, TrapTile> currentTrapSection;

    /**
     * This constuctor just sets attributes and calls the super constructor.
     * 
     * @param pathFinderStack
     *            The stack of IPathFinders, passed in from an explore
     * @param sensor
     *            The Sensor created by MyAIController, passed down
     * @param targetTrapSection
     *            The trap section that this escape is responsible for getting to
     */
    public PathFinderExplore(Stack<IPathFinder> pathFinderStack, Sensor sensor) {
        super(pathFinderStack, sensor);

        this.trapSections = new ArrayList<HashMap<Coordinate, TrapTile>>();
        this.currentTrapSection = new HashMap<Coordinate, TrapTile>();
    }

    /**
     * This method is responsible for returning an ArrayList of Coordinates for the
     * PathFollower to follow. In this basic implementation, the ArrayList just has
     * a single point, but in more complex implementations there could be multiple
     * points defining a path to follow. It also keeps an eye on which traps it sees
     * and records them.
     * 
     * @return A list of Coordinates to follow
     */
    @Override
    public ArrayList<Coordinate> update() {
        trackTraps();
        // TODO comment on why we need this "hasLeftStart" thing.
        if (!hasLeftStart && !start.equals(sensor.getPosition())) {
            hasLeftStart = true;
        }
        if (!sensor.isDirectlyBesideTileOfTypes(tileTypesToAvoid, WorldSpatial.RelativeDirection.LEFT)) {
            return super.goToWallTrap();
        } else {
            // We're aligned with a wall, follow it until we come across an obstacle.
            return super.followWallTrap();
        }
        // Comment about how we just generate one point and chuck it in the array. TODO
    }

    /**
     * This method looks to the left of the car, looking for traps. When it sees a
     * trap, it adds it to the currentTrapSection, which builds up a contiguous
     * section of traps. Once something breaks it up, we "commit" it to
     * trapSections, which is a list of all trap sections we've seen.
     */
    private void trackTraps() {
        ArrayList<MapTile.Type> traps = new ArrayList<MapTile.Type>();
        traps.add(MapTile.Type.TRAP);
        int[] leftModMap = DirectionUtils.MOD_MAP
                .get(DirectionUtils.getToSideOf(sensor.getOrientation(), WorldSpatial.RelativeDirection.LEFT));
        Coordinate left = new Coordinate(sensor.getPosition().x + leftModMap[0],
                sensor.getPosition().y + leftModMap[1]);
        // If the tile to our left is part a trap, add it to the current trap section.
        if (sensor.isDirectlyBesideTileOfTypes(traps, WorldSpatial.RelativeDirection.LEFT)) {
            currentTrapSection.put(left, (TrapTile) sensor.getCurrentView().get(left));
        }
        // If the tile to our left is not a trap and we have a non-empty
        // currentTrapSection, add it to the list of all trap sections.
        if (currentTrapSection.size() > 0
                && !sensor.isDirectlyBesideTileOfTypes(traps, WorldSpatial.RelativeDirection.LEFT)) {
            @SuppressWarnings("unchecked")
            HashMap<Coordinate, TrapTile> clone = (HashMap<Coordinate, TrapTile>) currentTrapSection.clone();
            trapSections.add(clone);
            // We've added the current trap section to the list of all trap sections, empty
            // the current trap section.
            currentTrapSection.clear();
        }
    }

    /**
     * This method allows us to do something before we're done. In this case, we
     * push an Escape pathfinder onto the stack for each trap section in
     * trapSections. Each escape pathfinder takes the appropriate trap section as a
     * constructor argument.
     */
    public boolean isDone() {
        boolean done = sensor.getPosition().equals(start) && hasLeftStart;
        if (done) {
            // A more complex implementation would rate each trap section and pick the
            // section
            // that looks least dangerous.
            for (HashMap<Coordinate, TrapTile> trapSection : trapSections) {
                pathFinderStack.push(new PathFinderEscape(pathFinderStack, sensor, trapSection));
            }
        }
        return done;
    }

}
