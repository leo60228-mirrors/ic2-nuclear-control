package shedar.mods.ic2.nuclearcontrol;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;

import org.lwjgl.opengl.GL11;

import shedar.mods.ic2.nuclearcontrol.api.CardState;
import shedar.mods.ic2.nuclearcontrol.api.IPanelDataSource;
import shedar.mods.ic2.nuclearcontrol.api.PanelString;
import shedar.mods.ic2.nuclearcontrol.panel.CardWrapperImpl;
import shedar.mods.ic2.nuclearcontrol.utils.StringUtils;
import cpw.mods.fml.client.FMLClientHandler;

public class TileEntityInfoPanelRenderer extends TileEntitySpecialRenderer
{
    
    private static String implodeArray(String[] inputArray, String glueString) 
    {
        String output = "";
        if (inputArray.length > 0) 
        {
            StringBuilder sb = new StringBuilder();
            for (int i=0; i<inputArray.length; i++) {
                if(inputArray[i]==null || inputArray[i].isEmpty())
                    continue;
                sb.append(glueString);
                sb.append(inputArray[i]);
            }
            output = sb.toString();
            if(output.length()>1)
                output = output.substring(1);
        }
        return output;
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f)
    {
        boolean isPanel = tileEntity instanceof TileEntityInfoPanel;
        if(isPanel)
        {
            TileEntityInfoPanel panel = (TileEntityInfoPanel)tileEntity;
            if(!panel.powered)
                return;
            int displaySettings = panel.getDisplaySettings();
            if(displaySettings == 0)
                return;
            ItemStack card = panel.getStackInSlot(TileEntityInfoPanel.SLOT_CARD);
            if(card == null || !(card.getItem() instanceof IPanelDataSource))
                return;

            CardWrapperImpl helper = new CardWrapperImpl(card);
            CardState state = helper.getState();
            List<PanelString> data;
            if(state != CardState.OK && state != CardState.CUSTOM_ERROR)
                data = StringUtils.getStateMessage(state);
            else
                data = panel.getCardData(displaySettings, (IPanelDataSource)card.getItem(), helper);
            if(data == null)
                return;
            
            GL11.glPushMatrix();
            GL11.glPolygonOffset( -10, -10 );
            GL11.glEnable ( GL11.GL_POLYGON_OFFSET_FILL );
            short side = (short)Facing.faceToSide[panel.getFacing()];
            Screen screen = panel.getScreen();
            float dx = 1F/16;
            float dz = 1F/16;
            float displayWidth = 1 - 2F/16;
            float displayHeight = 1 - 2F/16;
            if(screen!=null)
            {
                y -= panel.yCoord - screen.maxY;
                if(side == 0 || side == 1 || side == 2 || side == 3 || side == 5)
                    z -= panel.zCoord - screen.minZ;
                else
                    z -= panel.zCoord - screen.maxZ;
                
                if(side == 0 || side == 2 || side == 4)
                    x -= panel.xCoord - screen.minX;
                else
                    x -= panel.xCoord - screen.maxX;
            }
            GL11.glTranslatef((float)x, (float)y, (float)z);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, FMLClientHandler.instance().getClient().renderEngine.getTexture("/img/texture_thermo.png"));
            switch (side)
            {
                case 0:
                    if(screen!=null)
                    {
                        displayWidth+=screen.maxX - screen.minX;
                        displayHeight+=screen.maxZ - screen.minZ;
                    }
                    break;
                case 1:
                    GL11.glTranslatef(1, 1, 0);
                    GL11.glRotatef(180, 1, 0, 0);
                    GL11.glRotatef(180, 0, 1, 0);
                    if(screen!=null)
                    {
                        displayWidth+=screen.maxX - screen.minX;
                        displayHeight+=screen.maxZ - screen.minZ;
                    }
                    break;
                case 2:
                    GL11.glTranslatef(0, 1, 0);
                    GL11.glRotatef(0, 0, 1, 0);
                    GL11.glRotatef(90, 1, 0, 0);
                    if(screen!=null)
                    {
                        displayWidth+=screen.maxX - screen.minX;
                        displayHeight+=screen.maxY - screen.minY;
                    }
                    break;
                case 3:
                    GL11.glTranslatef(1, 1, 1);
                    GL11.glRotatef(180, 0, 1, 0);
                    GL11.glRotatef(90, 1, 0, 0);
                    if(screen!=null)
                    {
                        displayWidth+=screen.maxX - screen.minX;
                        displayHeight+=screen.maxY - screen.minY;
                    }
                    break;
                case 4:
                    GL11.glTranslatef(0, 1, 1);
                    GL11.glRotatef(90, 0, 1, 0);
                    GL11.glRotatef(90, 1, 0, 0);
                    if(screen!=null)
                    {
                        displayWidth+=screen.maxZ - screen.minZ;
                        displayHeight+=screen.maxY - screen.minY;
                    }
                    break;
                case 5:
                    GL11.glTranslatef(1, 1, 0);
                    GL11.glRotatef(-90, 0, 1, 0);
                    GL11.glRotatef(90, 1, 0, 0);
                    if(screen!=null)
                    {
                        displayWidth+=screen.maxZ - screen.minZ;
                        displayHeight+=screen.maxY - screen.minY;
                    }
                    break;
            }

            GL11.glTranslatef(dx+displayWidth/2, 1F, dz+displayHeight/2);
            GL11.glRotatef(-90, 1, 0, 0);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            
            switch (panel.rotation)
            {
                case 0:
                    break;
                case 1:
                    GL11.glRotatef(-90, 0, 0, 1);
                    float t = displayHeight;
                    displayHeight = displayWidth;
                    displayWidth = t;
                    break;
                case 2:
                    GL11.glRotatef(90, 0, 0, 1);
                    float tm = displayHeight;
                    displayHeight = displayWidth;
                    displayWidth = tm;
                    break;
                case 3:
                    GL11.glRotatef(180, 0, 0, 1);
                    break;
            }            
            
            FontRenderer fontRenderer = this.getFontRenderer();
            
            int maxWidth = 1;
            for (PanelString panelString : data)
            {
                String currentString = implodeArray(new String[]{panelString.textLeft, panelString.textCenter, panelString.textRight}," ");
                maxWidth = Math.max(fontRenderer.getStringWidth(currentString), maxWidth);
            }
            maxWidth+=4;

            int lineHeight = fontRenderer.FONT_HEIGHT + 2;
            int requiredHeight = lineHeight * data.size();
            float scaleX = displayWidth/maxWidth;
            float scaleY = displayHeight/requiredHeight;
            float scale = Math.min(scaleX, scaleY);
            GL11.glScalef(scale, -scale, scale);
            GL11.glDepthMask(false);
            
            int offsetX;
            int offsetY;
            
            int realHeight = (int)Math.floor(displayHeight/scale);
            int realWidth = (int)Math.floor(displayWidth/scale);

            if(scaleX < scaleY)
            {
                offsetX = 2;
                offsetY = (realHeight - requiredHeight) / 2;
            }
            else
            {
                offsetX = (realWidth - maxWidth) / 2 + 2;
                offsetY = 0;
            }
            Block block = Block.blocksList[panel.worldObj.getBlockId(panel.xCoord, panel.yCoord, panel.zCoord)];
            if(block==null)
            {
                block = Block.stone;
            }
            
            GL11.glDisable(GL11.GL_LIGHTING);
            
            int row = 0;
            for (PanelString panelString : data)
            {
                if(panelString.textLeft != null)
                    fontRenderer.drawString(panelString.textLeft, offsetX-realWidth/2, 1+offsetY-realHeight/2 + row * lineHeight, panelString.colorLeft!=0?panelString.colorLeft:panel.getColorTextHex());
                if(panelString.textCenter != null)
                    fontRenderer.drawString(panelString.textCenter, -fontRenderer.getStringWidth(panelString.textCenter)/2, offsetY - realHeight/2  + row * lineHeight, panelString.colorCenter!=0?panelString.colorCenter:panel.getColorTextHex());
                if(panelString.textRight != null)
                    fontRenderer.drawString(panelString.textRight, realWidth/2-fontRenderer.getStringWidth(panelString.textRight), 
                                            offsetY - realHeight/2  + row * lineHeight, panelString.colorRight!=0?panelString.colorRight:panel.getColorTextHex());
                row++;
            }

            GL11.glEnable(GL11.GL_LIGHTING);
            
            GL11.glDepthMask(true);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL );
            GL11.glPopMatrix();
            
        }
        
    }

}
