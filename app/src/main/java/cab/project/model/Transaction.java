package cab.project.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CHIRANJIT on 7/7/2016.
 */
public class Transaction
{

    public static List<Transaction> list = new ArrayList<>();

    private String transaction_id, transaction_purpose, message, timestamp;
    private double credit_amount, debit_amount;


    public void setTransactionId(String transaction_id)
    {
        this.transaction_id = transaction_id;
    }

    public String getTransactionId()
    {
        return this.transaction_id;
    }


    public void setTransactionPurpose(String transaction_purpose)
    {
        this.transaction_purpose = transaction_purpose;
    }

    public String getTransactionPurpose()
    {
        return this.transaction_purpose;
    }


    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return this.message;
    }


    public void setCreditAmount(double credit_amount)
    {
        this.credit_amount = credit_amount;
    }

    public double getCreditAmount()
    {
        return this.credit_amount;
    }


    public void setDebitAmount(double debit_amount)
    {
        this.debit_amount = debit_amount;
    }

    public double getDebitAmount()
    {
        return this.debit_amount;
    }


    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getTimestamp()
    {
        return this.timestamp;
    }
}