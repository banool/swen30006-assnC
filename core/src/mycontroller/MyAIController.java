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

    private Sensor sensor;                          // the sensor reading in the environment around the car
    private Stack<IPathFinder> pathFinderStack;     // responsible for handling the strategies/behaviour of the AI
    private IPathFinder activePathFinder;           // the AI's current traversal strategy/behaviour
    private IPathFollower pathFollower;             // coordinates the AI has to move towards

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
        if (coordsToFollow.get(0).equals(sensor.getPosition())) System.out.println("SAME");
        pathFollower.update(delta, coordsToFollow);
    }

    public float getTopSpeed() {
        return CAR_SPEED;
    }

}
