package stazthebox.sponge.spongeCast.broadcast;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;
import stazthebox.sponge.spongeAPI.TextUtils;

import java.util.concurrent.TimeUnit;

@ConfigSerializable
public class BroadcastMessage
{
	@Setting(value = "message", comment = "What message is going to be said.")
    private String message;

    @Setting(value = "json")
    private String jsonMessage;

    @Setting(value = "xml")
    private String xmlMessage;

	@Setting(value = "permission", comment = "If set, then only players who have this permission will see the message.")
    private String permission;

	@Setting(value = "pre-delay", comment = "After the post-delay of the last message, how long should we wait before sending this message.")
	private int preDelay = -1;

	@Setting(value = "post-delay", comment = "How long should we wait after sending the message before moving on to the next one?")
	private int postDelay = 0;

    @Setting(value = "delay", comment = "How long should we wait in-between messages (alias of pre-delay)")
    private int delay = 10;

    @Setting(value = "next", comment = "If we are in the next mode, what should be the next message played?")
    private int nextMessageInt = 1;

    private Text text;

	public int getDelay()
	{
		return preDelay < 0 ? delay : preDelay;
	}

    public BroadcastMessage setDelay(int delay) {
        this.delay = delay;
        return this;
    }

	public String getMessage()
	{
        if (message != null)
            return message;

        else if (jsonMessage != null)
            return jsonMessage;

        else if (xmlMessage != null)
            return xmlMessage;

        else return "";
    }

    public Text getText(BroadcastGroup group) {
        if (text == null) {
            if (message != null)
                text = TextSerializers.FORMATTING_CODE.deserialize(message);

            else if (jsonMessage != null)
                text = TextSerializers.JSON.deserialize(jsonMessage);

            else if (xmlMessage != null)
                text = TextSerializers.TEXT_XML.deserialize(xmlMessage);

            else
                text = Text.of();
        }

        if (text.getChildren().size() > 0) {
            Text child = text.getChildren().get(0);
            Text.Builder childBuilt = child.toBuilder();
            if (childBuilt.getColor() == TextColors.NONE || childBuilt.getStyle() == TextStyles.NONE)
                childBuilt.format(group.getTextFormat());
            Text built = TextUtils.changeTextChild(text, 0, childBuilt.build());
            return Text.join(group.getPrefixText(), built, group.getSuffixText());
        } else {
            Text.Builder childBuilt = text.toBuilder();
            if (childBuilt.getColor() == TextColors.NONE || childBuilt.getStyle() == TextStyles.NONE)
                childBuilt.format(group.getTextFormat());
            return Text.join(group.getPrefixText(), childBuilt.build(), group.getSuffixText());
        }

	}

	public String getPermission()
	{
		return permission;
	}

    public BroadcastMessage setPermission(String permission) {
        this.permission = permission;
        return this;
    }

	public int getPostDelay()
	{
		return postDelay;
	}

    public BroadcastMessage setPostDelay(int postDelay) {
        this.postDelay = postDelay;
        return this;
    }

	public int getNextMessageInt()
	{
		return nextMessageInt-1;
	}

    public BroadcastMessage setNextMessageInt(int nextMessageInt) {
        this.nextMessageInt = nextMessageInt;
        return this;
    }

    public Task.Builder createPreMessageTask(BroadcastGroup owner) {
        return Task.builder().execute(task -> onMessage(task, owner)).delay(getDelay(), TimeUnit.SECONDS);
    }

    public void onMessage(Task task, BroadcastGroup owner) {
        Text text = getText(owner);
        if (permission != null) {
            Sponge.getServer().getOnlinePlayers().stream().filter(player -> player.hasPermission(permission)).forEach(player -> player.sendMessage(text));
            MessageChannel.TO_CONSOLE.send(text);
        } else
            Sponge.getServer().getBroadcastChannel().send(text);
        Task.builder().delay(getPostDelay(), TimeUnit.SECONDS).execute(() -> owner.doNextMessage(this)).submit(task.getOwner());
    }
}
