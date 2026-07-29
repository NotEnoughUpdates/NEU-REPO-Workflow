package me.alex.workflow.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtils {
	public static <T> T readJsonFile(File file, Codec<T> codec) throws Exception {
		return readJsonFile(file.toPath(), codec);
	}

	public static <T> T readJsonFile(Path path, Codec<T> codec) throws Exception {
		BufferedReader reader = Files.newBufferedReader(path);
		JsonElement jsonElement = JsonParser.parseReader(reader);
		return codec.parse(JsonOps.INSTANCE, jsonElement).getOrThrow();
	}
}
