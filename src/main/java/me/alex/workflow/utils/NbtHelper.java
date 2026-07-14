package me.alex.workflow.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import de.hysky.skyblocker.utils.datafixer.LegacyStringNbtReader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;

public final class NbtHelper {
	public static final Codec<CompoundTag> LEGACY_SNBT_CODEC = Codec.STRING.flatXmap(
		str -> {
			try {
				return DataResult.success(LegacyStringNbtReader.parse(str));
			} catch (Exception ex) {
				return DataResult.error(() -> "Invalid SNBT - " + ex.getMessage());
			}
		}, tag -> DataResult.success(NbtUtils.structureToSnbt(tag)));
}
