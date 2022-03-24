package mod.forestation;

import mod.forestation.handler.PlantingHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Forestation implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        ServerEntityEvents.ENTITY_LOAD.register(Forestation::onEntityLoad);
        ServerEntityEvents.ENTITY_UNLOAD.register(Forestation::onEntityUnload);

        PlayerBlockBreakEvents.AFTER.register(Forestation::onBlockBreak);

        ServerTickEvents.END_WORLD_TICK.register(Forestation::onWorldTickEnd);
    }

    /**
     * This is a set of item entities for saplings that will be placed
     * if specific conditions in {@link PlantingHandler#checkConditions(ItemEntity)} are met.
     */
    private static Set<ItemEntity> saplings = new HashSet<>();

    private static void onWorldTickEnd(ServerWorld world)
    {
        saplings = saplings.stream().filter(e -> !PlantingHandler.tryPlant(e)).collect(Collectors.toSet());
    }

    private static void onBlockBreak(World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity)
    {
        if(blockState.isIn(BlockTags.SAPLINGS))
            blockBrokenPos = blockPos;
    }

    public static ItemEntity itemDropped;
    public static BlockPos blockBrokenPos;

    private static void onEntityLoad(Entity entity, ServerWorld world)
    {
        // Only do this if the entity is an item entity
        if(entity instanceof ItemEntity)
        {
            ItemStack stack = ((ItemEntity) entity).getStack();

            if(stack.isIn(ItemTags.SAPLINGS))
                if(entity != itemDropped && !entity.getBlockPos().equals(blockBrokenPos))
                    saplings.add((ItemEntity) entity);
        }
    }

    private static void onEntityUnload(Entity entity, ServerWorld world)
    {
        if(entity instanceof ItemEntity)
        {
            // Remove the entities if they are still present in the list.

            saplings.remove((ItemEntity) entity);
        }
    }
}
