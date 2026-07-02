package me.alex.workflow.checks;

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
		if (data == null) return false;

		boolean res = true;
		for (ChildCheck<T> childCheck : getChildren()) {
			boolean bl = childCheck.checkData(data);
			res &= bl;
			if (!bl) {
				LOGGER.error("Check {}/{} failed for {}!", this.getName(), childCheck.getName(), file.getName());
			}
		}
		return res;
	}
}
