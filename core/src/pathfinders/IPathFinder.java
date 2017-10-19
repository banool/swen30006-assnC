package pathfinders;

import java.util.ArrayList;

import mycontroller.Sensor;
import utilities.Coordinate;

public interface IPathFinder {

    // TODO comment on how we don't take the Sensor anymore in update, since we
    // have a reference to it from the constructor.
    public ArrayList<Coordinate> update();

    public boolean isDone();

}
