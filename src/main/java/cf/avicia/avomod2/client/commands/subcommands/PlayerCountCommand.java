package cf.avicia.avomod2.client.commands.subcommands;

import cf.avicia.avomod2.utils.Utils;
import cf.avicia.avomod2.webrequests.wynnapi.PlayerList;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.LiteralText;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class PlayerCountCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> command(String commandName) {
        return ClientCommandManager.literal(commandName)
                .then(ClientCommandManager.argument("world", word())
                        .executes(context -> {
                            Thread thread = new Thread(() -> {
                                String world = getString(context, "world");
                                PlayerList playerList = new PlayerList();
                                int playerCount = playerList.getAmountOfWorldPlayers(world);
                                if (playerCount != -1) {
                                    context.getSource().sendFeedback(new LiteralText("§b" + Utils.getFormattedWorld(world) + "§7 has §b" + playerCount + "§7 players online."));
                                } else {
                                    context.getSource().sendFeedback(new LiteralText("§4" + Utils.getFormattedWorld(world) + "§c is not online"));
                                }
                            });
                            thread.start();
                            return 0;
                        }));
    }
}
