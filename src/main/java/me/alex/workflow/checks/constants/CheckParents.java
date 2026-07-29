package me.alex.workflow.checks.constants;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import de.hysky.skyblocker.utils.CodecUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.alex.workflow.checks.AbstractCheck;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class CheckParents implements AbstractCheck {
	final String name = "Parents";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Pattern> getFilePatterns() {
		return List.of(Pattern.compile("constants/parents.json"));
	}

	@Override
	public boolean checkFile(File file) {
		Object2ObjectMap<String, List<String>> parents;
		try {
			BufferedReader reader = Files.newBufferedReader(file.toPath());
			JsonElement jsonElement = JsonParser.parseReader(reader);
			parents = CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow();
		} catch (Exception ex) {
			logFileIssue(file, "Failed to parse file: " + ex.getMessage());
			return false;
		}

		Set<String> seenItems = new ObjectOpenHashSet<>();
		for (var entry : parents.entrySet()) {
			String key = entry.getKey();
			List<String> values = entry.getValue();

			if (seenItems.contains(key)) {
				logFileIssue(file, "Duplicate key: " + key);
			} else seenItems.add(key);

			Set<String> groupItems = new ObjectOpenHashSet<>();
			for (String item : values) {
				if (seenItems.contains(item)) {
					logFileIssue(file, "Duplicate item: " + item);
				} else seenItems.add(item);
				if (groupItems.contains(item)) {
					logFileIssue(file, "Duplicate item in group: " + item);
				} else groupItems.add(item);
			}
			seenItems.addAll(groupItems);
		}

		return true;
	}

	public static final Codec<Object2ObjectMap<String, List<String>>> CODEC = CodecUtils.object2ObjectMapCodec(Codec.STRING, Codec.STRING.listOf());
}
