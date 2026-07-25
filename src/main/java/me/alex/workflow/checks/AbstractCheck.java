package me.alex.workflow.checks;

import me.alex.workflow.utils.CheckSummary;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import static me.alex.workflow.Main.LOGGER;

public interface AbstractCheck {
	String getName();

	List<Pattern> getFilePatterns();

	boolean checkFile(File file);

	default boolean logOnFailure() {
		return true;
	}

	default boolean checkFiles(List<File> files) {
		boolean res = true;
		boolean shouldLogFailure = logOnFailure();
		for (File file : files) {
			LOGGER.debug("{}: Checking file {}", this.getClass().getSimpleName(), file.getName());
			boolean bl = checkFile(file);
			if (!bl && shouldLogFailure) {
				logFileIssue(file, "Check failed!");
			}
			res &= bl;
		}
		return res;
	}

	default void logFileIssue(File file, String issue) {
		LOGGER.error("Check {}: {}", getName(), issue);
		CheckSummary.addFileIssue(file, getName(), issue);
	}
}
