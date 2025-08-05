package com.example.tlotlotau;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;
    private OnProductActionListener actionListener;

    public interface OnProductActionListener {
        void onEditProduct(Product product);
        void onDeleteProduct(Product product);
        void onPrintQrCode(Product product);
    }

    public ProductAdapter(Context context, List<Product> products, OnProductActionListener listener) {
        this.context = context;
        this.productList = products;
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(holder.getAdapterPosition());
        holder.tvProductName.setText(product.getProductName());
        holder.tvProductPrice.setText("Price: R" + product.getProductPrice());
        holder.tvProductQuantity.setText("Quantity: " + product.getProductQuantity());

        holder.btnEditProduct.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditProduct(product);
            } else {
                Intent intent = new Intent(context, EditProductActivity.class);
                intent.putExtra("product", product.getProductId());
                context.startActivity(intent);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDeleteProduct(product);
            }
        });

        holder.btnPrintQrCode.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onPrintQrCode(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvProductQuantity;
        Button btnEditProduct, btnDelete, btnPrintQrCode;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            btnEditProduct = itemView.findViewById(R.id.btnEditProduct);
            btnDelete = itemView.findViewById(R.id.btnDeleteProduct);
            btnPrintQrCode = itemView.findViewById(R.id.btnPrintQrCode);
        }
    }



    private Bitmap getProductQRCode(String productId) {
        // Placeholder for QR code generation logic
        return null; // Replace with actual QR code generation
    }

    private void printBitmap(Bitmap bitmap, String fileName) {
        // Placeholder for printing logic
    }

    public void updateProductList(List<Product> newProductList) {
        productList.clear();
        productList.addAll(newProductList);
        notifyDataSetChanged();
    }
}