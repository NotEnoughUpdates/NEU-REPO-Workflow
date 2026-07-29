package me.alex.workflow.checks.constants;

import com.mojang.serialization.Codec;
import de.hysky.skyblocker.utils.CodecUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.alex.workflow.checks.AbstractCheck;
import me.alex.workflow.utils.FileUtils;
import me.alex.workflow.utils.Items;

import java.io.File;
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
			parents = FileUtils.readJsonFile(file, CODEC);
		} catch (Exception ex) {
			logFileIssue(file, "Failed to parse file: " + ex.getMessage());
			return false;
		}

		boolean isValid = true;
		Set<String> seenItems = new ObjectOpenHashSet<>();
		for (var entry : parents.entrySet()) {
			String key = entry.getKey();
			List<String> values = entry.getValue();

			if (seenItems.contains(key)) {
				logFileIssue(file, "Duplicate key: " + key);
			} else seenItems.add(key);
			if (!Items.ITEMS.contains(key)) {
				logFileIssue(file, "Unknown parent item: " + key);
				isValid = false;
			}

			Set<String> groupItems = new ObjectOpenHashSet<>();
			for (String item : values) {
				if (seenItems.contains(item)) {
					logFileIssue(file, "Duplicate item: " + item);
					isValid = false;
				} else seenItems.add(item);
				if (groupItems.contains(item)) {
					logFileIssue(file, "Duplicate item in group: " + item);
					isValid = false;
				} else groupItems.add(item);
				if (!Items.ITEMS.contains(item)) {
					logFileIssue(file, "Unknown item: " + item);
					isValid = false;
				}
			}
			seenItems.addAll(groupItems);
		}

		return isValid;
	}

	public static final Codec<Object2ObjectMap<String, List<String>>> CODEC = CodecUtils.object2ObjectMapCodec(Codec.STRING, Codec.STRING.listOf());
}
