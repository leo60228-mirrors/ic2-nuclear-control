package shedar.mods.ic2.nuclearcontrol;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiButton;
import ic2.api.NetworkHelper;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiThermoInvertRedstone extends GuiButton
{
    TileEntityIC2Thermo thermo;
    private boolean checked;

    public GuiThermoInvertRedstone(int id, int x, int y, TileEntityIC2Thermo thermo)
    {
        super(id, x, y, 0, 0, "");
        height  = 15;
        width = 51;
        this.thermo = thermo;
        checked = thermo.isInvertRedstone();
    }

    @Override
    public void drawButton(Minecraft minecraft, int par2, int par3)
    {
        if (this.drawButton)
        {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, minecraft.renderEngine.getTexture("/img/GUIThermalMonitor.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            int delta = checked?15:0;
            drawTexturedModalRect(xPosition, yPosition+1, 199, delta, 51, 15);
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
            int value = checked?-2:-1;
            thermo.setInvertRedstone(checked);
            NetworkHelper.initiateClientTileEntityEvent(thermo, value);
            return true;
        }
        else
        {
            return false;
        }
    }

}
