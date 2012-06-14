package net.minecraft.src.nuclearcontrol;

import net.minecraft.src.Container;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.StatCollector;

import org.lwjgl.opengl.GL11;

public class GuiInfoPanel extends GuiContainer
{
    private String name;
    private ContainerInfoPanel container;

    public GuiInfoPanel(Container container)
    {
        super(container);
        this.container = (ContainerInfoPanel)container; 
        name = StatCollector.translateToLocal("tile.blockInfoPanel.name");
    }
    
    @Override
    public void initGui() {
        super.initGui();
        int h = fontRenderer.FONT_HEIGHT + 1;
        controlList.clear();
        controlList.add(new GuiInfoPanelCheckBox(0, guiLeft + 32, guiTop + 16, TileEntityInfoPanel.DISPLAY_ONOFF, 
                StatCollector.translateToLocal("msg.nc.cbInfoPanelOnOff"), container.panel, fontRenderer));
        controlList.add(new GuiInfoPanelCheckBox(1, guiLeft + 32, guiTop + 16 + h, TileEntityInfoPanel.DISPLAY_HEAT, 
                StatCollector.translateToLocal("msg.nc.cbInfoPanelHeat"), container.panel, fontRenderer));
        controlList.add(new GuiInfoPanelCheckBox(2, guiLeft + 32, guiTop + 16 + 2*h, TileEntityInfoPanel.DISPLAY_MAXHEAT, 
                StatCollector.translateToLocal("msg.nc.cbInfoPanelMaxHeat"), container.panel, fontRenderer));
        controlList.add(new GuiInfoPanelCheckBox(3, guiLeft + 32, guiTop + 16 + 3*h, TileEntityInfoPanel.DISPLAY_MELTING, 
                StatCollector.translateToLocal("msg.nc.cbInfoPanelMelting"), container.panel, fontRenderer));
        
        controlList.add(new GuiInfoPanelCheckBox(4, guiLeft + 32, guiTop + 16 + 4*h, TileEntityInfoPanel.DISPLAY_OUTPUT, 
                StatCollector.translateToLocal("msg.nc.cbInfoPanelOutput"), container.panel, fontRenderer));
        controlList.add(new GuiInfoPanelCheckBox(5, guiLeft + 32, guiTop + 16 + 5*h, TileEntityInfoPanel.DISPLAY_TIME, 
                StatCollector.translateToLocal("msg.nc.cbInfoPanelTimeRemaining"), container.panel, fontRenderer));
    };

    @Override
    protected void drawGuiContainerForegroundLayer()
    {
        fontRenderer.drawString(name, (xSize - fontRenderer.getStringWidth(name)) / 2, 6, 0x404040);
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {
        int texture = mc.renderEngine.getTexture("/img/GUIInfoPanel.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(texture);
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
    }
    
    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int which)
    {
        super.mouseMovedOrUp(mouseX, mouseY, which);
    }

}
