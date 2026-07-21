package me.alex.workflow.checks.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.alex.workflow.checks.ChildCheck;
import me.alex.workflow.checks.ParentCheck;
import me.alex.workflow.utils.NbtHelper;
import net.minecraft.nbt.CompoundTag;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static me.alex.workflow.Main.LOGGER;

public class ParseItems implements ParentCheck<ParseItems.Item> {
	final String name = "Parse Items";

	final List<ChildCheck<ParseItems.Item>> children = List.of(
		new CheckNbtDisplay(),
//		new CheckHeadTexture(),
		new CheckItemModel(),
		new CheckItemRecipes(),
		new CheckEnchantLevel()
	);


	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<ChildCheck<Item>> getChildren() {
		return children;
	}

	@Override
	public @Nullable Item parseFile(File file) {
		Item data;
		try {
			BufferedReader reader = Files.newBufferedReader(file.toPath());
			JsonElement jsonElement = JsonParser.parseReader(reader);
			data = Item.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow();
		} catch (Exception ex) {
			LOGGER.error("Failed to load item file {}", file.getName(), ex);
			return null;
		}
		return data;
	}

	@Override
	public List<Pattern> getFilePatterns() {
		return List.of(Pattern.compile("items/.*.json"));
	}

	public record Item(String itemId, String internalName, String displayName, int damage, CompoundTag nbtTag,
	                   List<String> lore, Optional<Dynamic<?>> recipe, Optional<List<Dynamic<?>>> recipes) {
		public static final Codec<Item> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("itemid").forGetter(Item::itemId),
			Codec.STRING.fieldOf("internalname").forGetter(Item::internalName),
			Codec.STRING.fieldOf("displayname").forGetter(Item::displayName),
			Codec.INT.fieldOf("damage").forGetter(Item::damage),
			NbtHelper.LEGACY_SNBT_CODEC.fieldOf("nbttag").forGetter(Item::nbtTag),
			Codec.STRING.listOf().fieldOf("lore").forGetter(Item::lore),
			Codec.PASSTHROUGH.optionalFieldOf("recipe").forGetter(Item::recipe),
			Codec.PASSTHROUGH.listOf().optionalFieldOf("recipes").forGetter(Item::recipes)
		).apply(instance, Item::new));
	}
}
