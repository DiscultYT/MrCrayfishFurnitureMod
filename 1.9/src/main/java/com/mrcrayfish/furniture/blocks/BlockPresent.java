/**
 * MrCrayfish's Furniture Mod
 * Copyright (C) 2016  MrCrayfish (http://www.mrcrayfish.com/)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mrcrayfish.furniture.blocks;

import java.util.Date;
import java.util.List;
import java.util.Random;

import com.mrcrayfish.furniture.MrCrayfishFurnitureMod;
import com.mrcrayfish.furniture.gui.inventory.ISimpleInventory;
import com.mrcrayfish.furniture.init.FurnitureAchievements;
import com.mrcrayfish.furniture.tileentity.TileEntityPresent;
import com.mrcrayfish.furniture.util.InventoryUtil;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.swing.TextComponent;

public class BlockPresent extends Block implements ITileEntityProvider
{
	public static final PropertyEnum<EnumDyeColor> COLOUR = PropertyEnum.<EnumDyeColor>create("colour", EnumDyeColor.class);
	
	private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 0.35, 0.75);
	
	public BlockPresent(Material material)
	{
		super(material);
		this.setHardness(0.5F);
		this.setStepSound(SoundType.CLOTH);
		this.isBlockContainer = true;
		this.setDefaultState(this.blockState.getBaseState().withProperty(COLOUR, EnumDyeColor.WHITE));
		this.setCreativeTab(MrCrayfishFurnitureMod.tabFurniture);
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player)
	{
		TileEntityPresent present = (TileEntityPresent) world.getTileEntity(pos);
		if (present != null)
		{
			world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.entity_player_levelup, SoundCategory.BLOCKS, 0.75F, 1.0F, false);
			player.addChatComponentMessage(new TextComponentString(TextFormatting.GREEN + "Merry Christmas" + TextFormatting.RESET + " from " + TextFormatting.RED + present.ownerName));
			player.addStat(FurnitureAchievements.unwrapPresent);
		}
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof IInventory)
		{
			IInventory inv = (IInventory) tileEntity;
			InventoryHelper.dropInventoryItems(world, pos, inv);
		}
		if (tileEntity instanceof ISimpleInventory)
		{
			ISimpleInventory inv = (ISimpleInventory) tileEntity;
			InventoryUtil.dropInventoryItems(world, pos, inv);
		}
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) 
	{
		return BOUNDING_BOX;
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB axisAligned, List<AxisAlignedBB> axisAlignedList, Entity collidingEntity) 
	{
		super.addCollisionBoxToList(pos, axisAligned, axisAlignedList, BOUNDING_BOX);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return null;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntityPresent();
	}

	public int getDaysInbetween(Date date, Date date2)
	{
		return (int) (date2.getTime() - date.getTime()) / (1000 * 60 * 60 * 24);
	}
	
	public MapColor getMapColor(IBlockState state)
    {
        return ((EnumDyeColor)state.getValue(COLOUR)).getMapColor();
    }
	
	@Override
	public int damageDropped(IBlockState state)
	{
		return ((EnumDyeColor)state.getValue(COLOUR)).getMetadata();
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((EnumDyeColor)state.getValue(COLOUR)).getMetadata();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(COLOUR, EnumDyeColor.byMetadata(meta));
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] { COLOUR });
	}
	
	@Override
	public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam)
	{
		super.onBlockEventReceived(worldIn, pos, state, eventID, eventParam);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventParam);
	}
	
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
	{
		for (EnumDyeColor dye : EnumDyeColor.values())
		{
			list.add(new ItemStack(itemIn, 1, dye.getMetadata()));
		}
	}
}
