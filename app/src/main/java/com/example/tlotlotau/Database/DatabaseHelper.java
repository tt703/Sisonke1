package com.example.tlotlotau.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.tlotlotau.Documents.Estimate.CreateEstimateActivity;
import com.example.tlotlotau.Documents.Invoice.Invoice;
import com.example.tlotlotau.Inventory.Product;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "invoices.db";
    private static final int DATABASE_VERSION = 14;

    private static final String TABLE_ESTIMATES = "estimates";
    private static final String COLUMN_ESTIMATE_ID = "_id";
    private static final String COLUMN_ESTIMATE_CUSTOMER_NAME = "customerName";
    private static final String COLUMN_ESTIMATE_CUSTOMER_ADDRESS = "customerAddress";
    private static final String COLUMN_ESTIMATE_CUSTOMER_CONTACT = "customerContact";
    private static final String COLUMN_ESTIMATE_ITEM_DETAILS = "itemDetails";
    private static final String COLUMN_ESTIMATE_TOTAL_AMOUNT = "totalAmount";
    private static final String COLUMN_ESTIMATE_FILE_PATH = "filePath";
    private static final String COLUMN_ESTIMATE_DATE_CREATED = "dateCreated";

    private static final String TABLE_INVOICES = "invoices";
    private static final String COLUMN_INVOICE_ID = "_id";
    private static final String COLUMN_INVOICE_CUSTOMER_NAME = "customerName";
    private static final String COLUMN_INVOICE_CUSTOMER_ADDRESS = "customerAddress";
    private static final String COLUMN_INVOICE_CUSTOMER_CONTACT = "customerContact";
    private static final String COLUMN_INVOICE_ITEM_DETAILS = "itemDetails";
    private static final String COLUMN_INVOICE_TOTAL_AMOUNT = "totalAmount";
    private static final String COLUMN_INVOICE_FILE_PATH = "filePath";
    private static final String COLUMN_INVOICE_DATE_CREATED = "dateCreated";

    private static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_PRODUCT_ID = "_id";
    private static final String COLUMN_PRODUCT_NAME = "product_name";
    private static final String COLUMN_PRODUCT_PRICE = "price";
    private static final String COLUMN_PRODUCT_DESCRIPTION = "description";
    private static final String COLUMN_PRODUCT_QUANTITY = "quantity";
    private static final String COLUMN_PRODUCT_DATE_CREATED = "date_created";
    private static final String COLUMN_PRODUCT_QR_CODE = "qr_code";

    private static final String TABLE_BUSINESSES= "business";
    private static final String COLUMN_BUSINESS_ID = "businessId";
    private static final String COLUMN_BUSINESS_NAME = "name";
    private static final String COLUMN_BUSINESS_ADDRESS = "address";
    private static final String COLUMN_BUSINESS_VAT_NUMBER = "vatNumber";
    private static final String COLUMN_BUSINESS_REGISTRATION_NUMBER = "registrationNumber";
    private static final String COLUMN_BUSINESS_BANK_NAME = "bankName";
    private static final String COLUMN_BUSINESS_ACCOUNT_NUMBER = "accountNumber";
    private static final String COLUMN_BUSINESS_BRANCH_CODE = "branchCode";
    private static final String COLUMN_BUSINESS_OWNER_ID = "ownerId";
    private static final String COLUMN_BUSINESS_UPDATED_AT = "updatedAt";
    private static final String COLUMN_BUSINESS_SYNCED = "synced";


    private static final String CREATE_TABLE_INVOICES = "CREATE TABLE " + TABLE_INVOICES + " (" +
            COLUMN_INVOICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_INVOICE_CUSTOMER_NAME + " TEXT, " +
            COLUMN_INVOICE_CUSTOMER_ADDRESS + " TEXT, " +
            COLUMN_INVOICE_CUSTOMER_CONTACT + " TEXT, " +
            COLUMN_INVOICE_ITEM_DETAILS + " TEXT, " +
            COLUMN_INVOICE_TOTAL_AMOUNT + " REAL, " +
            COLUMN_INVOICE_FILE_PATH + " TEXT, " +
            COLUMN_INVOICE_DATE_CREATED + " TEXT)";

    private static final String CREATE_TABLE_ESTIMATES = "CREATE TABLE " + TABLE_ESTIMATES + " (" +
            COLUMN_ESTIMATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ESTIMATE_CUSTOMER_NAME + " TEXT, " +
            COLUMN_ESTIMATE_CUSTOMER_ADDRESS + " TEXT, " +
            COLUMN_ESTIMATE_CUSTOMER_CONTACT + " TEXT, " +
            COLUMN_ESTIMATE_ITEM_DETAILS + " TEXT, " +
            COLUMN_ESTIMATE_TOTAL_AMOUNT + " REAL, " +
            COLUMN_ESTIMATE_FILE_PATH + " TEXT, " +
            COLUMN_ESTIMATE_DATE_CREATED + " TEXT)";

    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
            COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PRODUCT_NAME + " TEXT, " +
            COLUMN_PRODUCT_PRICE + " REAL, " +
            COLUMN_PRODUCT_DESCRIPTION + " TEXT, " +
            COLUMN_PRODUCT_QUANTITY + " INTEGER, " +
            COLUMN_PRODUCT_DATE_CREATED + " TEXT, " +
            COLUMN_PRODUCT_QR_CODE + " TEXT)";

    private static final String CREATE_TABLE_BUSINESSES = "CREATE TABLE " + TABLE_BUSINESSES + " (" +
            COLUMN_BUSINESS_ID + " TEXT PRIMARY KEY, " +
            COLUMN_BUSINESS_NAME + " TEXT, " +
            COLUMN_BUSINESS_ADDRESS + " TEXT, " +
            COLUMN_BUSINESS_VAT_NUMBER + " TEXT, " +
            COLUMN_BUSINESS_REGISTRATION_NUMBER + " TEXT, " +
            COLUMN_BUSINESS_BANK_NAME + " TEXT, " +
            COLUMN_BUSINESS_ACCOUNT_NUMBER + " TEXT, " +
            COLUMN_BUSINESS_BRANCH_CODE + " TEXT, " +
            COLUMN_BUSINESS_OWNER_ID + " TEXT, " +
            COLUMN_BUSINESS_UPDATED_AT + " TEXT, " +
            COLUMN_BUSINESS_SYNCED + " INTEGER DEFAULT 0)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_INVOICES);
        db.execSQL(CREATE_TABLE_PRODUCTS);
        db.execSQL(CREATE_TABLE_ESTIMATES);
        db.execSQL(CREATE_TABLE_BUSINESSES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVOICES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ESTIMATES);
        if(oldVersion<15){
            db.execSQL(CREATE_TABLE_BUSINESSES);
        }
        onCreate(db);
    }

    public boolean insertInvoice(String customerName, String customerAddress, String customerContact, String itemDetails, double totalAmount, String filePath) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.beginTransaction();
            try {
                ContentValues values = new ContentValues();
                values.put(COLUMN_INVOICE_CUSTOMER_NAME, customerName);
                values.put(COLUMN_INVOICE_CUSTOMER_ADDRESS, customerAddress);
                values.put(COLUMN_INVOICE_CUSTOMER_CONTACT, customerContact);
                values.put(COLUMN_INVOICE_ITEM_DETAILS, itemDetails);
                values.put(COLUMN_INVOICE_TOTAL_AMOUNT, totalAmount);
                values.put(COLUMN_INVOICE_FILE_PATH, filePath);
                values.put(COLUMN_INVOICE_DATE_CREATED, String.valueOf(System.currentTimeMillis()));

                long result = db.insert(TABLE_INVOICES, null, values);
                db.setTransactionSuccessful();
                return result != -1;
            } finally {
                db.endTransaction();
            }
        }
    }

    public List<Invoice> getAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String orderBy = COLUMN_INVOICE_DATE_CREATED + " DESC";
        Cursor cursor = db.query(TABLE_INVOICES, null, null, null, null, null, orderBy);
        while (cursor.moveToNext()) {
            Invoice invoice = new Invoice();
            invoice.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_ID)));
            invoice.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_CUSTOMER_NAME)));
            invoice.setCustomerAddress(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_CUSTOMER_ADDRESS)));
            invoice.setCustomerContact(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_CUSTOMER_CONTACT)));
            invoice.setItemDetails(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_ITEM_DETAILS)));
            invoice.setTotalAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_TOTAL_AMOUNT)));
            invoice.setFilePath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_FILE_PATH)));
            invoice.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_DATE_CREATED)));
            invoices.add(invoice);
        }
        cursor.close();
        return invoices;
    }

    public long insertProduct(String name, double price, int quantity, String qrCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, name);
        values.put(COLUMN_PRODUCT_PRICE, price);
        values.put(COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(COLUMN_PRODUCT_DESCRIPTION, "");
        values.put(COLUMN_PRODUCT_DATE_CREATED, String.valueOf(System.currentTimeMillis()));
        values.put(COLUMN_PRODUCT_QR_CODE, qrCode);
        long result = db.insert(TABLE_PRODUCTS, null, values);
        db.close();
        return result;
    }

    public boolean insertEstimate(String customerName, String customerAddress, String customerContact, String itemDetails, double totalAmount, String filePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ESTIMATE_CUSTOMER_NAME, customerName);
        values.put(COLUMN_ESTIMATE_CUSTOMER_ADDRESS, customerAddress);
        values.put(COLUMN_ESTIMATE_CUSTOMER_CONTACT, customerContact);
        values.put(COLUMN_ESTIMATE_ITEM_DETAILS, itemDetails);
        values.put(COLUMN_ESTIMATE_TOTAL_AMOUNT, totalAmount);
        values.put(COLUMN_ESTIMATE_FILE_PATH, filePath);
        values.put(COLUMN_ESTIMATE_DATE_CREATED, String.valueOf(System.currentTimeMillis()));
        long result = db.insert(TABLE_ESTIMATES, null, values);
        db.close();
        return result != -1;
    }

    public Product getProductByQRCode(String qrCodeContent) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCT_QR_CODE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{qrCodeContent});
        Product product = null;
        if (cursor.moveToFirst()) {
            product = new Product();
            product.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)));
            product.setProductName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)));
            product.setProductPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)));
            product.setProductDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION)));
            product.setProductQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY)));
            product.setProductDateCreated(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DATE_CREATED)));
            product.setProductQRCode(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QR_CODE)));
        }
        cursor.close();
        db.close();
        return product;
    }


    public int updateProductQuantity(int productId, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_QUANTITY, newQuantity);
        return db.update(TABLE_PRODUCTS, values, COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(productId)});
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Product product = new Product();
            product.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)));
            product.setProductName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)));
            product.setProductPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)));
            product.setProductDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION)));
            product.setProductQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY)));
            product.setProductDateCreated(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DATE_CREATED)));
            product.setProductQRCode(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QR_CODE)));
            products.add(product);
        }
        cursor.close();
        return products;
    }
    public Product getProductById(int productId){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCT_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(productId)});
        Product product = null;
        if(cursor.moveToFirst()){
            product = new Product();
            product.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)));
            product.setProductName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)));
            product.setProductPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)));
            product.setProductQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY)));
            product.setProductDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION)));
            product.setProductDateCreated(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DATE_CREATED)));
            product.setProductQRCode(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QR_CODE)));
            cursor.close();
        }
        db.close();
        return product;
    }
    public List<CreateEstimateActivity.Estimate> getAllEstimates() {
        List<CreateEstimateActivity.Estimate> estimates = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String orderBy = COLUMN_ESTIMATE_DATE_CREATED + " DESC";
        Cursor cursor = db.query(TABLE_ESTIMATES, null, null, null, null, null, orderBy);
        while (cursor.moveToNext()) {
            CreateEstimateActivity.Estimate estimate = new CreateEstimateActivity.Estimate();
            estimate.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_ID)));
            estimate.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_CUSTOMER_NAME)));
            estimate.setCustomerAddress(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_CUSTOMER_ADDRESS)));
            estimate.setCustomerContact(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_CUSTOMER_CONTACT)));
            estimate.setItemDetails(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_ITEM_DETAILS)));
            estimate.setTotalAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_TOTAL_AMOUNT)));
            estimate.setFilePath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_FILE_PATH)));
            estimate.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_DATE_CREATED)));
            estimates.add(estimate);
        }
        cursor.close();
        return estimates;
    }
    public int updateProduct(int productId, String name, double price, int quantity, String description){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME,name);
        values.put(COLUMN_PRODUCT_PRICE, price);
        values.put(COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(COLUMN_PRODUCT_DESCRIPTION, description);
        int rowsAffected = db.update(TABLE_PRODUCTS, values, COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(productId)});
        db.close();
        return rowsAffected;
    }

    public int deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PRODUCTS, COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(productId)});
    }
    public boolean upsertBusinessLocal(String businessId, String name, String address, String vatNumber, String registrationNumber,
                                        String bankName, String accountNumber, String branchCode, String ownerUid, long updatedAt, int synced) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BUSINESS_ID, businessId);
        values.put(COLUMN_BUSINESS_NAME, name);
        values.put(COLUMN_BUSINESS_ADDRESS, address);
        values.put(COLUMN_BUSINESS_VAT_NUMBER, vatNumber);
        values.put(COLUMN_BUSINESS_REGISTRATION_NUMBER, registrationNumber);
        values.put(COLUMN_BUSINESS_BANK_NAME, bankName);
        values.put(COLUMN_BUSINESS_ACCOUNT_NUMBER, accountNumber);
        values.put(COLUMN_BUSINESS_BRANCH_CODE, branchCode);
        values.put(COLUMN_BUSINESS_OWNER_ID, ownerUid);
        values.put(COLUMN_BUSINESS_UPDATED_AT, updatedAt);
        values.put(COLUMN_BUSINESS_SYNCED, synced);

        long result = db.insertWithOnConflict(TABLE_BUSINESSES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return result != -1;
    }





}