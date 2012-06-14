package net.minecraft.src.nuclearcontrol;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiButton;
import net.minecraft.src.ic2.api.NetworkHelper;

public class GuiInfoPanelCheckBox extends GuiButton
{
    private int mask;
    private TileEntityInfoPanel panel;
    private boolean checked;
    

    public GuiInfoPanelCheckBox(int id, int x, int y, int mask, String label, TileEntityInfoPanel panel, FontRenderer renderer)
    {
        super(id, x, y, 0, 0, label);
        height  = renderer.FONT_HEIGHT+1;
        width = renderer.getStringWidth(label)+8;
        this.panel = panel;
        this.mask = mask;
        checked = (panel.displaySettings & mask) > 0;
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
                value = panel.displaySettings | mask;
            else
                value = panel.displaySettings & (~mask);
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
