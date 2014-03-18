package fr.skyost.hungergames.utils.borders;

/**
 * Holds all the information about a border
 * @author ghowden
 *
 */
public class BorderParams {
	
	private int x;
	private int z;
	private String worldName;
	private int blockID;
	private int blockMeta;
	private int radius;
	private String typeID;
	
	public BorderParams(final int x, final int z, final int radius, final String typeID, final String worldName, final int blockID, final int blockMeta) {
		this.x = x;
		this.z = z;
		this.radius = radius;
		this.typeID = typeID;
		this.worldName = worldName;
		this.blockID = blockID;
		this.blockMeta = blockMeta;
	}
	
	public final int getX() {
		return x;
	}
	
	public final void setX(final int x) {
		this.x = x;
	}
	
	public final int getZ() {
		return z;
	}
	
	public final void setZ(final int z) {
		this.z = z;
	}
	
	public final int getRadius() {
		return radius;
	}
	
	public final void setRadius(final int radius) {
		this.radius = radius;
	}
	
	public final String getTypeID() {
		return typeID;
	}
	
	public final void setTypeID(final String typeID) {
		this.typeID = typeID;
	}
	
	public final String getWorldName() {
		return worldName;
	}
	
	public final void setWorldName(final String worldName) {
		this.worldName = worldName;
	}
	
	public final int getBlockID() {
		return blockID;
	}
	
	public final void setBlockID(final int blockID) {
		this.blockID = blockID;
	}
	
	public final int getBlockMeta() {
		return blockMeta;
	}
	
	public final void setBlockMeta(final int blockMeta) {
		this.blockMeta = blockMeta;
	}
	
}
