package me.alex.workflow.checks.constants;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.alex.workflow.checks.AbstractCheck;
import me.alex.workflow.utils.FileUtils;
import me.alex.workflow.utils.Items;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class CheckAbiphone implements AbstractCheck {
	final String name = "Abiphone";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Pattern> getFilePatterns() {
		return List.of(Pattern.compile("constants/abiphone.json"));
	}

	@Override
	public boolean checkFile(File file) {
		Map<String, AbiphoneContact> data;
		try {
			data = FileUtils.readJsonFile(file, AbiphoneContact.FILE_CODEC);
		} catch (Exception ex) {
			logFileIssue(file, "Failed to parse file", ex.getMessage());
			return false;
		}

		boolean isFileValid = true;
		for (var entry : data.entrySet()) {
			String npcName = entry.getKey();
			AbiphoneContact contact = entry.getValue();
			if (contact.npcItemId.isPresent()) {
				String itemName = contact.npcItemId.get();
				if (!Items.ITEMS.contains(itemName)) {
					logFileIssue(file, "Unknown item for " + npcName, itemName + " is not a valid item");
					isFileValid = false;
				}
			}
		}

		return isFileValid;
	}

	public record AbiphoneContact(Optional<String> npcItemId) {
		public static Codec<AbiphoneContact> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.optionalFieldOf("id").forGetter(AbiphoneContact::npcItemId)
		).apply(instance, AbiphoneContact::new));

		public static Codec<Map<String, AbiphoneContact>> FILE_CODEC = Codec.unboundedMap(Codec.STRING, AbiphoneContact.CODEC);
	}
}
