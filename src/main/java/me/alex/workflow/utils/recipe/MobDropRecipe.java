package me.alex.workflow.utils.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record MobDropRecipe(List<MobDropRecipe.Drop> drops) implements Recipe {
	public static final MapCodec<MobDropRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Drop.CODEC.listOf().fieldOf("drops").forGetter(MobDropRecipe::drops)
	).apply(instance, MobDropRecipe::new));

	@Override
	public String getType() {
		return "drop";
	}

	@Override
	public List<String> getInputs() {
		return List.of();
	}

	@Override
	public List<String> getOutputs() {
		return drops.stream().map(Drop::itemId).toList();
	}

	public record Drop(String itemId) {
		public static final Codec<Drop> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("id").forGetter(Drop::itemId)
		).apply(instance, Drop::new));
	}
}
