package me.alex.workflow.utils.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record LegacyRecipe(String A1, String B1, String C1,
                           String A2, String B2, String C2,
                           String A3, String B3, String C3) implements Recipe {
	public static final Codec<LegacyRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("A1").forGetter(LegacyRecipe::A1),
		Codec.STRING.fieldOf("B1").forGetter(LegacyRecipe::B1),
		Codec.STRING.fieldOf("C1").forGetter(LegacyRecipe::C1),
		Codec.STRING.fieldOf("A2").forGetter(LegacyRecipe::A2),
		Codec.STRING.fieldOf("B2").forGetter(LegacyRecipe::B2),
		Codec.STRING.fieldOf("C2").forGetter(LegacyRecipe::C2),
		Codec.STRING.fieldOf("A3").forGetter(LegacyRecipe::A3),
		Codec.STRING.fieldOf("B3").forGetter(LegacyRecipe::B3),
		Codec.STRING.fieldOf("C3").forGetter(LegacyRecipe::C3)
	).apply(instance, LegacyRecipe::new));

	@Override
	public String getType() {
		// unused
		return "";
	}

	@Override
	public List<String> getInputs() {
		return List.of(A1, B1, C1, A2, B2, C2, A3, B3, C3);
	}

	@Override
	public List<String> getOutputs() {
		// The output is the item where the recipe is defined, so no need to check.
		return List.of();
	}
}
