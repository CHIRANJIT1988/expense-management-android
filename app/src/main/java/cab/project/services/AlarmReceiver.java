package cab.project.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cab.project.network.InternetConnectionDetector;
import cab.project.sqlite.SQLiteDatabaseHelper;
import cab.project.syncapi.SyncTransaction;

import static cab.project.sqlite.SQLiteDatabaseHelper.TABLE_TRANSACTIONS;


public class AlarmReceiver extends BroadcastReceiver
{
	
	Context context;


	@Override
	public void onReceive(Context context, Intent intent) 
	{
		
		this.context = context;

		int alarm = intent.getExtras().getInt("alarm");

		if(alarm == 1)
		{

			if(new InternetConnectionDetector(context).isConnected())
			{

				//makeToast("Sync Alarm Received");
				syncData();
			}
		}
	}


	/*private void makeToast(String msg)
	{
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}*/


	private void syncData()
	{

		SQLiteDatabaseHelper helper = new SQLiteDatabaseHelper(context);

		if(helper.dbSyncCount(TABLE_TRANSACTIONS) != 0)
		{
			new SyncTransaction(context).execute();
		}
	}
}