package gr.indahouse.utils;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import gr.indahouse.R;

public class MyViewHolder extends RecyclerView.ViewHolder{

    private final String TAG = "MyViewHolder";

    CircleImageView profileImage;
    TextView name, discription;
    CardView cardView;
    ConstraintLayout singleViewProductConstraint;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        singleViewProductConstraint = itemView.findViewById(R.id.single_view_product_constraint);
        profileImage = itemView.findViewById(R.id.productImage);
        name = itemView.findViewById(R.id.productName);
        discription = itemView.findViewById(R.id.productDescription);
        cardView = itemView.findViewById(R.id.singleProductCardView);

    }
}
