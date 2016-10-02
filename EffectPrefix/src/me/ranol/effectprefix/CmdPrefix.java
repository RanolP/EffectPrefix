package me.ranol.effectprefix;

import java.util.ArrayList;
import java.util.List;

import me.ranol.effectprefix.api.Prefix;
import me.ranol.effectprefix.api.PrefixManager;
import me.ranol.effectprefix.tabcompletor.DefaultCommandExecutor;
import me.ranol.effectprefix.tabcompletor.LinkedListCompletions;
import me.ranol.effectprefix.tabcompletor.PlayerCompletions;
import me.ranol.effectprefix.tabcompletor.StringCompletions;
import me.ranol.effectprefix.utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdPrefix extends DefaultCommandExecutor {
	{
		StringCompletions user = new StringCompletions("view");
		addCompletion(user, 1);
		StringCompletions select = new StringCompletions("select");
		addCompletion(select, 1);
		StringCompletions info = new StringCompletions("info");
		addCompletion(info, 1);
		LinkedListCompletions prefixes = new LinkedListCompletions();
		prefixes.addWorker((list, sender) -> {
			list.clear();
			if (sender instanceof Player) {
				PrefixManager.getInstance()
						.getHasPrefix((OfflinePlayer) sender)
						.forEach(a -> list.add(a.getPrefixName()));
			}
		});
		addCompletion(prefixes.link(select), 2);
		addCompletion(new StringCompletions("help"), 1);
		addCompletion(new StringCompletions("list"), 1);
		addCompletion(new PlayerCompletions(user), 2);
		addCompletion(prefixes.link(info), 2);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		if (!(s instanceof Player)) {
			Util.sendWarning(s, "플레이어만 입력 가능한 명령어입니다!");
			return false;
		}
		if (a.length == 0) {
			help(s, l);
			return true;
		} else if (a[0].equalsIgnoreCase("view")) {
			OfflinePlayer op = (OfflinePlayer) s;
			List<String> prefixMessages = new ArrayList<>();
			if (a.length == 1) {
				get(op, prefixMessages);
				Util.sendMessageList(s, prefixMessages, 1, l + " view");
			}
			if (a.length == 2) {
				if (a[1].matches("[0-9]+")) {
					get(op, prefixMessages);
					Util.sendMessageList(s, prefixMessages,
							Integer.parseInt(a[1]), l + " view");
				} else if (s.isOp()) {
					OfflinePlayer player = Bukkit.getOfflinePlayer(a[1]);
					if (player == null) {
						Util.sendWarning(s, "접속했던 플레이어만 조회 가능합니다!");
						return false;
					}
					get(player, prefixMessages);
					Util.sendMessageList(s, prefixMessages, 1, l + " view");
				} else {
					Util.sendWarning(s, "숫자를 입력하세요!");
					return false;
				}
			}
			return true;
		} else if (a[0].equalsIgnoreCase("select")) {
			return select(s, a);
		} else if (a[0].equalsIgnoreCase("help")) {
			return help(s, l);
		} else if (a[0].equalsIgnoreCase("list")) {
			return list(s, a, l);
		} else if (a[0].equalsIgnoreCase("info")) {
			return info(s, a);
		}
		return false;
	}

	private void get(OfflinePlayer op, List<String> list) {
		PrefixManager
				.getInstance()
				.getSelectedPrefix(op)
				.forEach(
						(pref) -> {
							list.add("&f\"" + pref.getPrefixName() + "&f\""
									+ " &7/ &f\"" + pref.getPrefix() + "&f\"");
						});
	}

	private boolean select(CommandSender s, String[] a) {
		OfflinePlayer op = (OfflinePlayer) s;
		if (a.length == 1) {
			Util.sendWarning(s, "칭호의 이름을 입력해주세요.");
			return false;
		}
		Prefix prefix = PrefixManager.getInstance().get(a[1]);
		if (!PrefixManager.getInstance().hasPrefix(op, prefix)) {
			Util.sendWarning(s, "가지고 있지 않은 칭호입니다.");
			return false;
		}
		if (PrefixManager.getInstance().isSelected(op, prefix)) {
			Util.sendWarning(s, "이미 선택된 칭호입니다.");
			return false;
		}
		PrefixManager.getInstance().select(op, prefix);
		Util.sendMessageWithPrefix(s, "칭호 \'" + prefix.getPrefixName()
				+ "\'을(를) 선택했습니다.");
		return true;
	}

	private boolean list(CommandSender s, String[] a, String l) {
		List<String> prefixes = new ArrayList<>();
		PrefixManager.getInstance().getHasPrefix((OfflinePlayer) s)
				.forEach((pref) -> {
					prefixes.add(pref.getPrefixName());
				});
		int index = 1;
		if (a.length > 1 && a[1].matches("[-]?[0-9]+"))
			index = Integer.parseInt(a[1]);
		Util.sendMessageList(s, prefixes, index < 1 ? 1 : index, l + " list");
		return true;
	}

	private boolean info(CommandSender s, String[] a) {
		if (a.length == 1) {
			Util.sendWarning(s, "정보를 볼 칭호의 이름을 입력하세요.");
			return false;
		}
		Prefix prefix = PrefixManager.getInstance().get(a[1]);
		if (prefix == null
				|| !PrefixManager.getInstance().hasPrefix((OfflinePlayer) s,
						prefix)) {
			Util.sendWarning(s, "당신은 그 칭호를 가지고 있지 않습니다.");
			return false;
		}
		Util.sendMessage(s, "&6명칭: " + prefix.getPrefixName());
		Util.sendMessage(s, "&6접두사: &f\"" + prefix.getPrefix() + "&f\"");
		Util.sendMessage(s, "&6설명: " + prefix.getDescription());
		prefix.getEffects().forEach((effect) -> {
			Util.sendMessage(s, "&6|     " + effect.getDescription());
		});
		return true;
	}

	private boolean help(CommandSender s, String l) {
		Util.sendMessage(s, "&b!==========" + Util.getMessagePrefix()
				+ "&b==========!");
		Util.sendMessage(s, "&e<Required>&f, &e[Optional]");
		Util.sendMessage(s, "&6* /" + l
				+ " view [플레이어] [페이지] - 자신이 적용한 칭호를 봅니다.");
		Util.sendMessage(s, "&6* /" + l + " select <칭호> [슬롯] - 칭호를 선택합니다.");
		Util.sendMessage(s, "&6* /" + l + " list - 자신이 가진 칭호들을 봅니다.");
		Util.sendMessage(s, "&6* /" + l + " help - 도움말을 봅니다.");
		Util.sendMessage(s, "&6* /" + l + " info <칭호> - 칭호의 정보를 봅니다.");
		return true;
	}

}
