package io.github.allianaab2m.romechat

import io.github.allianaab2m.romechat.RomeChatData.RomeChatMode.*
import net.minecraft.world.entity.player.Player
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.ServerChatEvent
import net.minecraftforge.event.entity.EntityJoinLevelEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber

@EventBusSubscriber(modid = RomeChat.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
object RomeChatEventHandler {
    private var playerState: MutableMap<String, RomeChatData> = HashMap()

    @SubscribeEvent
    fun serverChatHandler(event: ServerChatEvent) {
        val state = playerState[event.player.stringUUID] ?: RomeChatData(ON)
        event.message = RomeChatHooks.editChatMessage(event.rawText, state)
    }

    @SubscribeEvent
    fun playerJoinLevelHandler(event: EntityJoinLevelEvent) {
        val entity = event.entity
        if (entity is Player) {
            playerState[entity.stringUUID] = RomeChatData(ON) // 初期化
        }
    }

    @SubscribeEvent
    fun registerCommandsHandler(event: RegisterCommandsEvent) {
        event.dispatcher.register(RomeChatHooks.toggleRCCommand(playerState))
    }

}