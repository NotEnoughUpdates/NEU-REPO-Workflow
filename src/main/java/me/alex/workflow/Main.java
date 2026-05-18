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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public final class Main {
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final Path REPO_BASE_PATH = Path.of(System.getenv("REPO_BASE_PATH"));
	public static final ExecutorService EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

	public static final AtomicBoolean ERRORS_DETECTED = new AtomicBoolean(false);

	static AbstractCheck[] CHECKS = new AbstractCheck[]{
		new ParseSNBT(),
		new ParseJSON(),
	};

	static boolean fileMatches(File file, List<Pattern> patterns) {
		String filePath = REPO_BASE_PATH.relativize(file.toPath()).toString().replace("\\", "/");
		return patterns.stream().anyMatch(p -> p.matcher(filePath).matches());
	}

	static void checkFiles() {
		List<File> changedFiles = ChangedFiles.getChangedFiles();
		if (changedFiles.isEmpty()) return;
		List<CompletableFuture<Void>> tasks = new ArrayList<>();
		for (AbstractCheck check : CHECKS) {
			tasks.add(CompletableFuture.runAsync(() -> {
				List<File> matchingFiles = changedFiles.stream().filter(file ->
					fileMatches(file, check.getFilePatterns())
				).toList();
				if (matchingFiles.isEmpty()) return;
				if (!check.checkFiles(matchingFiles)) ERRORS_DETECTED.set(true);
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
		LOGGER.info("Done checking!");
		if (ERRORS_DETECTED.get()) System.exit(1);
	}
}
