package me.alex.workflow.checks.item;

import me.alex.workflow.checks.CheckData;
import me.alex.workflow.checks.ChildCheck;

public class CheckNbtId implements ChildCheck<ParseItems.Item> {
	final String name = "Check NBT Id";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean checkData(CheckData<ParseItems.Item> checkData) {
		var data = checkData.data();
		String nbtId = data.nbtTag().getCompoundOrEmpty("ExtraAttributes").getStringOr("id", "");
		return data.internalName().equals(nbtId);
	}
}

