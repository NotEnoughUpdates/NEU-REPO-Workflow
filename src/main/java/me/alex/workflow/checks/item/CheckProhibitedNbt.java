package me.alex.workflow.checks.item;

import me.alex.workflow.checks.CheckData;
import me.alex.workflow.checks.ChildCheck;

import java.util.Set;

public class CheckProhibitedNbt implements ChildCheck<ParseItems.Item> {
	public static final Set<String> prohibitedKeys = Set.of(
		"timestamp", "uuid", "winning_bid",
		"blood_god_kills"
	);

	final String name = "Prohibited NBT";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean checkData(CheckData<ParseItems.Item> checkData) {
		Set<String> nbtKeys = checkData.data().nbtTag().getCompoundOrEmpty("ExtraAttributes").keySet();
		boolean success = true;
		for (String key : nbtKeys) {
			if (prohibitedKeys.contains(key)) {
				logFileIssue(checkData.file(), "NBT Tag contains prohibited key: %s".formatted(key));
				success = false;
			}
		}
		return success;
	}
}

