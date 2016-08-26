package stazthebox.sponge.spongeCast;


import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import stazthebox.sponge.spongeAPI.SimpleConfig;
import stazthebox.sponge.spongeCast.broadcast.BroadcastGroup;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by staz on 8/22/16.
 */

@Plugin(id = "spongecast")
public class SpongeCast
{
	private static SpongeCast INSTANCE;

	@Inject
	private Logger logger;

	@Inject
	@DefaultConfig(sharedRoot = false)
	private Path configFolder;

	private SimpleConfig config;
	private HashMap<String, BroadcastGroup> groups = new HashMap<>();

	public static SpongeCast getInstance() {
		return INSTANCE;
	}

	@Listener
	public void preInit(GamePreInitializationEvent event) throws ObjectMappingException, IOException
	{
		INSTANCE = this;
		config = new SimpleConfig(configFolder, "config.conf");
		config.load();


		Map<Object, ? extends CommentedConfigurationNode> map = config.getConfigRoot().getNode("messages").getChildrenMap();
		for (Object key : map.keySet()) {
			groups.put(String.valueOf(key), map.get(key).getValue(TypeToken.of(BroadcastGroup.class)));
		}
	}

	@Listener
	public void gameInit(GameInitializationEvent event) {
		groups.values().forEach(BroadcastGroup::begin);
	}
}
