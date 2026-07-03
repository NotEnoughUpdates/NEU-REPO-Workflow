package me.alex.workflow.checks;

import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Pattern;

import static me.alex.workflow.Main.LOGGER;

public final class ParseJSON implements AbstractCheck {
	final String name = "Parse JSON";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Pattern> getFilePatterns() {
		return List.of(Pattern.compile(".*\\.json"));
	}

	@Override
	public boolean checkFile(File file) {
		try {
			BufferedReader reader = Files.newBufferedReader(file.toPath());
			JsonParser.parseReader(reader);
		} catch (Exception ex) {
			LOGGER.error("Failed to read JSON File: {}", file.getName(), ex);
			return false;
		}
		return true;
	}
}
