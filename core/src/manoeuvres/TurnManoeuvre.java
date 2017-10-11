package manoeuvres;

import mycontroller.MyAIController;
import mycontroller.SensorData;
import world.WorldSpatial;

/**
 * TurnManoeuvre class which encapsulates both left and right turns based
 * on the RelativeDirection given to the controller.
 * @author daniel
 *
 */
public class TurnManoeuvre extends Manoeuvre {
    
    private MyAIController controller;
    private WorldSpatial.RelativeDirection turnDirection;
    private float startAngle;
    private float turnAmount;
    private GoForwardManoeuvre goForwardManoeuvre;
    
    public TurnManoeuvre(MyAIController controller, 
                        WorldSpatial.RelativeDirection turnDirection,
                        float startAngle,
                        float turnAmount) {
        super(controller);
        this.turnDirection = turnDirection;
        this.startAngle = startAngle;
        this.turnAmount = turnAmount;
    }
    
    // TODO this logic is not be right, but I'm just filling in the structure.
    public void update(float delta, SensorData sensorData) {
        float difference = difference(startAngle, controller.getAngle());
        while (difference < turnAmount) {
            turn(delta);
        }
    }
    
    // TODO In order to do this cheeky turning thing where we turn the other
    // way and apply reverse acceleration, we need to know if we are braking.
    // Alternatively, we could do the drive forward logic here too instead of
    // in a separate nested Manoeuvre (GoForwardManoeuvre).
    private void turn(float delta) {
        if (turnDirection == WorldSpatial.RelativeDirection.LEFT) {
            controller.turnLeft(delta);
        } else {
            controller.turnRight(delta);
        }
    }
    
    // Limits us to turns < 180 degrees right now.
    // Thanks https://stackoverflow.com/questions/7570808/how-do-i-calculate-the-difference-of-two-angle-measures
    private float difference(float startAngle2, float f) {
        float phi = Math.abs(f - startAngle2) % 360;       // This is either the distance or 360 - distance
        float difference = phi > 180 ? 360 - phi : phi;
        return difference;
    }
}
