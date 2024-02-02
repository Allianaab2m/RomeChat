package io.github.allianaab2m.romechat;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RomeChat.MOD_ID)
public class RomeChat {
    public static final String MOD_ID = "romechat";

    public RomeChat() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    }
}
