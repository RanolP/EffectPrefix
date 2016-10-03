package me.ranol.effectprefix.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

/**
 * @author Linmaru
 *
 */
public class RYamlConfiguration extends YamlConfiguration {
	File file;

	public void clear() {
		List<String> list = new ArrayList<>(getKeys(true));
		Collections.reverse(list);
		for (String s : list) {
			set(s, null);
		}
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	@Override
	public String saveToString() {
		String data = new String();
		boolean first = true;
		for (String s : super.saveToString().split("\\\\u")) {
			if (s.length() >= 4 && !first) {
				data += (char) Integer.parseInt(s.substring(0, 4), 16);
				if (s.length() >= 5) {
					data += s.substring(4);
				}
			} else {
				data += s;
				first = false;
			}
		}
		return data;
	}

	@Override
	public void save(File file) throws IOException {
		Validate.notNull(file, "File cannot be null");
		Files.createParentDirs(file);
		String data = saveToString();
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(file),
				Charsets.UTF_8)) {
			writer.write(data);
		}
	}

	public void load() {
		if (file == null)
			return;
		try {
			load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		if (file == null)
			return;
		try {
			save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void load(File file) throws FileNotFoundException, IOException,
			InvalidConfigurationException {
		Validate.notNull(file, "File cannot be null");
		FileInputStream stream = new FileInputStream(file);
		load(new InputStreamReader(stream, Charsets.UTF_8));
	}

	@Deprecated
	@Override
	public void load(InputStream stream) throws IOException,
			InvalidConfigurationException {
		Validate.notNull(stream, "Stream cannot be null");
		load(new InputStreamReader(stream, Charsets.UTF_8));
	}

	public static RYamlConfiguration loadConfiguration(File file) {
		Validate.notNull(file, "File cannot be null");
		RYamlConfiguration config = new RYamlConfiguration();
		try {
			config.load(file);
			config.setFile(file);
		} catch (FileNotFoundException e) {
		} catch (IOException | InvalidConfigurationException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, e);
		}
		return config;
	}

	@Deprecated
	public static RYamlConfiguration loadConfiguration(InputStream stream) {
		Validate.notNull(stream, "Stream cannot be null");
		RYamlConfiguration config = new RYamlConfiguration();
		try {
			config.load(stream);
		} catch (IOException | InvalidConfigurationException e) {
			Bukkit.getLogger().log(Level.SEVERE,
					"Cannot load configuration from stream", e);
		}
		return config;
	}

	public static RYamlConfiguration loadConfiguration(JavaPlugin plugin,
			String dir) {
		return loadConfiguration(new File(plugin.getDataFolder(), dir));
	}

	public static RYamlConfiguration loadConfiguration(String name) {
		return loadConfiguration(new File(name));
	}

	public static RYamlConfiguration loadConfiguration(Reader reader) {
		Validate.notNull(reader, "Stream cannot be null");
		RYamlConfiguration config = new RYamlConfiguration();
		try {
			config.load(reader);
		} catch (IOException | InvalidConfigurationException e) {
			Bukkit.getLogger().log(Level.SEVERE,
					"Cannot load configuration from stream", e);
		}
		return config;
	}
}
