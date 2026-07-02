package me.alex.workflow.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;

public final class NbtHelper {
	public static final Codec<CompoundTag> LEGACY_SNBT_CODEC = Codec.STRING.flatXmap(
		str -> {
			try {
				return DataResult.success(LegacySnbtParser.parse(str));
			} catch (Exception ex) {
				return DataResult.error(() -> "Invalid SNBT - " + ex.getMessage());
			}
		}, tag -> DataResult.success(NbtUtils.structureToSnbt(tag)));
}
