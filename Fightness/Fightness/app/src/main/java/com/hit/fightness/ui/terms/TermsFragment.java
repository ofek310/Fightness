package com.hit.fightness.ui.terms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.hit.fightness.R;

public class TermsFragment extends Fragment {

    private TermsViewModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(TermsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_terms, container, false);

        return root;
    }
}
