package me.alex.workflow.checks;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class RedundantSNBT implements AbstractCheck {
	final String name = "Redundant SNBT";
	final Map<String, @Nullable Integer> latestOverlays = new Object2IntOpenHashMap<>();

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Pattern> getFilePatterns() {
		return ParseSNBT.SNBT_PATTERN;
	}

	@Override
	public boolean checkFile(File file) {
		Path path = file.toPath();
		Integer value = latestOverlays.get(file.getName());
		int thisVersion = Integer.parseInt(path.getName(path.getNameCount() - 2).toString());

		if (value == null) {
			latestOverlays.put(file.getName(), thisVersion);
			return true;
		} else if (thisVersion != value) {
			int newest = Math.max(thisVersion, value);
			int oldest = Math.min(thisVersion, value);
			logFileIssue(file, "Outdated SNBT version %s (Newest: %s)".formatted(oldest, newest));
			latestOverlays.put(file.getName(), newest);
			return false;
		}

		return true;
	}
}
