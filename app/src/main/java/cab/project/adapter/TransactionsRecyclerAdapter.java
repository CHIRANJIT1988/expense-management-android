package cab.project.adapter;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cab.project.R;
import cab.project.helper.Helper;
import cab.project.model.Transaction;

import java.text.DecimalFormat;
import java.util.List;


public class TransactionsRecyclerAdapter extends RecyclerView.Adapter<TransactionsRecyclerAdapter.VersionViewHolder>
{

    private List<Transaction> list;

    private Context context;
    private OnItemClickListener clickListener;
    private DecimalFormat df = new DecimalFormat("0.00");


    public TransactionsRecyclerAdapter(Context context, List<Transaction> list)
    {
        this.context = context;
        this.list  = list;
    }


    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_transactions_new, viewGroup, false);
        return new VersionViewHolder(view);
    }


    @Override
    public void onBindViewHolder(VersionViewHolder versionViewHolder, int i)
    {

        ShapeDrawable background = new ShapeDrawable();
        background.setShape(new OvalShape()); // or RoundRectShape()

        Transaction transaction = list.get(i);


        versionViewHolder.transaction_purpose.setText(transaction.getTransactionPurpose().toUpperCase());

        if(transaction.getDebitAmount() == 0)
        {
            versionViewHolder.amount.setText(df.format(transaction.getCreditAmount()) + " Cr.");
        }

        else if(transaction.getCreditAmount() == 0)
        {
            versionViewHolder.amount.setText(df.format(transaction.getDebitAmount()) + " Dr.");
        }

        if(transaction.getMessage().equals(""))
        {
            versionViewHolder.message.setText("NIL");
        }

        else
        {
            versionViewHolder.message.setText(transaction.getMessage().toLowerCase());
        }


        try
        {
            versionViewHolder.timestamp.setText(Helper.dateTimeFormat(transaction.getTimestamp()));
        }

        catch (Exception e)
        {

        }
    }


    @Override
    public int getItemCount()
    {
        return list == null ? 0 : list.size();
    }


    class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView transaction_purpose;
        TextView amount;
        TextView message;
        TextView timestamp;


        public VersionViewHolder(View itemView)
        {

            super(itemView);

            transaction_purpose = (TextView) itemView.findViewById(R.id.transaction_purpose);
            amount = (TextView) itemView.findViewById(R.id.amount);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v)
        {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }


    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }


    public void SetOnItemClickListener(final OnItemClickListener itemClickListener)
    {
        this.clickListener = itemClickListener;
    }
}