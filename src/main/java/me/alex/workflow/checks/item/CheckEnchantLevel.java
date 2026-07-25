package me.alex.workflow.checks.item;

import me.alex.workflow.checks.CheckData;
import me.alex.workflow.checks.ChildCheck;
import net.minecraft.nbt.CompoundTag;

import java.util.Locale;

public class CheckEnchantLevel implements ChildCheck<ParseItems.Item> {
	final String name = "Check Enchantment Level";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean checkData(CheckData<ParseItems.Item> checkData) {
		var data = checkData.data();
		if (!data.itemId().equals("minecraft:enchanted_book")) return true;
		if (!data.internalName().contains(";")) return true;
		String[] parts = data.internalName().split(";", 2);
		if (parts.length != 2) return true;
		String enchantName = parts[0];
		int enchantLevel = Integer.parseInt(parts[1]);

		CompoundTag extraAttributes = data.nbtTag().getCompoundOrEmpty("ExtraAttributes");
		CompoundTag enchants = extraAttributes.getCompoundOrEmpty("enchantments");
		int actualValue = enchants.getIntOr(enchantName.toLowerCase(Locale.ROOT), -1);
		int alternateValue = enchants.getIntOr(enchantName.toUpperCase(Locale.ROOT), -1);

		return enchantLevel == Math.max(actualValue, alternateValue);
	}
}
