package com.application.pglocator;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.application.pglocator.constants.UserType;
import com.application.pglocator.ui.AddPGBottomFragment;
import com.application.pglocator.ui.feedback.AddFeedbackFragment;
import com.application.pglocator.ui.feedback.FeedbackFragment;
import com.application.pglocator.util.Globals;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.Arrays;

public class PGHomeActivity extends AppCompatActivity {

    public static final String ARG_USER = "user";
    private static final String TAG = PGHomeActivity.class.getSimpleName();
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pghome);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if (Globals.user.getUserType().equals(UserType.PG.getValue())) {
                Fragment foregroundFragment = getForegroundFragment();
                if (foregroundFragment instanceof FeedbackFragment) {
                    AddFeedbackFragment feedbackFragment = new AddFeedbackFragment();
                    feedbackFragment.show(getSupportFragmentManager(), TAG);
                } else {
                    AddPGBottomFragment addPGBottomFragment = new AddPGBottomFragment();
                    addPGBottomFragment.show(getSupportFragmentManager(), TAG);
                }
            } else if (Globals.user.getUserType().equals(UserType.USER.getValue())) {
                Fragment foregroundFragment = getForegroundFragment();
                if (foregroundFragment instanceof FeedbackFragment) {
                    AddFeedbackFragment feedbackFragment = new AddFeedbackFragment();
                    feedbackFragment.show(getSupportFragmentManager(), TAG);
                }
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_requests, R.id.nav_feedback)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        initHeader(navigationView);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (Arrays.asList(
                    R.id.nav_request_details,
                    R.id.nav_pgdetails,
                    R.id.nav_requests
            ).contains(destination.getId())) {
                fab.setVisibility(View.GONE);
            } else {
                fab.setVisibility(View.VISIBLE);
            }
        });
    }

    public Fragment getForegroundFragment() {
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        return navHostFragment == null ? null : navHostFragment.getChildFragmentManager().getFragments().get(0);
    }

    private void initHeader(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);
        TextView textViewName = headerView.findViewById(R.id.textViewName);
        textViewName.setText(Globals.user.getDisplayName());
        TextView textViewEmail = headerView.findViewById(R.id.textViewEmail);
        textViewEmail.setText(Globals.user.getEmail());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pghome, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}