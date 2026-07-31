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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;

import static me.alex.workflow.Main.LOGGER;

public final class GitHubApi {
	private static final String PR_FILE_URL = "https://api.github.com/repos/%s/%s/pulls/%s/files?per_page=100";
	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
		.connectTimeout(Duration.ofSeconds(10))
		.executor(Executors.newVirtualThreadPerTaskExecutor())
		.build();

	//region Pagination Handling
	private static @Nullable String getNextLink(String linksHeader) {
		String[] parts = linksHeader.split(",");
		for (String part : parts) {
			if (part.contains("rel=\"next\"")) {
				return part.split(";")[0].replace("<", "").replace(">", "").strip();
			}
		}
		return null;
	}

	private static List<PaginatableResponse> handlePagination(
		String name, Supplier<@Nullable PaginatableResponse> initial, Function<String, @Nullable PaginatableResponse> nextHandler
	) {
		LOGGER.info("Getting {} page #1", name);
		PaginatableResponse initialResponse = initial.get();
		if (initialResponse == null) return List.of();
		if (initialResponse.nextLink == null) return List.of(initialResponse);

		int i = 1;
		List<PaginatableResponse> responses = new ArrayList<>();
		responses.add(initialResponse);
		String nextLink = initialResponse.nextLink;
		while (nextLink != null) {
			i += 1;
			LOGGER.info("Getting {} page #{}", name, i);
			PaginatableResponse nextResponse = nextHandler.apply(nextLink);
			if (nextResponse != null) {
				responses.add(nextResponse);
				nextLink = nextResponse.nextLink;
			} else {
				nextLink = null;
			}
		}
		return responses;
	}
	//endregion

	//region Changed Files - API
	private static @Nullable PaginatableResponse getChangedFilesAPI(String link) {
		HttpRequest request = HttpRequest.newBuilder()
			.GET()
			.uri(URI.create(link))
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

		String nextLink = response.headers().firstValue("Link")
			.map(GitHubApi::getNextLink).orElse(null);

		return new PaginatableResponse(response.body(), nextLink);
	}

	private static @Nullable PaginatableResponse getChangedFilesAPI(String org, String repo, Integer prNum) {
		String link = PR_FILE_URL.formatted(org, repo, prNum);
		return getChangedFilesAPI(link);
	}
	//endregion

	public static List<String> getChangedFiles(String org, String repo, Integer prNum) {
		List<PaginatableResponse> responses = handlePagination("Changed Files",
			() -> GitHubApi.getChangedFilesAPI(org, repo, prNum),
			GitHubApi::getChangedFilesAPI
		);
		if (responses.isEmpty()) return List.of();

		List<String> files = new ArrayList<>();
		for (PaginatableResponse response : responses) {
			try {
				List<ChangedFile> data = ChangedFile.LIST_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(response.text)).getPartialOrThrow();
				data.stream().filter(f -> f.status() != ChangedStatus.REMOVED).map(ChangedFile::fileName).forEach(files::add);
			} catch (Exception ex) {
				LOGGER.error("Failed to get changed files!", ex);
				return List.of();
			}
		}
		return files;
	}

	record PaginatableResponse(String text, @Nullable String nextLink) {
	}
}
