package com.example.tlotlotau.Database;

import android.content.Context;
import android.database.Cursor;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.tlotlotau.Customer.Customer;
import com.example.tlotlotau.Documents.Estimate.Estimate;
import com.example.tlotlotau.Documents.Invoice.Invoice;
import com.example.tlotlotau.Inventory.Product;
import com.example.tlotlotau.Sales.SaleItem;
import com.example.tlotlotau.Sales.SaleRecord;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.SetOptions;
import com.google.android.gms.tasks.OnCompleteListener; // Added import
import com.google.android.gms.tasks.Task; // Added import

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class SyncManager {
    private static final String TAG = "SyncManager";

    private final Context ctx;
    private final FirebaseFirestore db;
    private final DatabaseHelper localDb;
    private final String businessId;

    private ListenerRegistration customersListener;
    private ListenerRegistration productsListener;
    private ListenerRegistration categoriesListener;
    private ListenerRegistration salesListener;
    private ListenerRegistration invoicesListener;
    private ListenerRegistration estimatesListener;

    // simple in-memory locks to prevent concurrent pushes for same local id (keyed by string)
    private final ConcurrentHashMap<String, Boolean> inProgressPushes = new ConcurrentHashMap<>();

    // per-device id to identify our own cloud writes (prevents echo processing)
    private final String clientId;

    public SyncManager(Context ctx) {
        this.ctx = ctx.getApplicationContext();
        db = FirebaseFirestore.getInstance();
        localDb = new DatabaseHelper(this.ctx);
        SharedPreferences prefs = this.ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        businessId = prefs.getString("businessId", null);

        // persistent client id
        String cid = prefs.getString("client_id", null);
        if (cid == null) {
            cid = UUID.randomUUID().toString();
            prefs.edit().putString("client_id", cid).apply();
        }
        clientId = cid;
    }

    // Flexible boolean reader (accepts Boolean, Number (0/1), "0"/"1"/"true"/"false")
    private boolean readBooleanFlexible(@NonNull DocumentSnapshot ds, @NonNull String key) {
        if (!ds.contains(key)) return false;
        Object val = ds.get(key);
        if (val instanceof Boolean) return (Boolean) val;
        if (val instanceof Number) return ((Number) val).intValue() != 0;
        if (val instanceof String) {
            String s = ((String) val).trim().toLowerCase();
            if (s.isEmpty()) return false;
            if (s.equals("true") || s.equals("1")) return true;
            if (s.equals("false") || s.equals("0")) return false;
            try { return Integer.parseInt(s) != 0; } catch (Exception ignored) {}
            return Boolean.parseBoolean(s);
        }
        return false;
    }

    // Flexible numeric readers
    private Long readLongFlexible(@NonNull DocumentSnapshot ds, @NonNull String key, Long fallback) {
        if (!ds.contains(key)) return fallback;
        Object val = ds.get(key);
        if (val instanceof Number) return ((Number) val).longValue();
        if (val instanceof String) {
            try { return Long.parseLong(((String) val).trim()); } catch (Exception ignored) {}
            try { return (long) Double.parseDouble(((String) val).trim()); } catch (Exception ignored) {}
        }
        return fallback;
    }

    private Double readDoubleFlexible(@NonNull DocumentSnapshot ds, @NonNull String key, Double fallback) {
        if (!ds.contains(key)) return fallback;
        Object val = ds.get(key);
        if (val instanceof Number) return ((Number) val).doubleValue();
        if (val instanceof String) {
            try { return Double.parseDouble(((String) val).trim()); } catch (Exception ignored) {}
        }
        return fallback;
    }

    private Integer readIntFlexible(@NonNull DocumentSnapshot ds, @NonNull String key, Integer fallback) {
        Long l = readLongFlexible(ds, key, fallback == null ? null : fallback.longValue());
        return l == null ? fallback : l.intValue();
    }

    // start / stop
    public void start() {
        if (businessId == null || businessId.trim().isEmpty()) {
            Log.w(TAG, "no businessId - sync won't start");
            return;
        }
        listenCustomers();
        listenCategories();
        listenProducts();
        listenSales();
        listenInvoices();
        listenEstimates();

        pushUnsyncedCustomers();
        pushUnsyncedProducts();
        pushUnsyncedCategories();
        pushUnsyncedSales();
        pushUnsyncedInvoices();
        pushUnsyncedEstimates();
    }

    // ... existing code ...

    public void stop() {
        try { if (customersListener != null) customersListener.remove(); } catch (Exception ignored) {}
        try { if (productsListener != null) productsListener.remove(); } catch (Exception ignored) {}
        try { if (categoriesListener != null) categoriesListener.remove(); } catch (Exception ignored) {}
        try { if (salesListener != null) salesListener.remove(); } catch (Exception ignored) {}
        try { if (invoicesListener != null) invoicesListener.remove(); } catch (Exception ignored) {}
        try { if (estimatesListener != null) estimatesListener.remove(); } catch (Exception ignored) {}

        // FIX: Explicitly close the database helper owned by this SyncManager
        try {
            if (localDb != null) {
                localDb.close();
            }
        } catch (Exception ignored) {
            Log.w(TAG, "Error closing localDb in SyncManager", ignored);
        }
    }

    // ... rest of the file ...

    // ---------------------------
    // Firestore --> Local
    // ---------------------------

    // Helper: skip apply if this doc was lastModifiedBy our clientId (prevents processing our own writes)
    private boolean isModifiedByThisClient(DocumentSnapshot ds) {
        if (!ds.contains("lastModifiedBy")) return false;
        Object v = ds.get("lastModifiedBy");
        if (v == null) return false;
        return clientId.equals(String.valueOf(v));
    }

    private void listenCustomers() {
        try {
            CollectionReference ref = db.collection("businesses").document(businessId).collection("customers");
            customersListener = ref.addSnapshotListener(MetadataChanges.INCLUDE, (snapshots, e) -> {
                if (e != null) { Log.w(TAG, "customers listen error", e); return; }
                if (snapshots == null) return;

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    DocumentSnapshot ds = dc.getDocument();
                    String cloudId = ds.getId();
                    // skip docs that we wrote (echo)
                    if (isModifiedByThisClient(ds)) continue;

                    try {
                        long cloudUpdated = readLongFlexible(ds, "updatedAt", System.currentTimeMillis());

                        switch (dc.getType()) {
                            case ADDED:
                            case MODIFIED:
                                boolean deleted = readBooleanFlexible(ds, "deleted");
                                if (deleted) {
                                    try { localDb.deleteCustomerByCloudId(cloudId); } catch (Exception ex) { Log.w(TAG, "deleteCustomerByCloudId failed", ex); }
                                } else {
                                    String name = ds.getString("name");
                                    String phone = ds.getString("phone");
                                    String email = ds.getString("email");
                                    String address = ds.getString("address");
                                    // Use safe reading methods
                                    String dateCreated = ds.contains("dateCreated") ? String.valueOf(ds.get("dateCreated")) : null;
                                    int numEstimates = readIntFlexible(ds, "numEstimates", 0);
                                    int numInvoices = readIntFlexible(ds, "numInvoices", 0);
                                    double totalAmount = readDoubleFlexible(ds, "totalAmount", 0.0);

                                    // call existing DB helper (should be idempotent) - DatabaseHelper has null check now
                                    localDb.upsertCustomerFromCloud(
                                            cloudId, name, phone, email, address,
                                            dateCreated, numEstimates, numInvoices, totalAmount, cloudUpdated
                                    );
                                }
                                break;
                            case REMOVED:
                                // remote remove -> delete local (safe wrapped)
                                try { localDb.deleteCustomerByCloudId(cloudId); } catch (Exception ex) { Log.w(TAG, "deleteCustomerByCloudId (removed) failed", ex); }
                                break;
                        }
                    } catch (Exception exInner) {
                        Log.w(TAG, "processing customer doc failed for id=" + cloudId, exInner);
                    }
                }
            });
        } catch (Exception ex) {
            Log.w(TAG, "listenCustomers exception", ex);
        }
    }

    private void listenProducts() {
        try {
            CollectionReference ref = db.collection("businesses").document(businessId).collection("products");
            productsListener = ref.addSnapshotListener(MetadataChanges.INCLUDE, (snapshots, e) -> {
                if (e != null) { Log.w(TAG, "products listen error", e); return; }
                if (snapshots == null) return;

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    DocumentSnapshot ds = dc.getDocument();
                    String cloudId = ds.getId();
                    if (isModifiedByThisClient(ds)) continue;

                    try {
                        long cloudUpdated = readLongFlexible(ds, "updatedAt", System.currentTimeMillis());

                        switch (dc.getType()) {
                            case ADDED:
                            case MODIFIED:
                                boolean deleted = readBooleanFlexible(ds, "deleted");
                                if (deleted) {
                                    try { localDb.deleteProductByCloudId(cloudId); } catch (Exception ex) { Log.w(TAG, "deleteProductByCloudId failed", ex); }
                                } else {
                                    String name = ds.getString("name");
                                    double price = readDoubleFlexible(ds, "price", 0.0);
                                    int qty = readIntFlexible(ds, "quantity", 0);
                                    localDb.upsertProductFromCloud(cloudId, name, price, qty, cloudUpdated);
                                }
                                break;
                            case REMOVED:
                                try { localDb.deleteProductByCloudId(cloudId); } catch (Exception ex) { Log.w(TAG, "deleteProductByCloudId (removed) failed", ex); }
                                break;
                        }
                    } catch (Exception exInner) {
                        Log.w(TAG, "processing product doc failed for id=" + cloudId, exInner);
                    }
                }
            });
        } catch (Exception ex) {
            Log.w(TAG, "listenProducts exception", ex);
        }
    }

    private void listenCategories() {
        try {
            CollectionReference ref = db.collection("businesses").document(businessId).collection("categories");
            categoriesListener = ref.addSnapshotListener(MetadataChanges.INCLUDE, (snapshots, e) -> {
                if (e != null) { Log.w(TAG, "categories listen error", e); return; }
                if (snapshots == null) return;

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    DocumentSnapshot ds = dc.getDocument();
                    String cloudId = ds.getId();
                    if (isModifiedByThisClient(ds)) continue;

                    try {
                        long cloudUpdated = readLongFlexible(ds, "updatedAt", System.currentTimeMillis());
                        switch (dc.getType()) {
                            case ADDED:
                            case MODIFIED:
                                boolean deleted = readBooleanFlexible(ds, "deleted");
                                if (deleted) {
                                    try { localDb.deleteCategoryByCloudId(cloudId); } catch (Exception ex) { Log.w(TAG, "deleteCategoryByCloudId failed", ex); }
                                } else {
                                    String name = ds.getString("name");
                                    localDb.upsertCategoryFromCloud(cloudId, name, cloudUpdated);
                                }
                                break;
                            case REMOVED:
                                try { localDb.deleteCategoryByCloudId(cloudId); } catch (Exception ex) { Log.w(TAG, "deleteCategoryByCloudId (removed) failed", ex); }
                                break;
                        }
                    } catch (Exception exInner) {
                        Log.w(TAG, "processing category doc failed for id=" + cloudId, exInner);
                    }
                }
            });
        } catch (Exception ex) {
            Log.w(TAG, "listenCategories exception", ex);
        }
    }

    private void listenSales() {
        try {
            CollectionReference ref = db.collection("businesses").document(businessId).collection("sales");
            salesListener = ref.addSnapshotListener(MetadataChanges.INCLUDE, (snapshots, e) -> {
                if (e != null) { Log.w(TAG, "sales listen error", e); return; }
                if (snapshots == null) return;

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    DocumentSnapshot ds = dc.getDocument();
                    String cloudId = ds.getId();
                    if (isModifiedByThisClient(ds)) continue;

                    try {
                        long cloudUpdated = readLongFlexible(ds, "updatedAt", System.currentTimeMillis());

                        switch (dc.getType()) {
                            case ADDED:
                            case MODIFIED:
                                boolean deleted = readBooleanFlexible(ds, "deleted");
                                if (deleted) {
                                    try { localDb.deleteSaleByCloudId(cloudId); } catch (Exception ex) { Log.w(TAG, "deleteSaleByCloudId failed", ex); }
                                } else {
                                    String userId = ds.getString("userId");
                                    String userName = ds.getString("userName");
                                    double subtotal = readDoubleFlexible(ds, "subtotal", 0.0);
                                    double tax = readDoubleFlexible(ds, "tax", 0.0);
                                    double total = readDoubleFlexible(ds, "total", 0.0);
                                    String paymentMethod = ds.getString("paymentMethod");
                                    Long ts = readLongFlexible(ds, "timestamp", null);

                                    localDb.upsertSaleFromCloud(cloudId, userId, userName, subtotal, tax, total, paymentMethod, ts, cloudUpdated);

                                    // fetch items subcollection defensively
                                    ds.getReference().collection("items").get().addOnSuccessListener(qsnap -> {
                                        try {
                                            for (DocumentSnapshot itemDoc : qsnap.getDocuments()) {
                                                try {
                                                    // skip our own item writes as well
                                                    if (isModifiedByThisClient(itemDoc)) continue;

                                                    String itemCloudId = itemDoc.getId();
                                                    boolean itemDeleted = readBooleanFlexible(itemDoc, "deleted");
                                                    long itemUpdated = readLongFlexible(itemDoc, "updatedAt", System.currentTimeMillis());
                                                    String productName = itemDoc.getString("productName");
                                                    double unitPrice = readDoubleFlexible(itemDoc, "unitPrice", 0.0);
                                                    int qty = readIntFlexible(itemDoc, "quantity", 0);
                                                    double lineTotal = readDoubleFlexible(itemDoc, "lineTotal", unitPrice * qty);

                                                    if (itemDeleted) {
                                                        try { localDb.deleteSaleItemByCloudId(itemCloudId); } catch (Exception ex) { Log.w(TAG, "deleteSaleItemByCloudId failed", ex); }
                                                    } else {
                                                        try { localDb.upsertSaleItemFromCloud(itemCloudId, cloudId, productName, qty, unitPrice, lineTotal, itemUpdated); }
                                                        catch (Exception ex) { Log.w(TAG, "upsertSaleItemFromCloud failed", ex); }
                                                    }
                                                } catch (Exception innerItemEx) {
                                                    Log.w(TAG, "processing single sale item failed", innerItemEx);
                                                }
                                            }
                                        } catch (Exception ex2) {
                                            Log.w(TAG, "processing sale items failed", ex2);
                                        }
                                    }).addOnFailureListener(err -> Log.w(TAG, "failed to fetch sale items", err));
                                }
                                break;
                            case REMOVED:
                                try { localDb.deleteSaleByCloudId(cloudId); } catch (Exception ex) { Log.w(TAG, "deleteSaleByCloudId (removed) failed", ex); }
                                break;
                        }
                    } catch (Exception exInner) {
                        Log.w(TAG, "processing sale doc failed for id=" + cloudId, exInner);
                    }
                }
            });
        } catch (Exception ex) {
            Log.w(TAG, "listenSales exception", ex);
        }
    }

    private void listenInvoices() {
        try {
            CollectionReference ref = db.collection("businesses").document(businessId).collection("invoices");
            invoicesListener = ref.addSnapshotListener(MetadataChanges.INCLUDE, (snapshots, e) -> {
                if (e != null) { Log.w(TAG, "invoices listen error", e); return; }
                if (snapshots == null) return;

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    DocumentSnapshot ds = dc.getDocument();
                    String cloudId = ds.getId();
                    if (isModifiedByThisClient(ds)) continue;

                    try {
                        long cloudUpdated = readLongFlexible(ds, "updatedAt", System.currentTimeMillis());

                        switch (dc.getType()) {
                            case ADDED:
                            case MODIFIED:
                                boolean deleted = readBooleanFlexible(ds, "deleted");
                                if (deleted) {
                                    try { localDb.deleteInvoiceByCloudId(cloudId); } catch (Exception ex) { Log.w(TAG, "deleteInvoiceByCloudId failed", ex); }
                                } else {
                                    String customerName = ds.getString("customerName");
                                    String customerAddress = ds.getString("customerAddress");
                                    String customerContact = ds.getString("customerContact");
                                    String customerEmail = ds.getString("customerEmail");
                                    String itemDetails = ds.getString("itemDetails");
                                    double total = readDoubleFlexible(ds, "totalAmount", 0.0);
                                    String filePath = ds.getString("filePath");
                                    Long dateCreated = readLongFlexible(ds, "dateCreated", null);

                                    localDb.upsertInvoiceFromCloud(cloudId, customerName, customerAddress, customerContact, customerEmail, itemDetails, total, filePath, dateCreated, cloudUpdated);
                                }
                                break;
                            case REMOVED:
                                try { localDb.deleteInvoiceByCloudId(cloudId); } catch (Exception ex) { Log.w(TAG, "deleteInvoiceByCloudId (removed) failed", ex); }
                                break;
                        }
                    } catch (Exception exInner) {
                        Log.w(TAG, "processing invoice doc failed for id=" + cloudId, exInner);
                    }
                }
            });
        } catch (Exception ex) {
            Log.w(TAG, "listenInvoices exception", ex);
        }
    }

    private void listenEstimates() {
        try {
            CollectionReference ref = db.collection("businesses").document(businessId).collection("estimates");
            estimatesListener = ref.addSnapshotListener(MetadataChanges.INCLUDE, (snapshots, e) -> {
                if (e != null) { Log.w(TAG, "estimates listen error", e); return; }
                if (snapshots == null) return;

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    DocumentSnapshot ds = dc.getDocument();
                    String cloudId = ds.getId();
                    if (isModifiedByThisClient(ds)) continue;

                    try {
                        long cloudUpdated = readLongFlexible(ds, "updatedAt", System.currentTimeMillis());

                        switch (dc.getType()) {
                            case ADDED:
                            case MODIFIED:
                                boolean deleted = readBooleanFlexible(ds, "deleted");
                                if (deleted) {
                                    try { localDb.deleteEstimateByCloudId(cloudId); } catch (Exception ex) { Log.w(TAG, "deleteEstimateByCloudId failed", ex); }
                                } else {
                                    String customerName = ds.getString("customerName");
                                    String customerAddress = ds.getString("customerAddress");
                                    String customerContact = ds.getString("customerContact");
                                    String customerEmail = ds.getString("customerEmail");
                                    String itemDetails = ds.getString("itemDetails");
                                    double total = readDoubleFlexible(ds, "totalAmount", 0.0);
                                    String filePath = ds.getString("filePath");
                                    Long dateCreated = readLongFlexible(ds, "dateCreated", null);

                                    localDb.upsertEstimateFromCloud(cloudId, customerName, customerAddress, customerContact, customerEmail, itemDetails, total, filePath, dateCreated, cloudUpdated);
                                }
                                break;
                            case REMOVED:
                                try { localDb.deleteEstimateByCloudId(cloudId); } catch (Exception ex) { Log.w(TAG, "deleteEstimateByCloudId (removed) failed", ex); }
                                break;
                        }
                    } catch (Exception exInner) {
                        Log.w(TAG, "processing estimate doc failed for id=" + cloudId, exInner);
                    }
                }
            });
        } catch (Exception ex) {
            Log.w(TAG, "listenEstimates exception", ex);
        }
    }

    // ---------------------------
    // Local -> Firestore (pushers)
    // ---------------------------

    // Customers
    public void pushLocalCustomerToCloud(long localId) {
        final String lockKey = "customer:" + localId;
        if (inProgressPushes.putIfAbsent(lockKey, true) != null) return; // already in progress

        Customer c;
        try {
            c = localDb.getCustomerByLocalId(localId);
            if (c == null) {
                inProgressPushes.remove(lockKey);
                return;
            }
        } catch (Exception ex) {
            Log.w(TAG, "pushLocalCustomerToCloud: failed to get local customer", ex);
            inProgressPushes.remove(lockKey);
            return;
        }

        Map<String,Object> doc = new HashMap<>();
        doc.put("name", c.getName());
        doc.put("phone", c.getPhone());
        doc.put("email", c.getEmail());
        doc.put("address", c.getAddress());
        doc.put("dateCreated", c.getDateCreated());
        doc.put("numEstimates", c.getNumEstimates());
        doc.put("numInvoices", c.getNumInvoices());
        doc.put("totalAmount", c.getTotalAmount());
        long now = System.currentTimeMillis();
        doc.put("updatedAt", now);
        doc.put("lastModifiedBy", clientId); // tag write
        doc.put("deleted", 0);

        final String existingCloudId = c.getCloudId();
        CollectionReference ref = db.collection("businesses").document(businessId).collection("customers");

        com.google.android.gms.tasks.Task<Void> setTask;
        final long finalLocalId = localId; // capture final localId for use inside closure

        // Common OnCompleteListener to release the lock regardless of success or failure
        OnCompleteListener<Void> onComplete = task -> inProgressPushes.remove(lockKey);

        if (existingCloudId == null || existingCloudId.trim().isEmpty()) {
            DocumentReference docRef = ref.document();
            final String newCloudId = docRef.getId();
            setTask = docRef.set(doc, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                try { localDb.markCustomerSyncedByLocalId(finalLocalId, newCloudId, now); } catch (Exception ex) { Log.w(TAG, "markCustomerSyncedByLocalId failed", ex); }
            });
        } else {
            final String cloudIdFinal = existingCloudId;
            setTask = ref.document(cloudIdFinal).set(doc, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                try { localDb.markCustomerSyncedByLocalId(finalLocalId, cloudIdFinal, now); } catch (Exception ex) { Log.w(TAG, "markCustomerSyncedByLocalId failed", ex); }
            });
        }

        // Attach the lock-releasing listener to the final task
        setTask.addOnCompleteListener(onComplete);
    }

    private void pushUnsyncedCustomers() {
        try (Cursor cur = localDb.getUnsyncedCustomersCursor()) {
            if (cur == null) return;
            while (cur.moveToNext()) {
                long localId = cur.getLong(cur.getColumnIndexOrThrow("_id"));
                pushLocalCustomerToCloud(localId);
            }
        } catch (Exception ex) {
            Log.w(TAG, "pushUnsyncedCustomers", ex);
        }
    }

    // Products
    public void pushLocalProductToCloud(int localId) {
        final String lockKey = "product:" + localId;
        if (inProgressPushes.putIfAbsent(lockKey, true) != null) return;
        try {
            Product p = localDb.getProductById(localId);
            if (p == null) {
                inProgressPushes.remove(lockKey);
                return;
            }

            Map<String,Object> doc = new HashMap<>();
            doc.put("name", p.getProductName());
            doc.put("price", p.getProductPrice());
            doc.put("quantity", p.getProductQuantity());
            doc.put("categoryId", p.getCategoryCId());
            long now = System.currentTimeMillis();
            doc.put("updatedAt", now);
            doc.put("lastModifiedBy", clientId);
            doc.put("deleted", 0);

            final String existingCloudId = p.getCloudId();
            CollectionReference ref = db.collection("businesses").document(businessId).collection("products");

            com.google.android.gms.tasks.Task<Void> setTask;

            // Common OnCompleteListener to release the lock
            OnCompleteListener<Void> onComplete = task -> inProgressPushes.remove(lockKey);


            if (existingCloudId == null || existingCloudId.trim().isEmpty()) {
                DocumentReference docRef = ref.document();
                final String newCloudId = docRef.getId();
                setTask = docRef.set(doc, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                    try { localDb.markProductSyncedByLocalId(localId, newCloudId, now); } catch (Exception ex) { Log.w(TAG, "markProductSyncedByLocalId failed", ex); }
                });
            } else {
                final String cloudIdFinal = existingCloudId;
                setTask = ref.document(cloudIdFinal).set(doc, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                    try { localDb.markProductSyncedByLocalId(localId, cloudIdFinal, now); } catch (Exception ex) { Log.w(TAG, "markProductSyncedByLocalId failed", ex); }
                });
            }
            setTask.addOnCompleteListener(onComplete);
        } catch (Exception ex) {
            Log.w(TAG, "pushLocalProductToCloud exception", ex);
            inProgressPushes.remove(lockKey);
        }
    }

    private void pushUnsyncedProducts() {
        try (Cursor cur = localDb.getUnsyncedProductsCursor()) {
            if (cur == null) return;
            while (cur.moveToNext()) {
                int localId = cur.getInt(cur.getColumnIndexOrThrow("_id"));
                pushLocalProductToCloud(localId);
            }
        } catch (Exception ex) {
            Log.w(TAG, "pushUnsyncedProducts", ex);
        }
    }

    // Categories
    public void pushLocalCategoryToCloud(long localId) {
        final String lockKey = "category:" + localId;
        if (inProgressPushes.putIfAbsent(lockKey, true) != null) return;
        try {
            Cursor c = localDb.getReadableDatabase().query("categories", null, "_id = ?", new String[]{String.valueOf(localId)}, null, null, null);
            if (c == null) {
                inProgressPushes.remove(lockKey);
                return;
            }
            try {
                if (!c.moveToFirst()) {
                    inProgressPushes.remove(lockKey);
                    return;
                }
                String name = c.getString(c.getColumnIndexOrThrow("name"));
                long now = System.currentTimeMillis();
                Map<String, Object> doc = new HashMap<>();
                doc.put("name", name);
                doc.put("updatedAt", now);
                doc.put("lastModifiedBy", clientId);
                doc.put("deleted", 0);

                String cloudId = c.getString(c.getColumnIndexOrThrow("cloud_id"));
                CollectionReference ref = db.collection("businesses").document(businessId).collection("categories");

                com.google.android.gms.tasks.Task<Void> setTask;
                final long finalLocalId = localId; // capture final localId for use inside closure

                // Common OnCompleteListener to release the lock
                OnCompleteListener<Void> onComplete = task -> inProgressPushes.remove(lockKey);

                if (cloudId == null || cloudId.trim().isEmpty()) {
                    DocumentReference docRef = ref.document();
                    String newCloudId = docRef.getId();
                    setTask = docRef.set(doc, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> localDb.markCategorySyncedByLocalId(finalLocalId, newCloudId, now));
                } else {
                    setTask = ref.document(cloudId).set(doc, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> localDb.markCategorySyncedByLocalId(finalLocalId, cloudId, now));
                }
                setTask.addOnCompleteListener(onComplete);
            } finally {
                c.close();
            }
        } catch (Exception ex) {
            Log.w(TAG, "pushLocalCategoryToCloud exception", ex);
            inProgressPushes.remove(lockKey);
        }
    }

    private void pushUnsyncedCategories() {
        try (Cursor cur = localDb.getUnsyncedCategoriesCursor()) {
            if (cur == null) return;
            while (cur.moveToNext()) {
                long localId = cur.getLong(cur.getColumnIndexOrThrow("_id"));
                pushLocalCategoryToCloud(localId);
            }
        } catch (Exception ex) {
            Log.w(TAG, "pushUnsyncedCategories", ex);
        }
    }

    public void pushLocalSaleToCloud(long localSaleId) {
        final String lockKey = "sale:" + localSaleId;
        if (inProgressPushes.putIfAbsent(lockKey, true) != null) return;
        try {
            SaleRecord sale = localDb.getSaleByLocalId(localSaleId);
            if (sale == null) {
                inProgressPushes.remove(lockKey);
                return;
            }
            long now = System.currentTimeMillis();

            Map<String,Object> saleDoc = new HashMap<>();
            saleDoc.put("userId", sale.getUserId());
            saleDoc.put("userName", sale.getUserName());
            saleDoc.put("subtotal", sale.getSubtotal());
            saleDoc.put("tax", sale.getTax());
            saleDoc.put("total", sale.getTotal());
            saleDoc.put("paymentMethod", sale.getPaymentMethod());
            saleDoc.put("timestamp", sale.getTimestamp() == null ? now : Long.parseLong(sale.getTimestamp()));
            saleDoc.put("updatedAt", now);
            saleDoc.put("lastModifiedBy", clientId);
            saleDoc.put("deleted", 0);

            String cloudId = sale.getCloudId();
            CollectionReference salesRef = db.collection("businesses").document(businessId).collection("sales");
            DocumentReference saleDocRef;
            if (cloudId == null || cloudId.trim().isEmpty()) {
                saleDocRef = salesRef.document();
                cloudId = saleDocRef.getId();
            } else {
                saleDocRef = salesRef.document(cloudId);
            }

            final String finalCloudId = cloudId;
            final long finalLocalSaleId = localSaleId;

            // Define the items push task as a Runnable
            Runnable pushItemsTask = () -> {
                List<SaleItem> items = localDb.getSaleItemsForSale(finalLocalSaleId);
                for (SaleItem it : items) {
                    Map<String,Object> itemDoc = new HashMap<>();
                    itemDoc.put("productId", it.product != null ? String.valueOf(it.product.getProductId()) : null);
                    itemDoc.put("productName", it.product != null ? it.product.getProductName() : "");
                    itemDoc.put("quantity", it.quantity);
                    itemDoc.put("unitPrice", it.product != null ? it.product.getProductPrice() : 0.0);
                    itemDoc.put("lineTotal", it.lineTotal());
                    itemDoc.put("updatedAt", now);
                    itemDoc.put("lastModifiedBy", clientId);
                    itemDoc.put("deleted", 0);

                    String itemCloudId = it.getCloudId();
                    DocumentReference itemDocRef;
                    if (itemCloudId == null || itemCloudId.trim().isEmpty()) {
                        itemDocRef = saleDocRef.collection("items").document();
                        itemCloudId = itemDocRef.getId();
                    } else {
                        itemDocRef = saleDocRef.collection("items").document(itemCloudId);
                    }

                    final String finalItemCloudId = itemCloudId;
                    long finalNow = now;
                    // Note: Item sync is also asynchronous, but since sale push won't proceed until this is done, we don't need a lock
                    itemDocRef.set(itemDoc, SetOptions.merge()).addOnSuccessListener(aVoid2 -> {
                        try { localDb.markSaleItemSyncedByLocalId(it.getLocalId(), finalItemCloudId, finalNow); } catch (Exception ex) { Log.w(TAG, "markSaleItemSyncedByLocalId failed", ex); }
                    }).addOnFailureListener(e -> Log.w(TAG, "pushLocalSaleItemToCloud failed", e));
                }
            };


            // Main sale header push task
            saleDocRef.set(saleDoc, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                        pushItemsTask.run(); // Run item push on success
                        // mark sale synced
                        try { localDb.markSaleSyncedByLocalId(finalLocalSaleId, finalCloudId, now); } catch (Exception ex) { Log.w(TAG, "markSaleSyncedByLocalId failed", ex); }
                    }).addOnFailureListener(e -> Log.w(TAG, "pushLocalSaleToCloud failed", e))
                    .addOnCompleteListener(task -> inProgressPushes.remove(lockKey)); // Release lock on completion
        } catch (Exception ex) {
            Log.w(TAG, "pushLocalSaleToCloud exception", ex);
            inProgressPushes.remove(lockKey);
        }
    }

    private void pushUnsyncedSales() {
        try (Cursor cur = localDb.getUnsyncedSalesCursor()) {
            if (cur == null) return;
            while (cur.moveToNext()) {
                long localId = cur.getLong(cur.getColumnIndexOrThrow("_id"));
                pushLocalSaleToCloud(localId);
            }
        } catch (Exception ex) {
            Log.w(TAG, "pushUnsyncedSales", ex);
        }
    }

    // Invoices
    public void pushLocalInvoiceToCloud(long localId) {
        final String lockKey = "invoice:" + localId;
        if (inProgressPushes.putIfAbsent(lockKey, true) != null) return;
        try {
            Invoice inv = localDb.getInvoiceByLocalId(localId);
            if (inv == null) {
                inProgressPushes.remove(lockKey);
                return;
            }

            Map<String,Object> doc = new HashMap<>();
            doc.put("customerName", inv.getCustomerName());
            doc.put("customerAddress", inv.getCustomerAddress());
            doc.put("customerContact", inv.getCustomerContact());
            doc.put("customerEmail", inv.getCustomerEmail());
            doc.put("itemDetails", inv.getItemDetails());
            doc.put("totalAmount", inv.getTotalAmount());
            doc.put("filePath", inv.getFilePath());
            long now = System.currentTimeMillis();
            doc.put("updatedAt", now);
            doc.put("lastModifiedBy", clientId);
            if (inv.getTimestamp() != null) {
                try { doc.put("dateCreated", Long.parseLong(inv.getTimestamp())); } catch (Exception ignored) {}
            }
            doc.put("deleted", 0);

            final String existingCloudId = inv.getCloudId();
            CollectionReference ref = db.collection("businesses").document(businessId).collection("invoices");

            com.google.android.gms.tasks.Task<Void> setTask;
            final long finalLocalId = localId; // capture final localId for use inside closure

            // Common OnCompleteListener to release the lock
            OnCompleteListener<Void> onComplete = task -> inProgressPushes.remove(lockKey);

            if (existingCloudId == null || existingCloudId.trim().isEmpty()) {
                DocumentReference docRef = ref.document();
                final String newCloudId = docRef.getId();
                setTask = docRef.set(doc, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                    try { localDb.markInvoiceSyncedByLocalId(finalLocalId, newCloudId, now); } catch (Exception ex) { Log.w(TAG, "markInvoiceSyncedByLocalId failed", ex); }
                });
            } else {
                final String cloudIdFinal = existingCloudId;
                setTask = ref.document(cloudIdFinal).set(doc, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                    try { localDb.markInvoiceSyncedByLocalId(finalLocalId, cloudIdFinal, now); } catch (Exception ex) { Log.w(TAG, "markInvoiceSyncedByLocalId failed", ex); }
                });
            }
            setTask.addOnCompleteListener(onComplete);

        } catch (Exception ex) {
            Log.w(TAG, "pushLocalInvoiceToCloud exception", ex);
            inProgressPushes.remove(lockKey);
        }
    }

    private void pushUnsyncedInvoices() {
        try (Cursor cur = localDb.getUnsyncedInvoicesCursor()) {
            if (cur == null) return;
            while (cur.moveToNext()) {
                long localId = cur.getLong(cur.getColumnIndexOrThrow("_id"));
                pushLocalInvoiceToCloud(localId);
            }
        } catch (Exception ex) {
            Log.w(TAG, "pushUnsyncedInvoices", ex);
        }
    }

    // Estimates
    public void pushLocalEstimateToCloud(long localId) {
        final String lockKey = "estimate:" + localId;
        if (inProgressPushes.putIfAbsent(lockKey, true) != null) return;
        try {
            Estimate est = localDb.getEstimateByLocalId(localId);
            if (est == null) {
                inProgressPushes.remove(lockKey);
                return;
            }

            Map<String,Object> doc = new HashMap<>();
            doc.put("customerName", est.getCustomerName());
            doc.put("customerAddress", est.getCustomerAddress());
            doc.put("customerContact", est.getCustomerContact());
            doc.put("customerEmail", est.getCustomerEmail());
            doc.put("itemDetails", est.getItemDetails());
            doc.put("totalAmount", est.getTotalAmount());
            doc.put("filePath", est.getFilePath());
            long now = System.currentTimeMillis();
            doc.put("updatedAt", now);
            doc.put("lastModifiedBy", clientId);
            if (est.getTimestamp() != null) {
                try { doc.put("dateCreated", Long.parseLong(est.getTimestamp())); } catch (Exception ignored) {}
            }
            doc.put("deleted", 0);

            final String existingCloudId = est.getCloudId();
            CollectionReference ref = db.collection("businesses").document(businessId).collection("estimates");

            com.google.android.gms.tasks.Task<Void> setTask;
            final long finalLocalId = localId; // capture final localId for use inside closure

            // Common OnCompleteListener to release the lock
            OnCompleteListener<Void> onComplete = task -> inProgressPushes.remove(lockKey);

            if (existingCloudId == null || existingCloudId.trim().isEmpty()) {
                DocumentReference docRef = ref.document();
                final String newCloudId = docRef.getId();
                setTask = docRef.set(doc, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                    try { localDb.markEstimateSyncedByLocalId(finalLocalId, newCloudId, now); } catch (Exception ex) { Log.w(TAG, "markEstimateSyncedByLocalId failed", ex); }
                });
            } else {
                final String cloudIdFinal = existingCloudId;
                setTask = ref.document(cloudIdFinal).set(doc, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                    try { localDb.markEstimateSyncedByLocalId(finalLocalId, cloudIdFinal, now); } catch (Exception ex) { Log.w(TAG, "markEstimateSyncedByLocalId failed", ex); }
                });
            }
            setTask.addOnCompleteListener(onComplete);

        } catch (Exception ex) {
            Log.w(TAG, "pushLocalEstimateToCloud exception", ex);
            inProgressPushes.remove(lockKey);
        }
    }

    private void pushUnsyncedEstimates() {
        try (Cursor cur = localDb.getUnsyncedEstimatesCursor()) {
            if (cur == null) return;
            while (cur.moveToNext()) {
                long localId = cur.getLong(cur.getColumnIndexOrThrow("_id"));
                pushLocalEstimateToCloud(localId);
            }
        } catch (Exception ex) {
            Log.w(TAG, "pushUnsyncedEstimates", ex);
        }
    }

    // ---------------------------
    // Notifiers
    // ---------------------------
    public void notifyLocalCustomerChanged(long localId) {
        try { localDb.markCustomerDirty(localId, System.currentTimeMillis()); } catch (Exception ignored) {}
        pushLocalCustomerToCloud(localId);
    }

    public void notifyLocalProductChanged(int localId) {
        try { localDb.markProductDirty(localId, System.currentTimeMillis()); } catch (Exception ignored) {}
        pushLocalProductToCloud(localId);
    }

    public void notifyLocalCategoryChanged(long localId) {
        try { localDb.markCategoryDirty(localId, System.currentTimeMillis()); } catch (Exception ignored) {}
        pushLocalCategoryToCloud(localId);
    }

    public void notifyLocalInvoiceChanged(long localId) {
        try { localDb.markInvoiceDirty(localId, System.currentTimeMillis()); } catch (Exception ignored) {}
        pushLocalInvoiceToCloud(localId);
    }

    public void notifyLocalEstimateChanged(long localId) {
        try { localDb.markEstimateDirty(localId, System.currentTimeMillis()); } catch (Exception ignored) {}
        pushLocalEstimateToCloud(localId);
    }
}