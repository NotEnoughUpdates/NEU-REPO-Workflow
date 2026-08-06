package me.alex.workflow.checks.constants;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.alex.workflow.checks.AbstractCheck;
import me.alex.workflow.checks.item.ParseItems;
import me.alex.workflow.utils.FileUtils;
import me.alex.workflow.utils.Items;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static me.alex.workflow.Main.REPO_BASE_PATH;

public class CheckAttributes implements AbstractCheck {
	private static final Path ATTRIBUTES_PATH = REPO_BASE_PATH.resolve("constants/attribute_shards.json");
	final String name = "Attribute Shards";

	@Nullable AttributeFile fileData;
	boolean constantStatus;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Pattern> getFilePatterns() {
		return List.of(
			Pattern.compile("constants/attribute_shards.json"),
			Pattern.compile("items/ATTRIBUTE_SHARD_.*;1.json")
		);
	}

	public boolean loadFileData() {
		File file = ATTRIBUTES_PATH.toFile();
		try {
			fileData = FileUtils.readJsonFile(file, AttributeFile.CODEC);
		} catch (Exception ex) {
			logFileIssue(file, "Failed to load file!", ex.getMessage());
			return false;
		}

		List<String> unknownItems = new ArrayList<>();
		for (Attribute attribute : fileData.attributes()) {
			if (!Items.ITEMS.contains(attribute.internalName)) {
				unknownItems.add(attribute.internalName);
			}
		}

		if (!unknownItems.isEmpty()) {
			logFileIssue(file, "Unknown Shard Item", unknownItems.toArray(new String[0]));
			return false;
		}

		return true;
	}

	@Override
	public boolean checkFiles(List<File> files) {
		constantStatus = loadFileData();
		if (fileData == null) return false;
		return AbstractCheck.super.checkFiles(files);
	}

	@Override
	public boolean checkFile(File file) {
		if (file.toPath().equals(ATTRIBUTES_PATH)) return constantStatus;
		if (fileData == null) return true;
		Optional<Attribute> attributeOptional = fileData.attributes.stream().filter(a ->
			a.internalName.equals(file.getName().replace(".json", ""))).findFirst();

		if (attributeOptional.isEmpty()) {
			logFileIssue(file, "Missing attribute constant data!");
			return false;
		}
		Attribute attribute = attributeOptional.get();

		ParseItems.Item item;
		try {
			item = FileUtils.readJsonFile(file, ParseItems.Item.CODEC);
		} catch (Exception ex) {
			logFileIssue(file, "Failed to load item!", ex.getMessage());
			return false;
		}

		boolean isValid = true;
		if (!item.lore().getFirst().contains(attribute.abilityName)) {
			logFileIssue(file, "Ability name does not match item!",
				"Item Lore: " + item.lore().getFirst(),
				"Ability Name: " + attribute.abilityName);
			isValid = false;
		}

		if (!item.displayName().contains(attribute.displayName)) {
			logFileIssue(file, "Display name does not match item!",
				"Item Display Name: " + item.displayName(),
				"Display Name: " + attribute.displayName
			);
			isValid = false;
		}

		String finalLine = item.lore().getLast();
		if (!finalLine.contains(attribute.rarity)) {
			logFileIssue(file, "Rarity does not match item!",
				"Item Lore: " + finalLine,
				"Rarity: " + attribute.rarity
			);
			isValid = false;
		}

		if (!finalLine.contains(attribute.shardId)) {
			logFileIssue(file, "Shard ID does not match item!",
				"Item Lore: " + finalLine,
				"Shard ID: " + attribute.shardId
			);
			isValid = false;
		}

		return isValid;
	}


	public record AttributeFile(List<Attribute> attributes) {
		public static final Codec<AttributeFile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Attribute.LIST_CODEC.fieldOf("attributes").forGetter(AttributeFile::attributes)
		).apply(instance, AttributeFile::new));
	}

	public record Attribute(String bazaarName, String displayName, String rarity, String internalName,
	                        String abilityName, String alignment, List<String> family, String shardId) {
		public static final Codec<Attribute> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("bazaarName").forGetter(Attribute::bazaarName),
			Codec.STRING.fieldOf("displayName").forGetter(Attribute::displayName),
			Codec.STRING.fieldOf("rarity").forGetter(Attribute::rarity),
			Codec.STRING.fieldOf("internalName").forGetter(Attribute::internalName),
			Codec.STRING.fieldOf("abilityName").forGetter(Attribute::abilityName),
			Codec.STRING.fieldOf("alignment").forGetter(Attribute::alignment),
			Codec.STRING.listOf().optionalFieldOf("family", List.of()).forGetter(Attribute::family),
			Codec.STRING.fieldOf("shardId").forGetter(Attribute::shardId)
		).apply(instance, Attribute::new));
		public static final Codec<List<Attribute>> LIST_CODEC = CODEC.listOf();
	}
}
