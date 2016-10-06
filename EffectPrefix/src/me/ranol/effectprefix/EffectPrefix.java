package me.ranol.effectprefix;

import me.ranol.effectprefix.events.ServerLoadCompleteEvent;
import me.ranol.effectprefix.utils.RYamlConfiguration;
import me.ranol.effectprefix.utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class EffectPrefix extends JavaPlugin {
	private static EffectPrefix plugin;
	private static RYamlConfiguration config;
	private int configVer = 1;

	@Override
	public void onEnable() {
		plugin = this;
		saveDefaultConfig();
		config = RYamlConfiguration.loadConfiguration(this, "config.yml");
		Util.coverConfig(this, configVer, "ConfigBackUp",
				"구버전 컨피그 발견! 컨피그를 덮어씁니다.");
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

	public static RYamlConfiguration config() {
		return config;
	}

	@Override
	public RYamlConfiguration getConfig() {
		return config;
	}

	public static EffectPrefix getInstance() {
		return plugin;
	}

	public void registerEvents(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, this);
	}
}
