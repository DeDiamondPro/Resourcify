/*
 * This file is part of Resourcify
 * Copyright (C) 2023 DeDiamondPro
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License Version 3
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
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