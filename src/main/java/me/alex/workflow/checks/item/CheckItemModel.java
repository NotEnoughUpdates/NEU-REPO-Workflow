package me.alex.workflow.checks.item;

import me.alex.workflow.checks.CheckData;
import me.alex.workflow.checks.ChildCheck;
import me.alex.workflow.checks.ParseSNBT;
import me.alex.workflow.utils.Items;
import net.minecraft.nbt.CompoundTag;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.Objects;

public class CheckItemModel implements ChildCheck<ParseItems.Item> {
	final String name = "Check Item Model";

	@Override
	public String getName() {
		return name;
	}

	public @Nullable CompoundTag getSnbtForItem(String itemId) {
		Path path = Items.SNBT.get(itemId);
		if (path == null) return null;
		return ParseSNBT.readSnbt(path);
	}

	@Override
	public boolean checkData(CheckData<ParseItems.Item> checkData) {
		var data = checkData.data();
		String nbtItemModel = data.nbtTag().getStringOr("ItemModel", null);
		if (nbtItemModel == null) return true;

		CompoundTag snbt = getSnbtForItem(data.internalName());
		if (snbt == null) return true;

		String snbtItemModel = snbt.getCompoundOrEmpty("components")
			.getStringOr("minecraft:item_model", null);
		if (snbtItemModel == null) return true;

		boolean bl = Objects.equals(nbtItemModel, snbtItemModel);
		if (!bl) logFileIssue(checkData.file(), "NBT Model does not match SNBT Model.",
			"NBT Model: `%s`".formatted(nbtItemModel),
			"SNBT Model: `%s`".formatted(snbtItemModel));
		return bl;
	}
}
