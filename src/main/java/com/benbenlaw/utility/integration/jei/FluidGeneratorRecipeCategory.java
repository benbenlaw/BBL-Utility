package com.benbenlaw.utility.integration.jei;

import com.benbenlaw.core.util.MouseUtil;
import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.UtilityBlocks;
import com.benbenlaw.utility.recipe.custom.FluidGeneratorRecipe;
import com.benbenlaw.utility.recipe.custom.ResourceGeneratorRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidGeneratorRecipeCategory implements IRecipeCategory<FluidGeneratorRecipe> {

    public static final ResourceLocation UID = Utility.rl( "fluid_generator");
    public static final ResourceLocation TEXTURE = Utility.rl("textures/gui/fluid_generator_jei.png");
    public static final IRecipeType<FluidGeneratorRecipe> RECIPE_TYPE = IRecipeType.create(UID, FluidGeneratorRecipe.class);

    private final IDrawable icon;
    private final int width = 104;
    private final int height = 20;

    public FluidGeneratorRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(UtilityBlocks.RESOURCE_GENERATOR.get()));
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public @NotNull IRecipeType<FluidGeneratorRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.utility.fluid_generator");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FluidGeneratorRecipe recipe, @NotNull IFocusGroup focuses) {

        Fluid fluid = BuiltInRegistries.FLUID.getOptional(ResourceLocation.parse(recipe.input())).orElseThrow();
        ItemStack bucketItem = fluid.getBucket().getDefaultInstance();

        builder.addSlot(RecipeIngredientRole.INPUT, 2, 2).add(bucketItem).addRichTooltipCallback(
                (ingredients, tooltip) -> {
                    tooltip.add(Component.translatable("jei.utility.fluid_generator_recipe", recipe.input()).withStyle(ChatFormatting.GOLD));
                }
        );
        builder.addSlot(RecipeIngredientRole.OUTPUT, 48, 2).add(recipe.output().getFluid());
    }

    @Override
    public void createRecipeExtras(@NotNull IRecipeExtrasBuilder builder, FluidGeneratorRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addAnimatedRecipeArrow(200).setPosition(60, 1);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, FluidGeneratorRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (MouseUtil.isMouseAboveArea((int) mouseX, (int) mouseY, 57, 1, 0, 0, 28, 18)) {
            tooltip.add(Component.literal(200 / 20 + "s"));
        }
    }

    @Override
    public void draw(FluidGeneratorRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, 0, 0, width, height, width, height);

    }
}
