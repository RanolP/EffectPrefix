package me.ranol.effectprefix;

import me.ranol.effectprefix.events.ServerLoadCompleteEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class EffectPrefix extends JavaPlugin {
	private static EffectPrefix plugin;

	@Override
	public void onEnable() {
		plugin = this;
		CmdPrefix user = new CmdPrefix();
		CmdPrefixAdmin admin = new CmdPrefixAdmin();
		getCommand("prefix").setExecutor(user);
		getCommand("prefix").setTabCompleter(user);
		getCommand("aprefix").setExecutor(admin);
		getCommand("aprefix").setTabCompleter(admin);
		registerEvents(new PrefixListener());
		Bukkit.getScheduler().scheduleSyncDelayedTask(
				this,
				() -> Bukkit.getPluginManager().callEvent(
						new ServerLoadCompleteEvent()), 1L);
	}

	public static EffectPrefix getInstance() {
		return plugin;
	}

	public void registerEvents(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, this);
	}
}
