package me.alex.workflow.checks.item;

import me.alex.workflow.checks.ChildCheck;
import me.alex.workflow.checks.ParseSNBT;
import me.alex.workflow.utils.Items;
import net.minecraft.nbt.CompoundTag;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.Objects;

import static me.alex.workflow.Main.LOGGER;

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
	public boolean checkData(ParseItems.Item data) {
		String nbtItemModel = data.nbtTag().getStringOr("ItemModel", null);
		if (nbtItemModel == null) return true;

		CompoundTag snbt = getSnbtForItem(data.internalName());
		if (snbt == null) return true;

		String snbtItemModel = snbt.getCompoundOrEmpty("components")
			.getStringOr("minecraft:item_model", null);
		if (snbtItemModel == null) return true;

		boolean bl = Objects.equals(nbtItemModel, snbtItemModel);
		if (!bl) LOGGER.error("Item {} has different item models: {} (NBT) vs {} (SNBT)", data.internalName(), nbtItemModel, snbtItemModel);
		return bl;
	}
}
