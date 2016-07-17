package com.brandon3055.brandonscore.network.wrappers;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.blocks.TileBCBase;
import com.brandon3055.brandonscore.network.PacketSyncableObject;
import com.brandon3055.brandonscore.utils.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by brandon3055 on 26/3/2016.
 */
public class SyncableInt extends SyncableObject {

    public int value;
    private int lastTickValue;

    public SyncableInt(int value, boolean syncInTile, boolean syncInContainer, boolean updateOnReceived) {
        super(syncInTile, syncInContainer, updateOnReceived);
        this.value = this.lastTickValue = value;
    }

    public SyncableInt(int value, boolean syncInTile, boolean syncInContainer) {
        super(syncInTile, syncInContainer);
        this.value = this.lastTickValue = value;
    }

    @Override
    public void detectAndSendChanges(TileBCBase tile, EntityPlayer player, boolean forceSync) {
        if (lastTickValue != value || forceSync) {
            lastTickValue = value;
            tile.dirtyBlock();
            if (player == null) {
                BrandonsCore.network.sendToAllAround(new PacketSyncableObject(tile, index, value, updateOnReceived), tile.syncRange());
            } else if (player instanceof EntityPlayerMP) {
                BrandonsCore.network.sendTo(new PacketSyncableObject(tile, index, value, updateOnReceived), (EntityPlayerMP) player);
            } else LogHelper.error("SyncableInt#detectAndSendChanges No valid destination for sync packet!");
        }
    }

    @Override
    public void updateReceived(PacketSyncableObject packet) {
        if (packet.dataType == PacketSyncableObject.INT_INDEX) {
            value = packet.intValue;
        }
    }

    @Override
    public void toNBT(NBTTagCompound compound) {
        compound.setInteger("SyncableInt" + index, value);
    }

    @Override
    public void fromNBT(NBTTagCompound compound) {
        if (compound.hasKey("SyncableInt" + index)) {
            value = compound.getInteger("SyncableInt" + index);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
