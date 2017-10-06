package tiles;

import world.Car;

public abstract class TrapTile extends MapTile{

	public TrapTile() {
		super(MapTile.Type.TRAP);
	}
	
	public abstract void applyTo(Car car, float delta);
	
	public abstract boolean canAccelerate();
	
	public abstract boolean canTurn();

}
