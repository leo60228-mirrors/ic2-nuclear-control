package shedar.mods.ic2.nuclearcontrol;

import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.Tessellator;
import net.minecraft.src.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

@SideOnly(Side.CLIENT)
public class MainBlockRenderer implements ISimpleBlockRenderingHandler
{
    private int modelId;

    public MainBlockRenderer(int modelId)
    {
        this.modelId = modelId;
    }
    
    @Override
    public void renderInventoryBlock(Block block, int metadata, int model, RenderBlocks renderer)
    {
        if(model == modelId){
            float[] size = BlockNuclearControlMain.blockSize[metadata];
            block.setBlockBounds(size[0], size[1], size[2], size[3], size[4], size[5]);
            Tessellator tesselator = Tessellator.instance;
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            tesselator.startDrawingQuads();
            tesselator.setNormal(0.0F, -1.0F, 0.0F);
            renderer.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(0, metadata));
            tesselator.draw();
            tesselator.startDrawingQuads();
            tesselator.setNormal(0.0F, 1.0F, 0.0F);
            renderer.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(1, metadata));
            tesselator.draw();
            tesselator.startDrawingQuads();
            tesselator.setNormal(0.0F, 0.0F, -1.0F);
            renderer.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(2, metadata));
            tesselator.draw();
            tesselator.startDrawingQuads();
            tesselator.setNormal(0.0F, 0.0F, 1.0F);
            renderer.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(3, metadata));
            tesselator.draw();
            tesselator.startDrawingQuads();
            tesselator.setNormal(-1.0F, 0.0F, 0.0F);
            renderer.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(4, metadata));
            tesselator.draw();
            tesselator.startDrawingQuads();
            tesselator.setNormal(1.0F, 0.0F, 0.0F);
            renderer.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(5, metadata));
            tesselator.draw();
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        }
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int model, RenderBlocks renderer)
    {
        if(model == modelId)
        {
            TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
            if(tileEntity instanceof IRotation)
            {
                switch(((IRotation) tileEntity).getFacing())
                {
                    case 0:
                        renderer.uvRotateBottom = ((IRotation) tileEntity).getRotation();
                        break;
                    case 1:
                        renderer.uvRotateTop = ((IRotation) tileEntity).getRotation();
                        break;
                    case 2:
                        renderer.uvRotateEast = ((IRotation) tileEntity).getRotation();
                        break;
                    case 3:
                        renderer.uvRotateWest = ((IRotation) tileEntity).getRotation();
                        break;
                    case 4:
                        renderer.uvRotateNorth = ((IRotation) tileEntity).getRotation();
                        break;
                    case 5:
                        renderer.uvRotateSouth = ((IRotation) tileEntity).getRotation();
                        break;
                        
                }
            }
            renderer.renderStandardBlock(block, x, y, z);
            renderer.uvRotateBottom = 0;
            renderer.uvRotateEast = 0;
            renderer.uvRotateNorth= 0;
            renderer.uvRotateSouth = 0;
            renderer.uvRotateTop = 0;
            renderer.uvRotateWest = 0;
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldRender3DInInventory()
    {
        return true;
    }

    @Override
    public int getRenderId()
    {
        return IC2NuclearControl.instance.modelId;
    }

}
