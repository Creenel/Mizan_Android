package com.example.mizan_android.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mizan_android.MizanApplication;
import com.example.mizan_android.R;
import com.example.mizan_android.data.AppDatabase;
import com.example.mizan_android.data.CaseDao;
import com.example.mizan_android.data.CaseEntity;
import com.example.mizan_android.data.User;
import com.example.mizan_android.data.UserDao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReportFragment extends Fragment {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_GALLERY_PICK = 3;

    private Spinner spinnerCrimeType;
    private EditText editDescription, editDate;
    private Button btnLocation, btnAttachments, btnSubmit;
    private LocationManager locationManager;
    private double latitude, longitude;

    private byte[] attachedMediaBytes;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private AppDatabase db;
    private UserDao userDao;
    private CaseDao caseDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_report, container, false);

        db = ((MizanApplication) requireActivity().getApplicationContext()).getDatabase();
        userDao = db.userDao();
        caseDao = db.caseDao();

        spinnerCrimeType = root.findViewById(R.id.spinner_crime_type);
        editDescription = root.findViewById(R.id.edit_description);
        editDate = root.findViewById(R.id.edit_date);
        btnLocation = root.findViewById(R.id.btn_location);
        btnAttachments = root.findViewById(R.id.btn_attachments);
        btnSubmit = root.findViewById(R.id.btn_submit);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.crime_types,
                R.layout.spinner_item
        );

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCrimeType.setAdapter(adapter);

        editDate.setOnClickListener(v -> showDatePicker());
        btnLocation.setOnClickListener(v -> requestUserLocation());
        btnAttachments.setOnClickListener(v -> showAttachmentOptions());
        btnSubmit.setOnClickListener(v -> submitReport());

        return root;
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) ->
                        editDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void showAttachmentOptions() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Add Attachments")
                .setItems(options, (d, which) -> {
                    if (which == 0) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        }
                    } else {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, REQUEST_GALLERY_PICK);
                    }
                }).show();
    }

    private void requestUserLocation() {
        locationManager = (LocationManager) requireContext().getSystemService(Activity.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override public void onLocationChanged(@NonNull Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Toast.makeText(requireContext(), "Location captured", Toast.LENGTH_SHORT).show();
                locationManager.removeUpdates(this);
            }
            @Override public void onProviderDisabled(@NonNull String provider) {}
            @Override public void onProviderEnabled(@NonNull String provider) {}
            @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestUserLocation();
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] bitmapToSmallBytes(Bitmap bitmap) {
        int maxSize = 800;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float ratio = Math.min((float) maxSize / width, (float) maxSize / height);
        if (ratio > 1f) ratio = 1f;

        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        return stream.toByteArray();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK || data == null) return;

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            if (bmp != null) {
                attachedMediaBytes = bitmapToSmallBytes(bmp);
                Toast.makeText(requireContext(), "Photo captured", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_GALLERY_PICK) {
            Uri selected = data.getData();
            if (selected != null) {
                try {
                    Bitmap b = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selected);
                    if (b != null) {
                        attachedMediaBytes = bitmapToSmallBytes(b);
                        Toast.makeText(requireContext(), "Image selected", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Failed to read image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void submitReport() {
        final String type = spinnerCrimeType.getSelectedItem().toString();
        final String description = editDescription.getText().toString().trim();
        final String date = editDate.getText().toString().trim();

        if (description.isEmpty() || date.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            try {
                User currentUser = userDao.getLoggedInUser();
                if (currentUser == null) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "No logged-in user", Toast.LENGTH_SHORT).show());
                    return;
                }

                CaseEntity c = new CaseEntity(
                        currentUser.getUserId(),
                        type,
                        description,
                        "pending",
                        date,
                        attachedMediaBytes
                );
                caseDao.insert(c);

                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Report submitted", Toast.LENGTH_LONG).show();
                    editDescription.setText("");
                    editDate.setText("");
                    attachedMediaBytes = null;
                });
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Submit failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
}