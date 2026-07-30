package me.alex.workflow.checks;

import me.alex.workflow.utils.CheckSummary;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.List;

import static me.alex.workflow.Main.LOGGER;

public interface ParentCheck<T> extends AbstractCheck {
	List<ChildCheck<T>> getChildren();

	@Nullable T parseFile(File file);

	@Override
	default boolean checkFile(File file) {
		T data = parseFile(file);
		if (data == null) {
			logFileIssue(file, "Failed to parse!");
			return false;
		}

		boolean res = true;
		for (ChildCheck<T> childCheck : getChildren()) {
			boolean bl = childCheck.checkData(new CheckData<>(file, data));
			res &= bl;
			if (!bl) {
				logChildFileIssue(file, childCheck.getName(), "Check failed!");
			}
		}
		return res;
	}

	default void logChildFileIssue(File file, String childName, String issue, String... details) {
		String name = "%s/%s".formatted(getName(), childName);
		LOGGER.error("Check {}: {}", name, issue);
		CheckSummary.addFileIssue(file, name, issue, details);
	}

	@Override
	default boolean logOnFailure() {
		return false;
	}
}
