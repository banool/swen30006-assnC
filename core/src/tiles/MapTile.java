package tiles;

/**
 * Represents a single MapTile
 * 
 *
 */
public class MapTile {
	
	public static final String tileNameSpace = "tiles.";
	
	public enum Type {WALL, UTILITY, TRAP, EMPTY, ROAD, START, FINISH};

	protected Type tileType;
	
	public MapTile(Type tileType) {
		this.tileType = tileType;
	}

	public Type getType() {
		return tileType;
	}
	
	public Boolean isType(Type tileType) {
		return this.tileType == tileType;
	}
}
