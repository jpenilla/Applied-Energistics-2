/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.menu.implementations;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

import appeng.api.config.SecurityPermissions;
import appeng.api.config.Settings;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.util.IConfigManager;
import appeng.core.definitions.AEItems;
import appeng.menu.SlotSemantics;
import appeng.menu.slot.FakeSlot;
import appeng.parts.automation.StorageStockpileSwitchPart;

/**
 * @see appeng.client.gui.implementations.StorageStockpileSwitchScreen
 */
public class StorageStockpileSwitchMenu extends UpgradeableMenu<StorageStockpileSwitchPart> {

    private static final String ACTION_SET_FLOOR = "setFloor";
    private static final String ACTION_SET_CEILING = "setCeiling";

    public static final MenuType<StorageStockpileSwitchMenu> TYPE = MenuTypeBuilder
            .create(StorageStockpileSwitchMenu::new, StorageStockpileSwitchPart.class)
            .requirePermission(SecurityPermissions.BUILD)
            .withInitialData((host, buffer) -> {
                GenericStack.writeBuffer(host.getConfig().getStack(0), buffer);
                buffer.writeVarLong(host.getCeiling());
                buffer.writeVarLong(host.getFloor());
            }, (host, menu, buffer) -> {
                menu.getHost().getConfig().setStack(0, GenericStack.readBuffer(buffer));
                menu.ceiling = buffer.readVarLong();
                menu.floor = buffer.readVarLong();
            })
            .build("storage_stockpile_switch");

    // Only synced once on menu-open, and only used on client
    private long ceiling;
    private long floor;

    public StorageStockpileSwitchMenu(MenuType<StorageStockpileSwitchMenu> menuType, int id, Inventory ip,
            StorageStockpileSwitchPart te) {
        super(menuType, id, ip, te);

        registerClientAction(ACTION_SET_CEILING, Long.class, this::setCeiling);
        registerClientAction(ACTION_SET_FLOOR, Long.class, this::setFloor);
    }

    public long getFloor() {
        return this.floor;
    }

    public long getCeiling() {
        return this.ceiling;
    }

    public void setFloor(long initialValue) {
        if (isClientSide()) {
            if (initialValue != this.floor) {
                this.floor = initialValue;
                sendClientAction(ACTION_SET_FLOOR, initialValue);
            }
        } else {
            getHost().setFloor(initialValue);
        }
    }

    public void setCeiling(long initialValue) {
        if (isClientSide()) {
            if (initialValue != this.ceiling) {
                this.ceiling = initialValue;
                sendClientAction(ACTION_SET_CEILING, initialValue);
            }
        } else {
            getHost().setCeiling(initialValue);
        }
    }

    @Override
    protected void setupConfig() {
        var inv = getHost().getConfig().createMenuWrapper();
        this.addSlot(new FakeSlot(inv, 0), SlotSemantics.CONFIG);
    }

    @Override
    public void onSlotChange(Slot s) {
        super.onSlotChange(s);
    }

    @Override
    protected void loadSettingsFromHost(IConfigManager cm) {
        if (cm.hasSetting(Settings.FUZZY_MODE)) {
            this.setFuzzyMode(cm.getSetting(Settings.FUZZY_MODE));
        }
        this.setRedStoneMode(cm.getSetting(Settings.REDSTONE_EMITTER_STOCKPILE_SWITCH));
    }

    public boolean supportsFuzzySearch() {
        return getHost().getConfigManager().hasSetting(Settings.FUZZY_MODE) && hasUpgrade(AEItems.FUZZY_CARD);
    }

    @Nullable
    public AEKey getConfiguredFilter() {
        return getHost().getConfig().getKey(0);
    }
}
