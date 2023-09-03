package dev.dediamondpro.resourcify.mixins;

//#if MC <= 11202

import net.minecraft.client.resources.AbstractResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;

@Mixin(AbstractResourcePack.class)
public interface AbstractResourcePackAccessor {
    @Accessor("resourcePackFile")
    File getResourcePackFile();
}
//#endif