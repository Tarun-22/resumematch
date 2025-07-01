package com.example.resumematch;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import androidx.appcompat.app.AppCompatActivity;

public class ScanResumeActivity extends AppCompatActivity {

    ImageView backButton;
    Button buttonCamera, buttonUpload;
    TextView textOCRPreview;

    ActivityResultLauncher<Intent> imagePickerLauncher;

    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_resume);

        // here we are declaring the variables for button, camera, and the text for preview
        backButton = findViewById(R.id.backButton);
        buttonCamera = findViewById(R.id.buttonCamera);
        buttonUpload = findViewById(R.id.buttonUpload);
        textOCRPreview = findViewById(R.id.textOCRPreview);

        backButton.setOnClickListener(v -> finish());

        //creating the launcher to pick the image
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        runOCR(selectedImageUri);
                    }
                });

        buttonUpload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        textOCRPreview.setMovementMethod(new android.text.method.ScrollingMovementMethod());


        buttonCamera.setOnClickListener(v ->
                textOCRPreview.setText("Camera scanning coming soon..."));
    }

    private void runOCR(Uri imageUri) {
        try {
            Bitmap bitmap;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), imageUri));
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            }

            InputImage image = InputImage.fromBitmap(bitmap, 0);
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

            textOCRPreview.setText("Scanning...");
            recognizer.process(image)
                    .addOnSuccessListener(visionText -> {
                        String resultText = visionText.getText();
                        textOCRPreview.setText(resultText.isEmpty() ? "No text found." : resultText);
                        Log.d("OCR_RESULT", resultText);

                    })
                    .addOnFailureListener(e -> {
                        textOCRPreview.setText("OCR failed: " + e.getMessage());
                        Toast.makeText(this, "OCR failed", Toast.LENGTH_SHORT).show();
                    });

        } catch (Exception e) {
            textOCRPreview.setText("Error loading image: " + e.getMessage());
        }
    }
}
