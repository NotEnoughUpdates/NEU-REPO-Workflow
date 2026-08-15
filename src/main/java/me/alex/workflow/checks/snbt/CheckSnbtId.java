package me.alex.workflow.checks.snbt;

import me.alex.workflow.checks.CheckData;
import me.alex.workflow.checks.ChildCheck;
import me.alex.workflow.checks.ParseSNBT;

import java.util.Optional;

public class CheckSnbtId implements ChildCheck<ParseSNBT.Item> {
	final String name = "Check SNBT Id";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean checkData(CheckData<ParseSNBT.Item> checkData) {
		var data = checkData.data();
		Optional<String> snbtId = data.tag()
			.getCompoundOrEmpty("components")
			.getCompoundOrEmpty("minecraft:custom_data")
			.getString("id");

		if (snbtId.isEmpty()) {
			logFileIssue(checkData.file(), "Missing Item Id!");
			return false;
		}

		if (snbtId.get().contains(";")) {
			logFileIssue(checkData.file(), "Item Id contains an illegal character!", snbtId.get());
			return false;
		}

		return true;
	}
}
