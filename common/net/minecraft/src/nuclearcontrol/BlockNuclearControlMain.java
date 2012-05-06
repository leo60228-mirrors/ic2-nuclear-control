package net.minecraft.src.nuclearcontrol;

import java.util.ArrayList;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Facing;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.mod_IC2NuclearControl;
import net.minecraft.src.forge.IConnectRedstone;
import net.minecraft.src.forge.ITextureProvider;
import net.minecraft.src.ic2.api.IWrenchable;

public class BlockNuclearControlMain extends BlockContainer implements ITextureProvider, IConnectRedstone
{
    public static final int DAMAGE_THERMAL_MONITOR = 0;
    public static final int DAMAGE_INDUSTRIAL_ALARM = 1;
    public static final int DAMAGE_HOWLER_ALARM = 2;
    
    public static final float[][] blockSize = {
        {0.0625F, 0, 0.0625F, 0.9375F, 0.4375F, 0.9375F},//Thermal Monitor
        {0.125F, 0, 0.125F, 0.875F, 0.4375F, 0.875F},//Industrial  Alarm
        {0.125F, 0, 0.125F, 0.875F, 0.4375F, 0.875F}//Howler  Alarm
        
    };
    
    private static final byte[][][] sideMapping = 
        {
            {//Thermal Monitor
                {1, 0, 17, 17, 17, 17},
                {0, 1, 17, 17, 17, 17},
                {17, 17, 1, 0, 33, 33},
                {17, 17, 0, 1, 33, 33},
                {33, 33, 33, 33, 1, 0},
                {33, 33, 33, 33, 0, 1}
            },
            {//Industrial Alarm
                {4, 3, 5, 5, 5, 5},
                {3, 4, 5, 5, 5, 5},
                {5, 5, 4, 3, 6, 6},
                {5, 5, 3, 4, 6, 6},
                {6, 6, 6, 6, 4, 3},
                {6, 6, 6, 6, 3, 4}
            },
            {//Howler Alarm
                {8, 7, 9, 9, 9, 9},
                {7, 8, 9, 9, 9, 9},
                {9, 9, 8, 7, 10, 10},
                {9, 9, 7, 8, 10, 10},
                {10, 10, 10, 10, 8, 7},
                {10, 10, 10, 10, 7, 8}
            }
        };
    

    public BlockNuclearControlMain(int i, int j)
    {
        super(i, j, Material.iron);
    }

    @Override
    public int getRenderType()
    {
        return mod_IC2NuclearControl.modelId;
    }
    
    @Override
    public boolean isBlockNormalCube(World world, int x, int y, int z)
    {
        return false;
    }

    @Override
    public String getTextureFile()
    {
        return "/img/texture_thermo.png";
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }
    
    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
    	for (int face = 0; face < 6; face++){
    		int side = Facing.faceToSide[face];
    		if(world.isBlockSolidOnSide(x + Facing.offsetsXForSide[side], 
    									y + Facing.offsetsYForSide[side], 
    									z + Facing.offsetsZForSide[side], face))
    			return true;
    	}
    	return false;
    }
    
    /**
     * Called when a block is placed using an item. Used often for taking the facing and figuring out how to position
     * the item.
     */
    @Override
    public void onBlockPlaced(World world, int x, int y, int z, int face)
    {
        int side = Facing.faceToSide[face];

        if(world.isBlockSolidOnSide(x + Facing.offsetsXForSide[side], 
				y + Facing.offsetsYForSide[side], 
				z + Facing.offsetsZForSide[side], face))
        {
            TileEntity tileentity = world.getBlockTileEntity(x, y, z);
            if(tileentity instanceof IWrenchable)
            {
            	((IWrenchable)tileentity).setFacing((short)side);
            }
        }
    }
    
    /**
     * Called whenever the block is added into the world.
     */
    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
    	for (int face = 0; face < 6; face++){
    		int side = Facing.faceToSide[face];
    		if(world.isBlockSolidOnSide(x + Facing.offsetsXForSide[side], 
    									y + Facing.offsetsYForSide[side], 
    									z + Facing.offsetsZForSide[side], face))
    		{
                TileEntity tileentity = world.getBlockTileEntity(x, y, z);
                if(tileentity instanceof IWrenchable)
                {
                	((IWrenchable)tileentity).setFacing((short)side);
                }
                break;
    		}
    	}
        dropBlockIfCantStay(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighbor)
    {
        int side = 0;
        TileEntity tileentity = world.getBlockTileEntity(x, y, z);
        if(tileentity instanceof IWrenchable)
        {
        	side = ((IWrenchable)tileentity).getFacing();
        }
		if(!world.isBlockSolidOnSide(x + Facing.offsetsXForSide[side], 
				y + Facing.offsetsYForSide[side], 
				z + Facing.offsetsZForSide[side], Facing.faceToSide[side]))
		{
			if(!world.isRemote){
				dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			}
            world.setBlockWithNotify(x, y, z, 0);
		}
		else
		{
		    RedstoneHelper.checkPowered(world, tileentity);
		}
    }

    /**
     * Tests if the block can remain at its current location and will drop as an item if it is unable to stay. Returns
     * True if it can stay and False if it drops. Args: world, x, y, z
     */
    private boolean dropBlockIfCantStay(World world, int x, int y, int z)
    {
        if (!canPlaceBlockAt(world, x, y, z))
        {
            if (world.getBlockId(x, y, z) == blockID)
            {
                dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
                world.setBlockWithNotify(x, y, z, 0);
            }
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public void setBlockBoundsForItemRender()
    {
        setBlockBounds( blockSize[0][0], blockSize[0][1], blockSize[0][2], 
                        blockSize[0][3], blockSize[0][4], blockSize[0][5]);
    }
    
    
    /**
     * Updates the blocks bounds based on its current state.
     */
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z)
    {
        int blockType = blockAccess.getBlockMetadata(x, y, z);
        
        if(blockType>=blockSize.length)
        {
            blockType = 0;
        }
            
        float baseX1 = blockSize[blockType][0];
        float baseY1 = blockSize[blockType][1];
        float baseZ1 = blockSize[blockType][2];
        
        float baseX2 = blockSize[blockType][3];
        float baseY2 = blockSize[blockType][4];
        float baseZ2 = blockSize[blockType][5]; 

        float tmp;
        
        int side = 0;
        TileEntity tileentity = blockAccess.getBlockTileEntity(x, y, z);
        if(tileentity instanceof IWrenchable)
        {
        	side = ((IWrenchable)tileentity).getFacing();
        }
        switch (side)
        {
            case 1:
            	baseY1 = 1 - baseY1;
            	baseY2 = 1 - baseY2;
                break;

            case 2:
            	tmp = baseY1;
            	baseY1 = baseZ1;
            	baseZ1 = tmp;

            	tmp = baseY2;
            	baseY2 = baseZ2;
            	baseZ2 = tmp;
                break;

            case 3:
            	tmp = baseY1;
            	baseY1 = baseZ1;
            	baseZ1 = 1 - tmp;

            	tmp = baseY2;
            	baseY2 = baseZ2;
            	baseZ2 = 1 - tmp;
                break;

            case 4:
            	tmp = baseY1;
            	baseY1 = baseX1;
            	baseX1 = tmp;

            	tmp = baseY2;
            	baseY2 = baseX2;
            	baseX2 = tmp;
                break;

            case 5:
            	tmp = baseY1;
            	baseY1 = baseX1;
            	baseX1 = 1 - tmp;

            	tmp = baseY2;
            	baseY2 = baseX2;
            	baseX2 = 1 - tmp;
                break;
        }
        setBlockBounds( Math.min(baseX1, baseX2), Math.min(baseY1, baseY2), Math.min(baseZ1, baseZ2), 
        				Math.max(baseX1, baseX2), Math.max(baseY1, baseY2), Math.max(baseZ1, baseZ2) );
    }

    public String getInvName()
    {
        return "IC2 Thermo";
    }

    public boolean canProvidePower()
    {
        return true;
    }

    public boolean blockActivated(World world, int x, int y, int z, EntityPlayer entityplayer)
    {
        int blockType = world.getBlockMetadata(x, y, z);
        if(blockType != DAMAGE_THERMAL_MONITOR)
        {
            return false;
        }
        if (entityplayer.isSneaking())
        {
            return false;
        }
        mod_IC2NuclearControl.launchGui(world, x, y, z, entityplayer);
        return true;
    }

    public boolean isIndirectlyPoweringTo(World world, int i, int j, int k, int l)
    {
        return false;
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public boolean isPoweringTo(IBlockAccess iblockaccess, int x, int y, int z, int direction)
    {
        TileEntity tileentity = iblockaccess.getBlockTileEntity(x, y, z);
        if(!(tileentity instanceof TileEntityIC2Thermo))
            return false;
        
        int targetX = x;
        int targetY = y;
        int targetZ = z;
        switch (direction) {
		case 0:
			targetY++;
			break;
		case 1:
			targetY--;
			break;
		case 2:
			targetZ++;
			break;
		case 3:
			targetZ--;
			break;
		case 4:
			targetX++;
			break;
		case 5:
			targetX--;
			break;
		}
        TileEntity targetEntity = iblockaccess.getBlockTileEntity(targetX, targetY, targetZ);
        if (targetEntity!=null && (NuclearHelper.getReactorAt(tileentity.worldObj, targetX, targetY, targetZ)!=null || 
    		NuclearHelper.getReactorChamberAt(tileentity.worldObj, targetX, targetY, targetZ)!=null))
        {
            return false;
        }
    	return ((TileEntityIC2Thermo)tileentity).getOnFire() == 1;
    }

    @Override
    public int getBlockTextureFromSideAndMetadata(int side, int metadata)
    {
    	int texture = sideMapping[metadata][0][side];
		return texture;
    }
    
    public int getBlockTexture(IBlockAccess blockaccess, int x, int y, int z, int side)
    {
        TileEntity tileentity = blockaccess.getBlockTileEntity(x, y, z);
        boolean isThermo = tileentity instanceof TileEntityIC2Thermo;
        boolean isIndustrialAlarm = tileentity instanceof TileEntityIndustrialAlarm;
        int metaSide = 0;
        if(tileentity instanceof IWrenchable)
        {
        	metaSide = ((IWrenchable)tileentity).getFacing();
        }
        int blockType = blockaccess.getBlockMetadata(x, y, z);
        int texture = sideMapping[blockType][metaSide][side];
        if(isIndustrialAlarm)
        {
            int light = ((TileEntityIndustrialAlarm)tileentity).lightLevel;
            switch(light)
            {
                case 7: 
                    texture +=16;
                    break;
                case 15: 
                    texture += 32;
                    break;
            }
            return texture;
        }
        if(texture!=0 || !isThermo)
    		return texture;
    	byte fireState = ((TileEntityIC2Thermo)tileentity).getOnFire();
    	switch (fireState)
        {
            case 1:
                texture = 16;
                break;
            case 0:
                texture = 0;
                break;
            default:
                texture = 32;
                break;
        }
	    return texture;
    }

    @Override
    public TileEntity getBlockEntity()
    {
        return null;
    }

    @Override
    public TileEntity getBlockEntity(int metadata)
    {
        switch (metadata)
        {
        case DAMAGE_THERMAL_MONITOR:
            return new TileEntityIC2Thermo();
        case DAMAGE_INDUSTRIAL_ALARM:
            return new TileEntityIndustrialAlarm();
        case DAMAGE_HOWLER_ALARM:
            return new TileEntityHowlerAlarm();
        }
        return null;
    }
    
    @Override
    protected int damageDropped(int i)
    {
        if(i >0 && i<=2)
            return i;
        else
            return 0;
    }
    
    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) 
    {
        TileEntity entity = world.getBlockTileEntity(x, y, z);
        if(entity instanceof TileEntityIndustrialAlarm)
        {
            return ((TileEntityIndustrialAlarm)entity).lightLevel;
        }
        return lightValue[blockID];
    }
    
    

    @Override
    public void addCreativeItems(ArrayList arraylist)
    {
        arraylist.add(new ItemStack(this, 1, DAMAGE_THERMAL_MONITOR));
        arraylist.add(new ItemStack(this, 1, DAMAGE_INDUSTRIAL_ALARM));
        arraylist.add(new ItemStack(this, 1, DAMAGE_HOWLER_ALARM));
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int direction)
    {
        return true;
    }

}
