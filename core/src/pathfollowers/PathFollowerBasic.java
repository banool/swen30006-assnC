package pathfollowers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;
import mycontroller.MyAIController;
import mycontroller.Sensor;
import utilities.Coordinate;
import utilities.PeekTuple;
import world.WorldSpatial;

public class PathFollowerBasic implements IPathFollower {

    private enum SpeedChange {SLOWDOWN, MAINTAIN, ACCELERATE}

    private MyAIController controller;
    private Sensor sensor;
    private final Double MAX_SPEED = 2.0;
    private final Double TURN_SPEED = 1.5;
    private SpeedChange desiredSpeedChange;

    public PathFollowerBasic(MyAIController controller, Sensor sensor) {
        this.controller = controller;
        this.sensor = sensor;
    }


    // Note: PathFinder is the collision-avoidance guide, we don't need to do that here.
    @Override
    public void update(float delta, ArrayList<Coordinate> coordsToFollow) {
        Coordinate coord = null;

        // If no coordinate given, this is an error!
        if (coordsToFollow.isEmpty()) {
            System.out.println("Error: No coordinate given to PathFollower by the PathFinder.");
        }
        // Otherwise, proceed
        else {
            coord = coordsToFollow.get(0);
            System.out.println("Coordinate given = (" + coord.x + ", " + coord.y + ").");
            System.out.println("Coordinate of car = (" + sensor.getPosition().x + ", " + sensor.getPosition().y + ").");

            // Get Degree of Coordinate
            float tarDegree = (float) getDegreeOfCoord(coord);

            // Get Relative Direction of the Coordinate
            WorldSpatial.RelativeDirection tarDirection = getDirection(tarDegree);

            // Get desired change in speed
            desiredSpeedChange = adjustVelocity(coord);

            // If degree of coordinate is same as degree of car, go straight forward or straight backward
            if (tarDirection == WorldSpatial.RelativeDirection.FORWARD ||
                    tarDirection == WorldSpatial.RelativeDirection.BACKWARD) {
                goForwardOrBackward(tarDirection, desiredSpeedChange);
            }

            else {
                // Generate All combinations of acceleration, reverse acceleration, Left and Right turns
                Vector2 currVelocity = sensor.getVelocity2();
                Vector2[] testSpeeds = getTestSpeeds(currVelocity);
                WorldSpatial.RelativeDirection[] testDirections = {WorldSpatial.RelativeDirection.LEFT,
                        WorldSpatial.RelativeDirection.RIGHT};

                // Find the best direction and change in velocity to reach the destination
                WorldSpatial.RelativeDirection bestDirection = null;
                float bestVelocity = currVelocity.len();
                float minDist = Float.MAX_VALUE;
                for (int speed = 0; speed < 3; speed++) {
                    for (int direction = 0; direction < 2; direction++) {

                        // Use controller.peek(...) to determine best combination to reach target coordinate
                        PeekTuple approxDest = controller.peek(testSpeeds[speed], tarDegree, testDirections[direction],
                                delta);
                        Coordinate approxCoord = approxDest.getCoordinate();
                        float projectedDistanceFromTarget = coord.distanceFrom(approxCoord);
                        System.out.println("----\nProjected Distance: " + projectedDistanceFromTarget);
                        System.out.println("Direction = " + testDirections[direction] + ". Speed = " +
                                testSpeeds[speed].len() + "----\n");
                        if (approxDest.getReachable() && projectedDistanceFromTarget < minDist) {
                            minDist = coord.distanceFrom(approxCoord);
                            bestDirection = testDirections[direction];
                            bestVelocity = testSpeeds[speed].len();
                        }
                    }
                }
                // Direct Controller in the direction and velocity that will take the car closest to the desired
                // destination
                moveCar(currVelocity.len(), bestVelocity, delta, bestDirection);
            }
        }
    }


    private void moveCar(float currentVelocity, float desiredVelocity, float delta,
                         WorldSpatial.RelativeDirection bestDirection) {
        if (currentVelocity < desiredVelocity) {
            System.out.println("Applying Forward Acceleration");
            controller.applyForwardAcceleration();
        } else {
            System.out.println("Applying Reverse Acceleration");
            controller.applyReverseAcceleration();
        }
        if (bestDirection == WorldSpatial.RelativeDirection.RIGHT) {
            System.out.println("Turning Right");
            controller.turnRight(delta);
        } else {
            System.out.println("Turning Left");
            controller.turnLeft(delta);
        }
    }


    private Vector2[] getTestSpeeds(Vector2 currVelocity) {
        float addedX = (Double.compare(currVelocity.x, 0.0) == 0 ?  (float) .001 : 0 );
        float addedY = (Double.compare(currVelocity.y, 0.0) == 0 ?  (float) .001 : 0 );
        Vector2[] testSpeeds = {
                new Vector2(currVelocity.x * (float) 1.1 + addedX, currVelocity.y * (float) 1.1 + addedY),
                new Vector2(currVelocity.x * (float) 1.05 + addedX, currVelocity.y * (float) 1.05 + addedY),
                new Vector2(currVelocity.x * (float) 0.95 + addedX, currVelocity.y * (float) 0.95 + addedY),
                new Vector2(currVelocity.x * (float) 0.9 + addedX, currVelocity.y * (float) 0.9 + addedY)};

        return testSpeeds;
    }


    private void goForwardOrBackward(WorldSpatial.RelativeDirection d, SpeedChange speedChange) {
        if (d == WorldSpatial.RelativeDirection.FORWARD) {
            if (speedChange == SpeedChange.ACCELERATE) {
                controller.applyForwardAcceleration();
            } else if (speedChange == SpeedChange.SLOWDOWN) {
                controller.applyReverseAcceleration();
            }
        } else if (d == WorldSpatial.RelativeDirection.BACKWARD) {
            if (speedChange == SpeedChange.ACCELERATE) {
                controller.applyReverseAcceleration();
            } else if (speedChange == SpeedChange.SLOWDOWN) {
                controller.applyForwardAcceleration();
            }
        }
    }


    private SpeedChange adjustVelocity(Coordinate coordinate) {
        Coordinate carPosition = sensor.getPosition();
        // If the PathFinder is telling us to slow down, or if we're above the max speed, slow down!
        if (coordinate.distanceFrom(carPosition) <= sensor.VISION_AHEAD / 3 || sensor.getVelocity() > MAX_SPEED) {
            return SpeedChange.SLOWDOWN;
        }
        // If the PathFinder is happy with our acceleration, or we're at max speed, maintain current speed
        else if (coordinate.distanceFrom(carPosition) < sensor.VISION_AHEAD || sensor.getVelocity() == MAX_SPEED) {
            return SpeedChange.MAINTAIN;
        }
        // If the PathFinder is saying to go to the coordinate at or beyond the edge of our visual map & we're below
        // max speed, Accelerate!
        else {
            return SpeedChange.ACCELERATE;
        }
    }


    // Get the degree of the Coordinate
    private double getDegreeOfCoord(Coordinate coord) {

        // Calculate the deltas as the next minus the current
        double delta_x = (double) coord.x - (double) sensor.getX();
        double delta_y = (double) coord.y - (double) sensor.getY();

        // Calculate the angle in radians using atan2
        double theta = Math.atan2(delta_y, delta_x);

        // Calculate the angle in degrees (coordinate from the east, 0 degrees; 270 is given as -90, 225 as -135, etc.)
        double angle = theta * 180 / Math.PI;


        // If angle is negative, make it positive (e.g. -90 to 270)
        if (Double.compare(angle, 0.0) < 0) {
            angle += 360.0;
        }
        return angle;
    }


    // Given our direction, what is the direction of the target coordinate?
    private WorldSpatial.RelativeDirection getDirection(double degree) {

        double tarAngle = degree;
        double carAngle = sensor.getAngle();

        if (tarAngle - carAngle == 0) {
            return WorldSpatial.RelativeDirection.FORWARD;
        } else if (tarAngle + 180 == carAngle ||
                tarAngle - 180 == carAngle) {
            return WorldSpatial.RelativeDirection.BACKWARD;
        } else if (Math.abs(carAngle - tarAngle) < 180) {
            if (carAngle < tarAngle) {
                return WorldSpatial.RelativeDirection.LEFT;
            } else {
                return WorldSpatial.RelativeDirection.RIGHT;
            }
        } else {
            if (carAngle < tarAngle) {
                return WorldSpatial.RelativeDirection.RIGHT;
            } else {
                return WorldSpatial.RelativeDirection.LEFT;
            }
        }
    }


//    private void turnToCoord(Coordinate coord) {
//
//        // Turn in Relative direction of the coordinate in relation to the current direction of the car
//        if (coordIsLeft(coord)) {
//            controller.turnLeft(delta);
//        }
//        else if (coord.IsRight(coord)) {
//            controller.turnRight(delta);
//        }
//
//        else if (coord.isDirectlyBehind(coord)) {
//            controller.applyReverseAcceleration();
//        }
//
//    }


    // Set a null Best Turn Direction and a Best Acceleration / Deacceleration
    // Generate diff PeekTuples using controller.peek(...) w/ different Velocities and diff Left & Right turn
    // directions, keeping track of the best combination of turn direction and acceleration / ReverseAcceleration
    // Just generate a list of 4 options: Accel+Left, Accel+Right, ReverseAccel+Left, ReverseAccel+Right?
    // Find Best Turn Direction and a Best Acceleration / Deacceleration


    // Old Proposed Strategies
    // Create a PriorityQueue of PeekTuples based on proximity of PeekTuple to Coordinate

    // get turn direction
    // getTurnDirection(coord);
    // get target speed
    // Get speed adjustment required
    // Apply turn and acceleration
    // Turn in Relative direction of the coordinate in relation to the current direction of the car
    // turnToCoord(coord);

    // If coord is far, accelerate
    // If the coord is close, deaccelerate
    // If

}