package dev.dediamondpro.resourcify.mixins;

import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.resource.ResourcePackManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Consumer;

@Mixin(ResourcePackOrganizer.class)
public interface ResourcePackOrganizerAccessor {
    @Accessor("applier")
    Consumer<ResourcePackManager> getApplier();
}
