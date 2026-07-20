package me.alex.workflow.utils.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

public record ForgeRecipe(List<String> inputs, Optional<String> overrideOutputId) implements Recipe {
	public static final MapCodec<ForgeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.listOf().fieldOf("inputs").forGetter(ForgeRecipe::getInputs),
		Codec.STRING.optionalFieldOf("overrideOutputId").forGetter(ForgeRecipe::overrideOutputId)
	).apply(instance, ForgeRecipe::new));

	@Override
	public String getType() {
		return "forge";
	}

	@Override
	public List<String> getInputs() {
		return inputs;
	}

	@Override
	public List<String> getOutputs() {
		return overrideOutputId.map(List::of).orElseGet(List::of);
	}
}
