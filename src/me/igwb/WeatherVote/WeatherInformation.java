/* This file is part of WeatherVote

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.igwb.WeatherVote;

import org.bukkit.World;

public class WeatherInformation {

    private final World world;
    private final Boolean weatherState;
    private final Boolean thundering;
    private final Boolean raining;
    private final Integer rainDuration;
    private final Integer thunderDuration;

    public WeatherInformation(World theWorld, Boolean theWeatherstate, Boolean isRaining, Boolean isThundering, Integer theRainDuration, Integer theThunderDuration) {

        world = theWorld;
        weatherState = theWeatherstate;
        raining = isRaining;
        thundering = isThundering;
        rainDuration = theRainDuration;
        thunderDuration = theThunderDuration;
    }

    public World getWorld() {
        return world;
    }

    public Boolean getWeatherState() {
        return weatherState;
    }

    public Boolean getThundering() {
        return thundering;
    }

    public Boolean getRaining() {
        return raining;
    }

    public Integer getRainDuration() {
        return rainDuration;
    }

    public Integer getThunderDuration() {
        return thunderDuration;
    }
}
