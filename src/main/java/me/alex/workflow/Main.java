package me.alex.workflow;

import com.mojang.logging.LogUtils;
import me.alex.workflow.checks.AbstractCheck;
import me.alex.workflow.checks.ParseJSON;
import me.alex.workflow.checks.ParseSNBT;
import me.alex.workflow.utils.ChangedFiles;
import net.minecraft.SharedConstants;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Main {
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final Path REPO_BASE_PATH = Path.of(System.getenv("REPO_BASE_PATH"));
	public static final ExecutorService EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

	static AbstractCheck[] CHECKS = new AbstractCheck[]{
		new ParseSNBT(),
		new ParseJSON(),
	};

	static void checkFiles() {
		List<File> changedFiles = ChangedFiles.getChangedFiles();
		if (changedFiles.isEmpty()) return;
		List<CompletableFuture<Void>> tasks = new ArrayList<>();
		for (AbstractCheck check : CHECKS) {
			tasks.add(CompletableFuture.runAsync(() -> {
				List<File> matchingFiles = changedFiles.stream().filter(file -> check.getFilePatterns().stream()
					.anyMatch(pattern -> pattern.matcher(file.getPath()).matches())
				).toList();
				if (matchingFiles.isEmpty()) return;
				check.checkFiles(matchingFiles);
			}, EXECUTOR));
		}
		CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new)).join();
	}

	static void main() {
		LOGGER.info("Initializing...");
		SharedConstants.tryDetectVersion();
//		Bootstrap.bootStrap();
		LOGGER.info("Done initializing!");
		checkFiles();
	}
}
