package me.alex.workflow.checks;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;

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
			BufferedReader bufferedReader = Files.newBufferedReader(file.toPath());
			JsonReader jsonReader = new JsonReader(bufferedReader);
			jsonReader.setStrictness(Strictness.STRICT);
			if (JsonParser.parseReader(jsonReader) == null) return false;
		} catch (JsonSyntaxException ex) {
			logFileIssue(file, "Invalid JSON: " + ex.getMessage());
			return false;
		} catch (Exception ex) {
			LOGGER.error("Failed to read JSON File: {}", file.getName(), ex);
			return false;
		}
		return true;
	}
}
