package com.example.harddisks.AuthPages;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.harddisks.R;

public class GreetingFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_greeting, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController = Navigation.findNavController(view);

        Button enter = view.findViewById(R.id.enter);
        Button registration = view.findViewById(R.id.registration);

        enter.setOnClickListener(v -> navController.navigate(R.id.action_greeting_to_authorization));

        registration.setOnClickListener(v -> navController.navigate(R.id.action_greeting_to_registration));


    }
}