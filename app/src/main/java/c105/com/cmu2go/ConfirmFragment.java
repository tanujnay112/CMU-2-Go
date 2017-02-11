package c105.com.cmu2go;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.xmlpull.v1.XmlPullParser;

public class ConfirmFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int RESULT_OK = 1;
    private FragmentSwitchListener listener;
    private GoogleApiClient mGoogleApiClient;
    TextView tvLoc;
    private TextView tvConf;

    String location;
    String order;
    private Button bConf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        location = getArguments().getString("location");
        order = getArguments().getString("choices");
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
        View view =  inflater.inflate(R.layout.fragment_confirm, container, false);
        mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();

        tvLoc = (TextView) view.findViewById(R.id.tvLoc);
        tvLoc.setText(MainActivity.place);
        Button b = (Button) view.findViewById(R.id.bLoc);
        tvConf = (TextView) view.findViewById(R.id.tvConf);
        tvConf.setText(location + ": " + order);
        bConf = (Button) view.findViewById(R.id.bConf);
        bConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tvLoc.getText().length()==0)
                    return;
                sendOrder(location, order, tvLoc.getText().toString());
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = null;
                try {
                    XmlPullParser parser = getResources().getXml(R.xml.coords);
                  LatLngBounds bnds = new LatLngBounds.Builder().include(new LatLng(40.443885, -79.938895)).
                          include(new LatLng(40.441566, -79.947663)).build();
                    i = (new PlacePicker.IntentBuilder()).setLatLngBounds(bnds).build(getActivity());
                    getActivity().startActivityForResult(i,PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                PlacePicker.getPlace(getContext(),i);
            }
        });
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    private void sendOrder(String location, String order, String place) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(getString(R.string.DIR_ORDERS));
        String key = myRef.child(location).push().getKey();
        myRef.child(location).child(key).setValue(new Order(MainActivity.uid, location, order, place, "-1"));
        database.getReference().child("Accounts").child(MainActivity.uid).child(location).setValue(key);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("RIPPP");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(getContext(), requestCode + " " + resultCode, Toast.LENGTH_LONG).show();
        if (requestCode == PLACE_PICKER_REQUEST) {
            Toast.makeText(getContext(), resultCode, Toast.LENGTH_LONG).show();
                Place place = PlacePicker.getPlace(data, getActivity());
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(getContext(), toastMsg, Toast.LENGTH_LONG).show();
        }
    }
}
