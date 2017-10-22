package mycontroller;

import utilities.Coordinate;
import utilities.DirectionUtils;

public class Move {

    public enum SpeedChange {SLOWDOWN, MAINTAIN, ACCELERATE}

    private final Coordinate carCoordinate;
    private final Coordinate targetCoordinate;
    private final float degree;
    private final SpeedChange speedChange;
    private final DirectionUtils.RelativeDirectionDU turnDirection;

    public Move(Coordinate carC, Coordinate tarC, float degree, SpeedChange speedChange,
                DirectionUtils.RelativeDirectionDU turnDirection) {
        this.carCoordinate = carC;
        this.targetCoordinate = tarC;
        this.degree = degree;
        this.speedChange = speedChange;
        this.turnDirection = turnDirection;
    }

    /**
     * Overrides equals, used when comparing Move objects for equality
     *
     * @param obj The object being tested for equality
     * @return boolean true if the object being compared is considered equal to the calling instance, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Move) {
            Move otherMove = (Move) obj;
            // Once move is only equal to another if all of the attributes are the same.
            return (this.carCoordinate.equals(otherMove.carCoordinate) &&
                    this.targetCoordinate.equals(otherMove.targetCoordinate) &&
                    Float.compare(this.degree, otherMove.degree) == 0 &&
                    this.speedChange == otherMove.speedChange &&
                    this.turnDirection == otherMove.turnDirection);
        }
        return false;
    }

    /**
     * getOppositeMove returns a Move representing the opposite move to the calling move.
     *
     * @return a Move object representing the opposite move to the calling move.
     */
    public Move getOppositeMove() {
        SpeedChange oppositeSpeedChange = null;
        DirectionUtils.RelativeDirectionDU oppositeDirection = null;
        if (speedChange == SpeedChange.SLOWDOWN) {
            oppositeSpeedChange = SpeedChange.ACCELERATE;
        } else {
            oppositeSpeedChange = SpeedChange.SLOWDOWN;
        }
        if (turnDirection == DirectionUtils.RelativeDirectionDU.RIGHT) {
            oppositeDirection = DirectionUtils.RelativeDirectionDU.LEFT;
        } else {
            oppositeDirection = DirectionUtils.RelativeDirectionDU.RIGHT;
        }
        return new Move(carCoordinate, targetCoordinate, degree, oppositeSpeedChange, oppositeDirection);
    }


    /**
     * @return Return the SpeedChange of this instance of Move.
     */
    public SpeedChange getSpeedChange() {
        return this.speedChange;
    }

    /**
     * @return Return the direction (DirectionUtils.RelativeDirectionDU) of this instance of Move.
     */
    public DirectionUtils.RelativeDirectionDU getTurnDirection() {
        return this.turnDirection;
    }
}
