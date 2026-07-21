package me.alex.workflow.utils;

import it.unimi.dsi.fastutil.objects.*;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static me.alex.workflow.Main.LOGGER;
import static me.alex.workflow.Main.REPO_BASE_PATH;

public class Items {
	public static final Object2ObjectMap<String, @Nullable Path> SNBT = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
	public static final ObjectSet<String> ITEMS = ObjectSets.synchronize(new ObjectOpenHashSet<>());

	public static void init() {
		try (Stream<Path> files = Files.walk(REPO_BASE_PATH.resolve("items"))) {
			files.forEach(path -> {
				String itemName = path.getFileName().toString().replace(".json", "");
				ITEMS.add(itemName);
			});
		} catch (IOException ex) {
			LOGGER.error("Failed to generate the list of all items!", ex);
			throw new RuntimeException(ex);
		}

		try (Stream<Path> files = Files.walk(REPO_BASE_PATH.resolve("itemsOverlay"), 2)) {
			files.forEach(path -> {
				String itemName = path.getFileName().toString().replace(".snbt", "");
				SNBT.put(itemName, path);
			});
		} catch (IOException ex) {
			LOGGER.error("Failed to generate the list of all item overlays!", ex);
			throw new RuntimeException(ex);
		}
	}
}
