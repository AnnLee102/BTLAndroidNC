package com.example.readstoryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private ActionBarDrawerToggle toggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Thiết lập Drawer Layout
        drawerLayout = findViewById(R.id.drawer_layout);


        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();



        // Thiết lập Bottom Navigation View
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(bottomNavListener);

        // Nạp fragment mặc định (Trang Chủ)
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_home); // Đặt mặc định vào Home
        }





        //lấy dữ liệu tác giả từ firebase
        storiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> authors = new HashSet<>();
                for (DataSnapshot storySnapshot : dataSnapshot.getChildren()) {
                    String author = storySnapshot.child("author").getValue(String.class);
                    if (author != null) {
                        authors.add(author);
                    }
                }
                updateNavigationMenu(authors);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load authors", Toast.LENGTH_SHORT).show();
            }
        });

        // Tìm ImageButton trong layout
        ImageButton btnOpenSearch = findViewById(R.id.search_button);

        // Gán sự kiện khi nhấn nút
        btnOpenSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent); // Chuyển đến SearchActivity
        });
    }

    // Listener cho Bottom Navigation View
    private final BottomNavigationView.OnItemSelectedListener bottomNavListener =
            new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    if (item.getItemId() == R.id.nav_home) {
                        selectedFragment = new HomeFragment();
                    } else if (item.getItemId() == R.id.nav_offers) {
                        selectedFragment = new OffersFragment();
                    } else if (item.getItemId() == R.id.nav_add) {
                        selectedFragment = new AddFragment();
                    } else if (item.getItemId() == R.id.nav_library) {
                        selectedFragment = new LibraryFragment();
                    }

                    if (selectedFragment != null) {
                        loadFragment(selectedFragment);
                    }
                    return true;
                }
            };

    DatabaseReference storiesRef = FirebaseDatabase.getInstance().getReference("stories");
    // Phương thức để nạp một fragment vào vùng chứa fragment của MainActivity
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    //Thêm danh sách tác giả bên trái
    private void updateNavigationMenu(Set<String> authors) {
        NavigationView navigationView = findViewById(R.id.navigation_view); // ID của NavigationView trong layout
        Menu menu = navigationView.getMenu();

        menu.clear(); // Xóa menu cũ

        // Sắp xếp danh sách tác giả theo thứ tự ABC
        List<String> sortedAuthors = new ArrayList<>(authors);
        Collections.sort(sortedAuthors);

        // Thêm tiêu đề "Tên tác giả"
        SpannableString styledTitle = new SpannableString("Tên tác giả");
        styledTitle.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, styledTitle.length(), 0); // In đậm
        styledTitle.setSpan(new ForegroundColorSpan(Color.WHITE), 0, styledTitle.length(), 0); // Đổi màu trắng
        MenuItem headerItem = menu.add(Menu.NONE, Menu.NONE, 0, styledTitle);
        headerItem.setEnabled(false); // Không thể nhấp

        // Thêm danh sách tác giả
        for (String author : sortedAuthors) {
            MenuItem item = menu.add(author);
            item.setOnMenuItemClickListener(menuItem -> {
                // Chuyển sang Fragment với dữ liệu tên tác giả
                Bundle bundle = new Bundle();
                bundle.putString("author_name", menuItem.getTitle().toString()); // Truyền tên tác giả

                AuthorFragment fragment = new AuthorFragment();
                fragment.setArguments(bundle);

                // Thay đổi fragment
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();

                return true;
            });
        }


        // Thêm khoảng trống cuối danh sách
        MenuItem spacerItem = menu.add(" ");
        spacerItem.setEnabled(false); // Không thể nhấp
        View spacer = new View(this);
        spacer.setMinimumHeight(150); // Chiều cao 100px
        spacerItem.setActionView(spacer); // Gán view cho item

        navigationView.invalidate(); // Làm mới NavigationView
    }





}
