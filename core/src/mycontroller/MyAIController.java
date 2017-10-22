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

    /**
     * Custom AI controller that encompasses and handles the logic of the car.
     * @param car The car which we are controlling.
     */
    public MyAIController(Car car) {
        super(car);
        sensor = new Sensor(this);

        /* This stack starts off empty at the start. This stack is responsible for handling, remembering and changing
         * the states of the AI's behaviour. It'll go through either an Explore, Escape, Trap Traversal (including the
         * specific traps) path finder strategy.
         */
        pathFinderStack = new Stack<IPathFinder>();

        /* The first pathfinder strategy is Explore */
        activePathFinder = new PathFinderExplore(pathFinderStack, sensor);
        pathFollower = new PathFollowerBasic(this, sensor);
    }

    /**
     * MyAIController.update() is responsible for continuously reading in sensor information and updating the pathfinder
     * strategies in the stack when possible.
     * @param delta
     */
    public void update(float delta) {
        sensor.update();

        /* Once the current pathfinder strategy has been completed, it is popped off from the stack for the next one
         * to be handled.
         */
        if (activePathFinder.isDone()) {
            activePathFinder = pathFinderStack.pop();
        }

        /* Move onto the next pathfinder (Explore, Escape, TrapTraverse) strategy */
        coordsToFollow = activePathFinder.update();
        pathFollower.update(delta, coordsToFollow);
    }

    public float getTopSpeed() {
        return CAR_SPEED;
    }

}
