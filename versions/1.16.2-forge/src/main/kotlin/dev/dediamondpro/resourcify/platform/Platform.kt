/*
 * This file is part of Resourcify
 * Copyright (C) 2023 DeDiamondPro
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.dediamondpro.resourcify.platform

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.resources.I18n
import net.minecraft.util.SharedConstants
import net.minecraft.util.text.TranslationTextComponent

object Platform {
    fun getMcVersion(): String {
        return SharedConstants.getVersion().name
    }

    fun getTranslateKey(screen: Screen): String {
        //#if MC < 11900
        val content = screen.title
        //#else
        //$$ val content = screen.title.content
        //#endif
        if (content !is TranslationTextComponent) {
            val optifineTranslation = I18n.format("of.options.shadersTitle")
            if (optifineTranslation != "of.options.shadersTitle" && optifineTranslation == screen.title.string) {
                return "of.options.shadersTitle"
            }
            return screen.title.string
        }
        return content.key
    }
}