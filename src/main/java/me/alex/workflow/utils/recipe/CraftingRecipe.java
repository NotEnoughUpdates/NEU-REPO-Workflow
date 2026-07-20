package me.alex.workflow.utils.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

public record CraftingRecipe(String A1, String B1, String C1,
                             String A2, String B2, String C2,
                             String A3, String B3, String C3,
                             int count, Optional<String> outputId) implements Recipe {
	public static final MapCodec<CraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.fieldOf("A1").forGetter(CraftingRecipe::A1),
		Codec.STRING.fieldOf("B1").forGetter(CraftingRecipe::B1),
		Codec.STRING.fieldOf("C1").forGetter(CraftingRecipe::C1),
		Codec.STRING.fieldOf("A2").forGetter(CraftingRecipe::A2),
		Codec.STRING.fieldOf("B2").forGetter(CraftingRecipe::B2),
		Codec.STRING.fieldOf("C2").forGetter(CraftingRecipe::C2),
		Codec.STRING.fieldOf("A3").forGetter(CraftingRecipe::A3),
		Codec.STRING.fieldOf("B3").forGetter(CraftingRecipe::B3),
		Codec.STRING.fieldOf("C3").forGetter(CraftingRecipe::C3),
		Codec.INT.optionalFieldOf("count", 1).forGetter(CraftingRecipe::count),
		Codec.STRING.optionalFieldOf("overrideOutputId").forGetter(CraftingRecipe::outputId)
	).apply(instance, CraftingRecipe::new));

	@Override
	public String getType() {
		return "crafting";
	}

	@Override
	public List<String> getInputs() {
		return List.of(A1, B1, C1, A2, B2, C2, A3, B3, C3);
	}

	@Override
	public List<String> getOutputs() {
		return outputId.map(List::of).orElseGet(List::of);
	}
}
