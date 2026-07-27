package me.alex.workflow.checks.snbt;

import me.alex.workflow.checks.CheckData;
import me.alex.workflow.checks.ChildCheck;
import me.alex.workflow.checks.ParseSNBT;
import me.alex.workflow.checks.item.CheckProhibitedNbt;
import net.minecraft.nbt.CompoundTag;

import java.util.Set;

public class CheckProhibitedSnbt implements ChildCheck<ParseSNBT.Item> {
	final String name = "Prohibited SNBT";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean checkData(CheckData<ParseSNBT.Item> checkData) {
		CompoundTag data = checkData.data().tag()
			.getCompoundOrEmpty("components")
			.getCompoundOrEmpty("minecraft:custom_data");
		Set<String> nbtKeys = data.keySet();
		boolean success = true;
		for (String key : CheckProhibitedNbt.prohibitedKeys) {
			if (nbtKeys.contains(key)) {
				logFileIssue(checkData.file(), "Custom Data contains prohibited key: %s".formatted(key));
				success = false;
			}
		}
		return success;
	}
}

