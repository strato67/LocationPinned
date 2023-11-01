package com.example.locationpinned;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.locationpinned.databinding.FragmentSecondBinding;

import java.util.concurrent.atomic.AtomicReference;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText latitudeInput = binding.latForm;
        EditText longitudeInput = binding.longitudeForm;
        TextView resultTextView = binding.resultText;

        //Creating new AddressObject for writing to database
        AddressObject address = new AddressObject();


        //Cancel button, returns to main fragment
        binding.buttonCancel.setOnClickListener(view1 -> NavHostFragment.findNavController(SecondFragment.this)
                .navigate(R.id.action_SecondFragment_to_FirstFragment));

        //Button for generating geocoded location for provided latitude and longitude
        binding.generateCode.setOnClickListener(v -> {
            String latRaw = latitudeInput.getText().toString();
            String longRaw = longitudeInput.getText().toString();

            if (!latRaw.isEmpty() && !longRaw.isEmpty()) {
                GeocoderMaker geocoderMaker = new GeocoderMaker(
                        Double.parseDouble(latitudeInput.getText().toString()),
                        Double.parseDouble(longitudeInput.getText().toString()), getContext());

                resultTextView.setText(geocoderMaker.geocodedAddress());
            }

        });

        //Button for saving location to database
        binding.buttonSave.setOnClickListener(v -> {
            String latRaw = latitudeInput.getText().toString();
            String longRaw = longitudeInput.getText().toString();
            String location = resultTextView.getText().toString();
            DBHelper db = new DBHelper(this.getContext());

            //Check that text fields are entered
            if (latRaw.isEmpty() || longRaw.isEmpty() || location.isEmpty()) {
                Toast.makeText(getContext(), "Missing latitude or longitude.", Toast.LENGTH_SHORT).show();
            } else if (location.equals("Invalid location")) {
                Toast.makeText(getContext(), "Invalid location.", Toast.LENGTH_SHORT).show();

            } else if (location.equals("Geocoded location:")) {
                Toast.makeText(getContext(), "Geocoded location required.", Toast.LENGTH_SHORT).show();
            } else {
                double latVal = Double.parseDouble(latRaw);
                double longVal = Double.parseDouble(longRaw);

                //Adding properties of AddressObject to be saved in database
                address.setLocationName(location);
                address.setLatitude(latVal);
                address.setLongitude(longVal);
                db.createAddress(address);

                //Return to main fragment
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);

            }

        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}