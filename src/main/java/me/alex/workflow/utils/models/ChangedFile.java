package me.alex.workflow.utils.models;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record ChangedFile(String fileName, ChangedStatus status) {
	public static final Codec<ChangedFile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("filename").forGetter(ChangedFile::fileName),
		ChangedStatus.CODEC.fieldOf("status").orElse(ChangedStatus.UNKNOWN).forGetter(ChangedFile::status)
	).apply(instance, ChangedFile::new));

	public static final Codec<List<ChangedFile>> LIST_CODEC = CODEC.listOf();
}
