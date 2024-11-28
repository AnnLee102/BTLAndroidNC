package com.example.readstoryapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.bumptech.glide.Glide;


public class AddFragment extends Fragment {

    private EditText etStoryName, etAuthorName, etStoryContent, etNewCategory;
    private Spinner spinnerCategory;
    private ImageView ivCoverImage;
    private Button btnSelectImage, btnSaveStory;
    private Uri selectedImageUri;
    private EditText etImageUrl;
    private static final int PICK_IMAGE_REQUEST = 1;
    private DatabaseReference mDatabase;

    // Các thể loại truyện
    String[] categories = {"Sách nói", "Sách điện tử", "Truyện tranh", "Hiệu Sồi", "Sách giấy", "Podcast"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        // Ánh xạ các View
        etStoryName = view.findViewById(R.id.et_story_name);
        etAuthorName = view.findViewById(R.id.et_author_name);
        etStoryContent = view.findViewById(R.id.et_story_content);
        etNewCategory = view.findViewById(R.id.et_new_category);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        ivCoverImage = view.findViewById(R.id.iv_cover_image);
        etImageUrl = view.findViewById(R.id.et_image_url);
        btnSelectImage = view.findViewById(R.id.btn_select_image);
        btnSaveStory = view.findViewById(R.id.btn_save_story);

        mDatabase = FirebaseDatabase.getInstance().getReference("stories");

        // Cập nhật danh sách thể loại
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, categories);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinnerCategory.setAdapter(adapter);

        // Nếu thêm thể loại
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == categories.length - 1) {
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
//        btnSelectImage.setOnClickListener(v -> openImageChooser());
        btnSelectImage.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etImageUrl.getText().toString().trim())) {
                openImageChooser(); // Nếu URL trống, cho phép chọn ảnh từ bộ sưu tập
            } else {
                Toast.makeText(getContext(), "URL ảnh đã được nhập, không thể chọn ảnh nữa.", Toast.LENGTH_SHORT).show();
            }
        });

        // Thêm sự kiện để tải ảnh từ URL
        etImageUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String imageUrl = charSequence.toString().trim();
                if (!TextUtils.isEmpty(imageUrl)) {
                    // Sử dụng Glide để tải ảnh từ URL
                    Glide.with(getContext())
                            .load(imageUrl)
                            .placeholder(android.R.color.darker_gray) // Placeholder nếu ảnh chưa tải
                            .into(ivCoverImage);
                } else {
                    ivCoverImage.setImageResource(android.R.color.darker_gray);  // Xóa ảnh nếu URL trống
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


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

        // Lấy URL từ EditText (nếu có)
        String imageUrl = etImageUrl.getText().toString().trim();
        String imageUriToSave;

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

        // Nếu có URL ảnh, sử dụng nó
        if (!TextUtils.isEmpty(imageUrl)) {
            imageUriToSave = imageUrl;
        } else {
            // Nếu không có URL, sử dụng ảnh đã chọn từ bộ sưu tập
            if (selectedImageUri != null) {
                imageUriToSave = selectedImageUri.toString();
            } else {
                Toast.makeText(getContext(), "Vui lòng chọn ảnh hoặc nhập URL ảnh", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Lấy ID mới cho câu chuyện
        String storyId = mDatabase.push().getKey();

        // Tạo đối tượng Story mới
        Story newStory = new Story(name, category, author, imageUriToSave, content);

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



    // Xóa các trường đã nhập sau khi lưu
    private void clearInputs() {
        etStoryName.setText("");
        etAuthorName.setText("");
        etStoryContent.setText("");
        etNewCategory.setText("");
        etImageUrl.setText("");
        ivCoverImage.setImageResource(android.R.color.darker_gray);
    }
}
