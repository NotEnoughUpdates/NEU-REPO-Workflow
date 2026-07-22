package me.alex.workflow.checks;

import me.alex.workflow.checks.snbt.CheckDataVersion;
import me.alex.workflow.checks.snbt.CheckEnchantLevel;
import me.alex.workflow.checks.snbt.CheckEnrichment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import static me.alex.workflow.Main.LOGGER;

public final class ParseSNBT implements ParentCheck<ParseSNBT.Item> {
	public static final List<Pattern> SNBT_PATTERN = List.of(Pattern.compile("itemsOverlay/.*\\.snbt"));

	final String name = "Parse SNBT";
	final List<ChildCheck<ParseSNBT.Item>> children = List.of(
		new CheckEnchantLevel(),
		new CheckDataVersion(),
		new CheckEnrichment()
	);

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<ChildCheck<Item>> getChildren() {
		return children;
	}

	@Override
	public List<Pattern> getFilePatterns() {
		return SNBT_PATTERN;
	}

	public static @Nullable CompoundTag readSnbt(Path path) {
		try {
			String content = Files.readString(path);
			return TagParser.parseCompoundFully(content);
		} catch (Exception ex) {
			LOGGER.error("Failed to read SNBT File: {}", path.getFileName(), ex);
			return null;
		}
	}

	@Override
	public @Nullable Item parseFile(File file) {
		CompoundTag tag = readSnbt(file.toPath());
		if (tag == null) return null;
		return new Item(tag, file.toPath(), file.getName().replace(".snbt", ""));
	}

	public record Item(CompoundTag tag, Path path, String internalName) {
	}
}
