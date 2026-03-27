package com.benbenlaw.utility.integration.jei;

import com.benbenlaw.core.util.MouseUtil;
import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.UtilityBlocks;
import com.benbenlaw.utility.event.client.ClientRecipeCache;
import com.benbenlaw.utility.recipe.custom.FluidGeneratorRecipe;
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
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FluidGeneratorRecipeCategory implements IRecipeCategory<FluidGeneratorRecipe> {

    public static final Identifier TEXTURE = Utility.identifier("textures/gui/fluid_generator_jei.png");
    public static final IRecipeType<FluidGeneratorRecipe> RECIPE_TYPE = IRecipeType.create(Utility.MOD_ID, "fluid_generator", FluidGeneratorRecipe.class);

    private final int width = 66;
    private final int height = 20;
    private final IDrawable icon;

    @Override
    public @Nullable Identifier getIdentifier(FluidGeneratorRecipe recipe) {
        return ClientRecipeCache.getCachedFluidGeneratorRecipes().stream()
                .filter(r -> r.equals(recipe))
                .findFirst()
                .map(r -> {
                    // Find the corresponding ID in the cache map
                    for (Map.Entry<Identifier, FluidGeneratorRecipe> entry : ClientRecipeCache.cachedFluidGeneratorRecipes.entrySet()) {
                        if (entry.getValue().equals(recipe)) {
                            return entry.getKey();
                        }
                    }
                    return null;
                })
                .orElse(null);
    }

    public FluidGeneratorRecipeCategory(IGuiHelper guiHelper) {
         this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(UtilityBlocks.RESOURCE_GENERATOR.get()));
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
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FluidGeneratorRecipe recipe, @NotNull IFocusGroup focuses) {

        Fluid fluid = BuiltInRegistries.FLUID.getOptional(Identifier.parse(recipe.input())).orElseThrow();
        ItemStack bucketItem = fluid.getBucket().getDefaultInstance();

        builder.addSlot(RecipeIngredientRole.INPUT, 2, 2).add(bucketItem).addRichTooltipCallback(
                (ingredients, tooltip) -> {
                    tooltip.add(Component.translatable("jei.utility.fluid_generator_recipe", recipe.input()).withStyle(ChatFormatting.GOLD));
                }
        );
        builder.addSlot(RecipeIngredientRole.OUTPUT, 48, 2).add(recipe.output().fluid().value()).addRichTooltipCallback(
                (ingredients, tooltip) -> {;
                    tooltip.add(Component.literal(recipe.output().amount() + "mb").withStyle(ChatFormatting.GOLD));
                }
        );
    }


    @Override
    public void getTooltip(ITooltipBuilder tooltip, FluidGeneratorRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (MouseUtil.isMouseAboveArea((int) mouseX, (int) mouseY, 19, 1, 0, 0, 28, 18)) {
            tooltip.add(Component.translatable("tooltip.core.ticks", 200));
        }
    }

    @Override
    public void draw(FluidGeneratorRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor GuiGraphicsExtractor, double mouseX, double mouseY) {
        GuiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, 0, 0, width, height, width, height);
    }

    @Override
    public void createRecipeExtras(@NotNull IRecipeExtrasBuilder builder, FluidGeneratorRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addAnimatedRecipeArrow(200).setPosition(22, 1);
    }
}
