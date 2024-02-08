package io.github.allianaab2m.romechat;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.allianaab2m.romechat.japanize.IMEConverter;
import io.github.allianaab2m.romechat.japanize.YukiKanaConverter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;


@Mod.EventBusSubscriber(modid = RomeChat.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RomeChatHooks {
    private enum RomeChatMode {
        ON,
        BRACKET_OFF,
        OFF,
    }

    private static RomeChatMode toggleMode(RomeChatMode mode) {
        return switch (mode) {
            case ON, BRACKET_OFF -> RomeChatMode.OFF;
            case OFF -> RomeChatMode.ON;
        };
    }

    static Map<String, RomeChatMode> playerData = new HashMap<>();
    @SubscribeEvent
    public static void SendMessage(final ServerChatEvent event) {
        String msg = event.getRawText();
        RomeChatMode mode = playerData.get(event.getPlayer().getStringUUID());
        if (mode == null) {
            mode = RomeChatMode.ON;
        }
        // もし`!`から始まるテキストだった場合はモードを反転，!を除去
        if (msg.startsWith("!")) {
            msg = msg.replaceFirst("!", "");
            mode = toggleMode(mode);
        }
        switch (mode) {
            case ON, BRACKET_OFF:
                if (msg.getBytes().length != msg.length()) {
                    // 全角文字が含まれるので変換しない
                    event.setMessage(Component.literal(msg));
                } else {
                    String convertedText = IMEConverter.Convert(YukiKanaConverter.conv(msg));
                    if (mode == RomeChatMode.BRACKET_OFF) {
                        event.setMessage(Component.literal(convertedText));
                    } else {
                        event.setMessage(Component.literal(convertedText + " (" + msg + ")"));
                    }
                }
                break;
            case OFF:
                event.setMessage(Component.literal(msg));
        }
    }
    @SubscribeEvent
    public static void ToggleCommand(final RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("toggleRC")
                .executes(context -> {
                    Entity entity = context.getSource().getEntity();
                    if (entity instanceof Player) {
                        String playerUUID = entity.getStringUUID();
                        RomeChatMode mode = playerData.get(playerUUID);

                        // 切り替え
                        mode = switch (mode) {
                            case ON -> RomeChatMode.BRACKET_OFF;
                            case BRACKET_OFF -> RomeChatMode.OFF;
                            case OFF -> RomeChatMode.ON;
                        };

                        playerData.put(playerUUID, mode);

                        String text = switch (mode) {
                            case ON -> "[RomeChat]: オン";
                            case BRACKET_OFF -> "[RomeChat]: 原文表示オフ";
                            case OFF -> "[RomeChat]: オフ";
                        };
                        entity.sendSystemMessage(Component.literal(text));
                    }
                    return Command.SINGLE_SUCCESS;
                });
        event.getDispatcher().register(builder);
    }

    @SubscribeEvent
    public static void PlayerDataInit(final EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            playerData.put(entity.getStringUUID(), RomeChatMode.ON);
        }
    }
}
