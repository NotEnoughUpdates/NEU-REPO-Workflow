package me.alex.workflow.checks.item;

import me.alex.workflow.checks.CheckData;
import me.alex.workflow.checks.ChildCheck;

public class CheckInternalName implements ChildCheck<ParseItems.Item> {
	final String name = "Validate Internal Name";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean checkData(CheckData<ParseItems.Item> checkData) {
		String fileName = checkData.file().getName().replace(".json", "");
		String internalName = checkData.data().internalName();
		if (!fileName.equals(internalName)) {
			logFileIssue(checkData.file(), "Internal name does not match file name!");
			return false;
		}
		return true;
	}
}
