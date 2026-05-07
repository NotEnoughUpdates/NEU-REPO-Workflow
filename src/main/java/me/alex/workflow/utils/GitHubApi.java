package me.alex.workflow.utils;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import me.alex.workflow.utils.models.ChangedFile;
import me.alex.workflow.utils.models.ChangedStatus;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;

import static me.alex.workflow.Main.LOGGER;

public final class GitHubApi {
	private static final String PR_FILE_URL = "https://api.github.com/repos/%s/%s/pulls/%s/files";
	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
		.connectTimeout(Duration.ofSeconds(10))
		.executor(Executors.newVirtualThreadPerTaskExecutor())
		.build();

	public static String getToken() {
		return "";
	}

	private static @Nullable String getChangedFilesAPI(String org, String repo, String prNum) {
		HttpRequest request = HttpRequest.newBuilder()
			.GET()
			.uri(URI.create(PR_FILE_URL.formatted(org, repo, prNum)))
			.version(HttpClient.Version.HTTP_2)
			.build();

		HttpResponse<String> response;
		try {
			response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException ex) {
			LOGGER.error("Failed to send HTTP request!", ex);
			return null;
		}

		if (response.statusCode() != 200) {
			LOGGER.error("Failed to get PR Changed Files! Status: {} Response: {}", response.statusCode(), response.body());
			return null;
		}

		return response.body();
	}

	public static List<String> getChangedFiles(String org, String repo, String prNum) {
		String res = getChangedFilesAPI(org, repo, prNum);
		if (res == null) return List.of();

		try {
			List<ChangedFile> files = ChangedFile.LIST_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(res)).getPartialOrThrow();
			return files.stream().filter(f -> f.status() != ChangedStatus.REMOVED).map(ChangedFile::fileName).toList();
		} catch (Exception ex) {
			LOGGER.error("Failed to get changed files!", ex);
			return List.of();
		}
	}
}
