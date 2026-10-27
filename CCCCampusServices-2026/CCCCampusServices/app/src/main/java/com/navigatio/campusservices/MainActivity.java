package com.navigatio.campusservices;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
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
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 100;

    private PreviewView cameraPreview;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ExecutorService cameraExecutor;
    private BarcodeScanner barcodeScanner;

    private boolean qrDetected = false;

    // Campus graph
    private CampusGraph campusGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_security_scan);

        Button btnScanQr = findViewById(R.id.btnScanQr);
        cameraPreview = findViewById(R.id.cameraPreview);

        cameraExecutor = Executors.newSingleThreadExecutor();

        // Load the campus graph
        campusGraph = new CampusGraph();

        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                        .build();

        barcodeScanner =
                BarcodeScanning.getClient(options);

        btnScanQr.setOnClickListener(v -> {
            qrDetected = false;
            startCamera();
        });
    }

    // ==========================================================
    // CAMERA PERMISSION
    // ==========================================================

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void startCamera() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST
            );

            return;
        }

        openCamera();
    }

    // ==========================================================
    // OPEN CAMERA
    // ==========================================================

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void openCamera() {

        cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {

            try {

                ProcessCameraProvider cameraProvider =
                        cameraProviderFuture.get();

                Preview preview =
                        new Preview.Builder().build();

                CameraSelector cameraSelector =
                        CameraSelector.DEFAULT_BACK_CAMERA;

                preview.setSurfaceProvider(
                        cameraPreview.getSurfaceProvider()
                );

                ImageAnalysis imageAnalysis =
                        new ImageAnalysis.Builder()
                                .setBackpressureStrategy(
                                        ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                                )
                                .build();

                imageAnalysis.setAnalyzer(
                        cameraExecutor,
                        imageProxy -> analyzeImage(imageProxy)
                );

                cameraProvider.unbindAll();

                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageAnalysis
                );

            } catch (ExecutionException |
                     InterruptedException e) {

                Toast.makeText(
                        this,
                        "Unable to start camera.",
                        Toast.LENGTH_SHORT
                ).show();
            }

        }, ContextCompat.getMainExecutor(this));
    }

    // ==========================================================
    // ANALYZE QR CODE
    // ==========================================================

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void analyzeImage(ImageProxy imageProxy) {

        if (qrDetected) {
            imageProxy.close();
            return;
        }

        android.media.Image mediaImage =
                imageProxy.getImage();

        if (mediaImage == null) {
            imageProxy.close();
            return;
        }

        InputImage image =
                InputImage.fromMediaImage(
                        mediaImage,
                        imageProxy.getImageInfo()
                                .getRotationDegrees()
                );

        barcodeScanner.process(image)

                .addOnSuccessListener(barcodes -> {

                    if (qrDetected) {
                        return;
                    }

                    for (Barcode barcode : barcodes) {

                        String qrValue =
                                barcode.getRawValue();

                        if (qrValue == null) {
                            continue;
                        }

                        qrValue = qrValue.trim();

                        if (qrValue.isEmpty()) {
                            continue;
                        }

                        // Check the scanned QR
                        validateQrNode(qrValue);

                        if (qrDetected) {
                            break;
                        }
                    }
                })

                .addOnFailureListener(e -> {
                    // Continue scanning the next frame
                })

                .addOnCompleteListener(task -> {
                    imageProxy.close();
                });
    }

    // ==========================================================
    // QR CODE VALIDATION
    // ==========================================================

    private void validateQrNode(String qrValue) {

        if (qrValue == null) {
            return;
        }

        final String scannedQr =
                qrValue.trim();

        // Check every node in CampusGraph
        for (NavigationNode node : campusGraph.getNodes()) {

            String nodeId =
                    node.getId();

            /*
             * Example:
             *
             * N001 -> CCC_NODE_001
             * N002 -> CCC_NODE_002
             * N003 -> CCC_NODE_003
             * N005 -> CCC_NODE_005
             * N015 -> CCC_NODE_015
             */

            String expectedQr =
                    "CCC_NODE_" + nodeId.substring(1);

            if (scannedQr.equalsIgnoreCase(expectedQr)) {

                qrDetected = true;

                final NavigationNode validNode =
                        node;

                runOnUiThread(() -> {

                    Toast.makeText(
                            MainActivity.this,
                            "Valid: "
                                    + validNode.getId()
                                    + " - "
                                    + validNode.getName(),
                            Toast.LENGTH_SHORT
                    ).show();

                    Intent intent =
                            new Intent(
                                    MainActivity.this,
                                    DashboardActivity.class
                            );

                    // Send the actual graph node ID
                    intent.putExtra(
                            "NODE_ID",
                            validNode.getId()
                    );

                    startActivity(intent);

                    finish();
                });

                return;
            }
        }

        // No matching node was found
        final String rejectedQr =
                scannedQr;

        runOnUiThread(() ->
                Toast.makeText(
                        MainActivity.this,
                        "QR code does not match a valid campus location: "
                                + rejectedQr,
                        Toast.LENGTH_LONG
                ).show()
        );
    }

    // ==========================================================
    // CAMERA PERMISSION RESULT
    // ==========================================================

    @Override
    @OptIn(markerClass = ExperimentalGetImage.class)
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
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                openCamera();

            } else {

                Toast.makeText(
                        this,
                        "Camera permission is required.",
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

        if (barcodeScanner != null) {
            barcodeScanner.close();
        }

        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }
}