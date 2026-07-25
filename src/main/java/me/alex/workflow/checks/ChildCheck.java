package me.alex.workflow.checks;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

public interface ChildCheck<T> extends AbstractCheck {
	default boolean checkFile(File file) {
		throw new RuntimeException();
	}

	default List<Pattern> getFilePatterns() {
		throw new RuntimeException();
	}

	boolean checkData(CheckData<T> data);
}
