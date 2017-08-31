package wibicom.wibeacon3;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.cloudant.sync.documentstore.AttachmentException;
import com.cloudant.sync.documentstore.ConflictException;
import com.cloudant.sync.documentstore.DocumentBodyFactory;
import com.cloudant.sync.documentstore.DocumentNotFoundException;
import com.cloudant.sync.documentstore.DocumentRevision;
import com.cloudant.sync.documentstore.DocumentStore;
import com.cloudant.sync.documentstore.DocumentStoreException;
import com.cloudant.sync.documentstore.DocumentStoreNotDeletedException;
import com.cloudant.sync.query.Query;
import com.cloudant.sync.query.QueryException;
import com.cloudant.sync.query.QueryResult;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import wibicom.wibeacon3.Historical.FragmentHistoricalDashboard;
import wibicom.wibeacon3.Historical.HistoricalDashboardActivity;

/**
 * Created by Michael Vaquier on 2017-07-17.
 */

public class DataHandler {

    private static DataHandler instance;
    private static String pushingbulkTo = null;
    private boolean createNewDatabase = false;
    private List<Object> dataToBePushed;

    CloudantClient client;

    File path;
    public DocumentStore ds;

    private final static String TAG = DataHandler.class.getName();

    public DataHandler() {



        path = MainActivity.getInstance().getApplicationContext().getDir("documentstores", Context.MODE_PRIVATE);

        try {
            ds = DocumentStore.getInstance(new File(path, "my_document_store"));

        } catch (DocumentStoreException dse) {
            dse.printStackTrace();
        }
        client = ClientBuilder.account("6f99adac-7671-4e45-9a80-0ba7638a5eb5-bluemix:dcb7a77744a9d8691e8cc098fe7ba645bb9311fe0311528c86ec21cc5ff8a066@6f99adac-7671-4e45-9a80-0ba7638a5eb5-bluemix")
                .username("6f99adac-7671-4e45-9a80-0ba7638a5eb5-bluemix")
                .password("dcb7a77744a9d8691e8cc098fe7ba645bb9311fe0311528c86ec21cc5ff8a066")
                .build();


        //new QueryWithIndexTask().execute("iotp_4rxa4d_default_2017-07-17");


    }

    public static DataHandler getInstance() {
        if (instance == null) {
            instance = new DataHandler();
        }
        return instance;
    }

    public void storeObject(Map<String, Object> obj) {
        HashMap<String, Object> data = (HashMap<String,Object>)obj.get("data");
        Log.d(TAG, ".storingObject() fore device " + data.get("localName") + ", and event type " + obj.get("eventType"));
        DocumentRevision revision = new DocumentRevision();
        revision.setBody(DocumentBodyFactory.create(obj));
        try {
            DataHandler.getInstance().ds.database().create(revision);
        } catch (AttachmentException e) {
            e.printStackTrace();
        } catch (ConflictException e) {
            e.printStackTrace();
        } catch (DocumentStoreException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllStoredDocuments(FragmentLocalStorage fragmentLocalStorage) {
        //new DeleteAllLocalFilesTask().execute(fragmentLocalStorage);
        try {

            ds.delete();
            ds = DocumentStore.getInstance(new File(path, "my_document_store"));
        } catch (DocumentStoreNotDeletedException e) {
            e.printStackTrace();
        } catch (DocumentStoreException dse) {
            dse.printStackTrace();
        }
        MainActivity.getInstance().displaySnackbar("All items have been deleted from the local storage");
    }

    private class DeleteAllLocalFilesTask extends AsyncTask<FragmentLocalStorage, FragmentLocalStorage, Void> {
        @Override
        protected Void doInBackground(FragmentLocalStorage... param) {
            Log.d(TAG, "executing all file deletion...");
            Map<String, Object> map = new HashMap<String, Object>();

            Query q = ds.query();
            try {
                QueryResult qr = q.find(map);
                Log.d(TAG, " .deleteAllStoredDocuments() deleting " + qr.size() + "files." );
                for(DocumentRevision dr: qr) {
                    ds.database().delete(dr);
                    publishProgress(param[0]);
                }
                MainActivity.getInstance().displaySnackbar(qr.size() + " items have been deleted from the local storage...");
            } catch (QueryException e) {
                e.printStackTrace();
            } catch (ConflictException e) {
                e.printStackTrace();
            } catch (DocumentNotFoundException e) {
                e.printStackTrace();
            } catch (DocumentStoreException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(FragmentLocalStorage... param) {
            param[0].setMessage("Your current storage is "+ (Integer.parseInt(param[0].getMessageDataCount())-1) +" files.");
        }
    }

    private class CreateDatabaseTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            MainActivity.getInstance().displaySnackbar("Creating database " + pushingbulkTo + "...");
            client.createDB(params[0]);
            return params[0];
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "database " + result + " has been created successfully.");
            new PushBulkTask().execute(dataToBePushed);
        }
    }

    private class getdatabaseListTask extends  AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            List<String> dbList = client.getAllDbs();
            for(String name : dbList) {
                if(name.equals(pushingbulkTo)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean dbExists) {
            createNewDatabase = dbExists;
            if(dbExists) {
                new PushBulkTask().execute(dataToBePushed);
            }
            else {
                new CreateDatabaseTask().execute(pushingbulkTo);
            }
        }
    }

    private class PushBulkTask extends AsyncTask<List<Object>, Void, List<Response>> {
        @Override
        protected List<Response> doInBackground(List<Object>... params) {
            MainActivity.getInstance().displaySnackbar("Pushing data to " + pushingbulkTo+ "...");

            Database db = client.database(pushingbulkTo, false);
            List<Response> out = db.bulk(params[0]);
            return out;
        }
        @Override
        protected void onPostExecute(List<Response> out) {
            Log.d(TAG, out.size() + " items have successfully been copied to database.");
            MainActivity.getInstance().displaySnackbar(out.size() + " items have successfully been pushed to database " + pushingbulkTo + "!");
        }

    }

    private class PushOneFileTask extends AsyncTask<Object, Void, Response> {
        @Override
        protected Response doInBackground(Object... params) {
            HashMap<String, Object> file = (HashMap<String,Object>)params[0];
            String dbName = "iotp_4rxa4d_default_" + file.get("timestamp").toString().split("T")[0];
            Log.d(TAG, "publishing to database " + dbName);
            Database db = client.database(dbName, false);
            return db.save(params[0]);

        }
        @Override
        protected void onPostExecute(Response response) {
            Log.d(TAG, "published");
        }
    }

    private class QueryWeatherWithIndexTask extends AsyncTask<String, Integer, HashMap<String,String>> {
        @Override
        protected HashMap<String,String> doInBackground(String... params) {
            Log.d(TAG, "Querry for "+params[1]+" "+params[2]+" "+params[0]);
            Database targetDatabase;
            if(params[0].equals("date") && doesDatabaseExist("iotp_4rxa4d_default_"+params[2])) {
                targetDatabase = client.database("iotp_4rxa4d_default_"+params[2], false);
            }
            else if(params[0].equals("bulk") && doesDatabaseExist(params[2])) {
                targetDatabase = client.database(params[2], false);
            }
            else {
                Log.d(TAG, "QueryWithIndexTask did not find the database "+params[2]);
                HashMap<String, String> results = new HashMap<String, String>();
                results.put("temperature", "");
                results.put("humidity", "");
                results.put("pressure", "");
                results.put("UV", "");
                results.put("CO2", "");
                results.put("sound", "");
                return results;
            }
            List<LinkedTreeMap> out = (List<LinkedTreeMap>) targetDatabase.findByIndex("{\"selector\": {\"deviceId\" : \""+params[1]+"\", \"$or\" : [{\"eventType\" : \"air\"}, {\"eventType\" : \"CO2\"}, {\"eventType\" : \"sound\"}] },\"fields\": [\"timestamp\",\"data.d\",\"eventType\"],\"sort\": []}", LinkedTreeMap.class);
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Log.d(TAG, "QueryWithIndexTask weather retrieved " + out.size() + " data points.");
            HashMap<String, String> results = new HashMap<String, String>();
            results.put("temperature", "");
            results.put("humidity", "");
            results.put("pressure", "");
            results.put("UV", "");
            results.put("CO2", "");
            results.put("sound", "");
            int size = out.size();
            int inc = size/25;
            boolean done = false;
            if (inc == 0) {
                done = true;
                publishProgress(25);
            }
            for(int i = 0; i < size; i++) {
                LinkedTreeMap<String,Object> thisMap = out.get(i);
                String eventType = (String)thisMap.get("eventType");
                if(!done && (i%inc) == 0 ) {
                    publishProgress(1);
                }
                LinkedTreeMap<String ,LinkedTreeMap<String,Double>> tempMapOut;
                tempMapOut = (LinkedTreeMap<String,LinkedTreeMap<String,Double>>) thisMap.get("data");
                LinkedTreeMap<String,Double> tempMap = tempMapOut.get("d");
                switch (eventType) {
                    case "air":
                        addDataPointToCSVString(thisMap, tempMap, results, "temperature", "temperature");
                        addDataPointToCSVString(thisMap, tempMap, results, "humidity", "humidity");
                        addDataPointToCSVString(thisMap, tempMap, results, "pressure", "pressure");
                        if(tempMap.get("UV") != null) {
                            addDataPointToCSVString(thisMap, tempMap, results, "UV", "UV");
                        }
                        break;
                    case "CO2":
                        addDataPointToCSVString(thisMap, tempMap, results, "CO2", "CO2");
                        break;
                    case "sound":
                        addDataPointToCSVString(thisMap, tempMap, results, "sound", "soundLevel");
                    default:
                        break;
                }
            }
            return results;
        }

        @Override
        protected void onPostExecute(HashMap<String,String> out) {
            HistoricalDashboardActivity historicalDashboardActivity = HistoricalDashboardActivity.getInstance();
            historicalDashboardActivity.dataReady("weather/CO2", out);
        }

        @Override
        protected void onProgressUpdate(Integer... i) {
            HistoricalDashboardActivity.getInstance().getFragmentHistoricalDashboard().getProgressBar().incrementProgressBy(i[0]);
        }
    }

    private class QueryLightBatteryWithIndexTask extends AsyncTask<String, Integer, HashMap<String,String>> {
        @Override
        protected HashMap<String,String> doInBackground(String... params) {
            Log.d(TAG, "Querry for "+params[1]+" "+params[2]+" "+params[0]);
            Database targetDatabase;
            if(params[0].equals("date") && doesDatabaseExist("iotp_4rxa4d_default_"+params[2])) {
                targetDatabase = client.database("iotp_4rxa4d_default_"+params[2], false);
            }
            else if(params[0].equals("bulk") && doesDatabaseExist(params[2])) {
                targetDatabase = client.database(params[2], false);
            }
            else {
                Log.d(TAG, "QueryWithIndexTask did not find the database "+params[2]);
                HashMap<String, String> results = new HashMap<String, String>();
                results.put("battery", "");
                results.put("light", "");
                results.put("SO2", "");
                results.put("CO", "");
                results.put("O3", "");
                results.put("NO2", "");
                results.put("PM", "");
                return results;
            }
            List<LinkedTreeMap> out = (List<LinkedTreeMap>) targetDatabase.findByIndex("{\"selector\": {\"deviceId\" : \""+params[1]+"\", \"$or\" : [{\"eventType\" : \"battery\"}, {\"eventType\" : \"health\"}, {\"eventType\" : \"gases\"}] },\"fields\": [\"timestamp\",\"data.d\",\"eventType\"],\"sort\": []}", LinkedTreeMap.class);
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Log.d(TAG, "QueryWithIndexTask battery/light retrieved " + out.size() + " data points.");
            HashMap<String, String> results = new HashMap<String, String>();
            results.put("battery", "");
            results.put("light", "");
            results.put("SO2", "");
            results.put("CO", "");
            results.put("O3", "");
            results.put("NO2", "");
            results.put("PM", "");
            int size = out.size();
            int inc = size/25;
            boolean done = false;
            if (inc == 0) {
                done = true;
                publishProgress(25);
            }
            for(int i = 0; i < size; i++) {
                LinkedTreeMap<String,Object> thisMap = out.get(i);
                String eventType = (String)thisMap.get("eventType");
                if(!done && (i%inc) == 0 ) {
                    publishProgress(1);
                }
                LinkedTreeMap<String ,LinkedTreeMap<String,Double>> tempMapOut;
                tempMapOut = (LinkedTreeMap<String,LinkedTreeMap<String,Double>>) thisMap.get("data");
                LinkedTreeMap<String,Double> tempMap = tempMapOut.get("d");
                switch (eventType) {
                    case "battery":
                        addDataPointToCSVString(thisMap, tempMap, results, "battery", "batteryLevel");
                        break;
                    case "health":
                        addDataPointToCSVString(thisMap, tempMap, results, "light", "light");
                        break;
                    case "gases":
                        addDataPointToCSVString(thisMap, tempMap, results, "SO2", "SO2");
                        addDataPointToCSVString(thisMap, tempMap, results, "CO", "CO");
                        addDataPointToCSVString(thisMap, tempMap, results, "O3", "O3");
                        addDataPointToCSVString(thisMap, tempMap, results, "NO2", "NO2");
                        addDataPointToCSVString(thisMap, tempMap, results, "PM", "PM");
                        break;
                    default:
                        break;
                }
            }
            return results;
        }

        @Override
        protected void onPostExecute(HashMap<String,String> out) {
            HistoricalDashboardActivity historicalDashboardActivity = HistoricalDashboardActivity.getInstance();
            historicalDashboardActivity.dataReady("battery/light/gases", out);
        }

        @Override
        protected void onProgressUpdate(Integer... i) {
            HistoricalDashboardActivity.getInstance().getFragmentHistoricalDashboard().getProgressBar().incrementProgressBy(i[0]);
        }
    }

    private class QueryAccelWithIndexTask extends AsyncTask<String, Integer, HashMap<String,String>> {
        @Override
        protected HashMap<String,String> doInBackground(String... params) {
            Log.d(TAG, "Querry for "+params[1]+" "+params[2]+" "+params[0]);
            Database targetDatabase;
            if(params[0].equals("date") && doesDatabaseExist("iotp_4rxa4d_default_"+params[2])) {
                targetDatabase = client.database("iotp_4rxa4d_default_"+params[2], false);
            }
            else if(params[0].equals("bulk") && doesDatabaseExist(params[2])) {
                targetDatabase = client.database(params[2], false);
            }
            else {
                Log.d(TAG, "QueryWithIndexTask did not find the database "+params[2]);
                HashMap<String, String> results = new HashMap<String, String>();
                results.put("accel", "");
                return results;
            }
            List<LinkedTreeMap> out = (List<LinkedTreeMap>) targetDatabase.findByIndex("{\"selector\": {\"deviceId\" : \""+params[1]+"\", \"eventType\" : \"accel\" },\"fields\": [\"timestamp\",\"data.d\",\"eventType\"],\"sort\": []}", LinkedTreeMap.class);
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Log.d(TAG, "QueryWithIndexTask accel retrieved " + out.size() + " data points.");
            HashMap<String, String> results = new HashMap<String, String>();
            results.put("accel", "");
            int size = out.size();
            int inc = size/25;
            boolean done = false;
            if (inc == 0) {
                done = true;
                publishProgress(25);
            }
            for(int i = 0; i < size; i++) {
                LinkedTreeMap<String,Object> thisMap = out.get(i);
                if(!done && (i%inc) == 0 ) {
                    publishProgress(1);
                }
                LinkedTreeMap<String ,LinkedTreeMap<String,Double>> tempMapOut;
                tempMapOut = (LinkedTreeMap<String,LinkedTreeMap<String,Double>>) thisMap.get("data");
                LinkedTreeMap<String,Double> tempMap = tempMapOut.get("d");
                String[] specialLabels = {"x", "y", "z"};
                addDataPointToCSVString(thisMap, tempMap, results, "accel", specialLabels);
            }
            return results;
        }

        @Override
        protected void onPostExecute(HashMap<String,String> out) {
            HistoricalDashboardActivity historicalDashboardActivity = HistoricalDashboardActivity.getInstance();
            historicalDashboardActivity.dataReady("accel", out);
        }

        @Override
        protected void onProgressUpdate(Integer... i) {
            HistoricalDashboardActivity.getInstance().getFragmentHistoricalDashboard().getProgressBar().incrementProgressBy(i[0]);
        }
    }

    private class QueryRSSIWithIndexTask extends AsyncTask<String, Integer, HashMap<String,String>> {
        @Override
        protected HashMap<String,String> doInBackground(String... params) {
            Log.d(TAG, "Querry for "+params[1]+" "+params[2]+" "+params[0]);
            Database targetDatabase;
            if(params[0].equals("date") && doesDatabaseExist("iotp_4rxa4d_default_"+params[2])) {
                targetDatabase = client.database("iotp_4rxa4d_default_"+params[2], false);
            }
            else if(params[0].equals("bulk") && doesDatabaseExist(params[2])) {
                targetDatabase = client.database(params[2], false);
            }
            else {
                Log.d(TAG, "QueryWithIndexTask did not find the database "+params[2]);
                HashMap<String, String> results = new HashMap<String, String>();
                results.put("rssi", "");
                return results;
            }
            List<LinkedTreeMap> out = (List<LinkedTreeMap>) targetDatabase.findByIndex("{\"selector\": {\"deviceId\" : \""+params[1]+"\", \"eventType\" : \"location\" },\"fields\": [\"timestamp\",\"data.d\",\"eventType\"],\"sort\": []}", LinkedTreeMap.class);
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Log.d(TAG, "QueryWithIndexTask rssi retrieved " + out.size() + " data points.");
            HashMap<String, String> results = new HashMap<String, String>();
            results.put("rssi", "");
            int size = out.size();
            int inc = size/25;
            boolean done = false;
            if (inc == 0) {
                done = true;
                publishProgress(25);
            }
            int skip;
            if(size<500) {
                skip = 1;
            }
            else if(size>=500 && size<3000) {
                skip = size/50;
            }
            else if(size>=3000 && size<10000) {
                skip = size/70;
            }
            else {
                skip = size/90;
            }
            int incremnt = 0;
            for(int i = 0; i < size; i+=skip) {
                LinkedTreeMap<String,Object> thisMap = out.get(i);
                if(!done && (i/inc) >= incremnt ) {
                    publishProgress(1 + incremnt- (i/inc));
                    incremnt++;
                }
                LinkedTreeMap<String ,LinkedTreeMap<String,Double>> tempMapOut;
                tempMapOut = (LinkedTreeMap<String,LinkedTreeMap<String,Double>>) thisMap.get("data");
                LinkedTreeMap<String,Double> tempMap = tempMapOut.get("d");
                addDataPointToCSVString(thisMap, tempMap, results, "rssi", "rssi");
            }
            return results;
        }

        @Override
        protected void onPostExecute(HashMap<String,String> out) {
            HistoricalDashboardActivity historicalDashboardActivity = HistoricalDashboardActivity.getInstance();
            historicalDashboardActivity.dataReady("RSSI", out);
        }

        @Override
        protected void onProgressUpdate(Integer... i) {
            HistoricalDashboardActivity.getInstance().getFragmentHistoricalDashboard().getProgressBar().incrementProgressBy(i[0]);
        }
    }

    private void addDataPointToCSVString(LinkedTreeMap<String, Object> tempMap, LinkedTreeMap<String, Double> tempDataMap, HashMap<String, String> resultMap, String resultKeyWord, String dataKeyWord) {
        String csvString;
        csvString = resultMap.get(resultKeyWord);
        csvString += tempMap.get("timestamp") + "," + tempDataMap.get(dataKeyWord) + "\\n";

        resultMap.put(resultKeyWord, csvString);
    }

    private void addDataPointToCSVString(LinkedTreeMap<String, Object> tempMap, LinkedTreeMap<String, Double> tempDataMap, HashMap<String, String> resultMap, String resultKeyWord, String[] dataKeyWord) {
        String csvString;
        csvString = resultMap.get(resultKeyWord);
        csvString += tempMap.get("timestamp") + ",";
        for(int i = 0;i < dataKeyWord.length ; i++) {
            csvString += tempDataMap.get(dataKeyWord[i]);
            if(!((i + 1) < dataKeyWord.length)) {
                csvString += "\\n";
            }
            else {
                csvString += ",";
            }
        }

        resultMap.put(resultKeyWord, csvString);
    }



    public void pushAllFilesInLocalStorage(String databaseName) {
        pushingbulkTo = databaseName;
        Map<String, Object> map = new HashMap<String, Object>();
        List<Object> out = new ArrayList<Object>();
        Query q = ds.query();
        try {
            QueryResult qr = q.find(map);
            Log.d(TAG, " .pushAllFilesInLocalStorage() preparing to push " + qr.size() + "files." );
            for(DocumentRevision dr: qr) {
                out.add(dr.getBody().asMap());
            }
            dataToBePushed = out;
            new getdatabaseListTask().execute();
        } catch (QueryException e) {
            e.printStackTrace();
        }
    }

    public void setDataCountMessage(TextView message) {
        new SetDataCountMessageTask().execute(message);
    }

    private class SetDataCountMessageTask extends AsyncTask<TextView, Void, Pair<TextView, Integer>> {
        @Override
        protected Pair<TextView,Integer> doInBackground(TextView... params) {
            Log.d(TAG, "executing data count in background...");
            Map<String, Object> map = new HashMap<String, Object>();
            List<Object> out = new ArrayList<Object>();
            Query q = ds.query();
            try {
                QueryResult qr = q.find(map);
                return new Pair<>(params[0], qr.size());
            } catch(QueryException e) {
                e.printStackTrace();
            }
            return new Pair<>(params[0], -1);
        }
        @Override
        protected void onPostExecute(Pair<TextView,Integer> result) {
            result.first.setText("Your current storage is " +  result.second + " files.");
        }
    }


    public void pushOneFile(Object file) {
        new PushOneFileTask().execute(file);
        HashMap<String, Object> data = (HashMap<String, Object>)file;
        Log.d(TAG, ".publishOneFile() for device " + data.get("localName") + " and eventType " + data.get("eventType"));
    }

    public void requestData(String[] request) {
        new QueryWeatherWithIndexTask().execute(request);
        new QueryLightBatteryWithIndexTask().execute(request);
        new QueryAccelWithIndexTask().execute(request);
        new QueryRSSIWithIndexTask().execute(request);
    }

    private boolean doesDatabaseExist(String name) {
        List<String> allDbs = client.getAllDbs();
        for(String thisName : allDbs) {
            if(name.equals(thisName)) {
                return true;
            }
        }
        return false;
    }



}
