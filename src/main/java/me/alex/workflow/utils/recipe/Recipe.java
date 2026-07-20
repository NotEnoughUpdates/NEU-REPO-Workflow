package me.alex.workflow.utils.recipe;

import com.mojang.serialization.Codec;

import java.util.List;

public interface Recipe {
	String getType();

	List<String> getInputs();

	List<String> getOutputs();

	Codec<? extends Recipe> RECIPE_DISPATCH = Codec.STRING.dispatch(Recipe::getType, type -> switch (type) {
		case "crafting" -> CraftingRecipe.CODEC;
		case "drops" -> MobDropRecipe.CODEC;
		case "forge" -> ForgeRecipe.CODEC;
		case "katgrade" -> KatUpgradeRecipe.CODEC;
		case "npc_shop" -> NpcShopRecipe.CODEC;
		case "trade" -> TradeRecipe.CODEC;
		default -> throw new IllegalStateException("Unknown recipe type: " + type);
	});
}
