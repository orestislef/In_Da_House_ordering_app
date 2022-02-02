package gr.indahouse.utils;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import gr.indahouse.R;

public class ExtrasViewHolder extends RecyclerView.ViewHolder {

    private final String TAG = "ExtrasViewHolder";

    public TextView extraName, extraPrice;
    public CardView extraCardView;
    public ConstraintLayout singleViewExtraConstraint;
    public ImageButton deleteExtraBtn;

    public ExtrasViewHolder(@NonNull View itemView) {
        super(itemView);

        singleViewExtraConstraint = itemView.findViewById(R.id.single_view_extra_constraint);
        extraName = itemView.findViewById(R.id.extraName);
        extraPrice = itemView.findViewById(R.id.extraPrice);
        extraCardView = itemView.findViewById(R.id.singleExtraCardView);
        deleteExtraBtn = itemView.findViewById(R.id.deleteExtraBtn);
    }
}
