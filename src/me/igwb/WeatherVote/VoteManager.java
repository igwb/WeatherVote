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

import java.util.ArrayList;
import java.util.Random;
import org.bukkit.World;


public class VoteManager {

    private WeatherVote parent;

    private boolean voteInProgress;
    private int sunVotes, rainVotes;
    private ArrayList<String> voters;
    private World voteWorld;
    private WeatherInformation eventInformation;

    public VoteManager(WeatherVote parentPlugin, World world, WeatherInformation information) {

        parent = parentPlugin;

        voters = new ArrayList<String>();
        voteWorld = world;
        eventInformation = information;


    }

    /***
     * Initiates a new vote.
     * @param duration How long the users can world.
     */
    protected void startVote(Integer duration) {

        parent.LogDebug("Starting new Vote in " + voteWorld.getName());

        voteInProgress = true;

        startTimer(parent.getFileConfig().getInt("TimeToVote"));

        parent.messageAllPlayers(parent.getLocale().getMessage("formation_starts"), voteWorld);
        parent.messageAllPlayers(parent.getLocale().getMessage("time_to_vote").replaceAll("%time_to_vote", String.valueOf(parent.getFileConfig().getInt("TimeToVote"))), voteWorld);
    }

    /***
     * Ends a vote.
     */
    protected void endVote() {
        parent.LogDebug("Ending vote in " + voteWorld.getName());

        Integer duration;
        Random r = new Random();

        duration = r.nextInt((parent.getFileConfig().getInt("RainTime.longest") - parent.getFileConfig().getInt("RainTime.shortest")) + 1) + parent.getFileConfig().getInt("RainTime.shortest");

        voteInProgress = false;

        if (getRainVotes() >= getSunVotes()) {
            voteWorld.setStorm(true);
            voteWorld.setWeatherDuration(duration * 20);
            voteWorld.setThundering(eventInformation.getThundering());
            voteWorld.setThunderDuration(Math.min(eventInformation.getThunderDuration(), duration * 20));

            parent.LogDebug("It will rain in " + voteWorld.getName() + " for " + duration + " seconds.");

            parent.messageAllPlayers(parent.getLocale().getMessage("vote_fail"), voteWorld);
        } else {
            parent.LogDebug("The sun will shine for " + voteWorld.getWeatherDuration());

            parent.messageAllPlayers(parent.getLocale().getMessage("vote_success"), voteWorld);
        }
    }

    /***
     * Check if there is currently a vote in progress.
     * @return The status.
     */
    public boolean getVoteInProgress() {
        return voteInProgress;
    }

    /***
     * Casts a vote for a player.
     * @param voter Name of the player who voted.
     * @param vote What type of vote did he cast?
     * @return Returns whatever happened.
     */
    public CastVoteResult castVote(String voter, VoteType vote) {

        //Return if there is nothing to vote for
        if (!voteInProgress) {
            parent.LogDebug(voter + " failed to vote in " + voteWorld.getName() + " because no vote was in progress!");
            return CastVoteResult.no_vote_in_progress;
        }

        //Check if the player has already voted
        if (!voters.contains(voter)) {
            switch (vote) {
            case sun:
                parent.LogDebug(voter + " voted for sun in " + voteWorld.getName());
                sunVotes++;
                break;
            case rain:
                parent.LogDebug(voter + " voted for rain in " + voteWorld.getName());
                rainVotes++;
                break;
            default:
                break;
            }

            voters.add(voter);
        } else {
            parent.LogDebug(voter + " attempted to vote twice in " + voteWorld.getName());
            return CastVoteResult.double_vote;
        }
        return CastVoteResult.success;
    }

    /***
     * Returns how many people voted for sun.
     * @return Sun vote count.
     */
    public int getSunVotes() {
        return sunVotes;
    }

    /***
     * Returns how many people voted for rain.
     * @return Rain vote count.
     */
    public int getRainVotes() {
        return rainVotes;
    }

    private void theTimeIsUp() {
        parent.theTimeIsUp(this);
    }


    /***
     * Starts a timer and class the theTimeIsUp() method once the time is up.
     * @param duration Duration in seconds.
     */
    public void startTimer(Integer duration) {
        parent.getServer().getScheduler().scheduleSyncDelayedTask(parent, new Runnable() {
            @Override
            public void run() {
                theTimeIsUp();
            }
        }, duration * 20L);
    }


    public World getVoteWorld() {
        return voteWorld;
    }
}
