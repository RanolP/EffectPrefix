package me.ranol.effectprefix.api.effects;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.ranol.effectprefix.EffectPrefix;
import me.ranol.effectprefix.api.Argument;
import me.ranol.effectprefix.api.Prefix;
import me.ranol.effectprefix.designpatterns.Observer;
import me.ranol.effectprefix.utils.Util;

import org.bukkit.Bukkit;

public class EffectManager extends Observer<List<PrefixEffect>> {
	private static final long serialVersionUID = -8321525303994506439L;
	private static List<PrefixEffect> classes = new ArrayList<>();
	private static EffectManager Instance;

	public static EffectManager getInstance() {
		if (Instance == null)
			synchronized (EffectManager.class) {
				Instance = new EffectManager();
			}
		return Instance;
	}

	/**
	 * @param clazz
	 *            - 등록할 클래스입니다. 클래스는 PrefixEffect를 상속받아야 하며, 인수를 받지 않는 생성자가
	 *            존재해야합니다.
	 */
	public static void register(Class<? extends PrefixEffect> clazz) {
		try {
			final PrefixEffect temp = clazz.newInstance();
			Field[] fields = clazz.getDeclaredFields();
			if (!parseRequired(temp, fields))
				return;
			if (!parseRequireOne(temp, fields))
				return;
			parseCompat(temp, fields);
			classes.add(temp);
			getInstance().updateAll(classes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static <T extends PrefixEffect> boolean parseRequired(T effect,
			Field[] fields) throws Exception {
		Class<? extends PrefixEffect> clazz = effect.getClass();
		RequirePlugins req = clazz.getAnnotation(RequirePlugins.class);
		if (req == null)
			return true;
		for (String s : req.value()) {
			String[] parse = parse(s);
			if (Bukkit.getPluginManager().getPlugin(parse[0]) == null) {
				Util.errors(clazz.getSimpleName() + " 활성화 도중 오류를 발견했습니다.",
						"풀 클래스 명: ", clazz.getClass().getName() + ".class",
						"커맨드: " + effect.getCommand(), "오류: 필요 플러그인을 찾지 못함: "
								+ parsePluginWithAuthor(parse));
				return false;
			} else {
				for (Field f : fields) {
					if (result(f, parse[0])) {
						Util.warning(effect.toString() + "은(는) 플러그인 "
								+ parsePluginWithAuthor(parse)
								+ "와(과) 연동되었습니다!");
						if (!parse[2].isEmpty())
							Util.warning("#Comment: " + parse[2]);
					}
				}
			}
		}
		return true;
	}

	private static boolean result(Field f, String name) {
		try {
			ResultTo result = f.getAnnotation(ResultTo.class);
			if (result != null) {
				if (name.equalsIgnoreCase(result.value())) {
					f.setAccessible(true);
					f.setBoolean(null, true);
					return true;
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	private static <T extends PrefixEffect> void parseCompat(T effect,
			Field[] fields) throws Exception {
		CompatiblePlugins comp = effect.getClass().getAnnotation(
				CompatiblePlugins.class);
		if (comp == null)
			return;
		for (String s : comp.value()) {
			String[] parse = parse(s);
			if (Bukkit.getPluginManager().getPlugin(parse[0]) != null) {
				Util.warning("커맨드 " + effect.toString() + " 은(는) " + parse[0]
						+ (parse[1].isEmpty() ? parse[1] : "by " + parse[1])
						+ "와 호환됩니다!");
				if (!parse[2].isEmpty())
					Util.warning("#Comment: " + parse[2]);
			}
		}
	}

	private static boolean parseRequireOne(PrefixEffect temp, Field[] fields) {
		Class<?> clazz = temp.getClass();
		RequireOnePlugins one = clazz.getAnnotation(RequireOnePlugins.class);
		if (one == null)
			return true;
		List<String> messages = new ArrayList<>();
		messages.add(clazz.getSimpleName() + " 활성화 도중 오류를 발견했습니다.");
		messages.add("풀 클래스 명: " + clazz.getName() + ".class");
		messages.add("커맨드: " + temp.getCommand());
		messages.add("오류: 필요 플러그인을 찾지 못함: ");
		for (String s : one.value()) {
			if (!EffectPrefix.config().contains(
					clazz.getSimpleName() + ".uncheckPlugins")) {
				EffectPrefix.config().set(
						clazz.getSimpleName() + ".uncheckPlugins",
						new ArrayList<String>());
				EffectPrefix.config().save();
			}
			String[] parse = parse(s);
			if (EffectPrefix.config()
					.getStringList(clazz.getSimpleName() + ".uncheckPlugins")
					.contains(parse[0]))
				continue;
			if (Bukkit.getPluginManager().getPlugin(parse[0]) == null) {
				messages.add(parsePluginWithAuthor(parse));
				continue;
			} else {
				for (Field f : fields) {
					if (result(f, "hooked") || result(f, parse[0])) {
						Util.errors(temp.toString() + " 은(는) 플러그인 "
								+ parsePluginWithAuthor(parse)
								+ "와(과) 연동되었습니다!", "원치 않는 연동이라면 컨피그의 ",
								clazz.getSimpleName()
										+ ".uncheckPlugins 목록에 추가해주세요 :)");
						if (!parse[2].isEmpty())
							Util.warning("#Comment: " + parse[2]);
					}
				}
				return true;
			}
		}
		messages.forEach((s) -> Util.warning(s));
		return false;
	}

	private static String[] parse(String s) {
		if (!s.contains("|")) {
			return new String[] { s, "", "" };
		} else if (s.split("\\|").length > 3) {
			return s.split("\\|");
		} else {
			return new String[] { s.split("\\|")[0], s.split("\\|")[1], "" };
		}
	}

	private static String parsePluginWithAuthor(String[] a) {
		return a[1].isEmpty() ? a[0] : a[0] + " by " + a[1];
	}

	public static Collection<String> getEffects() {
		List<String> result = new ArrayList<>();
		classes.forEach((e) -> result.add(e.getCommand()));
		return result;
	}

	public static PrefixEffect createEffect(String name, String arguments) {
		for (PrefixEffect effect : classes) {
			if (effect.getCommand().equals(name)) {
				return replaceVars(effect.getClass(), parseVars(arguments));
			}
		}
		return null;
	}

	/**
	 * @param original
	 *            - 인수들을 파싱할 변수입니다.
	 * @return 파싱된 변수들을 반환합니다.
	 */
	public static List<Object> parseVars(String original) {
		int open = -1;
		List<Object> result = new ArrayList<>();
		String copied = original;
		int searched = 0;
		while (true) {
			int index = copied.indexOf(' ');
			if (index == -1) {
				if (open != -1)
					result.add(parsing(original.substring(open,
							original.length() - 1)));
				else
					result.add(parsing(copied));
				break;
			}
			String get = copied.substring(0, index);
			searched += get.length() + 1;
			copied = copied.substring(index).trim();
			if (open != -1) {
				result.add(parsing(original.substring(open, searched - 2)));
				open = -1;
				continue;
			}
			if (get.startsWith("\'")) {
				open = searched - get.length();
			} else {
				result.add(parsing(get));
			}
		}
		return result;
	}

	private static Object parsing(String s) {
		if (s.equals("true") || s.equals("false")) {
			return Boolean.parseBoolean(s);
		} else if (s.matches("[-]?[0-9]+")) {
			return Integer.parseInt(s);
		} else if (s.matches("[-]?[0-9]+.[0-9]+")) {
			return Double.parseDouble(s);
		}
		return s;
	}

	/**
	 * @param clazz
	 *            - The Target Class
	 * @param values
	 *            - The Arguments
	 * @return PrefixEffect with arguments
	 */
	public static <T extends PrefixEffect> T replaceVars(Class<T> clazz,
			List<Object> values) {
		try {
			T result = clazz.newInstance();
			for (Field field : clazz.getDeclaredFields()) {
				Argument arg = field.getAnnotation(Argument.class);
				if (arg == null)
					continue;
				field.setAccessible(true);
				field.set(result, values.get(arg.value() - 1));
			}
			if (result != null)
				result.initialize();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T extends PrefixEffect> boolean hasOption(Class<T> clazz,
			Prefix prefix) {
		for (PrefixEffect eff : prefix.getEffects()) {
			if (eff.getClass().getSimpleName().equals(clazz.getSimpleName()))
				return true;
		}
		return false;
	}

	public static List<Prefix> hasOptionSet(
			Class<? extends PrefixEffect> clazz, List<Prefix> prefix) {
		List<Prefix> list = new ArrayList<>();
		prefix.stream().filter((p) -> hasOption(clazz, p)).map(p -> p)
				.forEach(p -> list.add(p));
		return list;
	}

	public static <T extends PrefixEffect> int getOptionIndex(Class<T> clazz,
			Prefix prefix) {
		for (int i = 0; i < prefix.getEffects().size(); i++) {
			if (prefix.getEffects().get(i).getClass().getSimpleName()
					.equals(clazz.getSimpleName()))
				return i;
		}
		return -1;
	}
}
