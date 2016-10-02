package me.ranol.effectprefix;

import java.util.ArrayList;
import java.util.List;

import me.ranol.effectprefix.api.Prefix;
import me.ranol.effectprefix.api.PrefixEffect;
import me.ranol.effectprefix.api.PrefixManager;
import me.ranol.effectprefix.designpatterns.ObserverTarget;
import me.ranol.effectprefix.events.PrefixCreateEvent;
import me.ranol.effectprefix.tabcompletor.DefaultCommandExecutor;
import me.ranol.effectprefix.tabcompletor.LinkedListCompletions;
import me.ranol.effectprefix.tabcompletor.StringCompletions;
import me.ranol.effectprefix.utils.Util;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdPrefixAdmin extends DefaultCommandExecutor implements
		ObserverTarget<List<Prefix>> {
	private static final long serialVersionUID = 7976125297312206961L;
	List<String> prefixes = new ArrayList<>();
	{
		StringCompletions addopt = new StringCompletions("addopt");
		addCompletion(addopt, 1);
		addCompletion(new StringCompletions("create"), 1);
		addCompletion(new StringCompletions("list"), 1);
		addCompletion(new StringCompletions("help"), 1);
		StringCompletions give = new StringCompletions("give");
		addCompletion(give, 1);
		StringCompletions take = new StringCompletions("take");
		addCompletion(take, 1);
		StringCompletions vname = new StringCompletions("vname");
		addCompletion(vname, 1);
		LinkedListCompletions prefix = new LinkedListCompletions(prefixes);
		addCompletion(prefix.link(addopt), 2);
		addCompletion(prefix.link(vname), 2);
		addCompletion(prefix.link(give), 2);
		addCompletion(prefix.link(take), 2);
		PrefixManager.getInstance().attach(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		if (a.length == 0) {
			help(s, l);
			return true;
		} else if (a[0].equalsIgnoreCase("create")) {
			if (a.length == 1) {
				Util.sendWarning(s, "칭호의 이름을 입력해주세요!");
				return false;
			}
			Prefix prefix = new Prefix(a[1]);
			PrefixCreateEvent event = new PrefixCreateEvent(prefix);
			Bukkit.getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				Util.sendMessageWithPrefix(s, "칭호 \'" + a[1]
						+ "\'을 생성할 수 없습니다.");
			} else {
				if (PrefixManager.getInstance().register(prefix))
					Util.sendMessageWithPrefix(s, "칭호 \'" + a[1]
							+ "\'이 생성되었습니다.");
				else
					Util.sendMessageWithPrefix(s, "\'" + a[1]
							+ "\'은 이미 존재하는 이름입니다.");
			}
			return true;
		} else if (a[0].equalsIgnoreCase("list")) {
			List<String> prefixes = new ArrayList<>();
			PrefixManager.getInstance().getAllPrefix().forEach((pref) -> {
				prefixes.add(pref.getPrefixName());
			});
			int index = 1;
			if (a.length > 1 && a[1].matches("[-]?[0-9]+"))
				index = Integer.parseInt(a[1]);
			Util.sendMessageList(s, prefixes, index < 1 ? 1 : index, l
					+ " list");
		} else if (a[0].equalsIgnoreCase("help")) {
			help(s, l);
			return true;
		} else if (a[0].equalsIgnoreCase("give")) {
			if (a.length == 1) {
				Util.sendWarning(s, "칭호의 이름을 적으세요!");
				return false;
			}
			Prefix prefix = PrefixManager.getInstance().get(a[1]);
			if (prefix == null) {
				Util.sendWarning(s, a[1] + "(이)라는 칭호는 없습니다!");
				return false;
			}
			if (a.length == 3) {
				OfflinePlayer player = Bukkit.getOfflinePlayer(a[2]);
				if (PrefixManager.getInstance().givePrefix(player, prefix)) {
					Util.sendMessageWithPrefix(s, a[2] + "에게 칭호를 성공적으로 지급했습니다.");
					if (player.isOnline())
						Util.sendMessageWithPrefix((Player) player,
								"칭호를 획득했습니다!");
				} else {
					Util.sendMessageWithPrefix(s, a[2] + "에게 칭호를 지급하지 못하였습니다.");
				}
			} else {
				if (PrefixManager.getInstance().givePrefix((Player) s, prefix)) {
					Util.sendMessageWithPrefix(s, "칭호를 획득했습니다!");
				} else {
					Util.sendMessageWithPrefix(s, "칭호를 획득하지 못했습니다.");
				}
			}
			return true;
		} else if (a[0].equalsIgnoreCase("take")) {
			if (a.length == 1) {
				Util.sendWarning(s, "칭호의 이름을 적으세요!");
				return false;
			}
			Prefix prefix = PrefixManager.getInstance().get(a[1]);
			if (prefix == null) {
				Util.sendWarning(s, a[1] + "(이)라는 칭호는 없습니다!");
				return false;
			}
			if (a.length == 3) {
				OfflinePlayer player = Bukkit.getOfflinePlayer(a[2]);
				if (PrefixManager.getInstance().takePrefix(player, prefix)) {
					Util.sendMessageWithPrefix(s, a[2] + "의 칭호를 성공적으로 뺏엇습니다.");
					if (player.isOnline())
						Util.sendMessageWithPrefix((Player) player,
								"칭호를 잃었습니다...");
				} else {
					Util.sendMessageWithPrefix(s, a[2] + "의 칭호를 뺏지 못하였습니다.");
				}
			} else {
				if (PrefixManager.getInstance().takePrefix((Player) s, prefix)) {
					Util.sendMessageWithPrefix(s, "칭호를 잃었습니다...");
				} else {
					Util.sendMessageWithPrefix(s, "칭호를 잃지 못했습니다.");
				}
			}
			return true;
		} else if (a[0].equalsIgnoreCase("vname")) {
			if (a.length == 1) {
				Util.sendWarning(s, "칭호의 이름을 적으세요!");
				return false;
			}
			Prefix prefix = PrefixManager.getInstance().get(a[1]);
			if (a.length == 2) {
				Util.sendMessageWithPrefix(s, "칭호의 이름을 초기화했습니다.");
				prefix.setPrefix(prefix.getPrefixName());
			} else {
				StringBuilder builder = new StringBuilder();
				for (int i = 2; i < a.length; i++) {
					builder.append(" " + a[i]);
				}
				prefix.setPrefix(ChatColor.translateAlternateColorCodes('&',
						builder.toString().trim()));
				Util.sendMessageWithPrefix(s, "칭호의 표기는 &e'&r"
						+ builder.toString().trim() + "&e'&a입니다.");
			}
			return true;
		} else if (a[0].equalsIgnoreCase("addopt")) {
			if (a.length == 1) {
				Util.sendWarning(s, "칭호의 이름을 적으세요!");
				return false;
			}
			if (a.length == 2) {
				Util.sendWarning(s, "추가할 옵션을 적으세요!");
				return false;
			}
			Prefix prefix = PrefixManager.getInstance().get(a[1]);
			if (prefix == null) {
				Util.sendWarning(s, a[1] + "(이)라는 칭호는 없습니다!");
				return false;
			}
			String key = a[2];
			StringBuilder arguments = new StringBuilder();
			if (a.length > 3)
				for (int i = 3; i < a.length; i++) {
					arguments.append(" " + a[i]);
				}
			PrefixEffect effect = PrefixEffect.createEffect(key, arguments
					.toString().trim());
			if (effect == null) {
				Util.sendWarning(s, a[2] + "(이)라는 옵션은 없습니다!");
				return false;
			}
			effect.setTarget(prefix);
			prefix.getEffects().add(effect);
			return true;
		}
		return false;
	}

	private void help(CommandSender s, String l) {
		Util.sendMessage(s, "&b!==========" + Util.getMessagePrefix()
				+ "&b==========!");
		Util.sendMessage(s, "&e<Required>&f, &e[Optional]");
		Util.sendMessage(s, "&6* /" + l + " create <이름> - 칭호를 만듭니다.");
		Util.sendMessage(s, "&6* /" + l + " addopt <이름> <옵션> <값> - 칭호를 수정합니다.");
		Util.sendMessage(s, "&6* /" + l + " list [페이지] - 만들어진 칭호를 봅니다.");
		Util.sendMessage(s, "&6* /" + l + " give <칭호명> [플레이어] - 칭호를 줍니다.");
		Util.sendMessage(s, "&6* /" + l + " take <칭호명> [플레이어] - 칭호를 뺏습니다.");
		Util.sendMessage(s, "&6* /" + l + " vname <칭호명> <표기> - 칭호의 표기를 정합니다.");
		Util.sendMessage(s, "&6* /" + l + " help - 도움말을 봅니다.");
	}

	@Override
	public void update(List<Prefix> data) {
		prefixes.clear();
		data.forEach((a) -> prefixes.add(a.getPrefixName()));
	}

}
