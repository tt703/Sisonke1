package com.example.tlotlotau;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PdfPageAdapter extends RecyclerView.Adapter<PdfPageAdapter.PdfPageViewHolder> {

    private final Context context;
    private PdfRenderer pdfRenderer;
    private int pageCount;

    public PdfPageAdapter(Context context, File pdfFile) {
        this.context = context;
        openPdf(pdfFile);
    }

    private void openPdf(File pdfFile) {
        try {
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(fileDescriptor);
            pageCount = pdfRenderer.getPageCount();
        } catch (IOException e) {
            e.printStackTrace();
            pageCount = 0;
        }
    }

    @NonNull
    @Override
    public PdfPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pdf_page, parent, false);
        return new PdfPageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfPageViewHolder holder, int position) {
        PdfRenderer.Page page = null;
        try {
            page = pdfRenderer.openPage(position);
            int width = page.getWidth();
            int height = page.getHeight();

            // You can scale the Bitmap as desired. Here we use original dimensions.
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            holder.pdfImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (page != null) {
                page.close();
            }
        }
    }

    @Override
    public int getItemCount() {
        return pageCount;
    }

    public static class PdfPageViewHolder extends RecyclerView.ViewHolder {
        ImageView pdfImageView;

        public PdfPageViewHolder(@NonNull View itemView) {
            super(itemView);
            pdfImageView = itemView.findViewById(R.id.pdfPageView);
        }
    }

    /**
     * Call this method from your activity/fragment onDestroy to properly close the PdfRenderer.
     */
    public void close() {
        if (pdfRenderer != null) {
            pdfRenderer.close();
        }
    }
}
