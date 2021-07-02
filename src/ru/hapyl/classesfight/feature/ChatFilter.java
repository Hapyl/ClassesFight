package ru.hapyl.classesfight.feature;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ChatFilter {

	private final Set<String> regex;

	public ChatFilter(JavaPlugin init) {
		this.regex = new HashSet<>();
		this.initWords();
	}

	public String test(String str) {
		return str;
	}

	private void initWords() {
		add("fuck", "nword");
	}

	private void add(String... str) {
		this.regex.addAll(Arrays.asList(str));
	}

}
