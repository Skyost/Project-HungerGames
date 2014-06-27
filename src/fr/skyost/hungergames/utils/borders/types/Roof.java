package fr.skyost.hungergames.utils.borders.types;

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.patterns.SingleBlockPattern;

import fr.skyost.hungergames.utils.borders.BorderParams;
import fr.skyost.hungergames.utils.borders.WorldEditBorder;
import fr.skyost.hungergames.utils.borders.exceptions.WorldEditMaxChangedBlocksException;

public class Roof extends WorldEditBorder {
	
	@Override
	protected void createBorder(final BorderParams params) throws WorldEditMaxChangedBlocksException {
		try {
			this.newEditSession(params).makeCylinder(new Vector(params.getX(), 255, params.getZ()), new SingleBlockPattern(new BaseBlock(params.getBlockID(), params.getBlockMeta())), params.getRadius(), params.getRadius(), 1, true);
		}
		catch(final MaxChangedBlocksException ex) {
			new WorldEditMaxChangedBlocksException();
		}
	}
	
	@Override
	public Type getType() {
		return Type.ROOF;
	}
	
	@Override
	public String getDescription() {
		return "Creates a circular roof over the area.";
	}
	
}
