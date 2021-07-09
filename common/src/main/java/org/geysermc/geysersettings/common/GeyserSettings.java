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

package org.geysermc.geysersettings.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.geysermc.geysersettings.common.config.GeyserSettingsConfig;
import org.geysermc.geysersettings.common.config.KickSettings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class GeyserSettings {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    private static final String FAILED_SETTINGS = "{\"success\":false}";

    public String sendSettings(List<GeyserSetting> settings) {
        try {
            ObjectNode node = JSON_MAPPER.createObjectNode();
            ObjectNode settingsNode = JSON_MAPPER.createObjectNode();
            for (GeyserSetting setting : settings) {
                setting.serialize(settingsNode);
            }
            node.set("settings", settingsNode);
            node.put("success", true);

            return JSON_MAPPER.writeValueAsString(node);
        } catch (Exception e) {
            e.printStackTrace();
            return FAILED_SETTINGS;
        }
    }

    public void loadNewServerConfig(File configFile) throws IOException {
        configFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(configFile);

        InputStream input = GeyserSettings.class.getResourceAsStream("/config.yml"); // resources need leading "/" prefix

        byte[] bytes = new byte[input.available()];

        input.read(bytes);

        for(char c : new String(bytes).toCharArray()) {
            fos.write(c);
        }

        fos.flush();
        input.close();
        fos.close();
    }

    public GeyserSettingsConfig loadServerConfig(File configFile) throws IOException {
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        JsonNode node = yamlMapper.readTree(configFile);
        KickSettings kickSettings = new KickSettings(node);
        List<GeyserSetting> geyserSettings = new ArrayList<>();
        JsonNode settingsNode = node.get("settings");
        if (settingsNode != null) {
            for (Iterator<Map.Entry<String, JsonNode>> it = settingsNode.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> setting = it.next();
                GeyserSettingType geyserSetting = GeyserSettingType.ALL_SETTINGS.get(setting.getKey());
                if (geyserSetting != null) {
                    geyserSettings.add(new GeyserSetting(geyserSetting, setting.getValue().booleanValue()));
                }
            }
        }

        return new GeyserSettingsConfig(kickSettings, geyserSettings);
    }
}
