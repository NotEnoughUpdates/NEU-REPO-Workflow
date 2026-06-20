package me.alex.workflow.checks;

import net.minecraft.nbt.NbtUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Pattern;

import static me.alex.workflow.Main.LOGGER;

public final class ParseSNBT implements AbstractCheck {
	String name = "Parse SNBT";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Pattern> getFilePatterns() {
		return List.of(Pattern.compile(".*\\.snbt"));
	}

	@Override
	public boolean checkFile(File file) {
		try {
			String content = Files.readString(file.toPath());
			NbtUtils.snbtToStructure(content);
		} catch (Exception ex) {
			LOGGER.error("Failed to read SNBT File: {}", file.getName(), ex);
			return false;
		}

		return true;
	}
}
