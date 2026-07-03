package me.alex.workflow.checks;

import net.minecraft.nbt.TagParser;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Pattern;

import static me.alex.workflow.Main.LOGGER;

public final class ParseSNBT implements AbstractCheck {
	public static final List<Pattern> SNBT_PATTERN = List.of(Pattern.compile("itemsOverlay/.*\\.snbt"));
	final String name = "Parse SNBT";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Pattern> getFilePatterns() {
		return SNBT_PATTERN;
	}

	@Override
	public boolean checkFile(File file) {
		try {
			String content = Files.readString(file.toPath());
			TagParser.parseCompoundFully(content);
		} catch (Exception ex) {
			LOGGER.error("Failed to read SNBT File: {}", file.getName(), ex);
			return false;
		}

		return true;
	}
}
