package com.mjr.extraplanets.armor;

import com.mjr.extraplanets.Constants;
import com.mjr.extraplanets.ExtraPlanets;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ZincArmor extends ItemArmor {
	public String name;

	public ZincArmor(String name, ArmorMaterial material, int placement) {
		super(material, 0, placement);
		setCreativeTab(ExtraPlanets.ArmorTab);

		if (placement == 0) {
			this.setTextureName(Constants.TEXTURE_PREFIX + name + "_helmet");
		} else if (placement == 1) {
			this.setTextureName(Constants.TEXTURE_PREFIX + name + "_chestplate");
		} else if (placement == 2) {
			this.setTextureName(Constants.TEXTURE_PREFIX + name + "_leggings");
		} else if (placement == 3) {
			this.setTextureName(Constants.TEXTURE_PREFIX + name + "_boots");
		}
		this.name = name;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		if (stack.getItem() == ExtraPlanets_Armor.zincHelmet || stack.getItem() == ExtraPlanets_Armor.zincChest || stack.getItem() == ExtraPlanets_Armor.zincBoots) {
			return Constants.TEXTURE_PREFIX + "textures/model/armor/" + name + "_layer_1.png";
		} else if (stack.getItem() == ExtraPlanets_Armor.zincLegings) {
			return Constants.TEXTURE_PREFIX + "textures/model/armor/" + name + "_layer_2.png";
		} else {
			return null;
		}
	}
}
