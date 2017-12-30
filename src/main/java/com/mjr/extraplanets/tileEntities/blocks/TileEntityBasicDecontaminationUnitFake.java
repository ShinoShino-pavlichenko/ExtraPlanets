package com.mjr.extraplanets.tileEntities.blocks;

import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock;
import micdoodle8.mods.miccore.Annotations.NetworkedField;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.mjr.extraplanets.tileEntities.machines.TileEntityBasicDecontaminationUnit;

public class TileEntityBasicDecontaminationUnitFake extends TileBaseElectricBlock
{
    // The the position of the main block
    @NetworkedField(targetSide = Side.CLIENT)
    public BlockPos mainBlockPosition;
    private WeakReference<TileEntityBasicDecontaminationUnit> mainTelepad = null;
    @NetworkedField(targetSide = Side.CLIENT)
    private boolean canConnect = false;

    public void setMainBlock(BlockPos mainBlock)
    {
        this.setMainBlockInternal(mainBlock);

        if (!this.worldObj.isRemote)
        {
            IBlockState state = this.worldObj.getBlockState(this.getPos());
            this.worldObj.notifyBlockUpdate(this.getPos(), state, state, 3);
        }
    }

    private void setMainBlockInternal(BlockPos mainBlock)
    {
        this.mainBlockPosition = mainBlock;
        this.updateConnectable();
    }

    public void onBlockRemoval()
    {
        TileEntityBasicDecontaminationUnit telepad = this.getBaseTelepad();

        if (telepad != null)
        {
            telepad.onDestroy(this);
        }
    }

    public boolean onActivated(EntityPlayer par5EntityPlayer)
    {
        TileEntityBasicDecontaminationUnit telepad = this.getBaseTelepad();
        return telepad != null && telepad.onActivated(par5EntityPlayer);
    }

    @Override
    public void update()
    {
        super.update();

        TileEntityBasicDecontaminationUnit telepad = this.getBaseTelepad();

        if (telepad != null)
        {
            this.storage.setCapacity(telepad.storage.getCapacityGC());
            this.storage.setMaxExtract(telepad.storage.getMaxExtract());
            this.storage.setMaxReceive(telepad.storage.getMaxReceive());
            this.extractEnergyGC(null, telepad.receiveEnergyGC(null, this.getEnergyStoredGC(), false), false);
        }
    }

    private TileEntityBasicDecontaminationUnit getBaseTelepad()
    {
        if (this.mainBlockPosition == null)
        {
            return null;
        }

        if (mainTelepad == null)
        {
            TileEntity tileEntity = this.worldObj.getTileEntity(this.mainBlockPosition);

            if (tileEntity != null)
            {
                if (tileEntity instanceof TileEntityBasicDecontaminationUnit)
                {
                    mainTelepad = new WeakReference<TileEntityBasicDecontaminationUnit>(((TileEntityBasicDecontaminationUnit) tileEntity));
                }
            }
        }

        if (mainTelepad == null)
        {
            this.worldObj.setBlockToAir(this.mainBlockPosition);
        }
        else
        {
            TileEntityBasicDecontaminationUnit telepad = this.mainTelepad.get();

            if (telepad != null)
            {
                return telepad;
            }
            else
            {
                this.worldObj.removeTileEntity(this.getPos());
            }
        }

        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        NBTTagCompound tagCompound = nbt.getCompoundTag("mainBlockPosition");
        this.setMainBlockInternal(new BlockPos(tagCompound.getInteger("x"), tagCompound.getInteger("y"), tagCompound.getInteger("z")));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        if (this.mainBlockPosition != null)
        {
            NBTTagCompound tagCompound = new NBTTagCompound();
            tagCompound.setInteger("x", this.mainBlockPosition.getX());
            tagCompound.setInteger("y", this.mainBlockPosition.getY());
            tagCompound.setInteger("z", this.mainBlockPosition.getZ());
            nbt.setTag("mainBlockPosition", tagCompound);
        }

        return nbt;
    }

    @Override
    public double getPacketRange()
    {
        return 30.0D;
    }

    @Override
    public int getPacketCooldown()
    {
        return 50;
    }

    @Override
    public boolean isNetworkedTile()
    {
            return true;
        }
    
    @Override
    public void getNetworkedData(ArrayList<Object> sendData)
        {
        if (this.mainBlockPosition == null)
        {
            if (this.worldObj.isRemote || !this.resetMainBlockPosition())
            {
                return;
        }
    }
        super.getNetworkedData(sendData);
    }

    private boolean resetMainBlockPosition()
        {
            for (int x = -1; x <= 1; x++)
            {
                for (int z = -1; z <= 1; z++)
                {
                for (int y = -2; y < 1; y += 2)
                {
                    final BlockPos vecToCheck = this.getPos().add(x, y, z);
                    if (this.worldObj.getTileEntity(vecToCheck) instanceof TileEntityBasicDecontaminationUnit)
                    {
                        this.setMainBlock(vecToCheck);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldUseEnergy()
    {
        return false;
    }

    @Override
    public EnumFacing getElectricInputDirection()
    {
        if (!this.canConnect)
        {
            return null;
        }

        return EnumFacing.UP;
    }

    @Override
    public EnumFacing getFront()
    {
        return EnumFacing.NORTH;
    }

    @Override
    public ItemStack getBatteryInSlot()
    {
        return null;
    }

    private void updateConnectable()
    {
        if (this.mainBlockPosition != null)
        {
            if (this.getPos().getX() == mainBlockPosition.getX() && this.getPos().getZ() == mainBlockPosition.getZ())
            {
                if (this.getPos().getY() > mainBlockPosition.getY())
                {
                    // If the block has the same x- and y- coordinates, but is above the base block, this is the
                    //      connectable tile
                    this.canConnect = true;
                    return;
                }
            }
        }

        this.canConnect = false;
    }
}
