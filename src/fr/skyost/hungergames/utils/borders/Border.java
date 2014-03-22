package fr.skyost.hungergames.utils.borders;

import fr.skyost.hungergames.utils.borders.exceptions.WorldEditMaxChangedBlocksException;

public abstract class Border {
	
	public enum Type {
		CYLINDER,
		ROOF,
		SQUARE,
		INVISIBLE;
	}
	
	/**
	 * Create a border.
	 * 
	 * @param params The border params.
	 * @throws WorldEditMaxChangedBlocksException Only for WorldEdit borders.
	 */
	
	protected abstract void createBorder(final BorderParams params) throws WorldEditMaxChangedBlocksException;
	
	/**
	 * Get the border type.
	 * 
	 * @return The border type.
	 */
	
	public abstract Type getType();
	
	/**
	 * Get the border description.
	 * 
	 * @return The border description.
	 */
	
	public abstract String getDescription();
	
	@Override
	public final boolean equals(Object o) {
		return o instanceof Border && ((Border) o).getType() == getType();
	}
}
