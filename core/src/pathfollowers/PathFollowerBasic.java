package pathfollowers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;
import mycontroller.Move;
import mycontroller.MyAIController;
import mycontroller.Sensor;
import utilities.Coordinate;
import utilities.PeekTuple;
import world.WorldSpatial;

public class PathFollowerBasic implements IPathFollower {


    private MyAIController controller;
    private Sensor sensor;
    private Move.SpeedChange desiredSpeedChange;
    private LinkedList<Move> previousMoves;
    private Boolean escaping;
    private Integer durationOfEscapeMove;
    private Integer lastHealth;
    private Move escapeMove;
    private Integer numEscapeMoves;

    private final Double MAX_SPEED = 2.0;
    private final Integer TRACKED_MOVES = 30;

    public PathFollowerBasic(MyAIController controller, Sensor sensor) {
        this.controller = controller;
        this.sensor = sensor;
        previousMoves = new LinkedList<Move>();
        lastHealth = sensor.getHealth();
        escaping = false;
        numEscapeMoves = 0;
    }

    /**
     * update takes delta, and a list of coordinates to follow, and sends messages to the car to turn and accelerate or
     * reverse accelerate in order to follow the first coordinate in the list. The list is used for extensibility
     * purposes.
     *
     * @param delta          The time step specified in Simulation
     * @param coordsToFollow The list of coordinates received from a PathFinder class, used to direct the car.
     */
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
            float tarDegree = (float) getDegreeOfCoord(coord);

            // Remove out-dated moves (only keep track of the last TRACKED_MOVES moves)
            if (previousMoves.size() > TRACKED_MOVES) {
                previousMoves.poll();
            }

            // If we're running into a wall, enter escape mode to correct course!
            if (allSame(previousMoves) && sensor.getHealth() < lastHealth && previousMoves.size() > 0) {
                escaping = true;
                escapeMove = previousMoves.peek().getOppositeMove();
            }

            // Alternate Update Method if in 'escape' mode. This is a collision-avoidance tactic, in case the moves
            // given by peek or pathFinder are erroneous.
            if (escaping) {
                updateEscape(delta, coord, tarDegree);
                return;
            }

            // If directed to do so, go straight
            WorldSpatial.RelativeDirection tarDirection = getDirection(tarDegree);
            if (tarDirection == WorldSpatial.RelativeDirection.FORWARD ||
                    tarDirection == WorldSpatial.RelativeDirection.BACKWARD) {
                goForwardOrBackward(tarDirection, adjustVelocity(coord));
            }

            // Otherwise, consider turning alternatives
            else {

                // Generate combinations of acceleration, reverse acceleration, Left & Right turns
                Vector2 currVelocity = sensor.getVelocity2();
                Vector2[] testSpeeds = getTestSpeeds(currVelocity);
                Move.SpeedChange[] speedChanges = getSpeedChanges(testSpeeds, currVelocity);
                WorldSpatial.RelativeDirection[] testDirections = {WorldSpatial.RelativeDirection.LEFT,
                        WorldSpatial.RelativeDirection.RIGHT};

                // Find the best direction and the index of the best change in velocity to reach the destination
                WorldSpatial.RelativeDirection bestDirection = null;
                int bestSpeedInd = 0;
                float minDist = Float.MAX_VALUE;
                for (int speed = 0; speed < 3; speed++) {
                    for (int d = 0; d < 2; d++) {

                        // Use controller.peek(...) to determine best combination to reach target coordinate
                        // TODO: BUG! peek(...) method doesn't accurately predict coordinate. Re-write peek(...).
                        PeekTuple approxDest = controller.peek(testSpeeds[speed], tarDegree, testDirections[d], delta);
                        Coordinate approxCoord = approxDest.getCoordinate();
                        float projectedDistanceFromTarget = coord.distanceFrom(approxCoord);

                        // Moves that lead to reachable destinations closest to the predicted coordinates are the best
                        if (approxDest.getReachable() && projectedDistanceFromTarget < minDist) {
                            minDist = coord.distanceFrom(approxCoord);
                            bestDirection = testDirections[d];
                            bestSpeedInd = speed;
                        }
                    }
                }
                // Move the car!
                moveCar(currVelocity.len(), testSpeeds[bestSpeedInd].len(), delta, bestDirection);

                // Update Queue of Last Moves
                previousMoves.offer(new Move(sensor.getPosition(), coord, sensor.getAngle(), speedChanges[bestSpeedInd],
                        bestDirection));
            }
        }
    }

    /**
     * updateEscape is an alternate update method, used during 'escape' mode.
     * I.e. when the car is running into a wall without stopping.
     *
     * @param delta     The time step specified in Simulation
     * @param coord     The coordinate given by the PathFinder class, directing the car
     * @param tarDegree The degree the targeted coordinate is from the direction the car is currently facing.
     */
    private void updateEscape(float delta, Coordinate coord, float tarDegree) {

        // If we're escaping and reach a point where all of the previous moves were the same, only move forward!
        if (escaping && numEscapeMoves > 0 allSame(previousMoves)){
            moveOnlyForward(delta, coord);
            numEscapeMoves += 1;
            return;
        }

        // Stop escaping once we are facing our goal
        if (tarDegree == sensor.getAngle()) {
            escaping = false;
            escapeMove = null;
        }

        // Normal method of escaping a terrible fate (moving in the exact opposite direction)
        if (escaping) {
            numEscapeMoves += 1;
            moveCar(escapeMove, delta, coord);
        }
    }


    /**
     * allSame takes a list of previous moves, and returns true if they are all the same.
     *
     * @param previousMoves A LinkedList of the moves that occurred immediately before the current update.
     * @return Boolean tue if all the moves in the list are the same, false otherwise.
     */
    private Boolean allSame(LinkedList<Move> previousMoves) {
        LinkedList<Move> moves = new LinkedList<Move>(previousMoves);
        Move firstMove = moves.poll();
        Move thisMove;

        // While there are more moves to compare, do so!
        do {
            thisMove = moves.poll();

            // If the first move is not the same as this move, they're not all the same
            if (thisMove == null || firstMove == null || !firstMove.equals(thisMove)) {
                return false;
            }
        } while (!moves.isEmpty());
        return true;
    }

    /**
     * moveOnlyForward takes a Move, a time step, and a target coordinate, and moves Forward in the direction described
     * in the escape move.
     *
     * @param delta       The time step specified in Simulation.
     * @param targetCoord The coordinate targeted by PathFinder.
     */
    private void moveOnlyForward(float delta, Coordinate targetCoord) {
        Move.SpeedChange speedChange;
        WorldSpatial.RelativeDirection direction = WorldSpatial.RelativeDirection.FORWARD;

        // Forward or reverse-acceleration based on the speed change specified in the Move.
        if (escapeMove.getSpeedChange() == Move.SpeedChange.ACCELERATE) {
            controller.applyForwardAcceleration();
            speedChange = Move.SpeedChange.ACCELERATE;
        } else {
            controller.applyReverseAcceleration();
            speedChange = Move.SpeedChange.SLOWDOWN;
        }
        // Update lastHealth and previousMoves.
        lastHealth = sensor.getHealth();
        previousMoves.offer(new Move(sensor.getPosition(), targetCoord, sensor.getAngle(), speedChange, direction));
    }


    /**
     * moveCar takes a specified escape move, and moves in the direction and with the acceleration specified by that
     * move.
     *
     * @param escapeMove  The Move generated when entering escape mode -> the opposite move to running into the wall.
     * @param delta       The time step specified in Simulation.
     * @param targetCoord The coordinate targeted by PathFinder.
     */
    private void moveCar(Move escapeMove, float delta, Coordinate targetCoord) {
        Move.SpeedChange speedChange;
        WorldSpatial.RelativeDirection direction;

        if (escapeMove.getSpeedChange() == Move.SpeedChange.ACCELERATE) {
            controller.applyForwardAcceleration();
            speedChange = Move.SpeedChange.ACCELERATE;
        } else {
            controller.applyReverseAcceleration();
            speedChange = Move.SpeedChange.SLOWDOWN;
        }
        if (escapeMove.getTurnDirection() == WorldSpatial.RelativeDirection.RIGHT) {
            controller.turnRight(delta);
            direction = WorldSpatial.RelativeDirection.RIGHT;
        } else {
            controller.turnLeft(delta);
            direction = WorldSpatial.RelativeDirection.LEFT;
        }
        lastHealth = sensor.getHealth();
        previousMoves.offer(new Move(sensor.getPosition(), targetCoord, sensor.getAngle(), speedChange, direction));
    }

    /**
     * moveCar takes a an existing velocity, a desired velocity, a time step, and a direction, and controls the car to
     * move in that direction.
     *
     * @param currentVelocity The current velocity the car is travelling at.
     * @param desiredVelocity The faster or slower velocity that was identified as desirable
     * @param delta           The time step specified in Simulation.
     * @param bestDirection   The direction identified as desirable.
     */
    private void moveCar(float currentVelocity, float desiredVelocity, float delta,
                         WorldSpatial.RelativeDirection bestDirection) {
        if (currentVelocity < desiredVelocity) {
            controller.applyForwardAcceleration();
        } else {
            controller.applyReverseAcceleration();
        }
        if (bestDirection == WorldSpatial.RelativeDirection.RIGHT) {
            controller.turnRight(delta);
        } else {
            controller.turnLeft(delta);
        }
        lastHealth = sensor.getHealth();
    }


    /**
     * getTestSpeeds generates an array of Vector2 objects representing different speeds, to be given to peek(...) to
     * generate different possible moves the car can make.
     * @param currVelocity The current velocity of the car.
     * @return An array of Vector2 objects representing the velocities to feed into peek(...).
     */
    private Vector2[] getTestSpeeds(Vector2 currVelocity) {
        float addedX = (Double.compare(currVelocity.x, 0.0) == 0 ? (float) .001 : 0);
        float addedY = (Double.compare(currVelocity.y, 0.0) == 0 ? (float) .001 : 0);
        Vector2[] testSpeeds = {
                new Vector2(currVelocity.x * (float) 1.1 + addedX, currVelocity.y * (float) 1.1 + addedY),
                new Vector2(currVelocity.x * (float) 1.05 + addedX, currVelocity.y * (float) 1.05 + addedY),
                new Vector2(currVelocity.x * (float) 0.95 + addedX, currVelocity.y * (float) 0.95 + addedY),
                new Vector2(currVelocity.x * (float) 0.9 + addedX, currVelocity.y * (float) 0.9 + addedY)};

        return testSpeeds;
    }

    /**
     * getSpeedChanges generates an array of Move.SpeedChange objects representing different speeds, to be given to
     * peek(...) to generate different possible moves the car can make.
     * @param testSpeeds The array of different possible speeds to be fed into peek(...).
     * @param currVelocity The current velocity of the car.
     * @return Move.SpeedChange[] an array of Move.SpeedChange objects representing the different changes in speed that
     * correlate exactly with the different Vector2 objects in testSpeeds.
     */
    private Move.SpeedChange[] getSpeedChanges(Vector2[] testSpeeds, Vector2 currVelocity) {
        float currentSpeed = currVelocity.len();
        Move.SpeedChange[] speedChanges = new Move.SpeedChange[testSpeeds.length];
        for (int i = 0; i < testSpeeds.length; i++) {
            if (testSpeeds[i].len() < currentSpeed) {
                speedChanges[i] = Move.SpeedChange.SLOWDOWN;
            } else {
                speedChanges[i] = Move.SpeedChange.ACCELERATE;
            }
        }
        return speedChanges;
    }


    /**
     * goForwardOrBackward takes a direction and a speed change, and directs the car only forward or backwards, based
     * on those arguments.
     * @param d Represents the direction specified by the calling object.
     * @param speedChange Represents the change in speed specified by the calling object.
     */
    private void goForwardOrBackward(WorldSpatial.RelativeDirection d, Move.SpeedChange speedChange) {
        if (d == WorldSpatial.RelativeDirection.FORWARD) {
            if (speedChange == Move.SpeedChange.ACCELERATE) {
                controller.applyForwardAcceleration();
            } else if (speedChange == Move.SpeedChange.SLOWDOWN) {
                controller.applyReverseAcceleration();
            }
        } else if (d == WorldSpatial.RelativeDirection.BACKWARD) {
            if (speedChange == Move.SpeedChange.ACCELERATE) {
                controller.applyReverseAcceleration();
            } else if (speedChange == Move.SpeedChange.SLOWDOWN) {
                controller.applyForwardAcceleration();
            }
        }
        // Update the last observed health
        lastHealth = sensor.getHealth();
    }


    /**
     * adjustVelocity takes a coordinate and indicates whether the PathFinder is communicating that the car should slow,
     * maintain its speed, or accelerate.
     * @param coordinate The targeted coordinate given by the PathFinder
     * @return A Move.SpeedChange that indicates the change in speed desired by the PathFinder.
     */
    private Move.SpeedChange adjustVelocity(Coordinate coordinate) {
        Coordinate carPosition = sensor.getPosition();

        // If the PathFinder is telling us to slow down, or if we're above the max speed, slow down!
        if (coordinate.distanceFrom(carPosition) <= sensor.VISION_AHEAD / 3 || sensor.getVelocity() > MAX_SPEED) {
            return Move.SpeedChange.SLOWDOWN;
        }
        // If the PathFinder is happy with our acceleration, or we're at max speed, maintain current speed
        else if (coordinate.distanceFrom(carPosition) < sensor.VISION_AHEAD || sensor.getVelocity() == MAX_SPEED) {
            return Move.SpeedChange.MAINTAIN;
        }
        // If the PathFinder is saying to go to the coordinate at or beyond the edge of our visual map & we're below
        // max speed, Accelerate!
        else {
            return Move.SpeedChange.ACCELERATE;
        }
    }


    /**
     * getDegreeOfCoord Gets the degree of the Coordinate, in relation to the direction the car is currently facing.
     * @param coord The coordinate being targeted, fed by PathFinder
     * @return double representing the degree of the coordinate in relation to the current direction of the car.
     */
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


    /**
     * getDirection takes a degree and returns the direction of that degree.
     *
     * @param degree A double representing the degree being converted into a direction.
     * @return A WorldSpatial.RelativeDirection representation of the direction.
     */
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

}