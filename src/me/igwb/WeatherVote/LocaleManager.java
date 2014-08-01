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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class LocaleManager {

    private ResourceBundle messages;


    public LocaleManager(File locale) {

        FileOutputStream fos;
        Writer out = null;

        try {

            if (!locale.exists()) {

                fos = new FileOutputStream(locale.getAbsolutePath());
                out = new OutputStreamWriter(fos, "ISO-8859-1");

                out.write("formation_starts= §a[WeatherVote] §rA storm started to form! Type /voteSun or /voteRain to vote!" + "\n");
                out.write("storm_too_strong= §a[WeatherVote] §rA storm started to form but it's too strong to be voted away!" + "\n");
                out.write("time_to_vote= §a[WeatherVote] §rYou have %time_to_vote seconds to cast your vote!" + "\n");
                out.write("vote_success= §a[WeatherVote] §rThe storm formation was stopped!" + "\n");
                out.write("vote_fail= §a[WeatherVote] §rThe storm formation could not be stopped!" + "\n");
                out.write("vote_registered_yes= §a[WeatherVote] §rYour vote for rain was successful!" + "\n");
                out.write("vote_registered_no= §a[WeatherVote] §rYour vote for sun was successful!" + "\n");
                out.write("vote_multiple= §a[WeatherVote] §4You can only vote once!" + "\n");
                out.write("no_vote_in_progress= §a[WeatherVote] §4No vote in progress!" + "\n");
                out.write("vote_no_permission= §a[WeatherVote] §4Insufficient permissions!" + "\n");
                out.write("playeronly= This command can only be run by a player!" + "\n");

                out.close();
            }
            messages = new PropertyResourceBundle(new FileInputStream(locale));

        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public String getMessage(String key) {

        return messages.getString(key).toString();
    }
}
