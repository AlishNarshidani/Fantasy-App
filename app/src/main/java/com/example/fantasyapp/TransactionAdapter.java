package com.example.fantasyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;


    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionAdapter.TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction,parent,false);

        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.textTransactionId.setText("Transaction ID: "+transaction.getTransactionId());
        holder.textTransactionType.setText(transaction.getTransactionType());
        holder.textStatus.setText(transaction.getTransactionStatus());
        holder.textAmount.setText("₹"+String.valueOf(transaction.getAmount()));
        holder.textDate.setText(new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(transaction.getTransactionDate()));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        TextView textTransactionType;
        TextView textAmount;
        TextView textDate;
        TextView textStatus;
        TextView textTransactionId;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewTransaction);
            textTransactionType = itemView.findViewById(R.id.textTransactionType);
            textAmount = itemView.findViewById(R.id.textAmount);
            textDate = itemView.findViewById(R.id.textDate);
            textStatus = itemView.findViewById(R.id.textStatus);
            textTransactionId = itemView.findViewById(R.id.textTransactionId);
        }
    }
}
