package me.alex.workflow.checks;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
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
		CompoundTag tag;
		try {
			String content = Files.readString(file.toPath());
			tag = TagParser.parseCompoundFully(content);
		} catch (Exception ex) {
			LOGGER.error("Failed to read SNBT File: {}", file.getName(), ex);
			return false;
		}

		String version = String.valueOf(tag.getCompoundOrEmpty("source").getIntOr("dataVersion", -1));
		Path path = file.toPath();
		String fileVersion = path.getName(path.getNameCount() - 2).toString();
		if (!version.equals(fileVersion)) {
			LOGGER.error("{}: File {}/{} has wrong version in data!", getName(), fileVersion, file.getName());
			return false;
		}

		return true;
	}
}
