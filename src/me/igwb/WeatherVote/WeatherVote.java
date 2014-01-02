/* WeatherVote
    Copyright (C) 2014 Bodo Beyer

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

import java.io.File;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class WeatherVote extends JavaPlugin implements Listener {

    private static Integer CONFIG_VERSION = 4;
    private VoteManager voteManager;
    private LocaleManager locale;
    private CommandListener commandExecutor;
    private WeatherInformation lastEventInformation;

    @Override
    public void onEnable() {

        this.getLogger().setLevel(Level.FINEST);

        getFileConfig();

        locale = new LocaleManager(new File(this.getDataFolder() + "/locale.properties"));
        voteManager = new VoteManager();
        commandExecutor = new CommandListener(this);

        registerEvents();
        registerCommands();
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void registerCommands() {
        this.getCommand("voteSun").setExecutor(commandExecutor);
        this.getCommand("voteRain").setExecutor(commandExecutor);
    }

    protected VoteManager getVoteManager() {

        return voteManager;
    }

    public FileConfiguration getFileConfig() {

        FileConfiguration config = this.getConfig();
        config.options().copyDefaults();
        this.saveDefaultConfig();

        //Check if the config version matches
        if (config.getInt("Version") != CONFIG_VERSION) {
            this.getLogger().severe("The config is not up to date!");
        }

        return config;
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {

        //Check if the world is enabled
        if (getFileConfig().getStringList("Worlds").contains(event.getWorld().getName())) {
            //Check if it's going to rain
            if (event.toWeatherState()) {
                Random r = new Random();


                if (r.nextInt(100) <= getFileConfig().getInt("FailChance")) {
                    if (!voteManager.getVoteInProgress()) {

                        lastEventInformation = new WeatherInformation(event.getWorld(), event.toWeatherState(), event.toWeatherState(), event.getWorld().isThundering(), event.getWorld().getWeatherDuration(), event.getWorld().getThunderDuration());
                        event.setCancelled(true);

                        //Start the vote and notify the players in the world the event takes place
                        voteManager.startVote();
                        messageAllPlayers(locale.getMessage("formation_starts"), event.getWorld());
                        messageAllPlayers(locale.getMessage("time_to_vote").replaceAll("%time_to_vote", String.valueOf(getFileConfig().getInt("TimeToVote"))), event.getWorld());

                        //Start the timer
                        startTimer(getFileConfig().getInt("TimeToVote"));
                    }
                } else {
                    messageAllPlayers(locale.getMessage("storm_too_strong"), event.getWorld());
                }
            }

        }
    }

    /***
     * Sends a message to all players in a specific world.
     * @param message The message to send.
     * @param world The world in which the players should get the message.
     */
    private void messageAllPlayers(String message, World world) {

        for (Player p : world.getPlayers()) {
            p.sendMessage(message);
        }
    }

    private void theTimeIsUp() {
        voteManager.endVote();
        if (voteManager.getRainVotes() >= voteManager.getSunVotes()) {
            lastEventInformation.getWorld().setStorm(true);
            lastEventInformation.getWorld().setWeatherDuration(lastEventInformation.getRainDuration());
            lastEventInformation.getWorld().setThundering(lastEventInformation.getThundering());
            lastEventInformation.getWorld().setThunderDuration(lastEventInformation.getThunderDuration());

            getLogger().log(Level.INFO, "test");
            getLogger().setLevel(Level.FINEST);
            getLogger().log(Level.FINEST, "Raining: " + lastEventInformation.getRaining());
            getLogger().log(Level.FINEST, "Rain duration: " + lastEventInformation.getRainDuration());
            getLogger().log(Level.FINEST, "Thundering: " + lastEventInformation.getThundering());
            getLogger().log(Level.FINEST, "Thunder duration: " + lastEventInformation.getThunderDuration());

            messageAllPlayers(locale.getMessage("vote_fail"), lastEventInformation.getWorld());
        } else {
            messageAllPlayers(locale.getMessage("vote_success"), lastEventInformation.getWorld());
        }
    }

    /***
     * Starts a timer and class the theTimeIsUp() method once the time is up.
     * @param duration Duration in seconds.
     */
    public void startTimer(Integer duration) {
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                theTimeIsUp();
            }
        }, duration * 20L);
    }


    public LocaleManager getLocale() {
        return locale;
    }

}
