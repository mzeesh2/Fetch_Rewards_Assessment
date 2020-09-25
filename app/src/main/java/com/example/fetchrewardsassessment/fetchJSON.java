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
    //Method to parse JSON website
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL("https://fetch-hiring.s3.amazonaws.com/hiring.json");
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            InputStream input = httpConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
            String line = "";

            //If there is still data to be parsed
            while(line != null){
                line = bufferedReader.readLine();
                data = data + line;
            }

            JSONArray dataArray = new JSONArray(data);

            //This List will be used to sort
            List<JSONObject> sort = new ArrayList<JSONObject>();

            //Populate the list with JSONObjects
            for(int i = 0; i < dataArray.length(); i++) {
                sort.add(dataArray.getJSONObject(i));
            }

            //Sort list
            Collections.sort(sort, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject object1, JSONObject object2) {
                    try {
                        //Variables for listId and id so that we can sort by both
                        int listID1 = Integer.parseInt(object1.getString("listId"));
                        int listID2 = Integer.parseInt(object2.getString("listId"));
                        int id1 = Integer.parseInt(object1.getString("id"));
                        int id2 = Integer.parseInt(object2.getString("id"));

                        //If listID is different, then sort by listID
                        if(listID1 > listID2){
                            return 1;
                        }

                        //If same listID is encountered, then sort by id which gives Item name
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

            //Parse and store data
            for(int i = 0; i < sort.size(); i++){
                JSONObject object = sort.get(i);

                //Filter out empty or null names
                if(object.getString("name").isEmpty() || object.getString("name") == "null") {
                    continue;
                }
                else{
                    groupParsed = "Group: " + object.get("listId") + "\n";
                    itemParsed = "Name: " + object.get("name") + "\n";

                    //If listID is the same as previous, then only send name of item for a more concise view
                    if(groupParsed.equals(previousGroupParsed)) {
                        parsedData = parsedData + itemParsed;
                    }

                    //If listID is different, then print out group number so user knows which group we are looking at
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
    //Send data back so screen can be populated
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        MainActivity.data.setText(this.parsedData);
    }
}
