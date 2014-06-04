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
	protected void createBorder(final BorderParams params) throws WorldEditMaxChangedBlocksException {
		try {
			Vector pos1 = new Vector(params.getX() + params.getRadius(), 256, params.getZ() + params.getRadius());
			Vector pos2 = new Vector(params.getX() - params.getRadius(), 0, params.getZ() - params.getRadius());
			this.newEditSession(params).makeCuboidWalls(new CuboidRegion(pos1, pos2), new SingleBlockPattern(new BaseBlock(params.getBlockID(), params.getBlockMeta())));
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
