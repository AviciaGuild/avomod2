package cf.avicia.avomod2.client.commands;

import cf.avicia.avomod2.client.commands.subcommands.*;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class CommandInitializer {
    private static LiteralCommandNode<FabricClientCommandSource> avomodCommand = null;

    public static void initializeCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            avomodCommand = dispatcher.register(
                    ClientCommandManager.literal("avomod")
                            .then(PlayerGuildCommand.command("playerguild"))
                            .then(PlayerGuildCommand.command("pg"))
                            .then(OnlineMembersCommand.command("onlinemembers"))
                            .then(OnlineMembersCommand.command("om"))
                            .then(PlayerCountCommand.command("playercount"))
                            .then(PlayerCountCommand.command("pc"))
                            .then(FindCommand.command("find"))
                            .then(LastSeenCommand.command("lastseen"))
                            .then(LastSeenCommand.command("ls"))
                            .then(ConfigsCommand.command("configs"))
                            .then(ConfigsCommand.command("cf"))
                            .then(UpCommand.command("up"))
                            .then(AgeCommand.command("age"))
                            .then(SoulpointsCommand.command("soulpoints"))
                            .then(SoulpointsCommand.command("sp"))
                            .then(AutoStreamCommand.command("autostream"))
                            .then(AutoStreamCommand.command("as"))
                            .then(LocationsCommand.command("locations"))
                            .then(LocationsCommand.command("l"))
                            .then(CongratulateCommand.command("congratulate"))
                            .then(WarsCommand.command("wars"))
                            .executes(context -> {
                                context.getSource().sendFeedback(Text.literal("Command for AvoMod2"));
                                return 0;
                            })
            );
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("am").redirect(avomodCommand).executes(context -> {
                        context.getSource().sendFeedback(Text.literal("Command for AvoMod2"));
                        return 0;
                    })
            );
        });
    }
}
