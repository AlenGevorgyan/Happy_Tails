package com.app.happytails.utils.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.happytails.R;

import java.util.ArrayList;

public class PatreonFragment extends Fragment {

    private EditText donateUrlEditText;
    private Button nextButton, addAmountBtn;
    private LinearLayout amountContainer;
    private ArrayList<EditText> productFields = new ArrayList<>();  // Change donationFields to productFields

    public PatreonFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patreon, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        donateUrlEditText = view.findViewById(R.id.donate_url);
        amountContainer = view.findViewById(R.id.amountContainer);
        nextButton = view.findViewById(R.id.postNextBtnP);
        addAmountBtn = view.findViewById(R.id.addAmountBtn);

        addAmountBtn.setOnClickListener(v -> addNewProductUrlField());  // Updated to add product URL field
        nextButton.setOnClickListener(v -> handleNextButtonClick());
    }

    // Add a new field for entering product URLs
    private void addNewProductUrlField() {
        if (productFields.size() >= 5) {  // Limit to 5 product URLs
            Toast.makeText(getContext(), "You can add up to 5 product URLs.", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText newProductUrlField = new EditText(getContext());
        newProductUrlField.setHint("Enter Product URL");
        newProductUrlField.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newProductUrlField.setPadding(8, 8, 8, 8);
        newProductUrlField.setBackgroundResource(R.drawable.edit_text_rounded_corner);
        newProductUrlField.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_URI);

        productFields.add(newProductUrlField);
        amountContainer.addView(newProductUrlField);
    }

    private void handleNextButtonClick() {
        String patreonUrl = donateUrlEditText.getText().toString().trim();

        if (patreonUrl.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a Patreon URL.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidUrl(patreonUrl)) {
            Toast.makeText(getContext(), "Invalid URL. Please enter a valid Patreon URL.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gather all product URLs from the input fields
        ArrayList<String> productList = new ArrayList<>();
        for (EditText field : productFields) {
            String productUrl = field.getText().toString().trim();
            if (!productUrl.isEmpty() && isValidUrl(productUrl)) {  // Only add valid URLs
                productList.add(productUrl);
            }
        }

        if (productList.isEmpty()) {
            Toast.makeText(getContext(), "Please add at least one product URL.", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle bundle = getArguments();
        if (bundle != null) {
            bundle.putString("patreonUrl", patreonUrl);
            bundle.putSerializable("productsList", productList);
            CreateFragment createFragment = new CreateFragment();
            createFragment.setArguments(bundle);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, createFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private boolean isValidUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }
}
