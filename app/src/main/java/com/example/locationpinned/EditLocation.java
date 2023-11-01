package com.example.locationpinned;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.locationpinned.databinding.FragmentEditLocationBinding;

public class EditLocation extends Fragment {

    FragmentEditLocationBinding binding;
    Bundle bundle;
    AddressObject address;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEditLocationBinding.inflate(inflater, container, false);

        //Receives AddressObject passed from FirstFragment (main page)
        bundle = this.getArguments();
        if (bundle != null) {
            address = (AddressObject) bundle.getSerializable("addressPayload");
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText latitudeInput = binding.latForm;
        EditText longitudeInput = binding.longitudeForm;
        TextView resultTextView = binding.resultText;

        //Setting EditText and TextView values to be current address coordinates & location
        latitudeInput.setText(String.valueOf(address.getLatitude()));
        longitudeInput.setText(String.valueOf(address.getLongitude()));
        resultTextView.setText(address.getLocationName());

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

                //Modifying AddressObject to be saved in database
                address.setLocationName(location);
                address.setLatitude(latVal);
                address.setLongitude(longVal);
                db.editAddress(address);

                //Return to main fragment
                NavHostFragment.findNavController(EditLocation.this)
                        .navigate(R.id.action_EditFragment_to_FirstFragment);

            }

        });

        //Cancel button, returns to main fragment
        binding.buttonCancel.setOnClickListener(v -> {
            NavHostFragment.findNavController(EditLocation.this)
                    .navigate(R.id.action_EditFragment_to_FirstFragment);

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }

}