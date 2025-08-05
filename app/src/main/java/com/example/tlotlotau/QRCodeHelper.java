package com.example.tlotlotau;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.WriterException;

public class QRCodeHelper {
    private static final String TAG = "QRCodeHelper";

    public static Bitmap generateQRCode(String content, int width, int height) {
        if (content == null || content.isEmpty()) {
            Log.e(TAG, "QR Code content cannot be empty.");
            return null;
        }
        if (width <= 0 || height <= 0) {
            Log.e(TAG, "Invalid QR code dimensions.");
            return null;
        }

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    content, BarcodeFormat.QR_CODE, width, height
            );

            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            Log.e(TAG, "Error generating QR Code: " + e.getMessage(), e);
            return null;
        }
    }
}
