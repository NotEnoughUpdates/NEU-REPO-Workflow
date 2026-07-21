package me.alex.workflow.checks.item;

import me.alex.workflow.checks.ChildCheck;
import me.alex.workflow.checks.ParseSNBT;
import me.alex.workflow.utils.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.Optional;

public class CheckHeadTexture implements ChildCheck<ParseItems.Item> {
	final String name = "Check Player Head Texture";

	@Override
	public String getName() {
		return name;
	}

	public @Nullable CompoundTag getSnbtForItem(String itemId) {
		Path path = Items.SNBT.get(itemId);
		if (path == null) return null;
		return ParseSNBT.readSnbt(path);
	}

	@Override
	public boolean checkData(ParseItems.Item data) {
		if (!data.itemId().equals("minecraft:skull") || data.damage() != 3) return true;
		ListTag nbtTexturesList = data.nbtTag().getCompoundOrEmpty("SkullOwner")
			.getCompoundOrEmpty("Properties")
			.getListOrEmpty("textures");
		if (nbtTexturesList.size() != 1) return false;

		Optional<CompoundTag> texturesOptional = nbtTexturesList.getFirst().asCompound();
		if (texturesOptional.isEmpty()) return false;

		String nbtValue = texturesOptional.get().getStringOr("Value", "");
		if (nbtValue.isEmpty()) return false;
		String nbtSignature = texturesOptional.get().getStringOr("Signature", "");

		CompoundTag snbt = getSnbtForItem(data.internalName());
		if (snbt == null) return true; // don't force items to have SNBT here.

		ListTag snbtTexturesList = snbt.getCompoundOrEmpty("components")
			.getCompoundOrEmpty("minecraft:profile")
			.getListOrEmpty("properties");
		if (snbtTexturesList.size() != 1) return false;
		Optional<CompoundTag> snbtTexturesOptional = snbtTexturesList.getFirst().asCompound();
		if (snbtTexturesOptional.isEmpty()) return false;

		String snbtValue = snbtTexturesOptional.get().getStringOr("value", "");
		String snbtSignature = snbtTexturesOptional.get().getStringOr("signature", "");

		return nbtValue.equals(snbtValue) && nbtSignature.equals(snbtSignature);
	}
}
