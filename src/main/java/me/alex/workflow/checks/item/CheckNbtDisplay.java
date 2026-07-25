package me.alex.workflow.checks.item;

import me.alex.workflow.checks.CheckData;
import me.alex.workflow.checks.ChildCheck;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.Objects;

public class CheckNbtDisplay implements ChildCheck<ParseItems.Item> {
	final String name = "Check NBT Display";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean checkData(CheckData<ParseItems.Item> checkData) {
		var data = checkData.data();
		CompoundTag display = Objects.requireNonNull(data.nbtTag().get("display")).asCompound().orElseThrow();
		String displayName = Objects.requireNonNull(display.get("Name")).asString().orElseThrow();
		List<String> lore = Objects.requireNonNull(display.get("Lore")).asList().orElseThrow()
			.stream().map(tag -> tag.asString().orElseThrow()).toList();
		if (!displayName.equals(data.displayName())) {
			logFileIssue(checkData.file(), "JSON display name does not match NBT Tag display name!");
			return false;
		}
		if (!lore.equals(data.lore())) {
			logFileIssue(checkData.file(), "JSON lore does not match NBT Tag lore!");
			return false;
		}
		return true;
	}
}
