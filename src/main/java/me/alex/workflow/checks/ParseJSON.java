package me.alex.workflow.checks;

import com.mojang.serialization.Codec;
import me.alex.workflow.utils.FileUtils;

import java.io.File;
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
			FileUtils.readJsonFile(file, Codec.PASSTHROUGH);
		} catch (Exception ex) {
			LOGGER.error("Failed to read JSON File: {}", file.getName(), ex);
			return false;
		}
		return true;
	}
}
