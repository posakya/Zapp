package com.example.aasis.zappfood;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;


public class Firstfragment extends Fragment {
    Animation Fade_in,Fade_out;
     ViewFlipper viewFlipper;
    Spinner spinner1;
    String[] quantity = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View  myView = inflater.inflate(R.layout.first_layout,container,false);
        viewFlipper=(ViewFlipper) myView.findViewById(R.id.ViewFlipper);
        viewFlipper.setAnimation(Fade_in);
        viewFlipper.setAnimation(Fade_out);
        viewFlipper.setAutoStart(true);
        viewFlipper.startFlipping();
        viewFlipper.setFlipInterval(2000);
        getActivity().setTitle("Home");
        spinner1=(Spinner) myView.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, quantity);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), (parent.getSelectedItemPosition()+1) + " selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        return myView;


    }


}
