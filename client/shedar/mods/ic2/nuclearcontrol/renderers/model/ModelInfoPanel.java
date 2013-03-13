package shedar.mods.ic2.nuclearcontrol.renderers.model;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import shedar.mods.ic2.nuclearcontrol.panel.Screen;
import shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityAdvancedInfoPanel;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelInfoPanel
{
    private double[] coordinates = new double[24];;
    
    private void assignWithRotation(int rotation, int offset, int sign, int tl, int tr, int br, int bl, double dtl, double dtr, double dbr, double dbl)
    {
        switch (rotation)
        {
        case 0:
            coordinates[tl*3+offset]+=sign*dtl;
            coordinates[tr*3+offset]+=sign*dtr;
            coordinates[br*3+offset]+=sign*dbr;
            coordinates[bl*3+offset]+=sign*dbl;
            break;
        case 1:
            coordinates[tl*3+offset]+=sign*dbl;
            coordinates[tr*3+offset]+=sign*dtl;
            coordinates[br*3+offset]+=sign*dtr;
            coordinates[bl*3+offset]+=sign*dbr;
            break;
        case 2:
            coordinates[tl*3+offset]+=sign*dtr;
            coordinates[tr*3+offset]+=sign*dbr;
            coordinates[br*3+offset]+=sign*dbl;
            coordinates[bl*3+offset]+=sign*dtl;
            break;
        case 3:
            coordinates[tl*3+offset]+=sign*dbr;
            coordinates[tr*3+offset]+=sign*dbl;
            coordinates[br*3+offset]+=sign*dtl;
            coordinates[bl*3+offset]+=sign*dtr;
            break;

        default:
            break;
        }
    }
    
    public double[] getDeltas(TileEntityAdvancedInfoPanel panel, Screen screen)
    {
        boolean isTopBottom = panel.rotateVert != 0; 
        boolean isLeftRight = panel.rotateHor != 0; 
        double dTopLeft = 0;
        double dTopRight = 0;
        double dBottomLeft = 0;
        double dBottomRight = 0;
        int height = screen.getHeight(panel);
        int width = screen.getWidth(panel);
        double maxDelta = 0;
        if(isTopBottom)
        {
            if(panel.rotateVert>0) // |\
            {                      // | \
                dBottomRight = dBottomLeft = height*Math.tan(Math.PI*panel.rotateVert/180);
                maxDelta = dBottomLeft;
            }
            else
            {
                dTopRight = dTopLeft = height*Math.tan(Math.PI*-panel.rotateVert/180);
                maxDelta = dTopRight;
            }
        }
        if(isLeftRight)
        {
            if(panel.rotateHor>0) // -------
            {                     // | . '  
                dTopRight = dBottomRight = width*Math.tan(Math.PI*panel.rotateHor/180);
                maxDelta = dTopRight;
            }
            else
            {
                dTopLeft = dBottomLeft = width*Math.tan(Math.PI*-panel.rotateHor/180);
                maxDelta = dBottomLeft;
            }
        }
        if(isTopBottom && isLeftRight)
        {
            if(dTopLeft == 0)
            {
                maxDelta = dBottomRight = dBottomLeft + dTopRight;
            }
            else if(dTopRight == 0)
            {
                maxDelta = dBottomLeft = dTopLeft + dBottomRight;
            }
            else if(dBottomLeft == 0)
            {
                maxDelta = dTopRight = dTopLeft + dBottomRight;
            }
            else
            {
                maxDelta = dTopLeft = dBottomLeft + dTopRight;
            }
        }
        double thickness = panel.thickness/16D;
        if(maxDelta > thickness)
        {
            double scale = thickness / maxDelta;
            dTopLeft = scale*dTopLeft;
            dTopRight = scale*dTopRight;
            dBottomLeft = scale*dBottomLeft;
            dBottomRight = scale*dBottomRight;
        }
        double[] res = {dTopLeft, dTopRight, dBottomLeft, dBottomRight};
        return res;
    }
    
    private void addSlopes(TileEntityAdvancedInfoPanel panel, Screen screen)
    {
        if(panel.rotateVert == 0 && panel.rotateHor == 0)
            return;
        double[] deltas = getDeltas(panel, screen);
        double dTopLeft = deltas[0];
        double dTopRight = deltas[1];
        double dBottomLeft = deltas[2];
        double dBottomRight = deltas[3];
        int facing = panel.facing;
        int rotation = panel.getRotation();
        switch (facing)
        {
        case 0:
            assignWithRotation(rotation, 1, -1, 4, 7, 6, 5, dTopLeft, dTopRight, dBottomRight, dBottomLeft);
            break;
        case 1:
            assignWithRotation(rotation, 1, 1, 3, 0, 1, 2, dTopLeft, dTopRight, dBottomRight, dBottomLeft);
            break;
        case 2:
            assignWithRotation(rotation, 2, -1, 5, 6, 2, 1, dTopLeft, dTopRight, dBottomRight, dBottomLeft);
            break;
        case 3:
            assignWithRotation(rotation, 2, 1, 7, 4, 0, 3, dTopLeft, dTopRight, dBottomRight, dBottomLeft);
            break;
        case 4:
            assignWithRotation(rotation, 0, -1, 6, 7, 3, 2, dTopLeft, dTopRight, dBottomRight, dBottomLeft);
            break;
        case 5:
            assignWithRotation(rotation, 0, 1, 4, 5, 1, 0, dTopLeft, dTopRight, dBottomRight, dBottomLeft);
            break;
        }
    }
    
    private void initCoordinates(Block block, Screen screen)
    {
        
        //       5o ----- o6
        //    4o ----- o7  
        //    /   |   /   
        //   /   1o  /    o2
        // 0o ----- o3   
        double blockMinX = block.getBlockBoundsMinX();
        double blockMinY = block.getBlockBoundsMinY();
        double blockMinZ = block.getBlockBoundsMinZ();
        
        double blockMaxX = block.getBlockBoundsMaxX();
        double blockMaxY = block.getBlockBoundsMaxY();
        double blockMaxZ = block.getBlockBoundsMaxZ();
        
        /* 0 */
        coordinates[0] = screen.minX+blockMinX;
        coordinates[1] = screen.minY+blockMinY;
        coordinates[2] = screen.minZ+blockMinZ;
        /* 1 */
        coordinates[3] = screen.minX+blockMinX;
        coordinates[4] = screen.minY+blockMinY;
        coordinates[5] = screen.maxZ+blockMaxZ;
        /* 2 */
        coordinates[6] = screen.maxX+blockMaxX;
        coordinates[7] = screen.minY+blockMinY;
        coordinates[8] = screen.maxZ+blockMaxZ;
        /* 3 */
        coordinates[9] = screen.maxX+blockMaxX;
        coordinates[10] = screen.minY+blockMinY;
        coordinates[11] = screen.minZ+blockMinZ;
        /* 4 */
        coordinates[12] = screen.minX+blockMinX;
        coordinates[13] = screen.maxY+blockMaxY;
        coordinates[14] = screen.minZ+blockMinZ;
        /* 5 */
        coordinates[15] = screen.minX+blockMinX;
        coordinates[16] = screen.maxY+blockMaxY;
        coordinates[17] = screen.maxZ+blockMaxZ;
        /* 6 */
        coordinates[18] = screen.maxX+blockMaxX;
        coordinates[19] = screen.maxY+blockMaxY;
        coordinates[20] = screen.maxZ+blockMaxZ;
        /* 7 */
        coordinates[21] = screen.maxX+blockMaxX;
        coordinates[22] = screen.maxY+blockMaxY;
        coordinates[23] = screen.minZ+blockMinZ;
    }
    
    
    private void addPoint(int point, double u, double v)
    {
        Tessellator.instance.addVertexWithUV(coordinates[point*3], coordinates[point*3+1], coordinates[point*3+2], u, v);
    }
    
    public void renderScreen(Block block, TileEntityAdvancedInfoPanel panel, double x, double y, double z)
    {
        Screen screen = panel.getScreen();
        if(screen == null)
            return;
        GL11.glPushMatrix();
        initCoordinates(block, screen);
        addSlopes(panel, screen);
        Tessellator tessellator = Tessellator.instance;
        
        tessellator.setColorOpaque(255,255,255);
        //bottom
        int texture = block.getBlockTexture(panel.worldObj, panel.xCoord, panel.yCoord, panel.zCoord, 0);
        int u = (texture & 15) << 4;
        int v = texture & 240;
        //tessellator.setNormal(0, -1, 0);
        addPoint(0, u/256D, v/256D);
        addPoint(3, (u+16D)/256, v/256D);
        addPoint(2, (u+16D)/256,(v+16D)/256);
        addPoint(1, u/256D, (v+16D)/256);

        texture = block.getBlockTexture(panel.worldObj, panel.xCoord, panel.yCoord, panel.zCoord, 1);
        u = (texture & 15) << 4;
        v = texture & 240;
        //tessellator.setNormal(0, 1, 0);
        addPoint(4, u/256D, v/256D);
        addPoint(5, (u+16D)/256, v/256D);
        addPoint(6, (u+16D)/256,(v+16D)/256);
        addPoint(7, u/256D, (v+16D)/256);
       
        texture = block.getBlockTexture(panel.worldObj, panel.xCoord, panel.yCoord, panel.zCoord, 4);
        u = (texture & 15) << 4;
        v = texture & 240;
        //Tessellator.instance.setNormal(-1, 0, 0);
        addPoint(5, u/256D, v/256D);
        addPoint(4, (u+16D)/256, v/256D);
        addPoint(0, (u+16D)/256,(v+16D)/256);
        addPoint(1, u/256D, (v+16D)/256);
        
        texture = block.getBlockTexture(panel.worldObj, panel.xCoord, panel.yCoord, panel.zCoord, 5);
        u = (texture & 15) << 4;
        v = texture & 240;
        //Tessellator.instance.setNormal(1, 0, 0);
        addPoint(2, u/256D, v/256D);
        addPoint(3, (u+16D)/256, v/256D);
        addPoint(7, (u+16D)/256,(v+16D)/256);
        addPoint(6, u/256D, (v+16D)/256);
         
        texture = block.getBlockTexture(panel.worldObj, panel.xCoord, panel.yCoord, panel.zCoord, 2);
        u = (texture & 15) << 4;
        v = texture & 240;
        Tessellator.instance.setNormal(0, 0, -1);
        addPoint(0, u/256D, v/256D);
        addPoint(4, (u+16D)/256, v/256D);
        addPoint(7, (u+16D)/256,(v+16D)/256);
        addPoint(3, u/256D, (v+16D)/256);
        
        texture = block.getBlockTexture(panel.worldObj, panel.xCoord, panel.yCoord, panel.zCoord, 3);
        u = (texture & 15) << 4;
        v = texture & 240;
        Tessellator.instance.setNormal(0, 0, 1);
        addPoint(6, u/256D, v/256D);
        addPoint(5, (u+16D)/256, v/256D);
        addPoint(1, (u+16D)/256,(v+16D)/256);
        addPoint(2, u/256D, (v+16D)/256);
        
        GL11.glPopMatrix();
    }
}
