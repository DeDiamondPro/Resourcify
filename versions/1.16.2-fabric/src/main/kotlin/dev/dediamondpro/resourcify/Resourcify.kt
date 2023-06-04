/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify

import dev.dediamondpro.resourcify.updater.UpdateChecker
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents

object Resourcify : ClientModInitializer {
    override fun onInitializeClient() {
        UpdateChecker.startUpdateCheck()
        var done = false
        ClientTickEvents.START_CLIENT_TICK.register {
            if (done) return@register
            UpdateChecker.displayScreenIfNeeded()
            done = false
        }
    }
}