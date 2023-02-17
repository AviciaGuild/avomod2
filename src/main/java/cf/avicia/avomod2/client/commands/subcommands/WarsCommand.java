package cf.avicia.avomod2.client.commands.subcommands;

import cf.avicia.avomod2.client.eventhandlers.hudevents.WarTracker;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.util.Date;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class WarsCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> command(String commandName) {
        return ClientCommandManager.literal(commandName)
                .then(ClientCommandManager.argument("daysSince", word())
                        .executes(context -> {
                            long wars;
                            long since = WarTracker.timeOfFirstWar();
                            String daysSinceString = getString(context, "daysSince");
                            try {
                                int daysSince = Integer.parseInt(daysSinceString);
                                since = System.currentTimeMillis() - (daysSince * 86400000L);
                                wars = WarTracker.getWars(since);
                            } catch (NumberFormatException e) {
                                wars = WarTracker.getWars(0);
                            }
                            sendWarCount(context, wars, since);
                            return 0;
                        })).executes(context -> {
                    long wars = WarTracker.getWars(0);
                    long since = WarTracker.timeOfFirstWar();
                    sendWarCount(context, wars, since);
                    return 0;
                });
    }

    private static void sendWarCount(CommandContext<FabricClientCommandSource> context, long wars, long since) {
        String plural = "";
        if (wars != 1) {
            plural = "s";
        }
        String outputMessage = String.format("§aYou have done §6 %s §bwar%s since §6 %s \n§dOnly wars done with avomod active are counted.", wars, plural, new Date(since));
        context.getSource().sendFeedback(Text.literal(outputMessage));
    }
}
