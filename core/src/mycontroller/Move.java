package mycontroller;

import utilities.Coordinate;
import world.WorldSpatial;

public class Move {

    public enum SpeedChange {SLOWDOWN, MAINTAIN, ACCELERATE}

    private final Coordinate carCoordinate;
    private final Coordinate targetCoordinate;
    private final float degree;
    private final SpeedChange speedChange;
    private final WorldSpatial.RelativeDirection turnDirection;

    public Move(Coordinate carC, Coordinate tarC, float degree, SpeedChange speedChange,
                WorldSpatial.RelativeDirection turnDirection) {
        this.carCoordinate = carC;
        this.targetCoordinate = tarC;
        this.degree = degree;
        this.speedChange = speedChange;
        this.turnDirection = turnDirection;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Move) {
            Move otherMove = (Move) obj;
            return (this.carCoordinate.equals(otherMove.carCoordinate) &&
                    this.targetCoordinate.equals(otherMove.targetCoordinate) &&
                    Float.compare(this.degree, otherMove.degree) == 0 &&
                    this.speedChange == otherMove.speedChange &&
                    this.turnDirection == otherMove.turnDirection);
        }
        return false;
    }

    public Move getOppositeMove() {
        SpeedChange oppositeSpeedChange = null;
        WorldSpatial.RelativeDirection oppositeDirection = null;
        if (speedChange == SpeedChange.SLOWDOWN) {
            oppositeSpeedChange = SpeedChange.ACCELERATE;
        }
        else {
            oppositeSpeedChange = SpeedChange.SLOWDOWN;
        }
        if (turnDirection == WorldSpatial.RelativeDirection.RIGHT) {
            oppositeDirection = WorldSpatial.RelativeDirection.LEFT;
        }
        else {
            oppositeDirection = WorldSpatial.RelativeDirection.RIGHT;
        }
        return new Move(carCoordinate, targetCoordinate, degree, oppositeSpeedChange, oppositeDirection);
    }




    public SpeedChange getSpeedChange() {
        return this.speedChange;
    }

    public WorldSpatial.RelativeDirection getTurnDirection() {
        return this.turnDirection;
    }
}