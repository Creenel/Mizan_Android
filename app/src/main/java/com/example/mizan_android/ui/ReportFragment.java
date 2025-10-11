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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mizan_android.R;

import java.io.IOException;
import java.util.Calendar;

public class ReportFragment extends Fragment {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_GALLERY_PICK = 3;

    private Spinner spinnerCrimeType;
    private EditText editDescription, editDate;
    private CheckBox checkboxAnonymous;
    private Button btnLocation, btnAttachments, btnSubmit;
    private LocationManager locationManager;

    private ImageButton backButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        backButton = view.findViewById(R.id.backButton);
        spinnerCrimeType = view.findViewById(R.id.spinner_crime_type);
        editDescription = view.findViewById(R.id.edit_description);
        editDate = view.findViewById(R.id.edit_date);
        checkboxAnonymous = view.findViewById(R.id.checkbox_anonymous);
        btnLocation = view.findViewById(R.id.btn_location);
        btnAttachments = view.findViewById(R.id.btn_attachments);
        btnSubmit = view.findViewById(R.id.btn_submit);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.crime_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCrimeType.setAdapter(adapter);

        editDate.setOnClickListener(v -> showDatePicker());
        btnLocation.setOnClickListener(v -> requestUserLocation());
        btnAttachments.setOnClickListener(v -> showAttachmentOptions());
        btnSubmit.setOnClickListener(v -> handleSubmit());
        backButton.setOnClickListener(v -> returnToHome());
        return view;
    }

    private void returnToHome(){
        requireActivity().getSupportFragmentManager().popBackStack();
    }
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year1, month1, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            editDate.setText(date);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showAttachmentOptions() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Attachments")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        }
                    } else {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, REQUEST_GALLERY_PICK);
                    }
                })
                .show();
    }

    private void requestUserLocation() {
        locationManager = (LocationManager) requireContext().getSystemService(Activity.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Toast.makeText(requireContext(), "Location logged: (" + latitude + ", " + longitude + ")", Toast.LENGTH_LONG).show();
                    locationManager.removeUpdates(this);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestUserLocation();
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                Toast.makeText(requireContext(), "Photo captured successfully", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_GALLERY_PICK && data != null) {
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImage);
                    Toast.makeText(requireContext(), "Image selected from gallery", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleSubmit() {
        String type = spinnerCrimeType.getSelectedItem().toString();
        String description = editDescription.getText().toString();
        String date = editDate.getText().toString();

        if (description.isEmpty() || date.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Report submitted successfully (local only)", Toast.LENGTH_LONG).show();
        }
    }
}
