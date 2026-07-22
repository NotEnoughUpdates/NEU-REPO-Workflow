package me.alex.workflow.checks.item;

import me.alex.workflow.checks.ChildCheck;
import net.minecraft.nbt.CompoundTag;

public class CheckEnrichment implements ChildCheck<ParseItems.Item> {
	final String name = "Check Enriched Talisman";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean checkData(ParseItems.Item data) {
		CompoundTag extraAttributes = data.nbtTag().getCompoundOrEmpty("ExtraAttributes");
		boolean hasNbtEnrichment = !extraAttributes.getStringOr("talisman_enrichment", "").isEmpty();

		boolean hasLoreEnrichment = !data.lore().isEmpty() && data.lore().getFirst().contains("Enriched with");

		return !(hasNbtEnrichment || hasLoreEnrichment);
	}
}
