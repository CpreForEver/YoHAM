package yoham.mc.event;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;


import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;

public class MorseChatEvent implements ClientSendMessageEvents.ModifyChat{
	public static final String MOD_ID = "morse";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Static map to hold the Morse code mappings
    private static final Map<Character, String> morseCodeMap = new HashMap<>();
    static { 
        morseCodeMap.put('a', ".-");
        morseCodeMap.put('b', "-...");
        morseCodeMap.put('c', "-.-.");
        morseCodeMap.put('d', "-..");
        morseCodeMap.put('e', ".");
        morseCodeMap.put('f', "..-.");
        morseCodeMap.put('g', "--.");
        morseCodeMap.put('h', "....");
        morseCodeMap.put('i', "..");
        morseCodeMap.put('j', ".---");
        morseCodeMap.put('k', "-.-");
        morseCodeMap.put('l', ".-..");
        morseCodeMap.put('m', "--");
        morseCodeMap.put('n', "-.");
        morseCodeMap.put('o', "---");
        morseCodeMap.put('p', ".--.");
        morseCodeMap.put('q', "--.-");
        morseCodeMap.put('r', ".-.");
        morseCodeMap.put('s', "...");
        morseCodeMap.put('t', "-");
        morseCodeMap.put('u', "..-");
        morseCodeMap.put('v', "...-");
        morseCodeMap.put('w', ".--");
        morseCodeMap.put('x', "-..-");
        morseCodeMap.put('y', "-.--");
        morseCodeMap.put('z', "--..");

        morseCodeMap.put('1', ".----");
        morseCodeMap.put('2', "..---");
        morseCodeMap.put('3', "...--");
        morseCodeMap.put('4', "....-");
        morseCodeMap.put('5', ".....");
        morseCodeMap.put('6', "-....");
        morseCodeMap.put('7', "--...");
        morseCodeMap.put('8', "---..");
        morseCodeMap.put('9', "----.");
        morseCodeMap.put('0', "-----");
    }

    public static String removeSpecialCharacters(String input) {
        return input.replaceAll("[^a-zA-Z0-9]", "");
    }

    @Override
    public String modifySendChatMessage(String message) {
        System.out.println("modifying chat");
        StringBuilder morseMessage = new StringBuilder();
        String input = removeSpecialCharacters(message); 
        for (char c : input.toCharArray()) {
            if (morseCodeMap.containsKey(Character.toLowerCase(c))) {
                morseMessage.append(morseCodeMap.get(Character.toLowerCase(c)));
            }
        }

        return morseMessage.toString();

    }

}