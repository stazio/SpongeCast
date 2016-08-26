package stazthebox.sponge.spongeCast.broadcast;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextFormat;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;
import stazthebox.sponge.spongeCast.SpongeCast;
import stazthebox.sponge.spongeCast.exceptions.InvalidMessageModeException;
import stazthebox.sponge.spongeCast.exceptions.MessageStopFailedException;
import stazthebox.sponge.spongeCast.exceptions.NextMessageOutOfBoundsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@ConfigSerializable
public class BroadcastGroup
{
	private List<Integer> alreadyPlayed;
	private Random random;
	private Task activeTask;

	@Setting(value = "mode", comment = "Choose between random, round, or next.")
	private String mode = "round";

	@Setting(value = "messages", comment = "What messages should be played?")
    private List<BroadcastMessage> messages = new ArrayList<>();

    @Setting(value = "prefix")
    private String prefix = "";

    @Setting(value = "suffix")
    private String suffix = "";

    @Setting(value = "format")
    private Map<String, String> textFormatMap = null;

    private TextFormat textFormat = null;

    private Text suffixText, prefixText;

	private Object plugin = SpongeCast.getInstance();

    public Text getPrefixText() {
        if (prefixText == null)
            return prefixText = TextSerializers.FORMATTING_CODE.deserialize(prefix);
        return prefixText;
    }

    public Text getSuffixText() {
        if (suffixText == null)
            return suffixText = TextSerializers.FORMATTING_CODE.deserialize(suffix);
        return suffixText;
    }

    public void doNextMessage(BroadcastMessage lastMessagePlayed) {
        int next = messages.indexOf(lastMessagePlayed);

		switch (mode) {
			case "random":
				// Add our message to the list of messages already played!
				alreadyPlayed.add(next);

				// If all the messages were already played, reset the list
				if (alreadyPlayed.size() == messages.size())
					alreadyPlayed.clear();

				// get the first integer that has not already been played
				do {
					next = random.nextInt(messages.size());
				}while (alreadyPlayed.contains(next));

                BroadcastMessage message = messages.get(next);
                activeTask = message.createPreMessageTask(this).submit(plugin);
                break;

			case "round":
				// Find the next number
				next++;
				if (next > messages.size()-1)
					next = 0;
				activeTask = messages.get(next).createPreMessageTask(this).submit(plugin);
				break;

			case "next":
				// Get the next message
				next = lastMessagePlayed.getNextMessageInt();
				if (next > messages.size() || next < 0)
				{
                    try { // So we are out of bounds! What do we doooo!??!?!!
                        // I wrote that at night, don't judge me random nerd who decided it would be a good idea to try and understand this
                        // mistake of a plugin                    That went past the 180 character line, what you gonna do bro???--^
                        throw new NextMessageOutOfBoundsException(next + " is not a valid message...");
                    } catch (NextMessageOutOfBoundsException e)
					{
						e.printStackTrace();
					}
				}else {
					message = messages.get(next);
					activeTask = message.createPreMessageTask(this).submit(plugin);
				}
				break;

			case "default":
				break;

			default:
				try
				{
					throw new InvalidMessageModeException(mode);
				} catch (InvalidMessageModeException e)
				{
					e.printStackTrace();
				}
				break;
		}
	}

	public void begin() {
		if (messages.size() > 0)
		{
			switch (mode)
			{
				case "random":
					alreadyPlayed = new ArrayList<>();
					random = new Random();
					activeTask = messages.get(random.nextInt(messages.size())).createPreMessageTask(this).submit(plugin);
					break;
				case "next":
				case "round":
					activeTask = messages.get(0).createPreMessageTask(this).submit(plugin);

				case "default":
					break;

				default:
					try
					{
						throw new InvalidMessageModeException(mode);
					} catch (InvalidMessageModeException e)
					{
						e.printStackTrace();
					}
					break;
			}
		}else
			try
			{
				throw new NextMessageOutOfBoundsException("There are no messages to play!");
			} catch (NextMessageOutOfBoundsException e)
			{
				e.printStackTrace();
			}
	}

	public void stop() {
		if (!activeTask.cancel())
			try
			{
				throw new MessageStopFailedException("There was an error, and messages are still going!");
			} catch (MessageStopFailedException e)
			{
				e.printStackTrace();
			}
	}

    public List<BroadcastMessage> getMessages() {
        return messages;
    }

    public TextFormat getTextFormat() {
        //	if (textFormat == null) {
        textFormat = TextFormat.NONE;

        if (textFormatMap != null) {
            // Colour formatting
            String colour;
            if (textFormatMap.containsKey("color"))
                colour = textFormatMap.get("color");
            else if (textFormatMap.containsKey("colour"))
                colour = textFormatMap.get("colour");
            else
                colour = null;

            if (colour != null) {
                switch (colour.toLowerCase()) {
                    case "aqua":
                        textFormat = textFormat.color(TextColors.AQUA);
                        break;
                    case "black":
                        textFormat = textFormat.color(TextColors.BLACK);
                        break;
                    case "blue":
                        textFormat = textFormat.color(TextColors.BLUE);
                        break;
                    case "dark aqua":
                        textFormat = textFormat.color(TextColors.DARK_AQUA);
                        break;
                    case "dark blue":
                        textFormat = textFormat.color(TextColors.DARK_BLUE);
                        break;
                    case "dark gray":
                    case "dark grey":
                        textFormat = textFormat.color(TextColors.DARK_GRAY);
                        break;
                    case "dark green":
                        textFormat = textFormat.color(TextColors.DARK_GREEN);
                        break;

                    case "dark purple":
                        textFormat = textFormat.color(TextColors.DARK_PURPLE);
                        break;
                    case "dark red":
                        textFormat = textFormat.color(TextColors.DARK_RED);
                        break;
                    case "gold":
                        textFormat = textFormat.color(TextColors.GOLD);
                        break;
                    case "gray":
                    case "grey":
                        textFormat = textFormat.color(TextColors.GRAY);
                        break;
                    case "green":
                        textFormat = textFormat.color(TextColors.GREEN);
                        break;
                    case "light purple":
                        textFormat = textFormat.color(TextColors.LIGHT_PURPLE);
                        break;
                    case "red":
                        textFormat = textFormat.color(TextColors.RED);
                        break;
                    case "white":
                        textFormat = textFormat.color(TextColors.WHITE);
                        break;
                    case "yellow":
                        textFormat = textFormat.color(TextColors.YELLOW);
                        break;
                }
            }

            // Style formatting
            if (textFormatMap.containsKey("style")) {
                String[] formats = textFormatMap.get("style").split(" ");
                TextStyle[] textStyles = new TextStyle[formats.length];

                for (int i = 0; i < formats.length; i++) {
                    switch (formats[i].toLowerCase()) {
                        case "italics":
                            textStyles[i] = TextStyles.ITALIC;
                            break;
                        case "bold":
                            textStyles[i] = TextStyles.BOLD;
                            break;
                        case "strike":
                        case "strikethrough":
                            textStyles[i] = TextStyles.STRIKETHROUGH;
                            break;
                        case "underline":
                            textStyles[i] = TextStyles.UNDERLINE;
                    }
                }
                textFormat = textFormat.style(TextStyles.of(textStyles));
            }
        }
        //	}
        return textFormat;
    }
}
