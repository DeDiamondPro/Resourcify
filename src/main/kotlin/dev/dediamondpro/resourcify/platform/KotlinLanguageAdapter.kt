/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

//#if MODERN==0
package dev.dediamondpro.resourcify.platform

import net.minecraftforge.fml.common.FMLModContainer
import net.minecraftforge.fml.common.ILanguageAdapter
import net.minecraftforge.fml.common.ModContainer
import net.minecraftforge.fml.relauncher.Side
import java.lang.reflect.Field
import java.lang.reflect.Method

class KotlinLanguageAdapter: ILanguageAdapter {

    override fun getNewInstance(
        fMLModContainer: FMLModContainer,
        clazz: Class<*>,
        classLoader: ClassLoader,
        method: Method?
    ): Any {
        return clazz.kotlin.objectInstance ?: clazz.newInstance()
    }

    override fun supportsStatics() = false

    @Throws(IllegalArgumentException::class, IllegalAccessException::class, NoSuchFieldException::class, SecurityException::class)
    override fun setProxy(target: Field, proxyTarget: Class<*>, proxy: Any?) {
        target.set(proxyTarget.kotlin.objectInstance, proxy)
    }

    override fun setInternalProxies(modContainer: ModContainer?, side: Side?, classLoader: ClassLoader?) {}
}
//#endif