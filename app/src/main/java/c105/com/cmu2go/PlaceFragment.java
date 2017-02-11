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

import com.fasterxml.jackson.databind.util.JSONPObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abel on 2/10/17.
 */

public class PlaceFragment extends Fragment {
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

    public String loadJSON() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("meal.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place, container, false);
        ListView listView = (ListView) view.findViewById(R.id.view);
        final List<String> names = new ArrayList<>();
        try {
            JSONObject meal = new JSONObject(loadJSON());
            JSONArray arr = meal.getJSONArray("locations");
            for(int i = 0; i < arr.length(); i++) {
                names.add(arr.getJSONObject(i).getString("name"));
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment food = new FoodFragment();
                Bundle args = new Bundle();
                args.putString("location", names.get(position));
                food.setArguments(args);
                listener.switchFragment(food);
            }
        });
        listView.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, names));
        return view;
    }
}
