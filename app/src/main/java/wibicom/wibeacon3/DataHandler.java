package wibicom.wibeacon3;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
import com.cloudant.sync.query.Query;
import com.cloudant.sync.query.QueryException;
import com.cloudant.sync.query.QueryResult;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

        pushingbulkTo = "test";



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

    public void deleteAllStoredDocuments() {
        Map<String, Object> map = new HashMap<String, Object>();

        Query q = ds.query();
        try {
            QueryResult qr = q.find(map);
            Log.d(TAG, " .deleteAllStoredDocuments() deleting " + qr.size() + "files." );
            for(DocumentRevision dr: qr) {
                ds.database().delete(dr);
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

    public int getDataCount() {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Object> out = new ArrayList<Object>();
        Query q = ds.query();
        try {
            QueryResult qr = q.find(map);
            return qr.size();
        } catch(QueryException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void pushOneFile(Object file) {
        new PushOneFileTask().execute(file);
        HashMap<String, Object> data = (HashMap<String, Object>)file;
        Log.d(TAG, ".publishOneFile() for device " + data.get("localName") + " and eventType " + data.get("eventType"));
    }


}
