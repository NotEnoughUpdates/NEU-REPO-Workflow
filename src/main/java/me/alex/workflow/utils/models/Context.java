package me.alex.workflow.utils.models;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Context(PullRequestContext pullRequestContext) {
	public static final Codec<Context> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		PullRequestContext.CODEC.fieldOf("pull_request").forGetter(Context::pullRequestContext)
	).apply(instance, Context::new));
}
