package c105.com.cmu2go;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeliverFragment2 extends Fragment {
    private FragmentSwitchListener listener;
    private String location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        location = getArguments().getString("location");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (FragmentSwitchListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_deliver, container, false);
        final ListView lv = (ListView) view.findViewById(R.id.lvLocs);
        final List<String> names = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child(getString(R.string.DIR_ORDERS)).child(location).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<Order> names = new ArrayList<Order>();
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    names.add(d.getValue(Order.class));
                }
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Fragment food = new MainFragment();
                        Bundle args = new Bundle();
                        FirebaseDatabase.getInstance().getReference("Accounts").child(names.get(position).account).child("status").setValue(MainActivity.uid);
                        FirebaseDatabase.getInstance().getReference().child("Accounts").child(MainActivity.uid).child("deliveries").setValue(names.get(position).account);
                        food.setArguments(args);
                        listener.switchFragment(food);
                        Toast.makeText(getActivity(), "Keep your location on! You are being tracked by your target.", Toast.LENGTH_SHORT).show();
                    }
                });
                lv.setAdapter(new OrderAdapter(getContext(), names));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }
}
