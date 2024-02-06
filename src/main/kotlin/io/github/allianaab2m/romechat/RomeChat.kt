package io.github.allianaab2m.romechat

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

@Mod(RomeChat.MOD_ID)
class RomeChat {
    init {
        val modEventBus = FMLJavaModLoadingContext.get().modEventBus
    }

    companion object {
        const val MOD_ID: String = "romechat"
    }
}
