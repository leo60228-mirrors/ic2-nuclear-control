package net.minecraft.src.nuclearcontrol;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiButton;
import net.minecraft.src.ic2.api.NetworkHelper;
import net.minecraft.src.nuclearcontrol.panel.PanelSetting;

public class GuiInfoPanelCheckBox extends GuiButton
{
    private TileEntityInfoPanel panel;
    private boolean checked;
    private PanelSetting setting;
    

    public GuiInfoPanelCheckBox(int id, int x, int y, PanelSetting setting, TileEntityInfoPanel panel, FontRenderer renderer)
    {
        super(id, x, y, 0, 0, setting.title);
        this.setting = setting;
        height  = renderer.FONT_HEIGHT+1;
        width = renderer.getStringWidth(setting.title)+8;
        this.panel = panel;
        checked = (panel.getDisplaySettings() & setting.displayBit) > 0;
    }

    @Override
    public void drawButton(Minecraft minecraft, int par2, int par3)
    {
        if (this.drawButton)
        {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, minecraft.renderEngine.getTexture("/img/GUIInfoPanel.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            int delta = checked?6:0;
            drawTexturedModalRect(xPosition, yPosition+1, 176, delta, 6, 6);
            minecraft.fontRenderer.drawString(displayString, xPosition+8, yPosition, 0x404040);
        }
    }

    @Override
    protected int getHoverState(boolean flag)
    {
        return 0;
    }

    @Override
    public boolean mousePressed(Minecraft minecraft, int i, int j)
    {
        if (super.mousePressed(minecraft, i, j))
        {
            checked = !checked;
            int value; 
            if(checked)
                value = panel.getDisplaySettings() | setting.displayBit;
            else
                value = panel.getDisplaySettings() & (~setting.displayBit);
            panel.setDisplaySettings(value);
            NetworkHelper.initiateClientTileEntityEvent(panel, value);
            return true;
        }
        else
        {
            return false;
        }
    }

}
