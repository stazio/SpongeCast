package stazthebox.sponge.spongeCast.msg;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.scheduler.Task;
import stazthebox.sponge.spongeCast.exceptions.InvalidMessageModeException;
import stazthebox.sponge.spongeCast.exceptions.MessageStopFailedException;
import stazthebox.sponge.spongeCast.exceptions.NextMessageOutOfBoundsException;
import stazthebox.sponge.spongeCast.SpongeCast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by staz on 8/22/16.
 */

@ConfigSerializable
public class MessageGroup
{

	private List<Integer> alreadyPlayed;
	private Random random;
	private Task activeTask;

	@Setting(value = "mode", comment = "Choose between random, round, or next.")
	private String mode = "round";

	@Setting(value = "messages", comment = "What messages should be played?")
	private List<Message> messages = new ArrayList<>();

	private Object plugin = SpongeCast.getInstance();


	public void doNextMessage(Message lastMessagePlayed) {
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

				Message message = messages.get(next);
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
					try
					{ // So we are out of bo
						// unds! What do we doooo!??!?!!
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

	public List<Message> getMessages()
	{
		return messages;
	}
}
