package me.alex.workflow.utils;

import me.alex.workflow.Main;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static me.alex.workflow.Main.LOGGER;
import static me.alex.workflow.Main.REPO_BASE_PATH;

public final class ChangedFiles {
	private static List<File> getAllFiles() {
		try {
			try (Stream<Path> files = Files.walk(Main.REPO_BASE_PATH)) {
				return files.map(Path::toFile).filter(File::isFile).toList();
			}
		} catch (IOException ex) {
			LOGGER.error("Failed to get changed files!", ex);
		}
		return List.of();
	}

	private static List<File> getPrFiles(@Nullable String org, @Nullable String repo, @Nullable Integer prNum) {
		if (org == null || repo == null || prNum == null) return List.of();
		List<String> files = GitHubApi.getChangedFiles(org, repo, prNum);
		return files.stream().map(REPO_BASE_PATH::resolve).map(Path::toFile).toList();
	}

	public static List<File> getChangedFiles() {
		if (GitHubContext.SHA == null || GitHubContext.REPO == null) return getAllFiles();
		return getPrFiles(GitHubContext.REPO.ownerName(), GitHubContext.REPO.repoName(), GitHubContext.PULL_NUM);
	}
}
