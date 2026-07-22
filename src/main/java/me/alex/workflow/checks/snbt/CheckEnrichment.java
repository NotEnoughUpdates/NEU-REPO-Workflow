package me.alex.workflow.checks.snbt;

import me.alex.workflow.checks.ChildCheck;
import me.alex.workflow.checks.ParseSNBT;
import me.alex.workflow.checks.item.ParseItems;
import net.minecraft.nbt.CompoundTag;

public class CheckEnrichment implements ChildCheck<ParseSNBT.Item> {
	final String name = "Check Enriched Talisman";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean checkData(ParseSNBT.Item data) {
		CompoundTag components = data.tag().getCompoundOrEmpty("components");
		CompoundTag customData = components.getCompoundOrEmpty("minecraft:custom_data");
		boolean hasNbtEnrichment = !customData.getStringOr("talisman_enrichment", "").isEmpty();

		return !hasNbtEnrichment;
	}
}
