package cf.avicia.avomod2.client.commands.subcommands;

import cf.avicia.avomod2.utils.Utils;
import cf.avicia.avomod2.webrequests.aviciaapi.UpTimes;
import com.google.gson.JsonElement;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Map;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class UpCommand {
    private static final int DEFAULT_LIST_AMOUNT = 5;

    public static LiteralArgumentBuilder<FabricClientCommandSource> command(String commandName) {
        return ClientCommandManager.literal(commandName)
                .then(ClientCommandManager.argument("amount", word()).executes(context -> {
                    try {
                        int amount = Integer.parseInt(getString(context, "amount"));
                        sendWorldAges(context, amount);
                    } catch (NumberFormatException e) {
                        context.getSource().sendFeedback(Text.of("§cInvalid amount entered"));
                    }
                    return 0;
                }).then(ClientCommandManager.argument("minAge", word()).executes(context -> {
                    try {
                        int minAge = Integer.parseInt(getString(context, "minAge"));
                        int amount = Integer.parseInt(getString(context, "amount"));
                        sendWorldAges(context, amount, minAge);
                    } catch (NumberFormatException e) {
                        context.getSource().sendFeedback(Text.of("§cInvalid amount entered"));
                    }
                    return 0;
                })))
                .executes(context -> {
                    sendWorldAges(context, DEFAULT_LIST_AMOUNT);
                    return 0;
                });
    }

    private static void sendWorldAges(CommandContext<FabricClientCommandSource> context, int amountToSend, int minAge) {
        if (amountToSend <= 0) {
            context.getSource().sendFeedback(new LiteralText("§cNo worlds match your criteria"));
            return;
        }
        Thread thread = new Thread(() -> {
            context.getSource().sendFeedback(new LiteralText(String.format("§bShowing %s worlds with minimum age %sm:", amountToSend, minAge)));
            UpTimes upTimes = new UpTimes();
            int iterations = 0;
            for (Map.Entry<String, JsonElement> worldData : upTimes.getWorldUpTimeData()) {
                if (iterations >= amountToSend) break;
                int worldAge = upTimes.getAge(worldData.getKey());
                if (worldAge >= minAge) {
                    context.getSource().sendFeedback(Utils.makeMessageThatRunsCommand("§6" + worldData.getKey() + ": §a" + Utils.getReadableTime(worldAge), "/switch " + worldData.getKey()));
                    iterations++;
                }
            }
        });
        thread.start();
    }

    private static void sendWorldAges(CommandContext<FabricClientCommandSource> context, int amountToSend) {
        sendWorldAges(context, amountToSend, 0);
    }
}
