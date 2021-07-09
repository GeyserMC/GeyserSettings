/*
 * Copyright (c) 2021 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.geysersettings.spigot;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.geysersettings.common.config.GeyserSettingsConfig;
import org.geysermc.geysersettings.common.Constants;
import org.geysermc.geysersettings.common.GeyserSettings;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class GeyserSettingsSpigot extends JavaPlugin implements Listener {
    private GeyserSettingsConfig config;
    private GeyserSettings geyserSettings;

    @Override
    public void onEnable() {
        this.geyserSettings = new GeyserSettings();
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File configFile = new File(getDataFolder(), "config.yml");
        try {
            if (!configFile.exists()) {
                geyserSettings.loadNewServerConfig(configFile);
            }

            this.config = geyserSettings.loadServerConfig(configFile);
        } catch (IOException e) {
            getLogger().severe("Unable to load config!");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, Constants.PLUGIN_MESSAGE_IDENTIFIER);
    }

    @EventHandler
    public void onPluginMessageRegister(PlayerRegisterChannelEvent event) {
        if (event.getChannel().equals(Constants.PLUGIN_MESSAGE_IDENTIFIER)) {
            if (config.getKickSettings().shouldKick()) {
                event.getPlayer().kickPlayer(config.getKickSettings().getKickMessage());
                return;
            }
            String message = this.geyserSettings.sendSettings(config.getGeyserSettings());
            event.getPlayer().sendPluginMessage(this, Constants.PLUGIN_MESSAGE_IDENTIFIER, message.getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
