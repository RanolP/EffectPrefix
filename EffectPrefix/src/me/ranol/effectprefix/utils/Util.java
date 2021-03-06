package me.ranol.effectprefix.utils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Util {
	private static String pref = "&8[&aEffectPrefix&8] &a";
	public static final char CHECK_MARK = '✓';
	public static final char X_MARK = '✗';
	public static final char BOX_WITH_X_MARK = '☒';
	public static final char BOX_WITH_CHECK_MARK = '☑';
	public static final char DELETE_MARK = '⌫';

	public static String getMessagePrefix() {
		return pref;
	}

	public static void playSound(Player player, Sound sound) {
		player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
	}

	public static void sendMessageWithPrefix(CommandSender s, String message) {
		s.sendMessage(ChatColor.translateAlternateColorCodes('&', pref
				+ message));
	}

	public static void sendMessage(CommandSender s, String message) {
		s.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}

	public static void sendWarning(CommandSender s, String message) {
		s.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c[!] &6"
				+ message));
	}

	public static void warning(String message) {
		Bukkit.getConsoleSender().sendMessage(
				ChatColor.translateAlternateColorCodes('&', "&c[!] &e"
						+ message));
	}

	public static void errors(String... message) {
		Arrays.asList(message).forEach((s) -> warning(s));
	}

	public static void sendMessageList(CommandSender s, List<String> list,
			int i, String l) {
		if ((i * 8 - 7 > list.size()) || (i <= 0)) {
			sendWarning(s, "목록을 찾을 수 없습니다.");
			return;
		}
		if (list.size() * 8 == 0)
			sendMessage(s, "&b" + list.size() + "개를 찾았습니다. &e" + i + "&b/&a"
					+ list.size() / 8);
		else {
			sendMessage(s, "&b" + list.size() + "개를 찾았습니다. &e" + i + "&b/&a"
					+ (list.size() / 8 + 1));
		}
		for (int j = (i - 1) * 8; j < i * 8; j++) {
			sendMessage(s, "&a" + (String) list.get(j));
			if (list.size() == j + 1) {
				break;
			}
			if ((i * 8 - 1 == j) && (list.size() > j + 1))
				sendMessage(s, "&a다음 목록을 보려면 &c/" + l + " " + (i + 1));
		}
	}

	public static RYamlConfiguration coverConfig(JavaPlugin plugin,
			int version, String folderName, String message) {
		RYamlConfiguration config = RYamlConfiguration.loadConfiguration(
				plugin, "config.yml");
		if (config.getInt("config", version) != version) {
			String saveName = "config-" + System.currentTimeMillis() + ".yml";
			Util.sendMessageWithPrefix(Bukkit.getConsoleSender(), message);
			File folder = new File(plugin.getDataFolder(), folderName);
			if (!folder.exists())
				folder.mkdirs();
			FileManager.copy(config.getFile(), new File(plugin.getDataFolder(),
					folderName + "\\" + saveName));
			config.getFile().delete();
			plugin.saveDefaultConfig();
		}
		return config;
	}
}
