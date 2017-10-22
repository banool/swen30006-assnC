package pathfinders;

import utilities.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import mycontroller.Sensor;
import tiles.TrapTile;

public class GrassTraverse extends TrapTraverse {

    private Sensor sensor;
    private Coordinate start;
    private Stack<IPathFinder> pathFinderStack;

    public GrassTraverse(Stack<IPathFinder> pathFinderStack, Sensor sensor, HashMap<Coordinate, TrapTile> trapSection) {
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
        // METHOD STUB
        return true;
    }

}
