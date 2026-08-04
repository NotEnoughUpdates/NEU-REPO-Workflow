package me.alex.workflow.checks.constants;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.alex.workflow.checks.AbstractCheck;
import me.alex.workflow.utils.FileUtils;
import me.alex.workflow.utils.Items;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CheckAttributes implements AbstractCheck {
	final String name = "Attribute Shards";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Pattern> getFilePatterns() {
		return List.of(Pattern.compile("constants/attribute_shards.json"));
	}

	@Override
	public boolean checkFile(File file) {
		AttributeFile fileData;
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
