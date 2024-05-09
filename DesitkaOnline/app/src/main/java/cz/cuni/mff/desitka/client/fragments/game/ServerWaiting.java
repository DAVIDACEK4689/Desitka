package cz.cuni.mff.desitka.client.fragments.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import cz.cuni.mff.desitka.JSON.GameStates;
import cz.cuni.mff.desitka.R;

/**
 * The ServerWaiting class represents the state where the client is waiting for the server.
 */
public class ServerWaiting extends Fragment implements GameStates {

    /**
     * Called when the fragment is first created.
     *
     * @param savedInstanceState If the fragment is being re-constructed from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.empty, container, false);
    }
}