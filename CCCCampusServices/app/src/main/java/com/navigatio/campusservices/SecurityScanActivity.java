
package com.navigatio.campusservices;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SecurityScanActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 100;

    private PreviewView cameraPreview;

    private ExecutorService cameraExecutor;

    private BarcodeScanner barcodeScanner;

    private boolean qrProcessed = false;
    private boolean invalidQrShown = false;

    // Campus graph
    private CampusGraph campusGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_security_scan);

        cameraPreview = findViewById(R.id.cameraPreview);

        cameraExecutor = Executors.newSingleThreadExecutor();

        // Load the campus graph
        campusGraph = new CampusGraph();

        barcodeScanner = BarcodeScanning.getClient();

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {

            startCamera();

        } else {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST
            );
        }
    }

    // ==========================================================
    // START CAMERA
    // ==========================================================

    private void startCamera() {

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {

            try {

                ProcessCameraProvider cameraProvider =
                        cameraProviderFuture.get();

                // CAMERA PREVIEW
                Preview preview = new Preview.Builder().build();

                preview.setSurfaceProvider(
                        cameraPreview.getSurfaceProvider()
                );

                // QR IMAGE ANALYSIS
                ImageAnalysis imageAnalysis =
                        new ImageAnalysis.Builder()
                                .setBackpressureStrategy(
                                        ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                                )
                                .build();

                imageAnalysis.setAnalyzer(
                        cameraExecutor,
                        this::analyzeImage
                );

                CameraSelector cameraSelector =
                        CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();

                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageAnalysis
                );

            } catch (ExecutionException | InterruptedException e) {

                runOnUiThread(() ->
                        Toast.makeText(
                                SecurityScanActivity.this,
                                "Unable to start camera.",
                                Toast.LENGTH_SHORT
                        ).show()
                );
            }

        }, ContextCompat.getMainExecutor(this));
    }

    // ==========================================================
    // ANALYZE QR IMAGE
    // ==========================================================

    private void analyzeImage(ImageProxy imageProxy) {

        if (qrProcessed) {
            imageProxy.close();
            return;
        }

        android.media.Image cameraImage = imageProxy.getImage();

        if (cameraImage == null) {
            imageProxy.close();
            return;
        }

        InputImage inputImage = InputImage.fromMediaImage(
                cameraImage,
                imageProxy.getImageInfo().getRotationDegrees()
        );

        barcodeScanner.process(inputImage)

                .addOnSuccessListener(barcodes -> {

                    if (qrProcessed) {
                        return;
                    }

                    for (Barcode barcode : barcodes) {

                        String rawValue = barcode.getRawValue();

                        if (rawValue == null) {
                            continue;
                        }

                        String qrValue = rawValue.trim();

                        if (qrValue.isEmpty()) {
                            continue;
                        }

                        checkQrCode(qrValue);

                        if (qrProcessed) {
                            break;
                        }
                    }
                })

                .addOnFailureListener(e -> {
                    // Continue scanning.
                })

                .addOnCompleteListener(task -> imageProxy.close());
    }

    // ==========================================================
    // CHECK QR CODE AGAINST CAMPUS GRAPH
    // ==========================================================

    private void checkQrCode(String qrValue) {

        if (qrProcessed) {
            return;
        }

        for (NavigationNode node : campusGraph.getNodes()) {

            // Ignore internal waypoints that do not have QR codes.
            if (!node.isQrEnabled()) {
                continue;
            }

            String nodeId = node.getId();

            // Prevent invalid IDs from causing substring errors.
            if (nodeId == null
                    || nodeId.length() < 2
                    || !nodeId.startsWith("N")) {
                continue;
            }

            // Example: N001 -> CCC_NODE_001
            String expectedQr = "CCC_NODE_" + nodeId.substring(1);

            if (qrValue.equalsIgnoreCase(expectedQr)) {

                qrProcessed = true;

                final String validNodeId = nodeId;
                final String validNodeName = node.getName();

                runOnUiThread(() -> {

                    Toast.makeText(
                            SecurityScanActivity.this,
                            "Valid QR: " + validNodeId
                                    + " - " + validNodeName,
                            Toast.LENGTH_SHORT
                    ).show();

                    Intent intent = new Intent(
                            SecurityScanActivity.this,
                            DashboardActivity.class
                    );

                    // Send the scanned starting node to the dashboard.
                    intent.putExtra("NODE_ID", validNodeId);

                    startActivity(intent);
                    finish();
                });

                return;
            }
        }

        // Display the invalid QR message only once per continuous scan.
        if (!invalidQrShown) {

            invalidQrShown = true;

            runOnUiThread(() -> {

                Toast.makeText(
                        SecurityScanActivity.this,
                        "Invalid campus QR code.",
                        Toast.LENGTH_SHORT
                ).show();

            });
        }
    }

    // ==========================================================
    // CAMERA PERMISSION RESULT
    // ==========================================================

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == CAMERA_PERMISSION_REQUEST) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                startCamera();

            } else {

                Toast.makeText(
                        this,
                        "Camera permission is required to scan the QR code.",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    // ==========================================================
    // CLEAN UP
    // ==========================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }

        if (barcodeScanner != null) {
            barcodeScanner.close();
        }
    }
}
