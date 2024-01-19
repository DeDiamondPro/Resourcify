package dev.dediamondpro.resourcify.mixins;

import net.minecraft.server.packs.FilePackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FilePackResources.class)
public interface AbstractResourcePackAccessor {
    @Accessor("zipFileAccess")
    FilePackResources.SharedZipFileAccess getFileWrapper();
}
