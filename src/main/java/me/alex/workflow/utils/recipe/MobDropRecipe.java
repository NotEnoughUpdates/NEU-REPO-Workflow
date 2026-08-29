package me.alex.workflow.utils.recipe;

import com.google.common.collect.Streams;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record MobDropRecipe(String name, Optional<Integer> level, List<MobDropRecipe.Drop> drops) implements Recipe {
	public static final MapCodec<MobDropRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.fieldOf("name").forGetter(MobDropRecipe::name),
		Codec.INT.optionalFieldOf("level").forGetter(MobDropRecipe::level),
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
		return drops.stream().map(Drop::outputs).flatMap(Collection::stream).toList();
	}

	public record Drop(String itemId, List<Drop> alternatives) {
		private Drop(String itemId) {
			this(itemId, List.of());
		}

		private static final Codec<Drop> PARTIAL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("id").forGetter(Drop::itemId)
		).apply(instance, Drop::new));

		public static final Codec<Drop> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("id").forGetter(Drop::itemId),
			Drop.PARTIAL_CODEC.listOf().optionalFieldOf("alternatives", List.of()).forGetter(Drop::alternatives)
		).apply(instance, Drop::new));

		public List<String> outputs() {
			if (alternatives.isEmpty()) return List.of(itemId);
			Stream<String> alternativeItemIds = alternatives.stream().map(Drop::itemId);
			return Streams.concat(Stream.of(itemId), alternativeItemIds).toList();
		}
	}
}
