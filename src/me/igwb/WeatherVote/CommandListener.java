/*  WeatherVote
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

        VoteManager vm;

        switch (command.getName().toLowerCase()) {
        case "votesun":
            if (sender instanceof Player) {
                if (sender.hasPermission(parentPlugin.getCommand("votesun").getPermission())) {
                    vm = parentPlugin.getVoteManager(((Player) sender).getWorld().getName());

                    if (vm != null) {
                        switch (vm.castVote(sender.getName(), VoteType.sun)) {
                        case success:
                            sender.sendMessage(parentPlugin.getLocale().getMessage("vote_registered_no"));
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
                } else {
                    sender.sendMessage(parentPlugin.getLocale().getMessage("vote_no_permission"));
                }
            } else {
                sender.sendMessage("This command can only be run by a player!");
            }
            return true;
        case "voterain":
            if (sender instanceof Player) {
                if (sender.hasPermission(parentPlugin.getCommand("voterain").getPermission())) {
                    vm = parentPlugin.getVoteManager(((Player) sender).getWorld().getName());
                    if (vm != null) {
                        switch (vm.castVote(sender.getName(), VoteType.rain)) {
                        case success:
                            sender.sendMessage(parentPlugin.getLocale().getMessage("vote_registered_yes"));
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
                } else {
                    sender.sendMessage(parentPlugin.getLocale().getMessage("vote_no_permission"));
                }
            } else {
                sender.sendMessage("This command can only be run by a player!");
            }
            return true;
        default:
            break;
        }
        return false;
    }

}
