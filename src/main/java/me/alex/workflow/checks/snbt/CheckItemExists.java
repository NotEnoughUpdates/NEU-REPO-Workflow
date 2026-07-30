package me.alex.workflow.checks.snbt;

import me.alex.workflow.checks.CheckData;
import me.alex.workflow.checks.ChildCheck;
import me.alex.workflow.checks.ParseSNBT;
import me.alex.workflow.utils.Items;

public class CheckItemExists implements ChildCheck<ParseSNBT.Item> {
	final String name = "Ensure Item Exists";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean checkData(CheckData<ParseSNBT.Item> checkData) {
		if (Items.ITEMS.contains(checkData.data().internalName())) return true;
		logFileIssue(checkData.file(), "No item found for SNBT file!");
		return false;
	}
}
