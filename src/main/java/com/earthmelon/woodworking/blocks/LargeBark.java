package com.earthmelon.woodworking.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;




public class LargeBark extends HorizontalDirectionalBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    // TODO: combine three models moved around as seen below or scale up a smaller model. Then rotate based on placed direction.
    protected static VoxelShape HITBOX = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
//            .move(1.0D, 0.0D, 0.0D);

    public LargeBark(Properties properties) {
        super(properties);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    public @NotNull VoxelShape getShape(BlockState p_56390_, BlockGetter p_56391_, BlockPos p_56392_, CollisionContext p_56393_) {
        return HITBOX;
    }
}
