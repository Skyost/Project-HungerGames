package fr.skyost.hungergames.utils.borders.types;

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.patterns.SingleBlockPattern;
import com.sk89q.worldedit.regions.CuboidRegion;

import fr.skyost.hungergames.utils.borders.BorderParams;
import fr.skyost.hungergames.utils.borders.WorldEditBorder;
import fr.skyost.hungergames.utils.borders.exceptions.WorldEditMaxChangedBlocksException;

public class Square extends WorldEditBorder {
	
	@Override
	protected void createBorder(BorderParams bp) throws WorldEditMaxChangedBlocksException {
		try {
			Vector pos1 = new Vector(bp.getX() + bp.getRadius(), 256, bp.getZ() + bp.getRadius());
			Vector pos2 = new Vector(bp.getX() - bp.getRadius(), 0, bp.getZ() - bp.getRadius());
			newEditSession(bp).makeCuboidWalls(new CuboidRegion(pos1, pos2), new SingleBlockPattern(new BaseBlock(bp.getBlockID(), bp.getBlockMeta())));
		}
		catch(MaxChangedBlocksException ex) {
			new WorldEditMaxChangedBlocksException();
		}
	}
	
	@Override
	public Type getType() {
		return Type.SQUARE;
	}
	
	@Override
	public String getDescription() {
		return "Creates a square wall around the map.";
	}
}
