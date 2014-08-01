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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class WeatherVote extends JavaPlugin implements Listener {

    private static Integer CONFIG_VERSION = 5;
    private LocaleManager locale;
    private CommandListener commandExecutor;
    private Logger log;
    private ArrayList<VoteManager> voteManagers;

    @Override
    public void onEnable() {

        getFileConfig();

        LogDebug("Debug mode enabled");

        locale = new LocaleManager(new File(this.getDataFolder() + "/locale.properties"));
        commandExecutor = new CommandListener(this);

        voteManagers = new ArrayList<VoteManager>();

        registerEvents();
        registerCommands();

        try {
            Metrics metrics = new Metrics(this);
            if (metrics.start()) {
                getLogger().log(Level.INFO, "Now submitting data to http://mcstats.org for " + this.getDescription().getName() + " Thank you!");
            } else {
                getLogger().log(Level.INFO, "No data is beeing submitted to http://mcstats.org");
            }
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not submit stats to mcstats.org");
        }
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void registerCommands() {
        this.getCommand("voteSun").setExecutor(commandExecutor);
        this.getCommand("voteRain").setExecutor(commandExecutor);
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

    /***
     * Returns the vote managers used for a specific world. Or null if no vote is in progress there.
     * @param worldName The world to get the VoteManager from.
     * @return The VoteManager or null
     */
    protected VoteManager getVoteManager(String worldName) {


        for (VoteManager vote : voteManagers) {
            if (vote.getVoteWorld().getName().equals(worldName)) {

                LogDebug("Vote manager found for " + worldName);

                return vote;
            }
        }

        LogDebug("Vote manager not found for " + worldName);

        return null;
    }

    protected void theTimeIsUp(VoteManager vm) {

        vm.endVote();
        voteManagers.remove(vm);
    }

    /***
     * Sends a message to all players in a specific world.
     * @param message The message to send.
     * @param world The world in which the players should get the message.
     */
    protected void messageAllPlayers(String message, World world) {

        for (Player p : world.getPlayers()) {
            p.sendMessage(message);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {

        Random r = new Random();
        Boolean voteInProgress = false;

        //Check if the world is enabled
        if (getFileConfig().getStringList("Worlds").contains(event.getWorld().getName())) {
            //Check if it's going to rain
            if (event.toWeatherState()) {


                if (getFileConfig().getInt("RainTime.longest") == 0) {
                    event.setCancelled(true);

                    LogDebug("Rain event cancelled.");

                    return;
                }


                //Check if there is a vote in progress in that world. Abort if so.
                for (VoteManager vote : voteManagers) {
                    if (vote.getVoteWorld().getName().equals(event.getWorld().getName())) {
                        voteInProgress = true;
                        continue;
                    }
                }

                LogDebug("Is vote in progress: " + voteInProgress + " " + event.getWorld().getName());

                if (voteInProgress) {
                    return;
                }

                if (r.nextInt(100) >= getFileConfig().getInt("FailChance")) {


                    VoteManager vm = new VoteManager(this, event.getWorld(), new WeatherInformation(event.getWorld(), event.toWeatherState(), event.toWeatherState(), event.getWorld().isThundering(), event.getWorld().getWeatherDuration(), event.getWorld().getThunderDuration()));

                    vm.startVote(getFileConfig().getInt("TimeToVote"));
                    voteManagers.add(vm);
                    event.setCancelled(true);
                } else {
                    messageAllPlayers(locale.getMessage("storm_too_strong"), event.getWorld());
                }
            } else {
                if (getFileConfig().getInt("SunTime.longest") == 0) {
                    event.setCancelled(true);

                    LogDebug("Sun event cancelled.");
                    return;
                }
                Integer duration;

                duration = r.nextInt((getFileConfig().getInt("SunTime.longest") - getFileConfig().getInt("SunTime.shortest")) + 1) + getFileConfig().getInt("SunTime.shortest");

                event.getWorld().setWeatherDuration(duration * 20);

                LogDebug("The sun will shine in " + event.getWorld().getName() + " for " + duration + " seconds.");
            }

        }
    }


    public void LogDebug(String msg) {

        if (getFileConfig().getBoolean("debug")) {
            getLogger().log(Level.INFO, msg);
        }
    }

    public LocaleManager getLocale() {
        return locale;
    }

}
