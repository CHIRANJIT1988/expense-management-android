package cab.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import cab.project.adapter.TransactionsRecyclerAdapter;
import cab.project.model.Transaction;
import cab.project.services.AlarmService;
import cab.project.sqlite.SQLiteDatabaseHelper;
import static cab.project.model.Transaction.list;


public class MainActivity extends AppCompatActivity
{

    private TransactionsRecyclerAdapter adapter;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    private boolean loading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private int pageCount = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("My Transaction's");

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MainActivity.this, EntryActivity.class));
            }
        });

        populate_transaction_list();

        Intent service = new Intent(getApplicationContext(), AlarmService.class);
        startService(service);
    }


    @Override
    protected void onResume()
    {

        super.onResume();
        adapter.notifyDataSetChanged();

        if(new SQLiteDatabaseHelper(this).dbRowCount(SQLiteDatabaseHelper.TABLE_TRANSACTION_PURPOSE) == 0)
        {
            addTransactionPurposeDialog();
        }
    }


    private void populate_transaction_list()
    {

        loading = true;
        pageCount = 1;

        list = new SQLiteDatabaseHelper(this).getAllTransactions(0, 20);

        adapter = new TransactionsRecyclerAdapter(this, list);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_list);

        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        registerForContextMenu(recyclerView);
        recyclerView.setAdapter(adapter);


        adapter.SetOnItemClickListener(new TransactionsRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position)
            {

                Intent intent = new Intent(MainActivity.this, EntryActivity.class);
                intent.putExtra("EDIT", true);
                intent.putExtra("POSITION", position);
                startActivity(intent);
            }
        });

        loadMoreOnScroll();
    }


    private void loadMoreOnScroll()
    {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                loading = true;

                int totalRecord = new SQLiteDatabaseHelper(getApplicationContext()).dbRowCount(SQLiteDatabaseHelper.TABLE_TRANSACTIONS);
                int totalPage;

                if (totalRecord % 10 == 0) {
                    totalPage = (totalRecord / 20);
                } else {
                    totalPage = (totalRecord / 20) + 1;
                }

                if (pageCount < totalPage) {

                    if (dy > 0) //check for scroll down
                    {

                        visibleItemCount = mLayoutManager.getChildCount();
                        totalItemCount = mLayoutManager.getItemCount();
                        pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                        if (loading) {

                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {

                                loading = false;
                                Log.v("onScrolled: ", "Last Item Wow ! " + pageCount);

                                List<Transaction> tempList = new SQLiteDatabaseHelper(getApplicationContext()).getAllTransactions(pageCount * 20, 20);


                                for (Transaction transaction : tempList) {
                                    list.add(list.size(), transaction);
                                }

                                adapter.notifyDataSetChanged();

                                recyclerView.scrollToPosition(totalItemCount - 1);
                                pageCount++;
                            }
                        }
                    }
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            case android.R.id.home:
            {
                finish();
                break;
            }

            case R.id.action_add:

                addTransactionPurposeDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    private void addTransactionPurposeDialog()
    {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final EditText edittext= new EditText(this);
        alert.setMessage("Transaction Purpose");
        alert.setCancelable(false);

        edittext.setHeight(60);
        edittext.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        //edittext.SetRawInputType(Android.Text.InputTypes.NumberFlagDecimal | Android.Text.InputTypes.ClassNumber);
        //edittext.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});


        alert.setView(edittext);


        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {

                if (edittext.getText().toString().trim().length() != 0)
                {

                    new SQLiteDatabaseHelper(MainActivity.this).insertTransactionPurpose(edittext.getText().toString().toLowerCase());
                    Toast.makeText(getApplicationContext(), "Added Successfully", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        });


        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {

                dialog.cancel();
            }
        });

        alert.show();
    }
}