package me.alex.workflow.utils.models;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Repository(String owner, String name) {
	public static final Codec<Repository> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("login").forGetter(Repository::owner),
		Codec.STRING.fieldOf("name").forGetter(Repository::name)
	).apply(instance, Repository::new));
}
