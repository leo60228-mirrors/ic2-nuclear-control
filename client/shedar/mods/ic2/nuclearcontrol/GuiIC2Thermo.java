package shedar.mods.ic2.nuclearcontrol;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.GuiTextField;
import net.minecraft.src.StatCollector;
import net.minecraft.src.ic2.api.NetworkHelper;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiIC2Thermo extends GuiContainer
{
    private TileEntityIC2Thermo thermo;
    private GuiTextField textboxHeat = null;
    private String name;
    
    public GuiIC2Thermo(TileEntityIC2Thermo thermo)
    {
        super(new ContainerEmpty(thermo));
        xSize = 191;
        ySize = 64;
        this.thermo = thermo;
        name = StatCollector.translateToLocal("tile.blockThermalMonitor.name");
    }
    
    private void updateHeat(int delta)
    {
        if(textboxHeat != null)
        {
            int heat = 0;
            try
            {
                String value = textboxHeat.getText();
                if(!"".equals(value))
                    heat = Integer.parseInt(value);
            }catch (NumberFormatException e) {
                // do noting
            }
            heat+=delta;
            if(heat<0)
                heat = 0;
            if(heat >= 1000000)
                heat = 1000000;
            if(thermo.getHeatLevel().intValue()!=heat){
                thermo.setHeatLevel(heat);
                NetworkHelper.initiateClientTileEntityEvent(thermo, heat);
            }
            textboxHeat.setText(new Integer(heat).toString());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        super.initGui();
        controlList.clear();
        this.controlList.add(new CompactButton(0, guiLeft + 47, guiTop + 20, 22, 12, "-1"));
        this.controlList.add(new CompactButton(1, guiLeft + 47, guiTop + 31, 22, 12, "-10"));
        this.controlList.add(new CompactButton(2, guiLeft + 12, guiTop + 20, 36, 12, "-100"));
        this.controlList.add(new CompactButton(3, guiLeft + 12, guiTop + 31, 36, 12, "-1000"));
        this.controlList.add(new CompactButton(4, guiLeft + 12, guiTop + 42, 57, 12, "-10000"));
        
        this.controlList.add(new CompactButton(5, guiLeft + 122, guiTop + 20, 22, 12, "+1"));
        this.controlList.add(new CompactButton(6, guiLeft + 122, guiTop + 31, 22, 12, "+10"));
        this.controlList.add(new CompactButton(7, guiLeft + 143, guiTop + 20, 36, 12, "+100"));
        this.controlList.add(new CompactButton(8, guiLeft + 143, guiTop + 31, 36, 12, "+1000"));
        this.controlList.add(new CompactButton(9, guiLeft + 122, guiTop + 42, 57, 12, "+10000"));
        
        textboxHeat = new GuiTextField(fontRenderer, 70, 21, 51, 12);
        textboxHeat.setFocused(true);
        textboxHeat.setText(thermo.getHeatLevel().toString());
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        if(textboxHeat!=null)
            textboxHeat.updateCursorCounter();
    }
    
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    protected void drawGuiContainerForegroundLayer()
    {
        fontRenderer.drawString(name, (xSize - fontRenderer.getStringWidth(name)) / 2, 6, 0x404040);
        if(textboxHeat != null)
            textboxHeat.drawTextBox();    
    }
    
    @Override
    public void onGuiClosed()
    {
        updateHeat(0);
        super.onGuiClosed();
    }    
    
    @Override protected void actionPerformed(GuiButton button)
    {
        int delta = Integer.parseInt(button.displayString.replace("+", ""));
        updateHeat(delta);
    };

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {
        int texture = mc.renderEngine.getTexture("/img/GUIThermalMonitor.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(texture);
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
    }

    @Override
    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 1)//Esc
        {
            mc.thePlayer.closeScreen();
        }
        else if(par1 == 13)//Enter
        {
            updateHeat(0);
        }
        else if (textboxHeat!=null &&  textboxHeat.isFocused() && (Character.isDigit(par1) || par1 == 0 || par1 == 8))
        {
            textboxHeat.textboxKeyTyped(par1, par2);
        }
    }    
}
