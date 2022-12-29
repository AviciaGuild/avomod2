package cf.avicia.avomod2.client.commands.subcommands;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.utils.Utils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class CongratulateCommand {

    private static final List<String> congratulateWorthyPlayers = new ArrayList<>();

    public static LiteralArgumentBuilder<FabricClientCommandSource> command(String commandName) {
        return ClientCommandManager.literal(commandName)
                .then(ClientCommandManager.argument("name", word())
                        .executes(context -> {
                            String username = getString(context, "name");
                            if (congratulateWorthyPlayers.contains(username)) {
                                String congratulateMessage = ConfigsHandler.getConfig("congratsMessage");
                                if (MinecraftClient.getInstance().player != null && congratulateMessage != null) {
                                    MinecraftClient.getInstance().player.sendMessage(Text.of(String.format("/msg %s %s", username, congratulateMessage)));
                                    congratulateWorthyPlayers.remove(username);
                                }
                            } else {
                                context.getSource().sendFeedback(Text.literal(String.format("§b%s §7has nothing to be congratulated for!", username)));
                            }
                            return 0;
                        }));
    }

    public static ActionResult onMessage(Text message) {
        if (Utils.textWithoutTimeStamp(message).getString().startsWith("[!] Congratulations") && ConfigsHandler.getConfigBoolean("clickToSayCongrats")) {
            String[] firstSplit = message.getString().split(" for")[0].split("to ");
            if (firstSplit.length <= 1) return ActionResult.SUCCESS;
            String username = firstSplit[1];
            congratulateWorthyPlayers.add(username);
            String congratsCommand = String.format("/avomod congratulate %s", username);
            MutableText congratulateMessage = Text.literal("§b§nClick to say Congratulations!");
            congratulateMessage.fillStyle(congratulateMessage.getStyle()
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, congratsCommand))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of(congratsCommand)))
            );
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(congratulateMessage);
        }
        return ActionResult.SUCCESS;
    }
}
