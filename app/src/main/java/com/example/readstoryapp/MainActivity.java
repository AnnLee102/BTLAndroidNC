package com.example.readstoryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private ActionBarDrawerToggle toggle;
    private ArrayList<String> searchHistory;
    private ArrayAdapter<String> searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Thiết lập Drawer Layout
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Thiết lập trình nghe sự kiện click cho Navigation Drawer
        navigationView.setNavigationItemSelectedListener(drawerListener);

        // Thiết lập Bottom Navigation View
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(bottomNavListener);

        // Nạp fragment mặc định (Trang Chủ)
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_home); // Đặt mặc định vào Home
        }

        // Khởi tạo danh sách lịch sử tìm kiếm
        searchHistory = new ArrayList<>();

        // Nút tìm kiếm
        ImageButton searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(v -> openSearchFragment());
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

    // Listener cho Navigation Drawer
    private final NavigationView.OnNavigationItemSelectedListener drawerListener =
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    // Xử lý sự kiện click vào các mục trong ngăn kéo
                    if (item.getItemId() == R.id.nav_poetry) {
                        Toast.makeText(MainActivity.this, "Thơ - Tản văn", Toast.LENGTH_SHORT).show();
                    } else if (item.getItemId() == R.id.nav_detective) {
                        Toast.makeText(MainActivity.this, "Trinh thám - Kinh dị", Toast.LENGTH_SHORT).show();
                    } else if (item.getItemId() == R.id.nav_marketing) {
                        Toast.makeText(MainActivity.this, "Marketing - Bán hàng", Toast.LENGTH_SHORT).show();
                    } else if (item.getItemId() == R.id.nav_management) {
                        Toast.makeText(MainActivity.this, "Quản trị - Lãnh đạo", Toast.LENGTH_SHORT).show();
                    }

                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
            };

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

    private void openSearchFragment() {
        // Mở fragment tìm kiếm
        SearchFragment searchFragment = new SearchFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, searchFragment); // Thay thế fragment hiện tại bằng fragment tìm kiếm
        transaction.addToBackStack(null); // Thêm vào back stack để có thể quay lại
        transaction.commit();
    }

    // Khởi tạo Fragment tìm kiếm
    public static class SearchFragment extends Fragment {

        private ArrayList<String> searchHistory;
        private ArrayAdapter<String> searchAdapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate layout tìm kiếm
            View rootView = inflater.inflate(R.layout.search_layout, container, false);

            searchHistory = new ArrayList<>();
            ListView searchHistoryList = rootView.findViewById(R.id.search_history_list);
            EditText searchInput = rootView.findViewById(R.id.search_input);
            ImageButton backButton = rootView.findViewById(R.id.back_button);

            // Xử lý khi nhấn nút quay lại
            backButton.setOnClickListener(v -> getFragmentManager().popBackStack());

            // Khởi tạo adapter cho danh sách lịch sử tìm kiếm
            searchAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, searchHistory);
            searchHistoryList.setAdapter(searchAdapter);

            // Xử lý khi nhấn phím Enter
            searchInput.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String query = searchInput.getText().toString().trim();
                    if (!query.isEmpty()) {
                        searchHistory.add(query); // Thêm từ khóa vào danh sách
                        searchAdapter.notifyDataSetChanged(); // Cập nhật danh sách
                        Toast.makeText(getActivity(), "Tìm kiếm: " + query, Toast.LENGTH_SHORT).show();
                        searchInput.setText(""); // Xóa nội dung sau khi tìm
                    }
                    return true; // Đã xử lý sự kiện
                }
                return false; // Không xử lý các phím khác
            });

            return rootView;
        }
    }

}
