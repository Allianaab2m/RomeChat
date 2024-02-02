package io.github.allianaab2m.romechat;

import io.github.allianaab2m.romechat.japanize.IMEConverter;
import io.github.allianaab2m.romechat.japanize.YukiKanaConverter;
import net.minecraft.network.chat.Component;

import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RomeChat.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RomeChatHooks {
    @SubscribeEvent
    public static void SendMessage(final ServerChatEvent event) {
        String msg = event.getRawText();
        // もし`!`から始まるテキストだった場合は変換せずに送信
        if (msg.startsWith("!")) {
            event.setMessage(Component.literal(msg.replaceFirst("!", "")));
        } else {
            if (msg.getBytes().length != msg.length()) {
                // 全角文字が含まれるので変換しない
                event.setMessage(Component.literal(msg));
            } else {
                String convertedText = IMEConverter.Convert(YukiKanaConverter.conv(msg));
                event.setMessage(Component.literal(convertedText + " (" + msg + ")"));
            }
        }
    }
}
