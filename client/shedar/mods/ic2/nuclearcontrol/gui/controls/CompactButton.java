package shedar.mods.ic2.nuclearcontrol.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CompactButton extends GuiButton
{

    public CompactButton(int par1, int par2, int par3, int par4, int par5, String par6Str)
    {
        super(par1, par2, par3, par4, par5, par6Str);
    }

    @Override
    public void drawButton(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.drawButton)
        {
            FontRenderer fontRenderer = par1Minecraft.fontRenderer;
            par1Minecraft.renderEngine.bindTexture("/mods/nuclearControl/textures/gui/GUIThermalMonitor.png");
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean var5 = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            int var6 = this.getHoverState(var5);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 64 + var6 * 12, this.width / 2 + width % 2, this.height);
            this.drawTexturedModalRect(this.xPosition + this.width / 2 + width % 2, this.yPosition, 200 - this.width / 2, 64 + var6 * 12, this.width / 2, this.height);
            this.mouseDragged(par1Minecraft, par2, par3);
            fontRenderer.drawString(displayString, xPosition+(width - fontRenderer.getStringWidth(displayString)) / 2, yPosition+2, 0x303030);
        }
    }
    
}
