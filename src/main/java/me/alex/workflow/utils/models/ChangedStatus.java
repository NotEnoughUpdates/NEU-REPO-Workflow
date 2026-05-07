package me.alex.workflow.utils.models;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum ChangedStatus implements StringRepresentable {
	ADDED,
	MODIFIED,
	REMOVED,
	RENAMED,
	UNKNOWN,
	;

	public static final Codec<ChangedStatus> CODEC = StringRepresentable.fromValues(ChangedStatus::values);

	@Override
	public String getSerializedName() {
		return name().toLowerCase(Locale.ROOT);
	}
}
