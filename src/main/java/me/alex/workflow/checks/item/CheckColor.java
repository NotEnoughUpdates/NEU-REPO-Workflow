package me.alex.workflow.checks.item;

import me.alex.workflow.checks.CheckData;
import me.alex.workflow.checks.ChildCheck;

public class CheckColor implements ChildCheck<ParseItems.Item> {
	String name = "Check Color";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean checkData(CheckData<ParseItems.Item> checkData) {
		var data = checkData.data();
		int dyeColor = data.nbtTag().getCompoundOrEmpty("extraAttributes").getIntOr("color", -1);
		if (dyeColor == 10511680) {
			logFileIssue(checkData.file(), "Invalid color in NBT!");
			return false;
		}
		return true;
	}
}
