package com.example.locationpinned;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.locationpinned.databinding.FragmentFirstBinding;
import com.google.android.material.search.SearchView;

import java.io.IOException;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    DBHelper db;
    private List<AddressObject> addressList;
    private List<AddressObject> searchedAddresses;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        //Retrieving initial list of address from database
        db = new DBHelper(getContext());
        addressList = db.getAllAddresses();

        binding = FragmentFirstBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Floating action button which allows for adding addresses, switches to the SecondFragment
        binding.fab.setOnClickListener(view1 -> NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment));

        LinearLayout addressListComponent = binding.addressListContainer;
        LinearLayout searchList = binding.searchList;
        Button reloadDataBtn = binding.reloadData;
        SearchView searchView = binding.searchView;
        searchView.setupWithSearchBar(binding.searchBarFirst);

        //Reset Data button, retrieves original data from coordinates text file and overwrites database
        reloadDataBtn.setOnClickListener(v -> {
            try {
                db.reloadDatabase(binding.getRoot().getContext());
                addressList = db.getAllAddresses();
                displayAddresses(addressList, addressListComponent);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

        //Displays all addresses on main page
        displayAddresses(addressList, addressListComponent);

        //Displays addresses in SearchView for queried addresses
        searchView.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            searchList.removeAllViews();
            String searchedText = searchView.getText().toString();

            searchedAddresses = db.getAddressByName(searchedText);

            //Populates searchView with address tiles
            displayAddresses(searchedAddresses, searchList);

            return false;
        });

    }

    //Function to display address tiles for a LinearLayout (main page and SearchView)
    public void displayAddresses(List<AddressObject> displayList, LinearLayout layout) {
        //Remove existing views
        layout.removeAllViews();

        //Loop through provided list of AddressObjects and build an address tile component
        for (AddressObject address : displayList) {
            View addressTile = getLayoutInflater().inflate(R.layout.address_tile, layout, false);
            addressTile.setId(address.getAddressID());

            TextView addressTitle = addressTile.findViewById(R.id.addressTitle);
            addressTitle.setText(address.getLocationName());

            TextView addressCoordinates = addressTile.findViewById(R.id.addressDescription);

            //Displaying coordinates on address tile
            String coordinates = address.getLatitude() + ", " +
                    address.getLongitude();
            addressCoordinates.setText(coordinates);

            ImageButton deleteButton = addressTile.findViewById(R.id.delete_button);


            /*OnClickListener for each address tile which opens a new EditLocation fragment
            and passed AddressObject payload as a Bundle for the fragment.
            * */
            addressTile.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("addressPayload", address);


                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_EditLocation, bundle);
            });

            //Delete button, removes address from database by id and removes address tile from view
            deleteButton.setOnClickListener(v -> {
                db.deleteAddress(addressTile.getId());
                addressList = db.getAllAddresses();
                layout.removeView(addressTile);
            });

            //Add new address tile to LinearLayout
            layout.addView(addressTile);

        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}