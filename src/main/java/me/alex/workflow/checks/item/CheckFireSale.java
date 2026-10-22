package me.alex.workflow.checks.item;

import me.alex.workflow.checks.CheckData;
import me.alex.workflow.checks.ChildCheck;

public class CheckFireSale implements ChildCheck<ParseItems.Item> {
	final String name = "Check Fire Sale";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean checkData(CheckData<ParseItems.Item> checkData) {
		boolean success = true;
		if (checkData.data().displayName().equals("§6§lUPCOMING SALE")) {
			logFileIssue(checkData.file(), "Fire Sale GUI Item Name");
			success = false;
		}
		if (checkData.data().lore().stream().anyMatch(x -> x.equals("§8Fire Sale"))) {
			logFileIssue(checkData.file(), "Contains Fire Sale GUI Lore");
			success = false;
		}
		return success;
	}
}
