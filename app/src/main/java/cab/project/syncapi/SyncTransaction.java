package cab.project.syncapi;


import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import cab.project.app.MyApplication;
import cab.project.configuration.Configuration;
import cab.project.helper.Security;
import cab.project.sqlite.SQLiteDatabaseHelper;

import static cab.project.configuration.Configuration.API_URL;
import static cab.project.sqlite.SQLiteDatabaseHelper.TABLE_TRANSACTIONS;
import static cab.project.sqlite.SQLiteDatabaseHelper.KEY_TRANSACTION_ID;


public class SyncTransaction
{

	private String URL = "";

	private Context context;
	private SQLiteDatabaseHelper helper;

	private static final int MAX_ATTEMPTS = 10;
	private int ATTEMPTS_COUNT;


	public SyncTransaction(Context context)
	{

		this.context = context;
		this.helper = new SQLiteDatabaseHelper(context);

		this.URL = API_URL + "sync-transaction.php";
	}
	

	public void execute()
	{

		StringRequest postRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

			@Override
			public void onResponse(String response)
			{

				try
				{

					Log.v("Response:", response);

					JSONArray jsonArray = new JSONArray(response);

					for(int i=0; i<jsonArray.length(); i++)
					{

						JSONObject jsonObj = (JSONObject) jsonArray.get(i);

						String transaction_id = jsonObj.getString("transaction_id");
						int sync_status = jsonObj.getInt("sync_status");

						new SQLiteDatabaseHelper(context).updateSyncStatus(TABLE_TRANSACTIONS, KEY_TRANSACTION_ID, transaction_id, sync_status);
					}
				}

				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error)
			{

				if(ATTEMPTS_COUNT != MAX_ATTEMPTS)
				{

					execute();

					ATTEMPTS_COUNT ++;

					Log.v("#Attempt No: ", "" + ATTEMPTS_COUNT);
				}
			}
		})

		{

			@Override
			protected Map<String, String> getParams()
			{

				Map<String ,String> params=new HashMap<>();

				try
				{
					String data = String.valueOf(helper.transactionsJSONData());
					params.put("responseJSON", Security.encrypt(data, Configuration.SECRET_KEY));
				}

				catch (Exception e)
				{
					Log.v("error ", "" + e.getMessage());
				}

				finally
				{
					Log.v("params ", "" + params);
				}

				return params;
			}
		};

		// Adding request to request queue
		MyApplication.getInstance().addToRequestQueue(postRequest);
	}
}