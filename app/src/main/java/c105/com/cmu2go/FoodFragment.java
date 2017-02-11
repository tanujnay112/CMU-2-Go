package c105.com.cmu2go;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by abel on 2/10/17.
 */

public class FoodFragment extends Fragment {
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
        View view = inflater.inflate(R.layout.fragment_food, container, false);
        TextView text = (TextView) view.findViewById(R.id.textView);
        text.setText(getArguments().getString("location"));

        final Spinner spinner1 = (Spinner) view.findViewById(R.id.spinner1);
        spinner1.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, new String[]{"Wheat spaghetti", "Campanelle", "Spinach fettucine", "Cheese fettucine", "Penne(Gluten-free)"}));

        Spinner spinner2 = (Spinner) view.findViewById(R.id.spinner2);
        spinner2.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, new String[]{"Spinach", "Mushrooms", "Peppers", "Onions", "Olives", "Broccolli", "Roasted Tomatoes", "Peas", "Egg plant", "Roasted Veggies"}));

        Spinner spinner3 = (Spinner) view.findViewById(R.id.spinner3);
        spinner2.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, new String[]{"Spinach", "Mushrooms", "Peppers", "Onions", "Olives", "Broccolli", "Roasted Tomatoes", "Peas", "Egg plant", "Roasted Veggies"}));

        Spinner spinner4 = (Spinner) view.findViewById(R.id.spinner4);
        spinner2.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, new String[]{"Spinach", "Mushrooms", "Peppers", "Onions", "Olives", "Broccolli", "Roasted Tomatoes", "Peas", "Egg plant", "Roasted Veggies"}));

        Spinner spinner5 = (Spinner) view.findViewById(R.id.spinner5);
        spinner3.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, new String[]{"Grilled chicke", "Meatballs", "Tofu", "Shrimp"}));

        Spinner spinner6 = (Spinner) view.findViewById(R.id.spinner6);
        spinner4.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, new String[]{"Marinara", "Alfredo", "Creamy tomato rosa", "Pasta cream"}));

        Button button = (Button) view.findViewById(R.id.button5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] choices = {spinner1.getSelectedItem().toString(), spinner1.getSelectedItem().toString(), spinner1.getSelectedItem().toString(),
                        spinner1.getSelectedItem().toString(), spinner1.getSelectedItem().toString(), spinner1.getSelectedItem().toString()};
                Bundle args = new Bundle();
                args.putString("location", getArguments().getString("location"));
                args.putStringArray("choices", choices);
                Fragment fragment = new ConfirmFragment();
                fragment.setArguments(args);
                listener.switchFragment(fragment);
            }
        });
        return view;
    }
}

