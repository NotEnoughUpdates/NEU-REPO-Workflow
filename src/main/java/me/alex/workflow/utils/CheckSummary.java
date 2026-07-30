package me.alex.workflow.utils;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static me.alex.workflow.Main.LOGGER;
import static me.alex.workflow.Main.REPO_BASE_PATH;

public final class CheckSummary {
	private static final Object2ObjectOpenHashMap<Path, List<FileIssue>> FILE_ISSUES = new Object2ObjectOpenHashMap<>();

	public static void addFileIssue(File file, String checkName, String issue, String... details) {
		FILE_ISSUES.compute(file.toPath(), (k, v) -> {
			if (v == null) v = new ArrayList<>();
			v.add(new FileIssue(checkName, issue, details));
			return v;
		});
	}

	public static void addSummary() {
		if (GitHubContext.STEP_SUMMARY == null) return;
		if (FILE_ISSUES.isEmpty()) return;
		LOGGER.info("Writing check summary...");

		File file = new File(GitHubContext.STEP_SUMMARY);
		try {
			file.createNewFile();
		} catch (IOException ex) {
			LOGGER.error("Failed to create summary file", ex);
		}

		try (OutputStream stream = new FileOutputStream(file)) {
			PrintWriter writer = new PrintWriter(stream, false, StandardCharsets.UTF_8);
			writeIssues(writer);
			writer.flush();
		} catch (IOException ex) {
			LOGGER.error("Failed to write check summary!", ex);
		}

		LOGGER.info("Done writing check summary!");
	}

	public static void writeIssues(PrintWriter writer) {
		writer.println("# Files with Issues (%s): ".formatted(FILE_ISSUES.size()));
		writer.write('\n');
		FILE_ISSUES.forEach((path, issues) -> {
			Path relativePath = REPO_BASE_PATH.relativize(path);
			writer.println("## %s".formatted(relativePath));
			for (FileIssue issue : issues) {
				writer.println("* %s (%s)".formatted(issue.text, issue.check));
				for (String detail : issue.details) {
					writer.println("  * %s".formatted(detail));
				}
			}
			writer.write('\n');
		});
	}

	private record FileIssue(String check, String text, String[] details) {
	}
}
