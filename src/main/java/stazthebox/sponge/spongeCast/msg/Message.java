package stazthebox.sponge.spongeCast.msg;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.concurrent.TimeUnit;

/**
 * Created by staz on 8/22/16.
 */

@ConfigSerializable
public class Message
{
	@Setting(value = "message", comment = "What message is going to be said.")
	private String message = "";

	@Setting(value = "permission", comment = "If set, then only players who have this permission will see the message.")
	private String permission = "";

	@Setting(value = "pre-delay", comment = "After the post-delay of the last message, how long should we wait before sending this message.")
	private int preDelay = -1;

	@Setting(value = "post-delay", comment = "How long should we wait after sending the message before moving on to the next one?")
	private int postDelay = 0;

	public int getDelay()
	{
		return preDelay < 0 ? delay : preDelay;
	}

	public Message setDelay(int delay)
	{
		this.delay = delay;
		return this;
	}

	@Setting(value = "delay", comment = "How long should we wait in-between messages (alias of pre-delay)")
	private int delay = 10;

	@Setting(value = "next", comment = "If we are in the next mode, what should be the next message played?")
	private int nextMessageInt = 1;

	public String getMessage()
	{
		return message;
	}

	public Message setMessage(String message)
	{
		this.message = message;
		return this;
	}

	public String getPermission()
	{
		return permission;
	}

	public Message setPermission(String permission)
	{
		this.permission = permission;
		return this;
	}

	public int getPreDelay()
	{
		return (preDelay == -1 ? delay : preDelay);
	}

	public Message setPreDelay(int preDelay)
	{
		this.preDelay = preDelay;
		return this;
	}

	public int getPostDelay()
	{
		return postDelay;
	}

	public Message setPostDelay(int postDelay)
	{
		this.postDelay = postDelay;
		return this;
	}

	public int getNextMessageInt()
	{
		return nextMessageInt-1;
	}

	public Message setNextMessageInt(int nextMessageInt)
	{
		this.nextMessageInt = nextMessageInt;
		return this;
	}

	public Task.Builder createPreMessageTask(MessageGroup owner)
	{
		return Task.builder().execute(task->onMessage(task, owner)).delay(getDelay(), TimeUnit.SECONDS);
	}

	public void onMessage(Task task, MessageGroup owner) {
		Sponge.getServer().getBroadcastChannel().send(TextSerializers.FORMATTING_CODE.deserialize(getMessage()));
		Task.builder().delay(getPostDelay(), TimeUnit.SECONDS).execute(()->owner.doNextMessage(this)).submit(task.getOwner());
	}
}
