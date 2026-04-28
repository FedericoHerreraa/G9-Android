package com.example.desarrollo_apps_1;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.desarrollo_apps_1.data.local.TokenManager;
import com.example.desarrollo_apps_1.databinding.ActivityMainBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Inject
    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);
            // Siempre empezamos en LoginFragment para que maneje la biometría o el autologin
            navGraph.setStartDestination(R.id.loginFragment);
            navController.setGraph(navGraph);

            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.loginFragment, R.id.homeFragment)
                    .build();

            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.loginFragment) {
                    if (getSupportActionBar() != null) getSupportActionBar().hide();
                    binding.bottomNavigation.setVisibility(View.GONE);
                } else {
                    if (getSupportActionBar() != null) getSupportActionBar().show();
                    binding.bottomNavigation.setVisibility(View.VISIBLE);
                }
            });

            observeSessionStatus();
        }
    }

    private void observeSessionStatus() {
        tokenManager.getSessionExpired().observe(this, expired -> {
            if (expired != null && expired) {
                tokenManager.resetSessionExpired();
                navigateToLogin();
            }
        });
    }

    private void navigateToLogin() {
        if (navController != null && navController.getCurrentDestination() != null 
                && navController.getCurrentDestination().getId() != R.id.loginFragment) {
            Toast.makeText(this, "Sesión expirada. Por favor, ingrese de nuevo.", Toast.LENGTH_LONG).show();
            navController.navigate(R.id.loginFragment);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
