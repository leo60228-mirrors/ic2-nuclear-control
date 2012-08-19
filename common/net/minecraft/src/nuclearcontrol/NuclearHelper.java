package net.minecraft.src.nuclearcontrol;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.ChunkPosition;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.ic2.api.IReactor;
import net.minecraft.src.ic2.api.IReactorChamber;

public class NuclearHelper {
	
	public static IReactor getReactorAt(World world, int x, int y, int z) 
	{
	    if(world == null)
	        return null;
		TileEntity entity = world.getBlockTileEntity(x, y, z);
		if(entity instanceof IReactor)
		    return (IReactor)entity;
		return null;
	}


	public static IReactorChamber getReactorChamberAt(World world, int x, int y, int z) 
	{
        if(world == null)
            return null;
		TileEntity entity = world.getBlockTileEntity(x, y, z);
		if(entity instanceof IReactorChamber)
		{
			return (IReactorChamber)entity;
		}
		return null;
	}

	public static IReactor getReactorAroundCoord(World world, int x, int y, int z) {
        if(world == null)
            return null;
		ChunkPosition[] around = { 
				new ChunkPosition(-1, 0, 0),
				new ChunkPosition( 1, 0, 0),
				new ChunkPosition( 0,-1, 0),
				new ChunkPosition( 0, 1, 0),
				new ChunkPosition( 0, 0,-1),
				new ChunkPosition( 0, 0, 1)
		};
		IReactor ent = null;
		for(int i=0;i<6 && ent == null;i++){
			ChunkPosition delta = around[i]; 
			ent = getReactorAt(world, x+delta.x, y+delta.y, z+delta.z);
		}
		return ent;
	}

	public static IReactorChamber getReactorChamberAroundCoord(World world, int x, int y, int z) 
	{
        if(world == null)
            return null;
		ChunkPosition[] around = { 
				new ChunkPosition(-1, 0, 0),
				new ChunkPosition( 1, 0, 0),
				new ChunkPosition( 0,-1, 0),
				new ChunkPosition( 0, 1, 0),
				new ChunkPosition( 0, 0,-1),
				new ChunkPosition( 0, 0, 1)
		};
		IReactorChamber ent = null;
		for(int i=0;i<6 && ent == null;i++){
			ChunkPosition delta = around[i]; 
			ent = getReactorChamberAt(world, x+delta.x, y+delta.y, z+delta.z);
		}
		return ent;
	}
	
	public static boolean isProducing(IReactor reactor)
	{
	    ChunkCoordinates position =  reactor.getPosition();
	    return !reactor.getWorld().isBlockIndirectlyGettingPowered(position.posX, position.posY, position.posZ);
	}
	
}
