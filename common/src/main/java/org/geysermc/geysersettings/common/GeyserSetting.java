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

import com.fasterxml.jackson.databind.node.ObjectNode;

public class GeyserSetting {
    private final GeyserSettingType setting;
    private final Object value;

    public GeyserSetting(GeyserSettingType setting, Object value) {
        this.setting = setting;
        if (value.getClass() != setting.getSerializationType()) {
            throw new RuntimeException("Value for " + setting.name() + " must be of type " + setting.getSerializationType());
        }
        this.value = value;
    }

    public GeyserSettingType getSetting() {
        return setting;
    }

    void serialize(ObjectNode node) {
        if (value instanceof String) {
            node.put(setting.getSerializationName(), (String) value);
        } else if (value instanceof Boolean) {
            node.put(setting.getSerializationName(), (boolean) value);
        }
    }
}
