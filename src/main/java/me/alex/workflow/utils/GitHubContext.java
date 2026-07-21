package me.alex.workflow.utils;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import me.alex.workflow.utils.models.Context;
import me.alex.workflow.utils.models.PullRequestContext;
import me.alex.workflow.utils.models.BaseRepository;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static me.alex.workflow.Main.LOGGER;

public final class GitHubContext {
	public static final @Nullable String EVENT_NAME = System.getenv("GITHUB_EVENT_NAME");
	public static final @Nullable String EVENT_PATH = System.getenv("GITHUB_EVENT_PATH");

	//region PR Specific Variables
	public static @Nullable String SHA;
	public static @Nullable BaseRepository REPO;
	public static @Nullable Integer PULL_NUM;
	//endregion

	public static void loadEventPayload() {
		if (EVENT_NAME == null || EVENT_PATH == null) return;
		if (!EVENT_NAME.equals("pull_request")) return;

		File eventFile = Path.of(EVENT_PATH).toFile();
		if (!eventFile.exists()) return;
		String content;
		try {
			content = Files.readString(eventFile.toPath());
			IO.println(content);
		} catch (IOException ex) {
			LOGGER.error("Failed to read event content!", ex);
			return;
		}

		Context.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(content))
			.ifSuccess(context -> {
				PullRequestContext prContext = context.pullRequestContext();
				SHA = prContext.head().sha();
				REPO = prContext.repo();
				PULL_NUM = prContext.prNumber();
			}).ifError(err ->
				LOGGER.error("Failed to parse event payload! Error: {}", err.message())
			);
	}

	static {
		loadEventPayload();
	}
}
