package me.ranol.effectprefix.api;

import java.util.ArrayList;
import java.util.List;

public class Prefix {
	private List<PrefixEffect> effects = new ArrayList<>();
	private String prefixName;
	private String prefix;
	private String description = "설명 없음";

	public Prefix(String prefixName) {
		this.prefixName = prefixName;
		this.prefix = prefixName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrefixName() {
		return prefixName;
	}

	public List<PrefixEffect> getEffects() {
		return effects;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof Prefix))
			return false;
		return ((Prefix) obj).prefixName.equals(prefixName);
	}
}
