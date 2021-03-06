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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DeliverFragment extends Fragment {
    private FragmentSwitchListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        FirebaseDatabase.getInstance().getReference().child(getString(R.string.DIR_ORDERS)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> names = new ArrayList<String>();
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    names.add(d.getKey());
                }
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Fragment food = new DeliverFragment2();
                        Bundle args = new Bundle();
                        args.putString("location", names.get(position));
                        food.setArguments(args);
                        listener.switchFragment(food);
                    }
                });
                lv.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, names));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }
}
