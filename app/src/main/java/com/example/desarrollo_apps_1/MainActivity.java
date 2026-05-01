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

import com.example.desarrollo_apps_1.data.local.NetworkMonitor;
import com.example.desarrollo_apps_1.data.local.TokenManager;
import com.example.desarrollo_apps_1.data.repository.FavoritoRepository;
import com.example.desarrollo_apps_1.data.repository.ReservaRepository;
import com.example.desarrollo_apps_1.databinding.ActivityMainBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Inject TokenManager tokenManager;
    @Inject NetworkMonitor networkMonitor;
    @Inject ReservaRepository reservaRepository;
    @Inject FavoritoRepository favoritoRepository;

    private boolean wasOffline = false;

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
            navGraph.setStartDestination(R.id.loginFragment);
            navController.setGraph(navGraph);

            appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.loginFragment, R.id.homeFragment)
                    .build();

            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.loginFragment || destination.getId() == R.id.otpFragment) {
                    if (getSupportActionBar() != null) getSupportActionBar().hide();
                    binding.bottomNavigation.setVisibility(View.GONE);
                } else {
                    if (getSupportActionBar() != null) getSupportActionBar().show();
                    binding.bottomNavigation.setVisibility(View.VISIBLE);
                }
            });

            observeSessionStatus();
            observeNetworkStatus();
        }
    }

    private void observeNetworkStatus() {
        networkMonitor.getIsConnected().observe(this, isConnected -> {
            if (binding.layoutOffline != null) {
                binding.layoutOffline.tvOfflineBanner.setVisibility(isConnected ? View.GONE : View.VISIBLE);
            }

            // Punto 8.20: Sincronización automática al recuperar conexión
            if (isConnected && wasOffline) {
                syncData();
                Toast.makeText(this, "Conexión recuperada. Sincronizando datos...", Toast.LENGTH_SHORT).show();
            }
            wasOffline = !isConnected;
        });
    }

    private void syncData() {
        if (tokenManager.isLoggedIn()) {
            // Refrescar reservas (esto actualiza Room automáticamente)
            reservaRepository.getMisReservas(result -> {});
            // Refrescar favoritos (esto actualiza Room automáticamente)
            favoritoRepository.syncFavoritos();
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
