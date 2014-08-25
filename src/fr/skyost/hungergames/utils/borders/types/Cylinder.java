package fr.skyost.hungergames.utils.borders.types;

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.patterns.SingleBlockPattern;

import fr.skyost.hungergames.utils.borders.BorderParams;
import fr.skyost.hungergames.utils.borders.WorldEditBorder;
import fr.skyost.hungergames.utils.borders.exceptions.WorldEditMaxChangedBlocksException;

public class Cylinder extends WorldEditBorder {
	
	@Override
	protected void createBorder(final BorderParams params) throws WorldEditMaxChangedBlocksException {
		try {
			this.newEditSession(params).makeCylinder(new Vector(params.getX(), 0, params.getZ()), new SingleBlockPattern(new BaseBlock(params.getBlockID(), params.getBlockMeta())), params.getRadius(), params.getRadius(), 256, false);
		}
		catch(final MaxChangedBlocksException ex) {
			throw new WorldEditMaxChangedBlocksException();
		}
	}
	
	@Override
	public Type getType() {
		return Type.CYLINDER;
	}
	
	@Override
	public String getDescription() {
		return "Creates a cylinder wall around the map.";
	}
	
}
