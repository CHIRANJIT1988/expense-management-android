package cab.project.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;

import cab.project.model.Transaction;

/**
 * Created by CHIRANJIT on 7/7/2016.
 */
public class SQLiteDatabaseHelper extends SQLiteOpenHelper
{

    private Context context;

    // Database version
    private static final int DATABASE_VERSION = 1;

    // Database name
    private static final String DATABASE_NAME = "CabDB";

    public static final String TABLE_TRANSACTIONS= "transactions";
    public static final String TABLE_TRANSACTION_PURPOSE= "transaction_purpose";

    private static final String KEY_ID = "id";
    public static final String KEY_TRANSACTION_ID = "transaction_id";
    private static final String KEY_TRANSACTION_PURPOSE = "transaction_purpose";
    private static final String KEY_CREDIT_AMOUNT = "credit_amount";
    private static final String KEY_DEBIT_AMOUNT = "debit_amount";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_SYNC_STATUS = "sync_status";


    private static final String CREATE_TABLE_TRANSACTIONS = "CREATE TABLE "
            + TABLE_TRANSACTIONS + "(" + KEY_TRANSACTION_ID + " TEXT PRIMARY KEY," + KEY_TRANSACTION_PURPOSE + " TEXT,"
            + KEY_CREDIT_AMOUNT + " REAL," + KEY_DEBIT_AMOUNT + " REAL," + KEY_MESSAGE + " TEXT,"  + KEY_TIMESTAMP + " TEXT," + KEY_SYNC_STATUS + " INTEGER INTEGER DEFAULT 0)";

    private static final String CREATE_TABLE_TRANSACTION_PURPOSE = "CREATE TABLE "
            + TABLE_TRANSACTION_PURPOSE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TRANSACTION_PURPOSE + " TEXT)";


    public SQLiteDatabaseHelper(Context context)
    {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(CREATE_TABLE_TRANSACTIONS);
        database.execSQL(CREATE_TABLE_TRANSACTION_PURPOSE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version)
    {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION_PURPOSE);
    }


    public boolean insertTransaction(Transaction transaction)
    {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_TRANSACTION_ID, transaction.getTransactionId());
        values.put(KEY_TRANSACTION_PURPOSE, transaction.getTransactionPurpose());
        values.put(KEY_CREDIT_AMOUNT, transaction.getCreditAmount());
        values.put(KEY_DEBIT_AMOUNT, transaction.getDebitAmount());
        values.put(KEY_MESSAGE, transaction.getMessage());
        values.put(KEY_TIMESTAMP, transaction.getTimestamp());

        // Inserting Row
        boolean createSuccessful = database.insert(TABLE_TRANSACTIONS, null, values) > 0;

        Log.v("createSuccessful", "" + createSuccessful);

        // Closing database connection
        database.close();

        return createSuccessful;
    }


    public boolean insertTransactionPurpose(String purpsoe)
    {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_TRANSACTION_PURPOSE, purpsoe);

        // Inserting Row
        boolean createSuccessful = database.insert(TABLE_TRANSACTION_PURPOSE, null, values) > 0;

        Log.v("createSuccessful", "" + createSuccessful);

        // Closing database connection
        database.close();

        return createSuccessful;
    }


    public ArrayList<Transaction> getAllTransactions(int x, int y)
    {

        ArrayList<Transaction> transactionList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_TRANSACTIONS + " ORDER BY " + KEY_TIMESTAMP + " DESC LIMIT "  + x + "," + y;

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                Transaction employeeObj = new Transaction();

                employeeObj.setTransactionId(cursor.getString(0));
                employeeObj.setTransactionPurpose(cursor.getString(1));
                employeeObj.setCreditAmount(cursor.getDouble(2));
                employeeObj.setDebitAmount(cursor.getDouble(3));
                employeeObj.setMessage(cursor.getString(4));
                employeeObj.setTimestamp(cursor.getString(5));

                transactionList.add(employeeObj);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return transactionList;
    }


    public ArrayList<String> getAllTransactionPurpose()
    {

        ArrayList<String> list = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_TRANSACTION_PURPOSE + " ORDER BY " + KEY_TRANSACTION_PURPOSE + " ASC";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {
                list.add(cursor.getString(1));
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        return list;
    }


    public String transactionsJSONData()
    {

        ArrayList<HashMap<String, String>> wordList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_TRANSACTIONS+ " WHERE " + KEY_SYNC_STATUS + " = '0'";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);


        if (cursor.moveToFirst())
        {

            do
            {

                HashMap<String, String> map = new HashMap<>();

                map.put(KEY_TRANSACTION_ID, cursor.getString(0));
                map.put(KEY_TRANSACTION_PURPOSE, cursor.getString(1));
                map.put(KEY_CREDIT_AMOUNT, cursor.getString(2));
                map.put(KEY_DEBIT_AMOUNT, cursor.getString(3));
                map.put(KEY_MESSAGE, cursor.getString(4));
                map.put(KEY_TIMESTAMP, cursor.getString(5));

                wordList.add(map);
            }

            while (cursor.moveToNext());
        }

        database.close();
        cursor.close();

        Gson gson = new GsonBuilder().create();

        // Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }


    public void updateTransaction(Transaction transaction)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_TRANSACTIONS + " SET " + KEY_TRANSACTION_PURPOSE + " = '" + transaction.getTransactionPurpose() + "', "
                + KEY_CREDIT_AMOUNT + " = '" + transaction.getCreditAmount() + "', " + KEY_DEBIT_AMOUNT + " = '" + transaction.getDebitAmount() + "', "
                + KEY_TIMESTAMP + " = '" + transaction.getTimestamp() + "', " + KEY_MESSAGE + " = '" + transaction.getMessage() + "', " + KEY_SYNC_STATUS+ " = '0' "
                + " WHERE " + KEY_TRANSACTION_ID + " = '" + transaction.getTransactionId() + "'";

        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }


    public void updateSyncStatus(String TABLE_NAME, String COLUMN_NAME, String id, int status)
    {

        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "UPDATE " + TABLE_NAME + " SET " + KEY_SYNC_STATUS + " = '" + status + "' WHERE " + COLUMN_NAME + " = '" + id + "'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }

    public int dbRowCount(String TABLE_NAME)
    {

        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();

        return count;
    }


    public int dbSyncCount(String TABLE_NAME)
    {

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_SYNC_STATUS + "='0'";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();

        return count;
    }
}