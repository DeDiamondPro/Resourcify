/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.util

import gg.essential.elementa.components.image.ImageProvider
import gg.essential.universal.UMatrixStack
import java.awt.Color

object EmptyImage : ImageProvider {
    override fun drawImage(
        matrixStack: UMatrixStack,
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        color: Color
    ) {
    }
}