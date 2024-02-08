package io.github.allianaab2m.romechat

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.github.allianaab2m.romechat.RomeChatData.RomeChatMode.*
import io.github.allianaab2m.romechat.japanize.IMEConverter.convIME
import io.github.allianaab2m.romechat.japanize.YukiKanaConverter.convRomaji
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player

object RomeChatHooks {
    fun editChatMessage(message: String, state: RomeChatData): Component {
        var msg = message
        var mode = state.mode

        if (msg.startsWith("!")) {
            msg = msg.replaceFirst("!".toRegex(), "")
            mode = mode.toggle()
        }
        return when (mode) {
            ON, BRACKET_OFF ->
                if (msg.toByteArray().size != msg.length) {
                    Component.literal(msg) // 全角文字が含まれるので変換しない
                } else {
                    val convertedText = msg.convRomaji().convIME()
                    if (mode == BRACKET_OFF) {
                        Component.literal(convertedText)
                    } else {
                        Component.literal("$convertedText ($msg)")
                    }
                }

            OFF -> Component.literal(msg)
        }
    }

    fun toggleRCCommand(playerState: MutableMap<String, RomeChatData>): LiteralArgumentBuilder<CommandSourceStack>? {
        return Commands.literal("toggleRC")
            .executes { context: CommandContext<CommandSourceStack> ->
                val entity = context.source.entity
                if (entity is Player) {
                    val state = playerState[entity.stringUUID] ?: RomeChatData(RomeChatData.RomeChatMode.ON)
                    state.cycleMode() // 切り替え

                    val text = when (state.mode) {
                        ON -> "[RomeChat]: オン"
                        BRACKET_OFF -> "[RomeChat]: 原文表示オフ"
                        OFF -> "[RomeChat]: オフ"
                    }
                    entity.sendSystemMessage(Component.literal(text))
                }
                Command.SINGLE_SUCCESS
            }
    }
}
