package me.alex.workflow.utils.models;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PullRequestContext(Head head, int prNumber, BaseRepository repo) {
	public static final Codec<PullRequestContext> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Head.CODEC.fieldOf("head").forGetter(PullRequestContext::head),
		Codec.INT.fieldOf("number").forGetter(PullRequestContext::prNumber),
		BaseRepository.CODEC.fieldOf("base").forGetter(PullRequestContext::repo)
	).apply(instance, PullRequestContext::new));
}

