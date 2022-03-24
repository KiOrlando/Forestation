package mod.forestation.handler;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PlantingHandler
{
    public static boolean tryPlant(ItemEntity entity)
    {
        final ItemStack stack = entity.getStack();
        final Item item = stack.getItem();

        if(item instanceof BlockItem)
        {
            if(!checkConditions(entity))
                return false;

            final World world = entity.getWorld();
            final BlockPos pos = entity.getBlockPos();
            
            final AutomaticItemPlacementContext pc = new AutomaticItemPlacementContext(world, pos, Direction.DOWN, stack, Direction.UP);

            return ((BlockItem) item).place(pc).isAccepted();
        }

        return false;
    }

    private static boolean checkConditions(ItemEntity entity)
    {
        final boolean c1 = hasNearbySaplings(entity.getWorld(), entity.getBlockPos());
//        final boolean c2 =

        return c1;// && c2;
    }

    // Max vertical and horizontal distance to check for other saplings.
    // Extends from -x to positive x.
    private static final int v = 2;
    private static final int h = 2;

    private static boolean hasNearbySaplings(World world, BlockPos pos)
    {
        for(int y = -v; y <= v; y++)
            for(int x = -h; x <= h; x++)
                for(int z = -h; z <= h; z++)
                   if(world.getBlockState(pos.add(x, y, z)).isIn(BlockTags.SAPLINGS))
                       return true;
        return false;
    }
}
