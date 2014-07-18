package fr.skyost.hungergames.utils.borders;

import java.io.Serializable;

import fr.skyost.hungergames.utils.borders.Border.Type;

/**
 * Holds all informations about a border.
 *
 * @author <b>Original :</b> ghowden.
 * </br><b>Modified :</b> Skyost.
 */

public class BorderParams implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String worldName;
	private final int x;
	private final int z;
	private int radius;
	private Type type;
	private int blockId;
	private int blockMeta;
	
	/**
	 * Create some new params.
	 * 
	 * @param worldName The world's name.
	 * @param x The X coord.
	 * @param z The Z coord.
	 * @param radius The radius.
	 * @param type The border type.
	 * @param blockId The block ID.
	 * @param blockMeta The block meta.
	 */
	
	public BorderParams(final String worldName, final int x, final int z, final int radius, final Type type, final int blockId, final int blockMeta) {
		this.worldName = worldName;
		this.x = x;
		this.z = z;
		this.radius = radius;
		this.type = type;
		this.blockId = blockId;
		this.blockMeta = blockMeta;
	}
	
	public final String getWorldName() {
		return worldName;
	}
	
	/**
	 * Get the X coord.
	 * 
	 * @return The X coord.
	 */
	
	public final int getX() {
		return x;
	}
	
	/**
	 * Get the Z coord.
	 * 
	 * @return The Z coord.
	 */
	
	public final int getZ() {
		return z;
	}
	
	/**
	 * Get the radius.
	 * 
	 * @return The radius.
	 */
	
	public final int getRadius() {
		return radius;
	}
	
	/**
	 * Get the border type.
	 * 
	 * @return The border type.
	 */
	
	public final Type getType() {
		return type;
	}
	
	/**
	 * Get the block ID.
	 * 
	 * @return The block ID.
	 */
	
	public final int getBlockID() {
		return blockId;
	}
	
	/**
	 * Get the block meta.
	 * 
	 * @return The block meta.
	 */
	
	public final int getBlockMeta() {
		return blockMeta;
	}
	
}
