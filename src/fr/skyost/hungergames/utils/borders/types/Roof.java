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
	protected void createBorder(BorderParams bp) throws WorldEditMaxChangedBlocksException {
		try {
			newEditSession(bp).makeCylinder(new Vector(bp.getX(), 255, bp.getZ()), new SingleBlockPattern(new BaseBlock(bp.getBlockID(), bp.getBlockMeta())), bp.getRadius(), bp.getRadius(), 1, true);
		}
		catch(MaxChangedBlocksException ex) {
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
