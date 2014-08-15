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

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandListener implements CommandExecutor {

    private WeatherVote parentPlugin;

    public CommandListener(WeatherVote parent) {

        parentPlugin = parent;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        VoteManager vm = null;
        VoteType voteType = null;

        //Check if we are dealing with a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(parentPlugin.getLocale().getMessage("playeronly"));
            return true;
        }

        //Determine the votetype
        switch (command.getName().toLowerCase()) {
        case "votesun":
            if (sender.hasPermission(parentPlugin.getCommand("votesun").getPermission())) {
                voteType = VoteType.sun;
            } else {
                sender.sendMessage(parentPlugin.getLocale().getMessage("vote_no_permission"));
                return true;
            }
            break;

        case "voterain":
            if (sender.hasPermission(parentPlugin.getCommand("voterain").getPermission())) {
                voteType = VoteType.rain;
            } else {
                sender.sendMessage(parentPlugin.getLocale().getMessage("vote_no_permission"));
                return true;
            }
            break;
        default:
            return true;
        }

        //Get the VoteManager if any.
        vm = parentPlugin.getVoteManager(((Player) sender).getWorld().getName());

        //Cast the vote if possible.
        if (vm != null) {
            switch (vm.castVote(sender.getName(), voteType)) {
            case success:
                if (command.getName().toLowerCase().equals("votesun")) {
                    sender.sendMessage(parentPlugin.getLocale().getMessage("vote_registered_no"));
                } else {
                    sender.sendMessage(parentPlugin.getLocale().getMessage("vote_registered_yes"));
                }
                break;
            case double_vote:
                sender.sendMessage(parentPlugin.getLocale().getMessage("vote_multiple"));
                break;
            case no_vote_in_progress:
                sender.sendMessage(parentPlugin.getLocale().getMessage("no_vote_in_progress"));
                break;
            default:
                break;
            }
        } else {
            sender.sendMessage(parentPlugin.getLocale().getMessage("no_vote_in_progress"));
        }

        return true;
    }

}
