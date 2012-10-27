package shedar.mods.ic2.nuclearcontrol;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiButton;
import ic2.api.NetworkHelper;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

import shedar.mods.ic2.nuclearcontrol.TileEntityInfoPanel;

@SideOnly(Side.CLIENT)
public class GuiInfoPanelShowLabels extends GuiButton
{
    private TileEntityInfoPanel panel;
    private boolean checked;

    public GuiInfoPanelShowLabels(int id, int x, int y, TileEntityInfoPanel panel)
    {
        super(id, x, y, 0, 0, "");
        height  = 9;
        width = 18;
        this.panel = panel;
        checked = panel.getShowLabels();
    }

    @Override
    public void drawButton(Minecraft minecraft, int par2, int par3)
    {
        if (this.drawButton)
        {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, minecraft.renderEngine.getTexture("/img/GUIInfoPanel.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            int delta = checked?12:21;
            drawTexturedModalRect(xPosition, yPosition+1, 176, delta, 18, 9);
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
            int value = checked?-1:-2;
            panel.setShowLabels(checked);
            NetworkHelper.initiateClientTileEntityEvent(panel, value);
            return true;
        }
        else
        {
            return false;
        }
    }

}
