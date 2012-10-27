package shedar.mods.ic2.nuclearcontrol;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Facing;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import ic2.api.IWrenchable;
import net.minecraftforge.common.ForgeDirection;

public class BlockNuclearControlMain extends BlockContainer
{
    public static final int DAMAGE_THERMAL_MONITOR = 0;
    public static final int DAMAGE_INDUSTRIAL_ALARM = 1;
    public static final int DAMAGE_HOWLER_ALARM = 2;
    public static final int DAMAGE_REMOTE_THERMO = 3;
    public static final int DAMAGE_INFO_PANEL = 4;
    public static final int DAMAGE_INFO_PANEL_EXTENDER = 5;
    public static final int DAMAGE_ENERGY_COUNTER = 6;
    public static final int DAMAGE_AVERAGE_COUNTER = 7;
    public static final int DAMAGE_RANGE_TRIGGER = 8;

    public static final int DAMAGE_MAX = 8;
    
    public static final float[][] blockSize = {
        {0.0625F, 0, 0.0625F, 0.9375F, 0.4375F, 0.9375F},//Thermal Monitor
        {0.125F, 0, 0.125F, 0.875F, 0.4375F, 0.875F},//Industrial  Alarm
        {0.125F, 0, 0.125F, 0.875F, 0.4375F, 0.875F},//Howler  Alarm
        {0, 0, 0, 1, 1, 1},//Remote Thermo
        {0, 0, 0, 1, 1, 1},//Info Panel
        {0, 0, 0, 1, 1, 1},//Info Panel Extender
        {0, 0, 0, 1, 1, 1},//Energy Counter
        {0, 0, 0, 1, 1, 1},//Average Counter
        {0, 0, 0, 1, 1, 1}//Range Trigger
        
    };
    
    private static final boolean[] solidBlockRequired =
        {true, true, true, false, false, false, false, false, false};
    
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
            },
            {//Remote Thermo
                {23, 25, 24, 24, 24, 24},
                {25, 23, 24, 24, 24, 24},
                {24, 24, 23, 25, 24, 24},
                {24, 24, 25, 23, 24, 24},
                {24, 24, 24, 24, 23, 25},
                {24, 24, 24, 24, 25, 23}
            },
            {//Info Panel
                {23, 80, 24, 24, 24, 24},
                {80, 23, 24, 24, 24, 24},
                {24, 24, 23, 80, 24, 24},
                {24, 24, 80, 23, 24, 24},
                {24, 24, 24, 24, 23, 80},
                {24, 24, 24, 24, 80, 23}
            },
            {//Info Panel Extender
                {39, 80, 40, 40, 40, 40},
                {80, 39, 40, 40, 40, 40},
                {40, 40, 39, 80, 40, 40},
                {40, 40, 80, 39, 40, 40},
                {40, 40, 40, 40, 39, 80},
                {40, 40, 40, 40, 80, 39}
            },
            {//Energy Counter
                {42, 41, 42, 42, 42, 42},
                {41, 42, 42, 42, 42, 42},
                {42, 42, 42, 41, 42, 42},
                {42, 42, 41, 42, 42, 42},
                {42, 42, 42, 42, 42, 41},
                {42, 42, 42, 42, 41, 42}
            },
            {//Average Counter
                {43, 41, 43, 43, 43, 43},
                {41, 43, 43, 43, 43, 43},
                {43, 43, 43, 41, 43, 43},
                {43, 43, 41, 43, 43, 43},
                {43, 43, 43, 43, 43, 41},
                {43, 43, 43, 43, 41, 43}
            },
            {//Range Trigger
                {23, 27, 24, 24, 24, 24},
                {27, 23, 24, 24, 24, 24},
                {24, 24, 23, 27, 24, 24},
                {24, 24, 27, 23, 24, 24},
                {24, 24, 24, 24, 23, 27},
                {24, 24, 24, 24, 27, 23}
            },
        };
    

    public BlockNuclearControlMain(int i, int j)
    {
        super(i, j, Material.iron);
        setHardness(0.5F);
        setRequiresSelfNotify();
        setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
    public int getRenderType()
    {
        return IC2NuclearControl.instance.modelId;
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
    public boolean canPlaceBlockAtlocal(World world, int x, int y, int z)
    {
    	for (int face = 0; face < 6; face++){
    		int side = Facing.faceToSide[face];
    		if(world.isBlockSolidOnSide(x + Facing.offsetsXForSide[side], 
    									y + Facing.offsetsYForSide[side], 
    									z + Facing.offsetsZForSide[side], ForgeDirection.getOrientation(face)))
    			return true;
    	}
    	return false;
    }
    
    /**
     * called before onBlockPlacedBy by ItemBlock and ItemReed
     */
    @Override
    public void updateBlockMetadata(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        super.updateBlockMetadata(world, x, y, z, side, hitX, hitY, hitZ);
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        int metadata = world.getBlockMetadata(x, y, z);
        if(metadata > DAMAGE_MAX)
        {
            metadata = 0;
        }

        if(!solidBlockRequired[metadata] || world.isBlockSolidOnSide(x + Facing.offsetsXForSide[Facing.faceToSide[side]], 
				y + Facing.offsetsYForSide[Facing.faceToSide[side]], 
				z + Facing.offsetsZForSide[Facing.faceToSide[side]], dir))
        {
            TileEntity tileentity = world.getBlockTileEntity(x, y, z);
            if(tileentity instanceof IWrenchable)
            {
            	((IWrenchable)tileentity).setFacing((short)side);
            }
        }
    }
    
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving player) 
    {
        TileEntity block = world.getBlockTileEntity(x, y, z);
        int metadata = world.getBlockMetadata(x, y, z);
        if(metadata > DAMAGE_MAX)
        {
            metadata = 0;
        }
        if (player != null && !solidBlockRequired[metadata] && block instanceof IWrenchable) 
        {
            IWrenchable wrenchable = (IWrenchable)block;
            int rotationSegment = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            if (player.rotationPitch >= 65) 
            {
                wrenchable.setFacing((short)1);
            } 
            else if (player.rotationPitch <= -65) 
            {
                wrenchable.setFacing((short)0);
            } 
            else 
            {
                switch (rotationSegment) 
                {
                case 0: wrenchable.setFacing((short)2); break;
                case 1: wrenchable.setFacing((short)5); break;
                case 2: wrenchable.setFacing((short)3); break;
                case 3: wrenchable.setFacing((short)4); break;
                default:
                    wrenchable.setFacing((short)0); break;
                }
            }
        }        
    }
    
    /**
     * Called whenever the block is added into the world.
     */
    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);
        int metadata = world.getBlockMetadata(x, y, z);
        if(metadata > DAMAGE_MAX)
        {
            metadata = 0;
        }
        if(solidBlockRequired[metadata])
    	for (int face = 0; face < 6; face++){
    		int side = Facing.faceToSide[face];
    		if(world.isBlockSolidOnSide(x + Facing.offsetsXForSide[side], 
    									y + Facing.offsetsYForSide[side], 
    									z + Facing.offsetsZForSide[side],  ForgeDirection.getOrientation(face)))
    		{
                TileEntity tileentity = world.getBlockTileEntity(x, y, z);
                if(tileentity instanceof IWrenchable)
                {
                	((IWrenchable)tileentity).setFacing((short)face);
                }
                break;
    		}
    	}
        dropBlockIfCantStay(world, x, y, z);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int i1, int i2)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if(tileEntity instanceof TileEntityHowlerAlarm)
        {
            ((TileEntityHowlerAlarm)tileEntity).setPowered(false);
        }
        if (!world.isRemote && tileEntity instanceof IInventory)
        {
            IInventory inventory = (IInventory)tileEntity;
            float range = 0.7F;

            for (int i = 0; i < inventory.getSizeInventory(); i++)
            {
                ItemStack itemStack = inventory.getStackInSlot(i);

                if (itemStack != null)
                {
                    double dx = (double)(world.rand.nextFloat() * range) + (double)(1.0F - range) * 0.5D;
                    double dy = (double)(world.rand.nextFloat() * range) + (double)(1.0F - range) * 0.5D;
                    double dz = (double)(world.rand.nextFloat() * range) + (double)(1.0F - range) * 0.5D;
                    EntityItem item = new EntityItem(world, (double)x + dx, (double)y + dy, (double)z + dz, itemStack);
                    item.delayBeforeCanPickup = 10;
                    world.spawnEntityInWorld(item);
                    inventory.setInventorySlotContents(i, null);
                }
            }
        }
        super.breakBlock(world, x, y, z, i1, i2);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighbor)
    {
        int side = 0;
        TileEntity tileentity = world.getBlockTileEntity(x, y, z);
        if(tileentity instanceof IWrenchable)
        {
        	side = Facing.faceToSide[((IWrenchable)tileentity).getFacing()];
        }
        int metadata = world.getBlockMetadata(x, y, z);
        
		if( solidBlockRequired[Math.min(metadata, solidBlockRequired.length-1)] && 
	        !world.isBlockSolidOnSide(x + Facing.offsetsXForSide[side], 
				y + Facing.offsetsYForSide[side], 
				z + Facing.offsetsZForSide[side],  ForgeDirection.getOrientation(side).getOpposite()))
		{
			if(!world.isRemote){
				dropBlockAsItem(world, x, y, z, metadata, 0);
			}
            world.setBlockWithNotify(x, y, z, 0);
		}
		else
		{
		    RedstoneHelper.checkPowered(world, tileentity);
		}
		super.onNeighborBlockChange(world, x, y, z, neighbor);
    }
    
    public static boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side, int metadata)
    {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        if(!solidBlockRequired[Math.min(metadata, solidBlockRequired.length-1)])
        {
            return true;
        }
        return (dir == ForgeDirection.DOWN  && world.isBlockSolidOnSide(x, y + 1, z, ForgeDirection.DOWN )) ||
                (dir == ForgeDirection.UP    && world.isBlockSolidOnSide(x, y - 1, z, ForgeDirection.UP   )) ||
                (dir == ForgeDirection.NORTH && world.isBlockSolidOnSide(x, y, z + 1, ForgeDirection.NORTH)) ||
                (dir == ForgeDirection.SOUTH && world.isBlockSolidOnSide(x, y, z - 1, ForgeDirection.SOUTH)) ||
                (dir == ForgeDirection.WEST  && world.isBlockSolidOnSide(x + 1, y, z, ForgeDirection.WEST )) ||
                (dir == ForgeDirection.EAST  && world.isBlockSolidOnSide(x - 1, y, z, ForgeDirection.EAST ));
    }

    /**
     * Tests if the block can remain at its current location and will drop as an item if it is unable to stay. Returns
     * True if it can stay and False if it drops. Args: world, x, y, z
     */
    private boolean dropBlockIfCantStay(World world, int x, int y, int z)
    {
        int metadata = world.getBlockMetadata(x, y, z);
        if(!solidBlockRequired[Math.min(metadata, solidBlockRequired.length-1)])
        {
            return true;
        }
        if (!canPlaceBlockAtlocal(world, x, y, z))
        {
            if (world.getBlockId(x, y, z) == blockID)
            {
                dropBlockAsItem(world, x, y, z, metadata, 0);
                world.setBlockWithNotify(x, y, z, 0);
            }
            return false;
        }
        else
        {
            return true;
        }
    }
    
    /**
     * Updates the blocks bounds based on its current state.
     */
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z)
    {
        int blockType = blockAccess.getBlockMetadata(x, y, z);
        
        if(blockType > DAMAGE_MAX)
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
        	side = Facing.faceToSide[((IWrenchable)tileentity).getFacing()];
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

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i1, float f1, float f2, float f3)
    {
        int blockType = world.getBlockMetadata(x, y, z);
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (player!=null && player.isSneaking())
        {
            if(tileEntity!=null && tileEntity instanceof IRotation && player.getCurrentEquippedItem()!=null
                    && (player.getCurrentEquippedItem().itemID == IC2NuclearControl.instance.IC2WrenchId ||
                            player.getCurrentEquippedItem().itemID == IC2NuclearControl.instance.IC2ElectricWrenchId ))
            {
                ((IRotation)tileEntity).rotate();
                return true;
            }
            return false;
        }
        switch(blockType)
        {
            case DAMAGE_INDUSTRIAL_ALARM:
            case DAMAGE_HOWLER_ALARM:
            case DAMAGE_THERMAL_MONITOR:
            case DAMAGE_REMOTE_THERMO:
            case DAMAGE_INFO_PANEL:
            case DAMAGE_ENERGY_COUNTER:
            case DAMAGE_AVERAGE_COUNTER:
            case DAMAGE_RANGE_TRIGGER:
                if(player instanceof EntityPlayerMP)
                    player.openGui(IC2NuclearControl.instance, blockType, world, x, y, z);
                return true;
            default:
                return false;
        }
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
        if(!(tileentity instanceof TileEntityIC2Thermo) && !(tileentity instanceof TileEntityRangeTrigger))
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
        if (tileentity instanceof TileEntityIC2Thermo && targetEntity!=null && 
            (NuclearHelper.getReactorAt(tileentity.worldObj, targetX, targetY, targetZ)!=null || 
    		NuclearHelper.getReactorChamberAt(tileentity.worldObj, targetX, targetY, targetZ)!=null))
        {
            return false;
        }
        if(tileentity instanceof TileEntityRemoteThermo)
        {
            TileEntityRemoteThermo thermo = (TileEntityRemoteThermo)tileentity;
            return thermo.getOnFire() >= thermo.getHeatLevel() ^ thermo.isInvertRedstone();
        }
        if(tileentity instanceof TileEntityRangeTrigger)
            return ((TileEntityRangeTrigger)tileentity).getOnFire() > 0 ^ ((TileEntityRangeTrigger)tileentity).isInvertRedstone();
    	return ((TileEntityIC2Thermo)tileentity).getOnFire() > 0 ^ ((TileEntityIC2Thermo)tileentity).isInvertRedstone();
    }

    @Override
    public int getBlockTextureFromSideAndMetadata(int side, int metadata)
    {
        if(metadata > DAMAGE_MAX)
        {
            metadata = 0;
        }
    	int texture = sideMapping[metadata][0][side];
		return texture;
    }
    
    @Override
    public int getBlockTexture(IBlockAccess blockaccess, int x, int y, int z, int side)
    {
        TileEntity tileentity = blockaccess.getBlockTileEntity(x, y, z);
        int metaSide = 0;
        if(tileentity instanceof IWrenchable)
        {
        	metaSide = Facing.faceToSide[((IWrenchable)tileentity).getFacing()];
        }
        int blockType = blockaccess.getBlockMetadata(x, y, z);
        if(blockType > DAMAGE_MAX)
        {
            blockType = 0;
        }
        int texture = sideMapping[blockType][metaSide][side];
        
        if(tileentity instanceof ITextureHelper)
        {
            texture = ((ITextureHelper)tileentity).modifyTextureIndex(texture); 
        }
	    return texture;
    }

    @Override
    public int damageDropped(int i)
    {
        if(i > 0 && i <= DAMAGE_MAX)
            return i;
        else
            return 0;
    }
    
    @Override
    public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) 
    {
        int metadata = world.getBlockMetadata(x, y, z);
        return !solidBlockRequired[metadata];
        
    }
    
    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) 
    {
        TileEntity entity = world.getBlockTileEntity(x, y, z);
        if(entity instanceof TileEntityIndustrialAlarm)
        {
            return ((TileEntityIndustrialAlarm)entity).lightLevel;
        }
        else if(entity instanceof TileEntityInfoPanel)
        {
            if(((TileEntityInfoPanel)entity).powered)
                return 7;
            else
                return 0;
        }
        else if(entity instanceof TileEntityInfoPanelExtender)
        {
            TileEntityInfoPanelExtender extender = (TileEntityInfoPanelExtender)entity; 
            if(extender.getScreen()!=null)
            {
                TileEntityInfoPanel core = extender.getScreen().getCore(); 
                if(core!=null && core.powered)
                    return 7;
                else
                    return 0;
            }
        }
        return lightValue[blockID];
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void getSubBlocks(int id, CreativeTabs tab, List itemList)
    {
        itemList.add(new ItemStack(this, 1, DAMAGE_THERMAL_MONITOR));
        itemList.add(new ItemStack(this, 1, DAMAGE_INDUSTRIAL_ALARM));
        itemList.add(new ItemStack(this, 1, DAMAGE_HOWLER_ALARM));
        itemList.add(new ItemStack(this, 1, DAMAGE_REMOTE_THERMO));
        itemList.add(new ItemStack(this, 1, DAMAGE_INFO_PANEL));
        itemList.add(new ItemStack(this, 1, DAMAGE_INFO_PANEL_EXTENDER));
        itemList.add(new ItemStack(this, 1, DAMAGE_ENERGY_COUNTER));
        itemList.add(new ItemStack(this, 1, DAMAGE_AVERAGE_COUNTER));
        itemList.add(new ItemStack(this, 1, DAMAGE_RANGE_TRIGGER));
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return null;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        switch (metadata)
        {
        case DAMAGE_THERMAL_MONITOR:
            return new TileEntityIC2Thermo();
        case DAMAGE_INDUSTRIAL_ALARM:
            return new TileEntityIndustrialAlarm();
        case DAMAGE_HOWLER_ALARM:
            return new TileEntityHowlerAlarm();
        case DAMAGE_REMOTE_THERMO:
            return new TileEntityRemoteThermo();
        case DAMAGE_INFO_PANEL:
            return new TileEntityInfoPanel();
        case DAMAGE_INFO_PANEL_EXTENDER:
            return new TileEntityInfoPanelExtender();
        case DAMAGE_ENERGY_COUNTER:
            return new TileEntityEnergyCounter();
        case DAMAGE_AVERAGE_COUNTER:
            return new TileEntityAverageCounter();
        case DAMAGE_RANGE_TRIGGER:
            return new TileEntityRangeTrigger();
        }
        return null;
    }
}
