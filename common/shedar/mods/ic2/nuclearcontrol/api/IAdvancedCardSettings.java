package shedar.mods.ic2.nuclearcontrol.api;

import net.minecraft.src.GuiScreen;

public interface IAdvancedCardSettings
{
    GuiScreen getSettingsScreen(ICardWrapper wrapper);
}
