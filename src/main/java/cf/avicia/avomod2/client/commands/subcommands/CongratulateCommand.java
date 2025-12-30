package cf.avicia.avomod2.client.commands.subcommands;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.utils.Utils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                                if (MinecraftClient.getInstance().getNetworkHandler() != null && congratulateMessage != null) {
                                    MinecraftClient.getInstance().getNetworkHandler().sendChatCommand(String.format("msg %s %s", username, congratulateMessage));
                                    congratulateWorthyPlayers.remove(username);
                                }
                            } else {
                                context.getSource().sendFeedback(Text.literal(String.format("§b%s §7has nothing to be congratulated for!", username)));
                            }
                            return 0;
                        }));
    }

    public static Text onMessage(Text message) {
        if (ConfigsHandler.getConfigBoolean("clickToSayCongrats")) {
            Pattern pattern = Pattern.compile("\\[!] Congratulations to (\\w+) for reaching .+!");
            String rawMessage = Utils.getUnformattedString(Utils.textWithoutTimeStamp(message).getString());
            Matcher matcher = pattern.matcher(rawMessage);
            if (matcher.find()) {
                String username = matcher.group(1);
                congratulateWorthyPlayers.add(username);
                String congratsCommand = String.format("/avomod congratulate %s", username);
                MutableText congratulateMessage = Text.literal(" §b§nClick to say Congratulations!");
                congratulateMessage.fillStyle(congratulateMessage.getStyle()
                        .withClickEvent(new ClickEvent.RunCommand(congratsCommand))
                        .withHoverEvent(new HoverEvent.ShowText(Text.of(congratsCommand)))
                );
                MutableText messageCopy = message.copy();
                messageCopy.getSiblings().add(congratulateMessage);
                return messageCopy;
            }
        }
        return message;
    }
}
