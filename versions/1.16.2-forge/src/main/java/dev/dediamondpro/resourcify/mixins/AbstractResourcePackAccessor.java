package dev.dediamondpro.resourcify.mixins;

import net.minecraft.resources.ResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;

@Mixin(ResourcePack.class)
public interface AbstractResourcePackAccessor {
    @Accessor("file")
    File getFile();
}
