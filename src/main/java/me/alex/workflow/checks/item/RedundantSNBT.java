package me.alex.workflow.checks.item;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.alex.workflow.checks.AbstractCheck;
import me.alex.workflow.checks.ParseSNBT;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static me.alex.workflow.Main.LOGGER;

public class RedundantSNBT implements AbstractCheck {
	final String name = "Redundant SNBT";
	Map<String, @Nullable Integer> latestOverlays = new Object2IntOpenHashMap<>();

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
			LOGGER.error("{}: File {} has outdated SNBT version {} (Newest: {})",
				getName(), file.getName(), oldest, newest);
			latestOverlays.put(file.getName(), newest);
			return false;
		}

		return true;
	}
}
