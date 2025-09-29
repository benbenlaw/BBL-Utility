package com.benbenlaw.utility.integration.jei;

import com.benbenlaw.core.util.MouseUtil;
import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.UtilityBlocks;
import com.benbenlaw.utility.block.entity.DryingTableBlockEntity;
import com.benbenlaw.utility.recipe.DryingTableRecipeType;
import com.benbenlaw.utility.recipe.custom.DryingTableRecipe;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DryingTableRecipeCategory implements IRecipeCategory<DryingTableRecipe> {

    public static final ResourceLocation UID = Utility.rl( "drying_table");
    public static final ResourceLocation TEXTURE = Utility.rl("textures/gui/drying_table_jei.png");
    public static final ResourceLocation WATERLOGGED = Utility.rl("textures/gui/sprites/waterlogged_progress_arrow.png");
    public static final IRecipeType<DryingTableRecipe> RECIPE_TYPE = IRecipeType.create(UID, DryingTableRecipe.class);

    private final IDrawable icon;
    private final int width = 66;
    private final int height = 20;

    public DryingTableRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(UtilityBlocks.DRYING_TABLE.get()));
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
    public @NotNull IRecipeType<DryingTableRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.utility.drying_table");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DryingTableRecipe recipe, @NotNull IFocusGroup focuses) {

        builder.addSlot(RecipeIngredientRole.INPUT, 2, 2).add(recipe.input().ingredient());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 48, 2).add(recipe.output());

    }

    @Override
    public void createRecipeExtras(@NotNull IRecipeExtrasBuilder builder, DryingTableRecipe recipe, @NotNull IFocusGroup focuses) {
        if (recipe.recipeType() == DryingTableRecipeType.DRYING) {
            builder.addAnimatedRecipeArrow(200).setPosition(22, 1);
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, DryingTableRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (MouseUtil.isMouseAboveArea((int) mouseX, (int) mouseY, 19, 1, 0, 0, 28, 18)) {

            if (recipe.recipeType() == DryingTableRecipeType.SOAKING) {
                tooltip.add(Component.translatable("jei.utility.soaking"));
            }

            tooltip.add(Component.literal(200 / 20 + "s"));
        }
    }

    @Override
    public void draw(DryingTableRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, 0, 0, width, height, width, height);

        if (recipe.recipeType() == DryingTableRecipeType.SOAKING) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, WATERLOGGED, 21, 1, 0, 0, 24, 16, 24, 16);
        }
    }
}
