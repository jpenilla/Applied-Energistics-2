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

package appeng.client.gui.implementations;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.api.config.FuzzyMode;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.client.gui.NumberEntryType;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.NumberEntryWidget;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.localization.GuiText;
import appeng.menu.implementations.StorageStockpileSwitchMenu;

public class StorageStockpileSwitchScreen extends UpgradeableScreen<StorageStockpileSwitchMenu> {

    private final SettingToggleButton<RedstoneMode> redstoneMode;
    private final SettingToggleButton<FuzzyMode> fuzzyMode;
    private final NumberEntryWidget floor;
    private final NumberEntryWidget ceiling;

    public StorageStockpileSwitchScreen(StorageStockpileSwitchMenu menu, Inventory playerInventory, Component title,
            ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.redstoneMode = new ServerSettingToggleButton<>(Settings.REDSTONE_EMITTER_STOCKPILE_SWITCH,
                RedstoneMode.LOW_SIGNAL);
        this.fuzzyMode = new ServerSettingToggleButton<>(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        this.addToLeftToolbar(this.redstoneMode);
        this.addToLeftToolbar(this.fuzzyMode);

        this.floor = widgets.addNumberEntryWidget("floor", NumberEntryType.of(menu.getConfiguredFilter()));
        this.floor.setTextFieldStyle(style.getWidget("floorInput"));
        this.floor.setLongValue(this.menu.getFloor());
        this.floor.setOnChange(this::saveFloor);
        this.floor.setOnConfirm(this::onClose);

        this.ceiling = widgets.addNumberEntryWidget("ceiling", NumberEntryType.of(menu.getConfiguredFilter()));
        this.ceiling.setTextFieldStyle(style.getWidget("ceilingInput"));
        this.ceiling.setLongValue(this.menu.getCeiling());
        this.ceiling.setOnChange(this::saveCeiling);
        this.ceiling.setOnConfirm(this::onClose);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        // Update the type in case the filter has changed
        this.floor.setType(NumberEntryType.of(menu.getConfiguredFilter()));

        this.fuzzyMode.set(menu.getFuzzyMode());
        this.fuzzyMode.setVisibility(menu.supportsFuzzySearch());

        this.redstoneMode.set(menu.getRedStoneMode());

        if (menu.getRedStoneMode() == RedstoneMode.LOW_SIGNAL) {
            this.setTextContent("ceiling_subtitle", GuiText.StockpileSwitchCeilingInv.text());
            this.setTextContent("floor_subtitle", GuiText.StockpileSwitchFloorInv.text());
        } else {
            this.setTextContent("ceiling_subtitle", GuiText.StockpileSwitchCeiling.text());
            this.setTextContent("floor_subtitle", GuiText.StockpileSwitchFloor.text());
        }

        for (final NumberEntryWidget widget : List.of(this.floor, this.ceiling)) {
            widget.hideButtons();
        }
    }

    private void saveFloor() {
        this.floor.getLongValue().ifPresent(menu::setFloor);
    }

    private void saveCeiling() {
        this.ceiling.getLongValue().ifPresent(menu::setCeiling);
    }
}
