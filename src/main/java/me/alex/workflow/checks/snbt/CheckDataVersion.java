package me.alex.workflow.checks.snbt;

import me.alex.workflow.checks.CheckData;
import me.alex.workflow.checks.ChildCheck;
import me.alex.workflow.checks.ParseSNBT;

public class CheckDataVersion implements ChildCheck<ParseSNBT.Item> {
	final String name = "Check SNBT Version";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean checkData(CheckData<ParseSNBT.Item> checkData) {
		ParseSNBT.Item data = checkData.data();
		String version = String.valueOf(data.tag().getCompoundOrEmpty("source").getIntOr("dataVersion", -1));
		String fileVersion = data.path().getName(data.path().getNameCount() - 2).toString();
		if (!version.equals(fileVersion)) {
			logFileIssue(checkData.file(), "File has wrong version in data - `%s` when should be `%s` !"
				.formatted(version, fileVersion));
			return false;
		}

		return true;
	}
}
