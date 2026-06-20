package me.alex.workflow.checks;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import static me.alex.workflow.Main.LOGGER;

public interface AbstractCheck {
	String getName();

	List<Pattern> getFilePatterns();

	boolean checkFile(File file);

	default boolean checkFiles(List<File> file) {
		boolean res = true;
		for (File f : file) {
			LOGGER.debug("{}: Checking file {}", this.getClass().getSimpleName(), f.getName());
			res &= checkFile(f);
		}
		return res;
	}
}
