package me.alex.workflow.checks.constants;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.hysky.skyblocker.utils.CodecUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.alex.workflow.checks.AbstractCheck;
import me.alex.workflow.checks.item.ParseItems;
import me.alex.workflow.utils.Constants;
import me.alex.workflow.utils.FileUtils;
import me.alex.workflow.utils.Items;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.alex.workflow.Main.LOGGER;
import static me.alex.workflow.Main.REPO_BASE_PATH;

public class CheckPetNums implements AbstractCheck {
	private static final Path PET_NUMS_PATH = REPO_BASE_PATH.resolve("constants/petnums.json");
	private static final Pattern LORE_PET_NUMBER = Pattern.compile("\\{([0-9A-Z_]+)}");

	final String name = "Pet Numbers";
	Object2ObjectMap<String, Object2ObjectMap<String, @Nullable RarityNums>> petNums = new Object2ObjectOpenHashMap<>();
	Set<String> validPets = new ObjectOpenHashSet<>();

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Pattern> getFilePatterns() {
		return List.of(
			Pattern.compile("constants/petnums.json"),
			Pattern.compile("items/[\\w_]+;\\d+.json")
		);
	}

	@Override
	public boolean checkFiles(List<File> files) {
		if (!loadPetNums()) return false;
		return AbstractCheck.super.checkFiles(files);
	}

	@Override
	public boolean checkFile(File file) {
		if (file.toPath().equals(PET_NUMS_PATH)) return validatePetNums();

		String[] parts = file.getName().replace(".json", "").split(";", 2);
		String petName = parts[0];
		if (!validPets.contains(parts[0])) return true;

		ParseItems.Item item;
		try {
			item = FileUtils.readJsonFile(file, ParseItems.Item.CODEC);
		} catch (Exception ex) {
			LOGGER.error("Failed to load item to check pet numbers!", ex);
			return false;
		}

		int rarityIndex = Integer.parseInt(parts[1]);
		String rarity = Constants.RARITY.get(rarityIndex);
		RarityNums nums = petNums.get(petName).get(rarity);
		if (nums == null) {
			logFileIssue(file, "No pet nums found for rarity %s".formatted(rarity));
			return false;
		}

		Set<String> petNumKeys = new ObjectOpenHashSet<>();
		petNumKeys.addAll(nums.ONE.statNums.keySet());
		for (int i = 0; i < nums.ONE.otherNums.size(); i++) {
			petNumKeys.add(String.valueOf(i));
		}

		for (String line : item.lore()) {
			Matcher matcher = LORE_PET_NUMBER.matcher(line);
			while (matcher.find()) {
				boolean removed = petNumKeys.remove(matcher.group(1));
				if (!removed) {
					logFileIssue(file, "Unknown pet number %s".formatted(matcher.group(1)));
				}
			}
		}

		if (!petNumKeys.isEmpty()) {
			logFileIssue(file, "Not all pet numbers are used!");
			return false;
		}

		return true;
	}

	public boolean loadPetNums() {
		try {
			petNums = FileUtils.readJsonFile(PET_NUMS_PATH, FILE_CODEC);
		} catch (Exception ex) {
			logFileIssue(PET_NUMS_PATH.toFile(), "Failed to parse: " + ex.getMessage());
			return false;
		}
		validPets = petNums.keySet();
		return true;
	}

	public boolean validatePetNums() {
		boolean isValid = true;
		for (var entry : petNums.entrySet()) {
			for (var rarityEntry : entry.getValue().entrySet()) {
				String key = String.format("%s;%s", entry.getKey(), Constants.RARITY.indexOf(rarityEntry.getKey()));
				if (!Items.ITEMS.contains(key)) {
					logFileIssue(PET_NUMS_PATH.toFile(), "Invalid pet: " + key);
					isValid = false;
				}

				RarityNums rarityNums = rarityEntry.getValue();
				assert rarityNums != null;
				if (rarityNums.ONE.otherNums.size() != rarityNums.ONE_HUNDRED.otherNums.size()) {
					logFileIssue(PET_NUMS_PATH.toFile(), "Pet %s has mismatching Lvl 1 & 100 otherNums".formatted(key));
				}
				if (rarityNums.ONE.statNums.size() != rarityNums.ONE_HUNDRED.statNums.size() ||
					!rarityNums.ONE.statNums.keySet().equals(rarityNums.ONE_HUNDRED.statNums.keySet())) {
					logFileIssue(PET_NUMS_PATH.toFile(), "Pet %s has mismatching Lvl 1 & 100 statNums".formatted(key));
				}
			}
		}

		return isValid;
	}

	static final Codec<Object2ObjectMap<String, Object2ObjectMap<String, RarityNums>>> FILE_CODEC =
		CodecUtils.object2ObjectMapCodec(Codec.STRING, CodecUtils.object2ObjectMapCodec(Codec.STRING, RarityNums.CODEC));

	record RarityNums(LevelNums ONE, LevelNums ONE_HUNDRED, Optional<String> statsLevellingCurve) {
		public static final Codec<RarityNums> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			LevelNums.CODEC.fieldOf("1").forGetter(RarityNums::ONE),
			LevelNums.CODEC.fieldOf("100").forGetter(RarityNums::ONE_HUNDRED),
			Codec.STRING.optionalFieldOf("stats_levelling_curve").forGetter(RarityNums::statsLevellingCurve)
		).apply(instance, RarityNums::new));
	}

	record LevelNums(List<Double> otherNums, Object2ObjectMap<String, Double> statNums) {
		public static final Codec<LevelNums> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.DOUBLE.listOf().fieldOf("otherNums").forGetter(LevelNums::otherNums),
			CodecUtils.object2ObjectMapCodec(Codec.STRING, Codec.DOUBLE).fieldOf("statNums").forGetter(LevelNums::statNums)
		).apply(instance, LevelNums::new));
	}
}
