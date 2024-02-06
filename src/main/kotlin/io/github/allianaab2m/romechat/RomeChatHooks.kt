package io.github.allianaab2m.romechat

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.github.allianaab2m.romechat.RomeChatHooks.RomeChatMode.*
import io.github.allianaab2m.romechat.japanize.convIME
import io.github.allianaab2m.romechat.japanize.convRomaji
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.ServerChatEvent
import net.minecraftforge.event.entity.EntityJoinLevelEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber

@EventBusSubscriber(modid = RomeChat.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
object RomeChatHooks {
    private fun toggleMode(mode: RomeChatMode): RomeChatMode {
        return when (mode) {
            ON, BRACKET_OFF -> OFF
            OFF -> ON
        }
    }

    private var playerData: MutableMap<String, RomeChatMode?> = HashMap()
    @SubscribeEvent
    fun sendMessage(event: ServerChatEvent) {
        var msg = event.rawText
        var mode = playerData[event.player.stringUUID]
        if (mode == null) {
            mode = ON
        }
        // もし`!`から始まるテキストだった場合はモードを反転，!を除去
        if (msg.startsWith("!")) {
            msg = msg.replaceFirst("!".toRegex(), "")
            mode = toggleMode(mode)
        }
        when (mode) {
            ON, BRACKET_OFF -> if (msg.toByteArray().size != msg.length) {
                // 全角文字が含まれるので変換しない
                event.message = Component.literal(msg)
            } else {
                val convertedText = msg.convRomaji().convIME()
                if (mode == BRACKET_OFF) {
                    event.message = Component.literal(convertedText)
                } else {
                    event.message = Component.literal("$convertedText ($msg)")
                }
            }

            OFF -> event.message = Component.literal(msg)
        }
    }

    @SubscribeEvent
    fun toggleCommand(event: RegisterCommandsEvent) {
        val builder = Commands.literal("toggleRC")
                .executes { context: CommandContext<CommandSourceStack> ->
                    val entity = context.source.entity
                    if (entity is Player) {
                        var mode = playerData[entity.stringUUID]

                        // 切り替え
                        mode = when (mode) {
                            ON -> BRACKET_OFF
                            BRACKET_OFF -> OFF
                            OFF -> ON
                            null -> ON
                        }

                        playerData[entity.stringUUID] = mode

                        val text = when (mode) {
                            ON -> "[RomeChat]: オン"
                            BRACKET_OFF -> "[RomeChat]: 原文表示オフ"
                            OFF -> "[RomeChat]: オフ"
                        }
                        entity.sendSystemMessage(Component.literal(text))
                    }
                    Command.SINGLE_SUCCESS
                }
        event.dispatcher.register(builder)
    }

    @SubscribeEvent
    fun playerDataInit(event: EntityJoinLevelEvent) {
        val entity = event.entity
        if (entity is Player) {
            playerData[entity.stringUUID] = ON
        }
    }

    enum class RomeChatMode {
        ON,
        BRACKET_OFF,
        OFF,
    }
}
