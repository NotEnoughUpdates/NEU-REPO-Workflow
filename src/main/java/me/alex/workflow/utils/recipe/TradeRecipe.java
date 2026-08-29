package me.alex.workflow.utils.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

public record TradeRecipe(String cost, Optional<Integer> min, Optional<Integer> max, String result) implements Recipe {
	public static final MapCodec<TradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.fieldOf("cost").forGetter(TradeRecipe::cost),
		Codec.INT.optionalFieldOf("min").forGetter(TradeRecipe::min),
		Codec.INT.optionalFieldOf("max").forGetter(TradeRecipe::max),
		Codec.STRING.fieldOf("result").forGetter(TradeRecipe::result)
	).apply(instance, TradeRecipe::new));

	@Override
	public String getType() {
		return "trade";
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
