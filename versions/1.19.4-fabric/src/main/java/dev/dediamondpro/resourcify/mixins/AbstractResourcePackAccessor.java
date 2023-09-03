package dev.dediamondpro.resourcify.mixins;

import net.minecraft.resource.ZipResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;

@Mixin(ZipResourcePack.class)
public interface AbstractResourcePackAccessor {
    @Accessor("backingZipFile")
    File getFile();
}
