package com.example.readstoryapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Arrays;

public class AddFragment extends Fragment {

    private EditText etStoryName, etAuthorName, etStoryContent, etNewCategory;
    private Spinner spinnerCategory;
    private ImageView ivCoverImage;
    private Button btnSelectImage, btnSaveStory;
    private Uri selectedImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private DatabaseReference mDatabase;

    // Các thể loại có sẵn trong Spinner
    String[] categories = {"Sách nói", "Sách điện tử", "Truyện tranh", "Hiệu Sồi", "Sách giấy", "Podcast"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        // Ánh xạ các View
        etStoryName = view.findViewById(R.id.et_story_name);
        etAuthorName = view.findViewById(R.id.et_author_name);
        etStoryContent = view.findViewById(R.id.et_story_content);
        etNewCategory = view.findViewById(R.id.et_new_category); // Thêm trường nhập thể loại mới
        spinnerCategory = view.findViewById(R.id.spinner_category);
        ivCoverImage = view.findViewById(R.id.iv_cover_image);
        btnSelectImage = view.findViewById(R.id.btn_select_image);
        btnSaveStory = view.findViewById(R.id.btn_save_story);

        mDatabase = FirebaseDatabase.getInstance().getReference("stories");

        // Cập nhật Spinner với danh sách thể loại
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, categories);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinnerCategory.setAdapter(adapter);

        // Lắng nghe sự thay đổi của Spinner
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == categories.length - 1) { // Nếu chọn "Khác", hiển thị trường nhập thể loại mới
                    etNewCategory.setVisibility(View.VISIBLE);
                } else {
                    etNewCategory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        // Chọn ảnh bìa
        btnSelectImage.setOnClickListener(v -> openImageChooser());

        // Lưu truyện
        btnSaveStory.setOnClickListener(v -> saveStory());

        return view;
    }


    // Xử lý kết quả khi người dùng chọn ảnh
    private ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            // Xử lý ảnh đã chọn
                            Uri imageUri = result.getData().getData();
                            if (imageUri != null) {
                                selectedImageUri = imageUri;
                                try {
                                    // Lấy Bitmap từ URI ảnh đã chọn
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                                    ivCoverImage.setImageBitmap(bitmap); // Hiển thị ảnh lên ImageView
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getContext(), "Không thể tải ảnh", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

    // Mở trình chọn ảnh
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*"); // Chỉ cho phép chọn ảnh
        imagePickerLauncher.launch(intent); // Khởi chạy Activity để chọn ảnh
    }

    // Lưu truyện lên Firebase
    private void saveStory() {
        String name = etStoryName.getText().toString().trim();
        String author = etAuthorName.getText().toString().trim();
        String content = etStoryContent.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(author) || TextUtils.isEmpty(content)) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Nếu thể loại là "Khác", lấy thể loại từ EditText
        if ("Khác".equals(category)) {
            category = etNewCategory.getText().toString().trim();
            if (TextUtils.isEmpty(category)) {
                Toast.makeText(getContext(), "Vui lòng nhập thể loại mới!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Lấy ID mới cho câu chuyện
        String storyId = mDatabase.push().getKey();

        // Tạo đối tượng Story mới
        Story newStory = new Story(name, category, author, selectedImageUri.toString(), content);

        // Lưu vào Firebase
        mDatabase.child(storyId).setValue(newStory)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Truyện đã được lưu!", Toast.LENGTH_SHORT).show();
                    clearInputs();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Xóa các trường nhập sau khi lưu
    private void clearInputs() {
        etStoryName.setText("");
        etAuthorName.setText("");
        etStoryContent.setText("");
        etNewCategory.setText("");
        ivCoverImage.setImageResource(android.R.color.darker_gray); // Đặt lại ảnh bìa
    }
}
