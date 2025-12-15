package com.example.mizan_android.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.room.Room;

import com.example.mizan_android.R;
import com.example.mizan_android.data.AppDatabase;
import com.example.mizan_android.data.User;
import com.example.mizan_android.data.UserDao;

public class HomeFragment extends Fragment {

    private TextView helloText;
    private ImageView glowCircle;
    private CardView reportButton;
    private UserDao userDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        helloText = view.findViewById(R.id.helloText);
        glowCircle = view.findViewById(R.id.glowCircle);
        reportButton = view.findViewById(R.id.reportButton);

        // init Room
        AppDatabase db = Room.databaseBuilder(requireContext(), AppDatabase.class, "mizan_db")
                .allowMainThreadQueries() // ⚠️ For demo only. Use background thread in production.
                .build();
        userDao = db.userDao();

        // set greeting from logged-in user
        User loggedUser = userDao.getLoggedInUser();
        if (loggedUser != null && loggedUser.getFullName() != null && !loggedUser.getFullName().isEmpty()) {
            helloText.setText("Hello, " + loggedUser.getFullName());
        } else {
            helloText.setText("Hello, User");
        }

        // Glow animation
        Animation glowAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.pulse);
        glowCircle.startAnimation(glowAnim);

        // Navigate to report fragment on click
        reportButton.setOnClickListener(v -> {
            // Replace the fragment in the activity's fragment container and add to back stack
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.nav_host_fragment, new com.example.mizan_android.ui.ReportFragment())
                    .addToBackStack("report")
                    .commit();
        });


        return view;
    }
}