package me.alex.workflow.utils.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record NpcShopRecipe(List<String> cost, String result) implements Recipe {
	public static final MapCodec<NpcShopRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.listOf().fieldOf("cost").forGetter(NpcShopRecipe::cost),
		Codec.STRING.fieldOf("result").forGetter(NpcShopRecipe::result)
	).apply(instance, NpcShopRecipe::new));

	@Override
	public String getType() {
		return "npc_shop";
	}

	@Override
	public List<String> getInputs() {
		return cost;
	}

	@Override
	public List<String> getOutputs() {
		return List.of(result);
	}
}
