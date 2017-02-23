package es.deusto.trekkingaventura.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.deusto.trekkingaventura.R;

public class EmptyAppFragment extends Fragment {

    public EmptyAppFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_empty, container, false);

        return rootView;
    }
}
