package cf.avicia.avomod2.client.commands.subcommands;

import cf.avicia.avomod2.utils.Utils;
import cf.avicia.avomod2.webrequests.aviciaapi.UpTimes;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.LiteralText;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class AgeCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> command(String commandName) {
        return ClientCommandManager.literal(commandName)
                .then(ClientCommandManager.argument("world", word())
                        .executes(context -> {
                            String world = Utils.getFormattedWorld(getString(context, "world"));
                            Thread thread = new Thread(() -> {
                                UpTimes upTimes = new UpTimes();
                                if (upTimes.isUp(world)) {
                                    context.getSource().sendFeedback(new LiteralText("§6" + world + "§7 is §a" + Utils.getReadableTime(upTimes.getAge(world)) + "§7 old"));
                                } else {
                                    context.getSource().sendFeedback(new LiteralText("§4" + world + "§c is not up"));
                                }
                            });
                            thread.start();
                            return 0;
                        }));
    }
}
