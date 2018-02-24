package com.vnator.adminshop.blocks.itemseller;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.ModGuiHandler;
import com.vnator.adminshop.blocks.BlockTileEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nullable;

public class BlockItemSeller extends BlockTileEntity<TileEntityItemSeller> {

	public BlockItemSeller(){super(Material.ROCK, "itemseller");}

	@Override
	public Class<TileEntityItemSeller> getTileEntityClass() {
		return TileEntityItemSeller.class;
	}

	@Nullable
	@Override
	public TileEntityItemSeller createTileEntity(World world, IBlockState state) {
		return new TileEntityItemSeller();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
									EnumFacing side, float hitX, float hitY, float hitZ){

		super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
		if(!world.isRemote){
			TileEntityItemSeller ent = (TileEntityItemSeller) world.getTileEntity(pos);
			if(ent.getPlayer() == null) {
				ent.setPlayer(player.getName());
				player.sendMessage(new TextComponentString("Registered Player to Item Seller!"));
				ent.markDirty();
			}

			if(ent.getPlayer().equals(player.getName())){
				player.sendMessage(new TextComponentString("You are the registered owner of this block."));
			}else{
				player.sendMessage(new TextComponentString("WARNING: You will lose any items inserted. " +
						"This block is registered to "+ent.getPlayer()));
			}

			IFluidHandlerItem handler = FluidUtil.getFluidHandler(player.getHeldItem(hand));//.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
			if(handler != null){
				//Put liquid into tank
				FluidStack liquid = handler.drain(1000, false);
				if(liquid != null && ent.fill(liquid, false) == 1000){
					ent.fill(handler.drain(1000, true), true);
					player.setHeldItem(hand, handler.getContainer());
				}
			}else{
				player.openGui(AdminShop.instance, ModGuiHandler.SELLER, world, pos.getX(), pos.getY(), pos.getZ());
			}
			//player.sendMessage(new TextComponentString("liquid in tank: "+ent.tank.getFluid().getFluid().getName()+" x"+ent.tank.getFluidAmount()));
		}
		return true;
	}
}
