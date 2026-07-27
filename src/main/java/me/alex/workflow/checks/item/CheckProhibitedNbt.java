package me.alex.workflow.checks.item;

import me.alex.workflow.checks.CheckData;
import me.alex.workflow.checks.ChildCheck;

import java.util.List;
import java.util.Set;

public class CheckProhibitedNbt implements ChildCheck<ParseItems.Item> {
	public static final List<String> prohibitedKeys = List.of("timestamp", "uuid");

	final String name = "Prohibited NBT";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean checkData(CheckData<ParseItems.Item> checkData) {
		Set<String> nbtKeys = checkData.data().nbtTag().keySet();
		boolean success = true;
		for (String key : prohibitedKeys) {
			if (nbtKeys.contains(key)) {
				logFileIssue(checkData.file(), "NBT Tag contains prohibited key: %s".formatted(key));
				success = false;
			}
		}
		return success;
	}
}

