package com.alumni.connect.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.alumni.connect.R;
import com.alumni.connect.data.local.SessionManager;
import com.alumni.connect.databinding.ActivityMainBinding;
import com.alumni.connect.ui.auth.AuthActivity;
import com.alumni.connect.util.Constants;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavController navController;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        setSupportActionBar(binding.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNav, navController);
            NavigationUI.setupWithNavController(binding.navigationView, navController);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar,
                R.string.nav_home, R.string.nav_home);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Update drawer header info
        View headerView = binding.navigationView.getHeaderView(0);
        TextView tvHeaderName = headerView.findViewById(R.id.tvHeaderName);
        TextView tvHeaderRoleEmail = headerView.findViewById(R.id.tvHeaderRoleEmail);

        if (tvHeaderName != null) tvHeaderName.setText(sessionManager.getFullName());
        if (tvHeaderRoleEmail != null) {
            String roleUpper = sessionManager.getRole().toUpperCase();
            tvHeaderRoleEmail.setText(sessionManager.getEmail() + " • " + roleUpper);
        }

        // Hide admin tab for non-admin users if desired or keep accessible
        if (!Constants.ROLE_ADMIN.equals(sessionManager.getRole())) {
            binding.navigationView.getMenu().findItem(R.id.nav_admin).setVisible(false);
        }

        binding.navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_logout) {
                sessionManager.clearSession();
                startActivity(new Intent(MainActivity.this, AuthActivity.class));
                finish();
                return true;
            }
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            binding.drawerLayout.closeDrawers();
            return handled;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && navController.navigateUp() || super.onSupportNavigateUp();
    }
}
