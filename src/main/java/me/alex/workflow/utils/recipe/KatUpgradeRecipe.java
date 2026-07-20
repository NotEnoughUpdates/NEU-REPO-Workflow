package me.alex.workflow.utils.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record KatUpgradeRecipe(String input, String output, List<String> items,
                               int coins, int time) implements Recipe {
	public static final MapCodec<KatUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.fieldOf("input").forGetter(KatUpgradeRecipe::input),
		Codec.STRING.fieldOf("output").forGetter(KatUpgradeRecipe::output),
		Codec.STRING.listOf().fieldOf("items").forGetter(KatUpgradeRecipe::items),
		Codec.INT.fieldOf("coins").forGetter(KatUpgradeRecipe::coins),
		Codec.INT.fieldOf("time").forGetter(KatUpgradeRecipe::time)
	).apply(instance, KatUpgradeRecipe::new));

	@Override
	public String getType() {
		return "katgrade";
	}

	@Override
	public List<String> getInputs() {
		return List.of();
	}

	@Override
	public List<String> getOutputs() {
		return List.of();
	}
}
