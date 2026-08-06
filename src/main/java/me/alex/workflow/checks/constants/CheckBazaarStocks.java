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

public class CheckBazaarStocks implements AbstractCheck {
	final String name = "Bazaar Stocks";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Pattern> getFilePatterns() {
		return List.of(Pattern.compile("constants/bazaarstocks.json"));
	}

	@Override
	public boolean checkFile(File file) {
		List<BazaarStock> stocks;
		try {
			stocks = FileUtils.readJsonFile(file, BazaarStock.LIST_CODEC);
		} catch (Exception ex) {
			logFileIssue(file, "Failed to load file", ex.getMessage());
			return false;
		}

		List<String> missingItems = new ArrayList<>();
		boolean isValid = true;
		for (BazaarStock stock : stocks) {
			if (!Items.ITEMS.contains(stock.internalName)) {
				missingItems.add(stock.internalName);
				isValid = false;
			}
		}

		if (!missingItems.isEmpty()) {
			logFileIssue(file, "Unknown Items", missingItems.toArray(new String[0]));
		}

		return isValid;
	}

	public record BazaarStock(String internalName, String apiName) {
		public static final Codec<BazaarStock> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("id").forGetter(BazaarStock::internalName),
			Codec.STRING.fieldOf("stock").forGetter(BazaarStock::apiName)
		).apply(instance, BazaarStock::new));

		public static final Codec<List<BazaarStock>> LIST_CODEC = CODEC.listOf();
	}
}
