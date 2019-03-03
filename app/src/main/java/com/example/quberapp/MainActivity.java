package com.example.quberapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    final JSONObject selObjListViews = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText goal = findViewById(R.id.goal_amount);





        final JSONArray jsonArray = new JSONArray();
        final TextView saveResults = findViewById(R.id.save_results);

        // Setup our listviews for this activity
        loadMainListViews();
        // Hide the mainUI until the user presses the Add jar buttonAddJar
        showHideMainActivityUI(View.INVISIBLE);

        Button buttonAddJar = findViewById(R.id.button1);
        buttonAddJar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHideMainActivityUI(View.VISIBLE);
            }
        });

        Button buttonSave = findViewById(R.id.button_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        try{
            JSONObject objJSON = new JSONObject();
            objJSON.put("jarItem", selObjListViews.get("jarSel"));

            String goalText = goal.getText().toString();
            if(goalText.length() == 0)
            {
                Toast.makeText(getApplicationContext(),"Missing Goal",Toast.LENGTH_LONG).show();

                return;
            }

            boolean isNum = isNumeric(goalText);
            Double goalAmount = isNum ? Double.parseDouble(goalText) : 0.00d;

            objJSON.put("goal",goalAmount);

            objJSON.put("rule", selObjListViews.get("ruleSel"));
            objJSON.put("action", selObjListViews.get("actionSel"));
            jsonArray.put(objJSON);
            goal.setText("");

           // Write out the results of what is saved so far
            saveResults.setText(jsonArray.toString());


        }
        catch (JSONException e){
            e.printStackTrace();
        }

            }
        });
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    // Setup the listviews
    private void loadMainListViews(){
        loadJarsList();
        loadRulesList();
        loadActionTypesList();

        // Testing getting some date for the Simple Rules api link
        //final String jsonArrayStrSimpleRulesList = requestSimpleRulesData();
        //saveResults.setText(jsonArrayStrSimpleRulesList);
    }

    // Show/Hide MainActivity components
   private void showHideMainActivityUI(int viewVisibility){

        final EditText goal = findViewById(R.id.goal_amount);
        final TextView jarLabel = findViewById(R.id.jar_title);
        final TextView ruleTypesLabel = findViewById(R.id.rule_types_title);
        final TextView actionsLabel = findViewById(R.id.actions_title);
        final ListView jarListView = findViewById(R.id.jar_list);
        final ListView ruleListView = findViewById(R.id.rule_list);
        final ListView actionListView = findViewById(R.id. action_list);
        final Button buttonSave = findViewById(R.id.button_save);
        jarListView.setVisibility(viewVisibility);
        ruleListView.setVisibility(viewVisibility);
        actionListView.setVisibility(viewVisibility);
        goal.setVisibility(viewVisibility);
        jarLabel.setVisibility(viewVisibility);
        ruleTypesLabel.setVisibility(viewVisibility);
        actionsLabel.setVisibility(viewVisibility);
        buttonSave.setVisibility(viewVisibility);
    }

    // Get sample data from api Simple Rules resource
    private String requestSimpleRulesData(){
        //final TextView saveResults = findViewById(R.id.save_results);

        //Some url endpoint that you may have
        String myUrl = getResources().getString(R.string.simpleRulesListUrl);
        //String to place our result in
        String result;
        //Instantiate new instance of our class
        HttpGetRequest getRequest = new HttpGetRequest();
        //Perform the doInBackground method, passing in our url
        try {
            result = getRequest.execute(myUrl).get();
            return result;
        }
        catch (InterruptedException e){
            //saveResults.setText(e.getMessage());
        }
        catch (ExecutionException e){
           // saveResults.setText(e.getMessage());
        }
        return "";
    }

    // Request the jarsList data via api
    private String requestJarsListData(){
        //final TextView saveResults = findViewById(R.id.save_results);
        //Some url endpoint that you may have
        String myUrl = getResources().getString(R.string.jarsListUrl);
        //String to place our result in
        String result;
        //Instantiate new instance of our class
        HttpGetRequest getRequest = new HttpGetRequest();
        //Perform the doInBackground method, passing in our url
        try {
            result = getRequest.execute(myUrl).get();
            return result;
        }
        catch (InterruptedException e){
           // saveResults.setText(e.getMessage());
        }
        catch (ExecutionException e){
            //saveResults.setText(e.getMessage());
        }
        return "";
    }

    // http async request
    public static class HttpGetRequest extends AsyncTask<String, Void, String> {
        private static final String REQUEST_METHOD = "GET";
        private static final int READ_TIMEOUT = 15000;
        private static final int CONNECTION_TIMEOUT = 15000;
        @Override
        protected String doInBackground(String... params){
            String stringUrl = params[0];
            String result;
            String inputLine;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(stringUrl);
                //Create a connection
                HttpURLConnection connection =(HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
            }
            catch(IOException e){
                e.printStackTrace();
                result = null;
            }
            return result;
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);
        }
    }

    private void loadActionTypesList(){
        final ListView actionListView = findViewById(R.id. action_list);
        // Actions list
        String jsonArrayStrActionList = "[\n" +
                "      {\n" +
                "          \"ruleActionTypeId\": 1,\n" +
                "          \"ruleActionTypeName\": \"Top up\"\n" +
                "      },\n" +
                "      {\n" +
                "          \"ruleActionTypeId\": 1,\n" +
                "          \"ruleActionTypeName\": \"Percentage\"\n" +
                "      },\n" +
                "      {\n" +
                "          \"ruleActionTypeId\": 1,\n" +
                "          \"ruleActionTypeName\": \"Specific Amount\"\n" +
                "      }\n" +
                " ]";

        List<String> actionsList = new ArrayList<>();
        try
        {
            JSONArray jsonarray = new JSONArray(jsonArrayStrActionList);
            for (int i = 0; i < jsonarray.length(); i++) {

                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String ruleActionTypeName = jsonobject.getString("ruleActionTypeName");
                actionsList.add(ruleActionTypeName);
            }
        }catch(JSONException e){}

        ArrayAdapter adapter = new ArrayAdapter<>(this,R.layout.activity_actionlistview,actionsList) ;
        actionListView.setAdapter(adapter);
        actionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    selObjListViews.put("actionSel",position);
                }catch (JSONException e){

                }
            }
        });
    }

    private void loadRulesList(){

        final ListView ruleListView = findViewById(R.id.rule_list);
        // Rules list sample data
        final String jsonArrayStrRuleList = "[\n" +
                "      {\n" +
                "          \"ruleTypeId\": 1,\n" +
                "          \"ruleTypeName\": \"Merchant\"\n" +
                "      },\n" +
                "      {\n" +
                "          \"ruleTypeId\": 4,\n" +
                "          \"ruleTypeName\": \"Calendar\"\n" +
                "      },\n" +
                " ]";

        List<String> rulesList = new ArrayList<>();
        try
        {
            JSONArray jsonarray = new JSONArray(jsonArrayStrRuleList);
            for (int i = 0; i < jsonarray.length(); i++) {

                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String ruleTypeId = jsonobject.getString("ruleTypeId");
                String ruleTypeName = jsonobject.getString("ruleTypeName");
                rulesList.add(ruleTypeName);
            }
        }catch(JSONException e){}
        ArrayAdapter adapter = new ArrayAdapter<>(this,R.layout.activity_rulelistview,rulesList) ;
        ruleListView.setAdapter(adapter);
        ruleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    selObjListViews.put("ruleSel",position);
                }catch (JSONException e){

                }
            }
        });
    }

    private void loadJarsList(){
        //final TextView saveResults = findViewById(R.id.save_results);
        final ListView jarListView = findViewById(R.id.jar_list);

        // Jars jsonarray - sample data
//        final String jsonArrayStrJarList = "[{\n" +
//                "      \"jarsList\": [\n" +
//                "          \"Summer Vacations\",\n" +
//                "          \"Rainy Day Savingss\",\n" +
//                "          \"New iPhones\"\n" +
//                "      ]\n" +
//                " }\n]";

        // Jar list
        List<String> jarList = new ArrayList<>();
        // Request the data from the api
        final String jsonArrayStrJarList = requestJarsListData();
        //saveResults.setText(jsonArrayStrJarList);
        JSONObject jsonObject;
        try{
            jsonObject = new JSONObject(jsonArrayStrJarList);
            JSONArray jsonArrayJarList = (JSONArray)jsonObject.get("jarsList");
            for (int i = 0; i < jsonArrayJarList.length(); i++) {
                jarList.add(jsonArrayJarList.getString(i));
            }
        }
        catch (JSONException e){}
        ArrayAdapter adapter = new ArrayAdapter<>(this,R.layout.activity_listview,jarList);
        jarListView.setAdapter(adapter);
        jarListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    selObjListViews.put("jarSel",position);
                }
                catch (JSONException e){}
            }
        });
    }



}