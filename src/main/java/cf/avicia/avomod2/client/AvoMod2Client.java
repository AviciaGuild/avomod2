package cf.avicia.avomod2.client;

import cf.avicia.avomod2.client.commands.CommandInitializer;
import com.mojang.brigadier.arguments.ArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.text.LiteralText;

@Environment(EnvType.CLIENT)
public class AvoMod2Client implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CommandInitializer.initializeCommands();
    }
}
