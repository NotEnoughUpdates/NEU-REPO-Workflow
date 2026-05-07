package me.alex.workflow;

import com.mojang.logging.LogUtils;
import me.alex.workflow.checks.AbstractCheck;
import me.alex.workflow.checks.ParseJSON;
import me.alex.workflow.checks.ParseSNBT;
import me.alex.workflow.utils.ChangedFiles;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class Main {
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final Path REPO_BASE_PATH = Path.of(System.getenv("REPO_BASE_PATH"));
	public static final ExecutorService EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

	static AbstractCheck[] CHECKS = new AbstractCheck[]{
		new ParseSNBT(),
		new ParseJSON(),
	};

	static boolean checkFiles() {
		List<File> changedFiles = ChangedFiles.getChangedFiles();
		if (changedFiles.isEmpty()) return false;
		for (AbstractCheck check : CHECKS) {
			EXECUTOR.execute(() -> {
				List<File> matchingFiles = changedFiles.stream().filter(file -> check.getFilePatterns().stream()
					.anyMatch(pattern -> pattern.matcher(file.getPath()).matches())
				).toList();
				if (matchingFiles.isEmpty()) return;
				check.checkFiles(matchingFiles);
			});
		}
		return true;
	}

	static void waitForThreads() {
		try {
			if (!EXECUTOR.awaitTermination(1, TimeUnit.MINUTES)) {
				EXECUTOR.shutdown();
			}
		} catch (InterruptedException ex) {
			LOGGER.error("Interrupted!", ex);
			EXECUTOR.shutdown();
		}
	}

	static void main() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
		if (checkFiles()) waitForThreads();
	}
}
