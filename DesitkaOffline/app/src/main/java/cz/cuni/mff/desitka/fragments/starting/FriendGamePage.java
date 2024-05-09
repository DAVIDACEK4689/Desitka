package cz.cuni.mff.desitka.fragments.starting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import cz.cuni.mff.desitka.Preferences;
import cz.cuni.mff.desitka.R;
import cz.cuni.mff.desitka.activities.GameActivity;
import cz.cuni.mff.desitka.activities.StartingActivity;
import cz.cuni.mff.desitka.interfaces.StartingStates;

/**
 * Represents the friend game page of the application.
 * This class is responsible for handling user interactions on the friend game page.
 */
public class FriendGamePage extends Fragment {
    TextView[] textViewsCounts;
    EditText[] editTextsNames;
    Button createGame;
    Button moveBack;
    int playersCount;
    int alertID;

    /**
     * Called when the fragment is first created.
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            playersCount = Preferences.getPlayersCount(requireContext());
            alertID = -1;
            return;
        }
        playersCount = savedInstanceState.getInt("playerCountIndex");
        alertID = savedInstanceState.getInt("alertID");
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friend_game_page, container, false);
        initialize(view);
        addListeners();
        return view;
    }

    /**
     * Initializes the view components.
     * @param view The view to initialize.
     */
    private void initialize(View view) {
        initializePlayerCounts(view);
        initializeEditText(view);
        initializeButtons(view);

        setPlayerCountColor();
        makeEditTextsVisible();
        showAlertDialog(alertID);
    }

    /**
     * Sets the color of the player count text views.
     */
    private void setPlayerCountColor() {
        int color = ContextCompat.getColor(requireContext(), R.color.button_background);
        textViewsCounts[playersCount-2].setBackgroundColor(color);
    }

    /**
     * Initializes the buttons in the view.
     * @param view The view to initialize the buttons in.
     */
    private void initializeButtons(View view) {
        createGame = view.findViewById(R.id.create_game);
        moveBack = view.findViewById(R.id.move_back);
    }

    /**
     * Initializes the edit text fields in the view.
     * @param view The view to initialize the edit text fields in.
     */
    private void initializeEditText(View view) {
        editTextsNames = new EditText[] {
            view.findViewById(R.id.edit_text1),
            view.findViewById(R.id.edit_text2),
            view.findViewById(R.id.edit_text3),
            view.findViewById(R.id.edit_text4),
            view.findViewById(R.id.edit_text5)
        };
    }

    /**
     * Initializes the player count text views in the view.
     * @param view The view to initialize the player count text views in.
     */
    private void initializePlayerCounts(View view) {
        textViewsCounts = new TextView[] {
            view.findViewById(R.id.player_count1),
            view.findViewById(R.id.player_count2),
            view.findViewById(R.id.player_count3),
            view.findViewById(R.id.player_count4)
        };
    }

    /**
     * Adds listeners to the view components.
     */
    private void addListeners() {
        addTextViewListeners();
        addEditTextListeners();
        addCreateGameListener();
        addMoveBackListener();
    }

    /**
     * Adds listeners to the TextView components.
     */
    private void addTextViewListeners() {
        int neutralColor = ContextCompat.getColor(requireContext(), R.color.card_background);
        int selectedColor = ContextCompat.getColor(requireContext(), R.color.button_background);

        for (TextView textView : textViewsCounts) {
            textView.setOnClickListener(v -> {
                textViewsCounts[playersCount-2].setBackgroundColor(neutralColor);
                textView.setBackgroundColor(selectedColor);
                playersCount = Integer.parseInt(textView.getText().toString());
                makeEditTextsVisible();
            });
        }
    }

    /**
     * Adds listeners to the EditText components.
     */
    private void addEditTextListeners() {
        for (EditText editText : editTextsNames) {
            editText.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                if (isKeyboardShown(editText.getRootView())) {
                    hideViews();
                    return;
                }
                showViews();
            });
        }
    }

    /**
     * Adds a listener to the createGame button.
     */
    private void addCreateGameListener() {
        createGame.setOnClickListener(v -> {
            String[] playerNames = getPlayerNames();
            if (playerNames != null) {
                if (!areNamesUnique(playerNames)) {
                    showAlertDialog(R.string.duplicate_names_error);
                    return;
                }
                // start game
                Preferences.setPlayersCount(requireContext(), playersCount);
                Intent intent = new Intent(requireContext(), GameActivity.class);
                intent.putExtra("playerNames", playerNames);
                requireActivity().finish();
                startActivity(intent);
            }
        });
    }

    /**
     * Adds a listener to the moveBack button.
     */
    private void addMoveBackListener() {
        moveBack.setOnClickListener(v -> ((StartingActivity) requireActivity())
                .replaceFragment(StartingStates.StartingState.WelcomePage));
    }

    /**
     * Gets the player names from the edit text fields.
     * @return An array of player names, or null if any name is empty.
     */
    private String[] getPlayerNames() {
        String[] playerNames = new String[playersCount];
        for (int i = 0; i < playersCount; i++) {
            String name = editTextsNames[i].getText().toString().trim();
            if (name.isEmpty()) {
                showAlertDialog(R.string.empty_names_error);
                return null;
            }
            playerNames[i] = name;
        }
        return playerNames;
    }

    /**
     * Checks if the player names are unique.
     * @param playerNames An array of player names.
     * @return True if the names are unique, false otherwise.
     */
    private boolean areNamesUnique(String[] playerNames) {
        Set<String> namesSet = new HashSet<>(Arrays.asList(playerNames));
        return namesSet.size() == playersCount;
    }

    /**
     * Checks if the keyboard is shown.
     * @param rootView The root view.
     * @return True if the keyboard is shown, false otherwise.
     */
    private boolean isKeyboardShown(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    /**
     * Shows the view components.
     */
    private void showViews() {
        for (TextView textView : textViewsCounts) {
            textView.setVisibility(View.VISIBLE);
        }
        createGame.setVisibility(View.VISIBLE);
        moveBack.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the view components.
     */
    private void hideViews() {
        for (TextView textView : textViewsCounts) {
            textView.setVisibility(View.GONE);
        }
        createGame.setVisibility(View.GONE);
        moveBack.setVisibility(View.GONE);
    }

    /**
     * Shows an alert dialog with the specified error message.
     * @param errorID The resource ID of the error message.
     */
    public void showAlertDialog(int errorID) {
        if (errorID != -1) {
            alertID = errorID;
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setCancelable(false)
                    .setMessage(errorID)
                    .setPositiveButton(R.string.ok, (dialog, id) -> dialog.cancel());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            Button button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryColor));
            button.setOnClickListener(v -> {
                alertID = -1;
                alertDialog.dismiss();
            });
        }
    }

    /**
     * Makes the edit text fields visible based on the number of players.
     */
    private void makeEditTextsVisible() {
        for (int i = 0; i < 5; i++) {
            if (i < playersCount) {
                editTextsNames[i].setVisibility(View.VISIBLE);
                continue;
            }
            editTextsNames[i].setVisibility(View.GONE);
            editTextsNames[i].setText("");
        }
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it can later be reconstructed in a new instance of its process is restarted.
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("playerCountIndex", playersCount);
        outState.putInt("alertID", alertID);
    }
}