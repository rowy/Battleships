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

import java.awt.Image;

import com.jostrobin.battleships.common.data.enums.ShipType;

/**
 * @author rowyss
 *         Date: 27.12.11 Time: 11:17
 */
public interface Theme
{
    String getThemeName();

    Image getBackground();

    Image getByShipType(ShipType type);
    
    Image getGreendDot();
    
    Image getRedDot();

}
