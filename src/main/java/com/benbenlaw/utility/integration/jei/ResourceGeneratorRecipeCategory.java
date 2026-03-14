package com.benbenlaw.utility.integration.jei;

import com.benbenlaw.core.util.MouseUtil;
import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.UtilityBlocks;
import com.benbenlaw.utility.event.ClientRecipeCache;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class ResourceGeneratorRecipeCategory implements IRecipeCategory<ResourceGeneratorRecipe> {

    public static final Identifier TEXTURE = Utility.identifier("textures/gui/resource_generator_jei.png");
    public static final IRecipeType<ResourceGeneratorRecipe> RECIPE_TYPE = IRecipeType.create(Utility.MOD_ID, "resource_generator", ResourceGeneratorRecipe.class);

    private final int width = 104;
    private final int height = 20;
    private final IDrawable icon;

    @Override
    public @Nullable Identifier getIdentifier(@NonNull ResourceGeneratorRecipe recipe) {
        return ClientRecipeCache.getCachedResourceGeneratorRecipes().stream()
                .filter(r -> r.equals(recipe))
                .findFirst()
                .map(r -> {
                    // Find the corresponding ID in the cache map
                    for (Map.Entry<Identifier, ResourceGeneratorRecipe> entry : ClientRecipeCache.cachedResourceGeneratorRecipes.entrySet()) {
                        if (entry.getValue().equals(recipe)) {
                            return entry.getKey();
                        }
                    }
                    return null;
                })
                .orElse(null);
    }

    public ResourceGeneratorRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(UtilityBlocks.RESOURCE_GENERATOR.get()));
    }

    @Override
    public @NotNull IRecipeType<ResourceGeneratorRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.utility.resource_generator");
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
    public void setRecipe(IRecipeLayoutBuilder builder, ResourceGeneratorRecipe recipe, @NotNull IFocusGroup focuses) {

        builder.addSlot(RecipeIngredientRole.INPUT, 2, 2).add(recipe.leftFluid().getFluid()).addRichTooltipCallback(
                (ingredients, tooltip) -> {
                    tooltip.add(Component.literal(recipe.leftFluid().getAmount() + "mb").withStyle(ChatFormatting.GOLD));
                    if (recipe.consumeLeft()) {
                        tooltip.add(Component.translatable("jei.utility.consumed").withStyle(ChatFormatting.RED));
                    }
                }
        );
        builder.addSlot(RecipeIngredientRole.INPUT, 21, 2).add(recipe.input().create());
        builder.addSlot(RecipeIngredientRole.INPUT, 40, 2).add(recipe.rightFluid().getFluid()).addRichTooltipCallback(
                (ingredients, tooltip) -> {
                    tooltip.add(Component.literal(recipe.leftFluid().getAmount() + "mb").withStyle(ChatFormatting.GOLD));
                    if (recipe.consumeRight()) {
                        tooltip.add(Component.translatable("jei.utility.consumed").withStyle(ChatFormatting.RED));
                    }
                }
        );
        builder.addSlot(RecipeIngredientRole.OUTPUT, 86, 2).add(recipe.output().create());
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, ResourceGeneratorRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (MouseUtil.isMouseAboveArea((int) mouseX, (int) mouseY, 57, 1, 0, 0, 28, 18)) {
            tooltip.add(Component.translatable("tooltip.core.ticks", 200));
        }
    }

    @Override
    public void draw(ResourceGeneratorRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, 0, 0, width, height, width, height);

    }

    @Override
    public void createRecipeExtras(@NotNull IRecipeExtrasBuilder builder, ResourceGeneratorRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addAnimatedRecipeArrow(200).setPosition(60, 1);
    }
}
