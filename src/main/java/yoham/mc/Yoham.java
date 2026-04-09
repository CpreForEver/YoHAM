package yoham.mc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
// import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
// import net.minecraft.*;
// import net.minecraft.network.chat.Component;
// import net.minecraft.network.chat.OutgoingChatMessage;
// import net.minecraft.network.chat.PlayerChatMessage;
import yoham.mc.event.MorseChatEvent;

public class Yoham implements ModInitializer {
	public static final String MOD_ID = "yoham";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    

    @Override
    public void onInitialize() {
        System.out.println("YoHAM Server");
        
        // Register the chat event listener
        // ClientSendMessageEvents.MODIFY_CHAT.register((message) ->  {
        //     System.out.println("Player sent message: " + message);
        //     // Remove special characters and convert to Morse codeS
        //     String cleanedMessage = MorseCodeConverter.removeSpecialCharacters(message);
        //     String morseCodeMessage = MorseCodeConverter.toMorseCode(cleanedMessage);

        //     // Log or process the Morse code message as needed
        //     LOGGER.info("Converted Morse Code: " + morseCodeMessage);
        //     return morseCodeMessage;
        // });

        // Register the chat event listener for receiving messages in the chat
        // ClientSendMessageEvents.MODIFY_CHAT.register(new MorseChatEvent());
        // System.out.println("Tefer Server");

        // // Allow all chat messages
        // ClientSendMessageEvents.ALLOW_CHAT.register((message) -> {
        //     System.out.println("Allowing chat");
        //     return true;
        // });

        ClientSendMessageEvents.MODIFY_CHAT.register((message) ->{
            return message.toUpperCase();
        });

        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            System.out.println("Boop!");
            player.sendSystemMessage(Component.literal("Thanks for Playing!"));
            return InteractionResult.PASS;
        });
        // ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
        //     var player = handler.player;
        //     var message = OutgoingChatMessage.create(new PlayerChatMessage(null, null, null, null, null))
        //     player.sendChatMessage(Component.literal("Welcome to the server!"), false, null);
        // });

    }
}