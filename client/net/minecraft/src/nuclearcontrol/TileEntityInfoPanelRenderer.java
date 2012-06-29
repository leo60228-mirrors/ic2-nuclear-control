package net.minecraft.src.nuclearcontrol;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.Facing;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.ModLoader;
import net.minecraft.src.StatCollector;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

import org.lwjgl.opengl.GL11;

public class TileEntityInfoPanelRenderer extends TileEntitySpecialRenderer
{

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f)
    {
        boolean isPanel = tileEntity instanceof TileEntityInfoPanel;
        if(isPanel)
        {
            TileEntityInfoPanel panel = (TileEntityInfoPanel)tileEntity;
            if(!panel.powered || panel.displaySettings == 0 || 
                    (panel.deltaX == 0 && panel.deltaY == 0 && panel.deltaZ == 0))
            {
                return;
            }
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
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, ModLoader.getMinecraftInstance().renderEngine.getTexture("/img/texture_thermo.png"));
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

            GL11.glTranslatef(dx, 1F, dz);
            GL11.glRotatef(-90, 1, 0, 0);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            
            FontRenderer fontRenderer = this.getFontRenderer();
            
            int rows = 0;
            int maxWidth = 1;
            String txtOnOff = null;
            List<String> lines = new ArrayList<String>();  
            String txtHeat = null;
            String txtMaxHeat = null;
            String txtMelting = null;
            String txtOutput = null;
            String txtRemains = null;
            if((panel.displaySettings & TileEntityInfoPanel.DISPLAY_HEAT) > 0)
            {
                txtHeat = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelHeat"), panel.heat);
                maxWidth = Math.max(fontRenderer.getStringWidth(txtHeat), maxWidth);
                lines.add(txtHeat);
                rows++;
            }
            if((panel.displaySettings & TileEntityInfoPanel.DISPLAY_MAXHEAT) > 0)
            {
                rows++;
                txtMaxHeat = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelMaxHeat"), panel.maxHeat);
                maxWidth = Math.max(fontRenderer.getStringWidth(txtMaxHeat), maxWidth);
                lines.add(txtMaxHeat);
            }
            if((panel.displaySettings & TileEntityInfoPanel.DISPLAY_MELTING) > 0)
            {
                rows++;
                txtMelting = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelMelting"), panel.maxHeat*85/100);
                maxWidth = Math.max(fontRenderer.getStringWidth(txtMelting), maxWidth);
                lines.add(txtMelting);
            }
            if((panel.displaySettings & TileEntityInfoPanel.DISPLAY_OUTPUT) > 0)
            {
                rows++;
                txtOutput = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelOutput"), panel.output);
                maxWidth = Math.max(fontRenderer.getStringWidth(txtOutput), maxWidth);
                lines.add(txtOutput);
            }
            
            if((panel.displaySettings & TileEntityInfoPanel.DISPLAY_TIME) > 0)
            {
                int hours = panel.timeLeft / 3600;
                int minutes = (panel.timeLeft % 3600) / 60;
                int seconds = panel.timeLeft % 60;

                String time = String.format("%d:%02d:%02d", hours, minutes, seconds);                
                rows++;
                txtRemains = String.format(StatCollector.translateToLocal("msg.nc.InfoPanelTimeRemaining"), time);
                maxWidth = Math.max(fontRenderer.getStringWidth(txtRemains), maxWidth);
                lines.add(txtRemains);
            }

            boolean renderRightOnOff = false;
            int txtColor = 0;
            int onOffColor = panel.reactorPowered?0x00ff00:0xff0000;;
            if((panel.displaySettings & TileEntityInfoPanel.DISPLAY_ONOFF) > 0)
            {
                if(panel.reactorPowered)
                    txtOnOff = StatCollector.translateToLocal("msg.nc.InfoPanelOn");
                else
                    txtOnOff = StatCollector.translateToLocal("msg.nc.InfoPanelOff");
                String concatString;
                if(lines.size()>0)
                {
                    renderRightOnOff = true;
                    concatString = lines.get(0)+" "+txtOnOff;
                }
                else
                {
                    concatString = txtOnOff;
                    lines.add(txtOnOff);
                    txtColor = onOffColor;
                    rows++;
                }
                maxWidth = Math.max(fontRenderer.getStringWidth(concatString), maxWidth);
            }
            maxWidth+=4;

            int lineHeight = fontRenderer.FONT_HEIGHT + 2;
            int requiredHeight = lineHeight * rows;
            float scaleX = displayWidth/maxWidth;
            float scaleY = displayHeight/requiredHeight;
            float scale = Math.min(scaleX, scaleY);
            GL11.glScalef(scale, -scale, scale);
            GL11.glDepthMask(false);
            
            int row = 0;
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
            if(txtOnOff!=null && renderRightOnOff)
            {
                    fontRenderer.drawString(txtOnOff, offsetX+realWidth-offsetX-fontRenderer.getStringWidth(txtOnOff), 
                            offsetY, onOffColor);
            }
            for (String line : lines)
            {
                fontRenderer.drawString(line, offsetX, 1+offsetY + row * lineHeight, txtColor);
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
