package c105.com.cmu2go;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.List;

/**
 * Created by abel on 2/10/17.
 */

public class MainActivity extends AppCompatActivity implements FragmentSwitchListener{

    static String place = "";
    Fragment current = null;
    static String uid = "";
    static String order = "";
    static String location = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.contentFragment, new MainFragment());
        fragmentTransaction.commit();

    }

    @Override
    public void switchFragment(android.support.v4.app.Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.contentFragment, fragment);
        transaction.addToBackStack(fragment.getClass().getName());
        transaction.commit();
        current = fragment;
    }

    @Override
    public void onBackPressed(){
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(this, Integer.toString(requestCode) + " " + Integer.toString(resultCode), Toast.LENGTH_SHORT).show();
           // Toast.makeText(this, Integer.toString(resultCode), Toast.LENGTH_LONG).show();
        if(requestCode == 1 && resultCode == -1) {
            Place place = PlacePicker.getPlace(data, this);
            String toastMsg = String.format("Place: %s", place.getName());
            Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            this.place = place.getName().toString();
            ((TextView) current.getView().findViewById(R.id.tvLoc)).setText(place.getName());
        }
    }
}
