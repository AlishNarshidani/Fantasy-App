package com.example.fantasyapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePage extends AppCompatActivity {

   BottomNavigationView bottomNavigationView;
   FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            return insets;
        });

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        frameLayout = findViewById(R.id.framelayout1);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if(itemId == R.id.contestMenu)
                {
                    loadFrament(new ContestFragment(),false);
                } else if (itemId == R.id.miniGamesMenu) {
                    loadFrament(new miniGamesFragment(),false);
                }else if (itemId == R.id.walletMenu) {
                    loadFrament(new WallletFragment(),false);
                }else{
                    loadFrament(new ProfileFragment(),false);
                }
                return true;
            }
        });
        loadFrament(new ContestFragment(),true);
    }
    private void loadFrament(Fragment fragment, boolean isAppInitialized){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(isAppInitialized)
        {
            fragmentTransaction.add(R.id.framelayout1,fragment);
        }
        else{
            fragmentTransaction.replace(R.id.framelayout1,fragment);
        }

        fragmentTransaction.commit();
    }
}