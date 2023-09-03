package dev.dediamondpro.resourcify.mixins;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiScreenResourcePacks.class)
public interface PackScreenAccessor {

    @Accessor("parentScreen")
    GuiScreen getParentScreen();
}
