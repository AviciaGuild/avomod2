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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class SoulpointsCommand {
    private static final int DEFAULT_LIST_AMOUNT = 5;

    private static final java.util.Comparator<Map.Entry<String, JsonElement>> SP_TIME_COMPARATOR =
            Comparator.comparingInt(m -> -m.getValue().getAsJsonObject().get("age").getAsInt() % 20);

    public static LiteralArgumentBuilder<FabricClientCommandSource> command(String commandName) {
        return ClientCommandManager.literal(commandName)
                .then(ClientCommandManager.argument("amount", word()).executes(context -> {
                    try {
                        int amount = Integer.parseInt(getString(context, "amount"));
                        sendSoulpointWorlds(context, amount);
                    } catch (NumberFormatException e) {
                        context.getSource().sendFeedback(Text.of("§cInvalid amount entered"));
                    }
                    return 0;
                }))
                .executes(context -> {
                    sendSoulpointWorlds(context, DEFAULT_LIST_AMOUNT);
                    return 0;
                });
    }

    private static void sendSoulpointWorlds(CommandContext<FabricClientCommandSource> context, int amountToSend) {
        if (amountToSend <= 0) {
            context.getSource().sendFeedback(new LiteralText("§cNo worlds match your criteria"));
            return;
        }
        Thread thread = new Thread(() -> {
            context.getSource().sendFeedback(new LiteralText(String.format("§bShowing %s worlds which may get soulpoints soon:", amountToSend)));
            UpTimes upTimes = new UpTimes();
            int iterations = 0;
            ArrayList<Map.Entry<String, JsonElement>> worldList = upTimes.getWorldUpTimeData();
            worldList.sort(SP_TIME_COMPARATOR);
            for (Map.Entry<String, JsonElement> worldData : worldList) {
                if (iterations >= amountToSend) break;
                int worldAge = upTimes.getAge(worldData.getKey());
                if (worldAge % 20 >= 16) {
                    context.getSource().sendFeedback(Utils.makeMessageThatRunsCommand("§6" + worldData.getKey() + ": §a" + Utils.getReadableTime(worldAge), "/switch " + worldData.getKey()));
                    iterations++;
                }
            }
        });
        thread.start();
    }
}
