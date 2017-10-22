package pathfinders;

import utilities.Coordinate;

import java.util.ArrayList;
import java.util.Stack;

import mycontroller.Sensor;

public class PathFinderEscape implements IPathFinder {
    
    private Sensor sensor;
    private Coordinate start;
    private Stack<IPathFinder> pathFinderStack;

    public PathFinderEscape(Stack<IPathFinder> pathFinderStack, Sensor sensor) {
        this.sensor = sensor;
        start = sensor.getPosition();
        this.pathFinderStack = pathFinderStack;
    }

    @Override
    public ArrayList<Coordinate> update() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isDone() {
        // TODO Auto-generated method stub
        return false;
    }
}
