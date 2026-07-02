package me.alex.workflow.checks.item;

import me.alex.workflow.checks.ChildCheck;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static me.alex.workflow.Main.LOGGER;

public class CheckNbtDisplay implements ChildCheck<ParseItem.Item> {
	String name = "Check NBT Display";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean checkData(ParseItem.Item data) {
		CompoundTag display = Objects.requireNonNull(data.nbtTag().get("display")).asCompound().orElseThrow();
		String displayName = Objects.requireNonNull(display.get("Name")).asString().orElseThrow();
		List<String> lore = Objects.requireNonNull(display.get("Lore")).asList().orElseThrow()
			.stream().map(tag -> tag.asString().orElseThrow()).toList();
		if (!displayName.equals(data.displayName())) {
			LOGGER.error("JSON display name does not match NBT Tag display name!");
			return false;
		}
		if (!lore.equals(data.lore())) {
			LOGGER.error("JSON lore does not match NBT Tag lore!");
			return false;
		}
		return true;
	}

	@Override
	public List<Pattern> getFilePatterns() {
		return List.of();
	}
}
