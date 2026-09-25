package me.alex.workflow.checks.snbt;

import me.alex.workflow.checks.CheckData;
import me.alex.workflow.checks.ChildCheck;
import me.alex.workflow.checks.ParseSNBT;

public class CheckColor implements ChildCheck<ParseSNBT.Item> {
	String name = "Check Color";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean checkData(CheckData<ParseSNBT.Item> checkData) {
		var data = checkData.data();
		int dyeColor = data.tag().getCompoundOrEmpty("components").getIntOr("minecraft:dyed_color", -1);
		if (dyeColor == 10511680) {
			logFileIssue(checkData.file(), "Invalid dyed color component!");
			return false;
		}
		return true;
	}
}
