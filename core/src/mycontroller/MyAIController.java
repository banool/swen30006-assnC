package mycontroller;

import java.util.ArrayList;
import java.util.Stack;

import controller.CarController;
import pathfinders.IPathFinder;
import pathfinders.PathFinderExplore;
import pathfollowers.IPathFollower;
import pathfollowers.PathFollowerBasic;
import utilities.Coordinate;
import world.Car;

public class MyAIController extends CarController {

    private final float CAR_SPEED = 3;

    /* TODO Comment this! */
    private Sensor sensor;
    private Stack<IPathFinder> pathFinderStack;
    private IPathFinder activePathFinder;
    private IPathFollower pathFollower;

    private ArrayList<Coordinate> coordsToFollow;

    public MyAIController(Car car) {
        super(car);
        sensor = new Sensor(this);
        // TODO comment, empty stack at the start.
        pathFinderStack = new Stack<IPathFinder>();
        // TODO comment set the starting pathfinder to Explore.
        // Something about defaults, maybe this could be more configurable.
        // Probably not something we have to worry about though.
        activePathFinder = new PathFinderExplore(pathFinderStack, sensor);
        pathFollower = new PathFollowerBasic();
    }

    // TODO comment this heavily
    public void update(float delta) {
        sensor.update();
        if (activePathFinder.isDone()) {
            activePathFinder = pathFinderStack.pop();
        }
        coordsToFollow = activePathFinder.update();
        pathFollower.update(this, delta, coordsToFollow, sensor);
    }

    public float getTopSpeed() {
        return CAR_SPEED;
    }

}
