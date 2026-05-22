package com.benbenlaw.utility.recipe.custom;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record CompressionRecipe(Item item, int inputCount, ItemStack output, boolean is3x3) {
}
