package me.alex.workflow.checks.constants;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.alex.workflow.checks.AbstractCheck;
import me.alex.workflow.utils.FileUtils;
import me.alex.workflow.utils.Items;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class CheckSacks implements AbstractCheck {
	final String name = "Sacks";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Pattern> getFilePatterns() {
		return List.of(Pattern.compile("constants/sacks.json"));
	}

	@Override
	public boolean checkFile(File file) {
		SacksFile data;
		try {
			data = FileUtils.readJsonFile(file, SacksFile.CODEC);
		} catch (Exception ex) {
			logFileIssue(file, "Failed to parse file", ex.getMessage());
			return false;
		}

		boolean isValid = true;
		for (var entry : data.sacks.entrySet()) {
			List<String> unknownItems = new ArrayList<>();
			String sackName = entry.getKey();
			Sack sack = entry.getValue();
			if (!Items.ITEMS.contains(sack.item)) {
				logFileIssue(file, "Unknown Display Item for: " + sackName, sack.item + " is not a valid item");
				isValid = false;
			}

			for (String item : sack.contents) {
				if (!Items.ITEMS.contains(item)) {
					unknownItems.add(item);
					isValid = false;
				}
			}

			if (!unknownItems.isEmpty()) {
				logFileIssue(file, "Unknown Sack Items for: " + sackName, unknownItems.toArray(new String[0]));
			}
		}

		return isValid;
	}

	public record SacksFile(Map<String, Sack> sacks) {
		public static final Codec<SacksFile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.unboundedMap(Codec.STRING, Sack.CODEC).fieldOf("sacks").forGetter(SacksFile::sacks)
		).apply(instance, SacksFile::new));
	}

	private record Sack(String item, List<String> contents) {
		public static final Codec<Sack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("item").forGetter(Sack::item),
			Codec.STRING.listOf().fieldOf("contents").forGetter(Sack::contents)
		).apply(instance, Sack::new));
	}
}
