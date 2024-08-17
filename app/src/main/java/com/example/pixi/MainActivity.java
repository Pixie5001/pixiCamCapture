package com.example.pixi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private Bitmap capturedImage;
    private Bitmap referenceImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton captureButton = findViewById(R.id.btc);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        ImageButton selectFromGalleryButton = findViewById(R.id.button2);
        selectFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        // Load the reference image when the activity is created
        loadReferenceImage();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            capturedImage = (Bitmap) extras.get("data");

            // Process the captured image and compare it with the reference image
            compareImages();
        } else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    capturedImage = BitmapFactory.decodeStream(inputStream);

                    // Process the selected image and compare it with the reference image
                    compareImages();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to open image from gallery", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void loadReferenceImage() {
        // Load the reference image from the app's resources
        referenceImage = BitmapFactory.decodeResource(getResources(), R.drawable.varad);
    }

    private void compareImages() {
        if (capturedImage != null && referenceImage != null) {
            // Detect facial landmarks in captured and reference images
            List<List<Landmark>> capturedLandmarks = detectFacialLandmarks(capturedImage);
            List<List<Landmark>> referenceLandmarks = detectFacialLandmarks(referenceImage);

            // Check if both images have exactly one face detected
            if (capturedLandmarks.size() != 1 || referenceLandmarks.size() != 1) {
                Toast.makeText(MainActivity.this, "Exactly one face must be present in both images", Toast.LENGTH_SHORT).show();
                return;
            }

            // Compare the detected landmarks to determine if the same person is present
            if (areLandmarksSimilar(capturedLandmarks.get(0), referenceLandmarks.get(0))) {
                // Faces are similar
                Toast.makeText(MainActivity.this, "Facial recognition successful: Same person detected", Toast.LENGTH_SHORT).show();

                // Load the reference image when the activity is created
                loadReferenceImage();

                // Pass the URL of the reference image to the next activity using Intent
                Intent intent = new Intent(MainActivity.this, similarimage.class);
                intent.putExtra("reference_image_url", "android.resource://" + getPackageName() + "/" + R.drawable.varad);
                startActivity(intent);

            } else {
                // Faces are not similar
                Toast.makeText(MainActivity.this, "Facial recognition failed: Different persons detected", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Reference image not loaded yet", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean areLandmarksSimilar(List<Landmark> capturedLandmarks, List<Landmark> referenceLandmarks) {
        // Compare the positions of specific landmarks (e.g., eyes, nose, and mouth) to determine similarity

        // Define thresholds for similarity
        float eyeDistanceThreshold = 60.0f; // Example threshold in pixels
        float noseMouthDistanceThreshold = 40.0f; // Example threshold in pixels

        // Compute distances between landmarks
        float capturedEyeDistance = getEyesDistance(capturedLandmarks.get(Landmark.LEFT_EYE), capturedLandmarks.get(Landmark.RIGHT_EYE));
        float referenceEyeDistance = getEyesDistance(referenceLandmarks.get(Landmark.LEFT_EYE), referenceLandmarks.get(Landmark.RIGHT_EYE));

        float capturedNoseMouthDistance = getNoseMouthDistance(capturedLandmarks.get(Landmark.NOSE_BASE), capturedLandmarks.get(Landmark.BOTTOM_MOUTH));
        float referenceNoseMouthDistance = getNoseMouthDistance(referenceLandmarks.get(Landmark.NOSE_BASE), referenceLandmarks.get(Landmark.BOTTOM_MOUTH));

        // Compare distances with thresholds
        boolean isEyesSimilar = Math.abs(capturedEyeDistance - referenceEyeDistance) < eyeDistanceThreshold;
        boolean isNoseMouthSimilar = Math.abs(capturedNoseMouthDistance - referenceNoseMouthDistance) < noseMouthDistanceThreshold;

        // Return true if all landmarks are similar
        return isEyesSimilar && isNoseMouthSimilar;
    }



    private float getEyesDistance(Landmark leftEye, Landmark rightEye) {
        float dx = leftEye.getPosition().x - rightEye.getPosition().x;
        float dy = leftEye.getPosition().y - rightEye.getPosition().y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private float getNoseMouthDistance(Landmark noseBase, Landmark bottomMouth) {
        float dx = noseBase.getPosition().x - bottomMouth.getPosition().x;
        float dy = noseBase.getPosition().y - bottomMouth.getPosition().y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }



    private List<List<Landmark>> detectFacialLandmarks(Bitmap image) {
        FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                //.setApiKey(API_KEY) // Set the API key
                .build();

        if (!detector.isOperational()) {
            Toast.makeText(this, "Face detector not operational", Toast.LENGTH_SHORT).show();
            return new ArrayList<>();
        }

        Frame frame = new Frame.Builder().setBitmap(image).build();
        SparseArray<Face> faces = detector.detect(frame);

        List<List<Landmark>> facialLandmarks = new ArrayList<>();
        for (int i = 0; i < faces.size(); i++) {
            Face face = faces.valueAt(i);
            List<Landmark> landmarks = face.getLandmarks();
            facialLandmarks.add(landmarks);
        }

        detector.release();
        return facialLandmarks;
    }
}
