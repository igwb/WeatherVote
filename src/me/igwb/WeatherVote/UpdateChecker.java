package me.igwb.WeatherVote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class UpdateChecker implements Runnable {

    WeatherVote parent;

    public UpdateChecker (WeatherVote parent) {
        this.parent = parent;
    }

    @Override
    public void run() {
        try {
            URL url = null;

            url = new URL("https://api.curseforge.com/servermods/files?projectIds=52547");


            URLConnection conn = url.openConnection();
            conn.addRequestProperty("User-Agent", "WeatherVote by igwb");
            conn.setDoOutput(true);

            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final String response = reader.readLine();

            final JSONArray array = (JSONArray) JSONValue.parse(response);

            JSONObject info = (JSONObject) array.get(array.size() - 1);

            String latestVersion = (String) (info.get("name"));

            parent.needsUpdating = !latestVersion.contains(parent.PLUGINVERSION);
            parent.updateCheckSuccessful = true;

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
