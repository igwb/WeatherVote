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
