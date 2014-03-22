package fr.skyost.hungergames.utils.borders;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;

import fr.skyost.hungergames.utils.borders.Border.Type;
import fr.skyost.hungergames.utils.borders.exceptions.BorderTypeNotFoundException;
import fr.skyost.hungergames.utils.borders.exceptions.WorldEditMaxChangedBlocksException;
import fr.skyost.hungergames.utils.borders.exceptions.WorldNotFoundException;
import fr.skyost.hungergames.utils.borders.types.Cylinder;
import fr.skyost.hungergames.utils.borders.types.Invisible;
import fr.skyost.hungergames.utils.borders.types.Roof;
import fr.skyost.hungergames.utils.borders.types.Square;

/**
 * The border creator.
 * 
 * @author <b>Original :</b> ghowden.
 * <br><b>Modified version :</b> Skyost.
 */

public class BorderCreator {
	
	/**
	 * Build a border using the specified params.
	 * 
	 * @param params The border params.
	 * @throws WorldEditMaxChangedBlocksException If an error occured with WorldEdit (if it is a <b>WorldEditBorder</b>).
	 * @throws WorldNotFoundException If the world was not found.
	 * @throws BorderTypeNotFoundException If the specified type was not found.
	 */
	
	public BorderCreator(final BorderParams params) throws WorldEditMaxChangedBlocksException, WorldNotFoundException, BorderTypeNotFoundException {
		final World world = Bukkit.getWorld(params.getWorldName());
		if(world == null) {
			throw new WorldNotFoundException();
		}
		final Border border = getBorderByType(params.getType());
		if(border == null) {
			throw new BorderTypeNotFoundException();
		}
		border.createBorder(params);
	}
	
	/**
	 * Get available border types.
	 * 
	 * @return Available border types.
	 */
	
	public static final List<String> getBorderTypes() {
		final ArrayList<String> result = new ArrayList<String>();
		for(final Type type : Type.values()) {
			result.add(type.name());
		}
		return result;
	}
	
	/**
	 * Get a border by its type.
	 * 
	 * @param type The border's type.
	 * 
	 * @return Your border. Not a <b>WorldEditBorder</b>.
	 */
	
	public static final Border getBorderByType(final Type type) {
		switch(type) {
		case CYLINDER:
			return new Cylinder();
		case ROOF:
			return new Roof();
		case SQUARE:
			return new Square();
		case INVISIBLE:
			return new Invisible();
		default:
			return null;
		}
	}
	
}
