package cf.avicia.avomod2.client.commands;

import cf.avicia.avomod2.client.commands.subcommands.*;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.LiteralText;

public class CommandInitializer {
    public static void initializeCommands() {


         LiteralCommandNode<FabricClientCommandSource> avomodCommand = ClientCommandManager.DISPATCHER.register(
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
                    .executes(context -> {
                        context.getSource().sendFeedback(new LiteralText("Command for AvoMod2"));
                        return 0;
                    })
        );

        ClientCommandManager.DISPATCHER.register(
                ClientCommandManager.literal("am").redirect(avomodCommand)
        );
    }
}
