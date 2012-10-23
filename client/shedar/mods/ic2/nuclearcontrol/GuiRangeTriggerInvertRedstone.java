package shedar.mods.ic2.nuclearcontrol;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiButton;
import net.minecraft.src.ic2.api.NetworkHelper;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiRangeTriggerInvertRedstone extends GuiButton
{
    TileEntityRangeTrigger trigger;
    private boolean checked;

    public GuiRangeTriggerInvertRedstone(int id, int x, int y, TileEntityRangeTrigger trigger)
    {
        super(id, x, y, 0, 0, "");
        height  = 15;
        width = 18;
        this.trigger = trigger;
        checked = trigger.isInvertRedstone();
    }

    @Override
    public void drawButton(Minecraft minecraft, int par2, int par3)
    {
        if (this.drawButton)
        {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, minecraft.renderEngine.getTexture("/img/GUIRangeTrigger.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            int delta = checked?15:0;
            drawTexturedModalRect(xPosition, yPosition+1, 176, delta, 18, 15);
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
            trigger.setInvertRedstone(checked);
            NetworkHelper.initiateClientTileEntityEvent(trigger, value);
            return true;
        }
        else
        {
            return false;
        }
    }

}
