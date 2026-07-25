package me.alex.workflow.checks.item;

import com.mojang.serialization.DataResult;
import me.alex.workflow.checks.CheckData;
import me.alex.workflow.checks.ChildCheck;
import me.alex.workflow.utils.Items;
import me.alex.workflow.utils.recipe.LegacyRecipe;
import me.alex.workflow.utils.recipe.Recipe;

import static me.alex.workflow.Main.LOGGER;

public class CheckItemRecipes implements ChildCheck<ParseItems.Item> {
	final String name = "Check Item Recipes";

	@Override
	public String getName() {
		return name;
	}

	public static boolean checkItemExists(String recipeItem) {
		String itemId = recipeItem.split(":")[0];
		if (itemId.equals("SKYBLOCK_COIN")) return true;
		boolean bl = Items.ITEMS.contains(itemId);
		if (!bl) LOGGER.error("Item {} in recipe does not exist!", itemId);
		return bl;
	}

	@Override
	public boolean checkData(CheckData<ParseItems.Item> checkData) {
		var data = checkData.data();
		if (data.recipe().isPresent()) {
			DataResult<LegacyRecipe> recipe = LegacyRecipe.CODEC.parse(data.recipe().get());
			if (recipe.error().isPresent()) {
				logFileIssue(checkData.file(), "Failed to parse legacy recipe: %s".formatted(recipe.error().get()));
				return false;
			}
			var inputError = recipe.getOrThrow().getInputs().stream().filter(s -> !s.isEmpty())
				.map(CheckItemRecipes::checkItemExists).filter(bl -> !bl).findFirst();
			if (inputError.isPresent()) return false;
		}

		if (data.recipes().isPresent()) {
			var recipes = data.recipes().get();
			for (var recipeDynamic : recipes) {
				DataResult<? extends Recipe> result = Recipe.RECIPE_DISPATCH.parse(recipeDynamic);
				if (result.error().isPresent()) {
					logFileIssue(checkData.file(), "Failed to parse recipe: %s".formatted(result.error().get()));
					return false;
				}
				Recipe recipe = result.getOrThrow();
				var inputError = recipe.getInputs().stream().filter(s -> !s.isEmpty())
					.map(CheckItemRecipes::checkItemExists).filter(bl -> !bl).findFirst();
				if (inputError.isPresent()) {
					logFileIssue(checkData.file(), "An input item could not be found!");
					return false;
				}

				var outputError = recipe.getOutputs().stream().map(CheckItemRecipes::checkItemExists)
					.filter(bl -> !bl).findFirst();
				if (outputError.isPresent()) {
					logFileIssue(checkData.file(), "An output item could not be found!");
					return false;
				}
			}
		}

		return true;
	}
}
