/*
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

package com.jostrobin.battleships.view.theme;

import java.awt.*;

import com.jostrobin.battleships.common.data.enums.ShipType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * @author rowyss
 *         Date: 13.01.12 Time: 23:57
 */
public class FolderBasedTheme extends BaseTheme implements DescriptionBasedTheme
{

    private ThemeDescription themeDescription;
    private static final String BASE_DIR = "tiles";

    public ThemeDescription getThemeDescription()
    {
        return themeDescription;
    }

    @CacheEvict(value = "images", allEntries = true)
    public void setThemeDescription(ThemeDescription themeDescription)
    {
        this.themeDescription = themeDescription;
    }

    @CacheEvict(value = "images", allEntries = true)
    public void setThemeDescription(String themeDescriptionName)
    {
        this.themeDescription = ThemeDescription.valueOf(themeDescriptionName);
    }

    @Override
    public String getThemeName()
    {
        return themeDescription.getName();
    }

    @Cacheable(value = "images", key = "'background'")
    @Override
    public Image getBackground()
    {
        StringBuffer buffer = new StringBuffer(BASE_DIR);
        buffer.append("/")//
                .append(themeDescription.getFolder())//
                .append("/")//
                .append("background")//
                .append(".bmp");
        String imgUrl = buffer.toString();
        return loadImage(imgUrl);
    }

    @Cacheable(value = "images")
    @Override
    public Image getByShipType(ShipType type)
    {
        String imageUrl = generateImagePath(type);
        return loadImage(imageUrl);
    }

    private String generateImagePath(ShipType shipType)
    {
        StringBuffer buffer = new StringBuffer(BASE_DIR);
        buffer.append("/")//
                .append(themeDescription.getFolder())//
                .append("/")//
                .append(shipType.name().toLowerCase())//
                .append(".bmp");
        return buffer.toString();
    }

    @Cacheable(value = "images", key = "'greenDor'")
    @Override
    public Image getGreendDot()
    {
        StringBuffer buffer = new StringBuffer(BASE_DIR);
        buffer.append("/")//
                .append(themeDescription.getFolder())//
                .append("/")//
                .append("green_dot")//
                .append(".bmp");
        return loadImage(buffer.toString());
    }

    @Cacheable(value = "images", key = "'redDot'")
    @Override
    public Image getRedDot()
    {
        StringBuffer buffer = new StringBuffer(BASE_DIR);
        buffer.append("/")//
                .append(themeDescription.getFolder())//
                .append("/")//
                .append("red_dot")//
                .append(".bmp");
        return loadImage(buffer.toString());
    }
}
