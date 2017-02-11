package c105.com.cmu2go;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.util.Data;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Tanuj on 2/11/17.
 */


public class OrderAdapter extends ArrayAdapter<Order> {
    private ArrayList<Order> o;
    public OrderAdapter(Context context, ArrayList<Order> o) {
        super(context, 0, o);
        this.o = o;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Order ord = o.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.order_row, parent, false);
        }
        TextView tv1 = (TextView) convertView.findViewById(R.id.textView1);
        tv1.setText(ord.food);
        TextView tv2 = (TextView) convertView.findViewById(R.id.textView2);
        tv2.setText(ord.location);
        final TextView tv3 = (TextView) convertView.findViewById(R.id.textView3);
        FirebaseDatabase.getInstance().getReference("/Accounts").child(ord.account)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DataSnapshot f = dataSnapshot.child("andrewId");
                        tv3.setText(f.getValue().toString());
                        Toast.makeText(getContext(), f.getValue().toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        return convertView;
    }
}
