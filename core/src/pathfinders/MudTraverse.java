package pathfinders;

import utilities.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import mycontroller.Sensor;
import tiles.TrapTile;

/**
 * Extends the abstract TrapTraverse class. Responsible for navigating over / through Mud traps, given
 * that the car is already adjacent to a Mud trap section.
 *
 * @author Hao Le, Daniel Porteous, David Stern
 * 2017-10-22.
 * Group 17.
 */
public class MudTraverse extends TrapTraverse {

    private Sensor sensor;
    private Coordinate start;
    private Stack<IPathFinder> pathFinderStack;

    public MudTraverse(Stack<IPathFinder> pathFinderStack, Sensor sensor, HashMap<Coordinate, TrapTile> trapSection) {
        this.sensor = sensor;
        start = sensor.getPosition();
        this.pathFinderStack = pathFinderStack;
    }

    @Override
    public ArrayList<Coordinate> update() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isDone() {
        // TODO Auto-generated method stub
        return true;
    }

}
