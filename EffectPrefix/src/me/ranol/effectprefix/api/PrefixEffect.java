package me.ranol.effectprefix.api;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.ranol.effectprefix.EffectPrefix;
import me.ranol.effectprefix.designpatterns.Observer;
import me.ranol.effectprefix.effects.EffAddHealth;
import me.ranol.effectprefix.effects.EffHoloVisible;
import me.ranol.effectprefix.effects.EffXpGet;
import me.ranol.effectprefix.utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;

public abstract class PrefixEffect implements Listener, Serializable {
	private static final long serialVersionUID = -741790439302165315L;
	private static List<PrefixEffect> classes = new ArrayList<>();
	private Prefix target;
	private static Observer<List<PrefixEffect>> ob = new Observer<List<PrefixEffect>>();

	public static void registeration() {
		register(EffXpGet.class);
		register(EffAddHealth.class);
		register(EffHoloVisible.class);
	}

	public static Observer<List<PrefixEffect>> getObserver() {
		return ob;
	}

	public static Collection<String> getEffects() {
		List<String> result = new ArrayList<>();
		classes.forEach((e) -> result.add(e.getCommand()));
		return result;
	}

	public abstract Material getMainIcon();

	/**
	 * @param clazz
	 *            - 등록할 클래스입니다. 클래스는 PrefixEffect를 상속받아야 하며, 인수를 받지 않는 생성자가
	 *            존재해야합니다.
	 */
	public static void register(Class<? extends PrefixEffect> clazz) {
		try {
			PrefixEffect temp = clazz.newInstance();
			RequirePlugins req = clazz.getAnnotation(RequirePlugins.class);
			if (req != null) {
				Field[] fields = clazz.getDeclaredFields();
				for (String s : req.value()) {
					if (Bukkit.getPluginManager().getPlugin(
							s.contains("|") ? s.split("\\|")[0] : s) == null) {
						Util.warning(clazz.getSimpleName()
								+ " 활성화 도중 오류를 발견했습니다.");
						Util.warning("풀 클래스 명: " + clazz.getName() + ".class");
						Util.warning("커맨드: " + temp.getCommand());
						Util.warning("오류: 필요 플러그인을 찾지 못함: "
								+ (s.contains("|") ? s.split("\\|")[0] + " by "
										+ s.split("\\|")[1] : s));
						return;
					} else {
						for (Field f : fields) {
							f.setAccessible(true);
							ResultTo anno = f.getAnnotation(ResultTo.class);
							if (anno != null) {
								if (s.equals(anno.value()))
									f.setBoolean(null, true);
							}
						}
					}
				}
			}
			CompatiblePlugins comp = clazz
					.getAnnotation(CompatiblePlugins.class);
			if (comp != null) {
				for (String s : comp.value()) {
					if (Bukkit.getPluginManager().getPlugin(
							s.contains("|") ? s.split("\\|")[0] : s) != null) {
						Util.warning("커맨드 "
								+ temp.getCommand()
								+ "("
								+ clazz.getSimpleName()
								+ ") 은(는) "
								+ (s.contains("|") ? s.split("\\|")[0] + " by "
										+ s.split("\\|")[1] : s) + "와 호환됩니다!");
					}
				}
			}
			classes.add(temp);
			ob.updateAll(classes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param name
	 *            - Effect의 명칭입니다.
	 * @param arguments
	 *            - Effect가 받는 인수들입니다.
	 * @return - 다음 작업을 수행한 PrefixEffect를 반환합니다.
	 */
	public static PrefixEffect createEffect(String name, String arguments) {
		for (PrefixEffect effect : classes) {
			if (effect.getCommand().equals(name)) {
				return replaceVars(effect.getClass(), parseVars(arguments));
			}
		}
		return null;
	}

	public void setTarget(Prefix target) {
		this.target = target;
	}

	public Prefix getTarget() {
		return target;
	}

	public boolean isSelected(OfflinePlayer player) {
		return PrefixManager.getInstance().isSelected(player, target);
	}

	public PrefixEffect() {
		EffectPrefix.getInstance().registerEvents(this);
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

	public abstract void initialize();

	protected static float parse(float f) {
		return Float.parseFloat(String.format("%G", f));
	}

	protected static double parse(double d) {
		return Double.parseDouble(String.format("%G", d));
	}

	/**
	 * @return command without arguments
	 */
	public abstract String getCommand();

	/**
	 * @param vars
	 *            - replace arguments.
	 * @return replaced description
	 */
	public abstract String getDescription();
}
