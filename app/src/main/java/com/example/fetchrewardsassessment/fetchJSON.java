package com.example.fetchrewardsassessment;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class fetchJSON extends AsyncTask<Void, Void, Void> {
    String data = "";
    String parsedData = "";
    String previousGroupParsed = "";
    String groupParsed = "";
    String itemParsed = "";

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL("https://fetch-hiring.s3.amazonaws.com/hiring.json");
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            InputStream input = httpConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
            String line = "";

            while(line != null){
                line = bufferedReader.readLine();
                data = data + line;
            }

            JSONArray dataArray = new JSONArray(data);
            List<JSONObject> sort = new ArrayList<JSONObject>();

            for(int i = 0; i < dataArray.length(); i++) {
                sort.add(dataArray.getJSONObject(i));
            }

            Collections.sort(sort, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject jsonObject, JSONObject t1) {
                    try {
                        int listID1 = Integer.parseInt(jsonObject.getString("listId"));
                        int listID2 = Integer.parseInt(t1.getString("listId"));
                        int id1 = Integer.parseInt(jsonObject.getString("id"));
                        int id2 = Integer.parseInt(t1.getString("id"));
                        if(listID1 > listID2){
                            return 1;
                        }
                        else if(listID1 == listID2){
                            if(id1 > id2){
                                return 1;
                            }

                            else if(id1 == id2){
                                return 0;
                            }

                            else{
                                return -1;
                            }
                        }
                        else{
                            return -1;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });

            for(int i = 0; i < sort.size(); i++){
                JSONObject object = sort.get(i);
                if(object.getString("name").isEmpty() || object.getString("name") == "null") {
                    continue;
                }
                else{
                    groupParsed = "Group: " + object.get("listId") + "\n";
                    itemParsed = "Name: " + object.get("name") + "\n";

                    if(groupParsed.equals(previousGroupParsed)) {
                        parsedData = parsedData + itemParsed;
                    }
                    else {
                        parsedData = parsedData + groupParsed + itemParsed;
                        previousGroupParsed = groupParsed;
                    }
                }
            }
        }catch(MalformedURLException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        MainActivity.data.setText(this.parsedData);
    }
}
