package com.example.tlotlotau.Documents;

import android.content.Context;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.IOException;

public class PdfUtils {

    /**
     * Returns the number of pages in the given PDF file using Android's built-in PdfRenderer.
     *
     * @param context the context
     * @param pdfFile the PDF file
     * @return the page count; returns 0 if an error occurs
     */
    public static int getPageCount(Context context, File pdfFile) {
        PdfRenderer pdfRenderer = null;
        ParcelFileDescriptor fileDescriptor = null;
        try {
            // Open the file in read-only mode
            fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(fileDescriptor);
            return pdfRenderer.getPageCount();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (pdfRenderer != null) {
                pdfRenderer.close();
            }
            if (fileDescriptor != null) {
                try {
                    fileDescriptor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
