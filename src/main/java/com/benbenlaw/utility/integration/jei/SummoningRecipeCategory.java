package com.benbenlaw.utility.integration.jei;

import com.benbenlaw.core.util.MouseUtil;
import com.benbenlaw.utility.Utility;
import com.benbenlaw.utility.block.UtilityBlocks;
import com.benbenlaw.utility.event.ClientRecipeCache;
import com.benbenlaw.utility.recipe.custom.ResourceGeneratorRecipe;
import com.benbenlaw.utility.recipe.custom.SummoningRecipe;
import com.benbenlaw.utility.util.BlockTarget;
import com.benbenlaw.utility.util.TemperatureValues;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class SummoningRecipeCategory implements IRecipeCategory<SummoningRecipe> {

    public static final Identifier TEXTURE = Utility.identifier("textures/gui/summoning_block_jei.png");
    public static final IRecipeType<SummoningRecipe> RECIPE_TYPE = IRecipeType.create(Utility.MOD_ID, "summoning", SummoningRecipe.class);

    private final int width = 86;
    private final int height = 39;
    private final IDrawable icon;

    @Override
    public @Nullable Identifier getIdentifier(SummoningRecipe recipe) {
        return ClientRecipeCache.getCachedSummoningRecipes().stream()
                .filter(r -> r.equals(recipe))
                .findFirst()
                .map(r -> {
                    // Find the corresponding ID in the cache map
                    for (Map.Entry<Identifier, SummoningRecipe> entry : ClientRecipeCache.cachedSummoningRecipes.entrySet()) {
                        if (entry.getValue().equals(recipe)) {
                            return entry.getKey();
                        }
                    }
                    return null;
                })
                .orElse(null);
    }

    public SummoningRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(UtilityBlocks.SUMMONING_BLOCK.get()));
    }

    @Override
    public @NotNull IRecipeType<SummoningRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.utility.summoning");
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
    public void setRecipe(IRecipeLayoutBuilder builder, SummoningRecipe recipe, @NotNull IFocusGroup focuses) {

        EntityType<?> entityType = recipe.summonedEntity();
        ItemStack spawnEgg = Objects.requireNonNull(SpawnEggItem.byId(entityType)).isPresent()
                ? new ItemStack(SpawnEggItem.byId(entityType).get())
                : ItemStack.EMPTY;

        Optional<TagKey<Block>> tagTarget = recipe.belowBlock().getTag();
        Optional<Block> blockTarget = recipe.belowBlock().getBlock();

        if (tagTarget.isPresent()) {
            TagKey<Item> tag = TagKey.create(Registries.ITEM, tagTarget.get().location());
            Ingredient ingredient = Ingredient.of(BuiltInRegistries.ITEM.getOrThrow(tag));
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 2, 21).add(ingredient).addRichTooltipCallback(
                    (ingredients, tooltip) -> {
                        tooltip.add(Component.translatable("jei.utility.summoning_block_below_block").withStyle(ChatFormatting.GOLD));
                    }
            );;
        }

        else if (blockTarget.isPresent()) {
            ItemStack blockItem = blockTarget.get().asItem().getDefaultInstance();

            if (blockTarget.get() == Blocks.WATER) {
                blockItem = new ItemStack(Items.WATER_BUCKET);
            }

            if (blockTarget.get() == Blocks.LAVA) {
                blockItem = new ItemStack(Items.LAVA_BUCKET);
            }

            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 2, 21).add(blockItem).addRichTooltipCallback(
                    (ingredients, tooltip) -> {
                        tooltip.add(Component.translatable("jei.utility.summoning_block_below_block").withStyle(ChatFormatting.GOLD));
                    }
            );
        }

        builder.addSlot(RecipeIngredientRole.INPUT, 2, 2).add(recipe.input().ingredient());

        if (spawnEgg != ItemStack.EMPTY) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 26, 21).add(spawnEgg);
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, SummoningRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (MouseUtil.isMouseAboveArea((int) mouseX, (int) mouseY, 21, 2, 0, 0, 28, 18)) {
            tooltip.add(Component.translatable("tooltip.core.ticks", 100));
            if (recipe.temperatureVariant().isPresent()) {

                if (recipe.temperatureVariant().get() == TemperatureValues.WARM) {
                    tooltip.add(Component.translatable("jei.utility.summoning_requirement_warm").withStyle(ChatFormatting.GOLD));
                }
                if (recipe.temperatureVariant().get() == TemperatureValues.COLD) {
                    tooltip.add(Component.translatable("jei.utility.summoning_requirement_cold").withStyle(ChatFormatting.GOLD));
                }
                if (recipe.temperatureVariant().get() == TemperatureValues.TEMPERATE) {
                    tooltip.add(Component.translatable("jei.utility.summoning_requirement_temperate").withStyle(ChatFormatting.GOLD));
                }
            }
        }

        if (MouseUtil.isMouseAboveArea((int) mouseX, (int) mouseY, 50, 2, 0, 0, 34, 35)) {
            tooltip.add((Component.translatable("jei.utility.summoned_entity", recipe.summonedEntity().getDescription())));
        }
    }

    @Override
    public void draw(SummoningRecipe recipe, IRecipeSlotsView view, GuiGraphics gg, double mouseX, double mouseY) {
        gg.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, 0, 0, width, height, width, height);

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        LivingEntity entity = (LivingEntity) recipe.summonedEntity()
                .create(mc.level, EntitySpawnReason.MOB_SUMMONED);

        recipe.entityData().ifPresent(tag -> {
            ValueInput input = TagValueInput.create(
                    ProblemReporter.DISCARDING,
                    mc.level.registryAccess(),
                    tag
            );
            entity.load(input);
        });

        if (entity == null) return;

        Matrix3x2fStack pose = gg.pose();
        float guiLeft = pose.m20();
        float guiTop  = pose.m21();

        int x1 = (int) guiLeft + 52;
        int y1 = (int) guiTop  + 2;
        int x2 = (int) guiLeft + 83;
        int y2 = (int) guiTop  + 36;

        float areaW = x2 - x1;
        float areaH = y2 - y1;

        float entH = entity.getBbHeight();
        float entW = entity.getBbWidth();

        float entFootprint = Mth.sqrt(entW * entW + entH * entH);

        float scaleH = areaH / entH;
        float scaleW = areaW / entFootprint;

        float scale = Math.min(scaleH, scaleW);

        scale = Mth.clamp(scale, 6.0F, 18.0F);

        float yOffset = (areaH - entH * scale) / 2 / scale;

        int screenMouseX = (int) (mouseX + guiLeft);
        int screenMouseY = (int) (mouseY + guiTop);

        InventoryScreen.renderEntityInInventoryFollowsMouse(
                gg,
                x1, y1, x2, y2,
                (int) scale,
                yOffset,
                screenMouseX, screenMouseY,
                entity
        );

    }

    @Override
    public void createRecipeExtras(@NotNull IRecipeExtrasBuilder builder, SummoningRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addAnimatedRecipeArrow(200).setPosition(23, 2);
    }
}
