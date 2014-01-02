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

import java.util.ArrayList;


public class VoteManager {

    private boolean voteInProgress;
    private int sunVotes, rainVotes;
    private ArrayList<String> voters;

    public VoteManager() {

    }

    /***
     * Initiates a new vote.
     */
    public void startVote() {
        voters = new ArrayList<String>();
        sunVotes = 0;
        rainVotes = 0;
        voteInProgress = true;
    }

    /***
     * Ends a vote.
     */
    public void endVote() {
        voteInProgress = false;
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
     * @param pro Did he vote pro?
     * @return Returns whatever happened.
     */
    public CastVoteResult castVote(String voter, VoteType vote) {

        //Return if there is nothing to vote for
        if (!voteInProgress) {
            return CastVoteResult.no_vote_in_progress;
        }

        //Check if the player has already voted
        if (!voters.contains(voter)) {
            switch (vote) {
            case sun:
                sunVotes++;
                break;
            case rain:
                rainVotes++;
                break;
            default:
                break;
            }
        } else {
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
}
