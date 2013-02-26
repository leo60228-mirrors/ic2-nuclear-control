package shedar.mods.ic2.nuclearcontrol.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.inventory.Container;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAdvancedInfoPanel extends GuiInfoPanel
{
    public GuiAdvancedInfoPanel(Container container)
    {
        super(container);
        ySize = 212;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {
        int texture = mc.renderEngine.getTexture("/img/GUIAdvancedInfoPanel.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(texture);
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
    }
    
    @Override
    protected void initControls()
    {
        
    }

}
