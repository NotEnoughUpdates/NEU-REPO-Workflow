package me.alex.workflow.checks;

import java.io.File;

public interface ChildCheck<T> extends AbstractCheck {
	default boolean checkFile(File file) {
		throw new RuntimeException();
	}

	boolean checkData(T data);
}
