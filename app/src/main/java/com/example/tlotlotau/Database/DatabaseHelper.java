package com.example.tlotlotau.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.tlotlotau.Inventory.CategoryC;
import com.example.tlotlotau.Customer.Customer;
import com.example.tlotlotau.Documents.Estimate.Estimate;
import com.example.tlotlotau.Documents.Invoice.Invoice;
import com.example.tlotlotau.Inventory.Product;
import com.example.tlotlotau.Sales.SaleItem;
import com.example.tlotlotau.Sales.SaleRecord;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Sisonke.db";
    private static final int DATABASE_VERSION = 27;

    public static final String TABLE_CUSTOMERS = "customers";
    public static final String COLUMN_CUSTOMER_ID = "_id";
    private static final String COLUMN_CUSTOMER_NAME = "name";
    private static final String COLUMN_CUSTOMER_PHONE = "phone";
    private static final String COLUMN_CUSTOMER_EMAIL = "email";
    private static final String COLUMN_CUSTOMER_ADDRESS = "address";
    private static final String COLUMN_CUSTOMER_DATE_CREATED = "date_created";
    private static final String COLUMN_CUSTOMER_NUM_ESTIMATES = "num_estimates";
    private static final String COLUMN_CUSTOMER_NUM_INVOICES = "num_invoices";
    private static final String COLUMN_CUSTOMER_TOTAL_AMOUNT = "total_amount";
    private static final String COLUMN_CUSTOMER_CLOUD_ID = "cloud_id";
    private static final String COLUMN_CUSTOMER_SYNCED = "synced";
    private static final String COLUMN_CUSTOMER_UPDATED_AT = "updated_at";
    private static final String COLUMN_CUSTOMER_DELETED = "deleted";
    private static final String COLUMN_ESTIMATE_CLOUD_ID = "cloud_id";
    private static final String COLUMN_ESTIMATE_SYNCED = "synced_at";
    private static final String COLUMN_ESTIMATE_DELETED = "deleted";
    private static final String COLUMN_ESTIMATE_DIRTY_AT = "dirty_at";





    private static final String TABLE_ESTIMATES = "estimates";
    private static final String COLUMN_ESTIMATE_ID = "_id";
    private static final String COLUMN_ESTIMATE_CUSTOMER_NAME = "customerName";
    private static final String COLUMN_ESTIMATE_CUSTOMER_ADDRESS = "customerAddress";
    private static final String COLUMN_ESTIMATE_CUSTOMER_CONTACT = "customerContact";
    private static final String COLUMN_ESTIMATE_CUSTOMER_EMAIL = "customerEmail";
    private static final String COLUMN_ESTIMATE_ITEM_DETAILS = "itemDetails";
    private static final String COLUMN_ESTIMATE_TOTAL_AMOUNT = "totalAmount";
    private static final String COLUMN_ESTIMATE_FILE_PATH = "filePath";
    private static final String COLUMN_ESTIMATE_DATE_CREATED = "dateCreated";

    private static final String TABLE_INVOICES = "invoices";
    private static final String COLUMN_INVOICE_ID = "_id";
    private static final String COLUMN_INVOICE_CUSTOMER_NAME = "customerName";
    private static final String COLUMN_INVOICE_CUSTOMER_ADDRESS = "customerAddress";
    private static final String COLUMN_INVOICE_CUSTOMER_CONTACT = "customerContact";
    private static final String COLUMN_INVOICE_CUSTOMER_EMAIL = "customerEmail";
    private static final String COLUMN_INVOICE_ITEM_DETAILS = "itemDetails";
    private static final String COLUMN_INVOICE_TOTAL_AMOUNT = "totalAmount";
    private static final String COLUMN_INVOICE_FILE_PATH = "filePath";
    private static final String COLUMN_INVOICE_DATE_CREATED = "dateCreated";
    private static final String COLUMN_INVOICE_CLOUD_ID = "cloud_id";
    private static final String COLUMN_INVOICE_SYNCED = "synced_at";
    private static final String COLUMN_INVOICE_DELETED = "deleted";
    private static final String COLUMN_INVOICE_DIRTY_AT = "dirty_at";

    private static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_PRODUCT_ID = "_id";
    private static final String COLUMN_PRODUCT_NAME = "product_name";
    private static final String COLUMN_PRODUCT_PRICE = "price";
    private static final String COLUMN_PRODUCT_DESCRIPTION = "description";
    private static final String COLUMN_PRODUCT_QUANTITY = "quantity";
    private static final String COLUMN_PRODUCT_DATE_CREATED = "date_created";
    private static final String COLUMN_PRODUCT_QR_CODE = "qr_code";
    private static final String  COLUNM_PRODUCT_CATEGORY_ID = "category_id";
    private static final String COLUMN_PRODUCT_CLOUD_ID = "cloud_id";
    private static final String COLUMN_PRODUCT_SYNCED = "synced";
    private static final String COLUMN_PRODUCT_UPDATED_AT = "updated_at";
    private static final String COLUMN_PRODUCT_DELETED = "deleted";




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

    private static final String TABLE_CATEGORIES = "categories";
    public static final String COLUMN_CATEGORY_ID = "_id";
    public static final String COLUMN_CATEGORY_NAME = "name";
    private static final String COLUMN_CATEGORY_DATE_CREATED = "date_created";
    private static final String COLUMN_CATEGORY_CLOUD_ID = "cloud_id";
    private static final String COLUMN_CATEGORY_SYNCED = "synced_at";
    private static final String COLUMN_CATEGORY_DELETED = "deleted";
    private static final String COLUMN_CATEGORY_DIRTY_AT = "dirty_at";

    private static final String TABLE_SALES = "sales";
    private static final String COLUMN_SALE_ID = "_id";
    private static final String COLUMN_SALE_USER_ID = "user_id";
    private static final String COLUMN_SALE_USER_NAME = "user_name";
    private static final String COLUMN_SALE_USER_ROLE = "user_role";
    private static final String COLUMN_SALE_SUBTOTAL = "subtotal";
    private static final String COLUMN_SALE_TAX = "tax";
    private static final String COLUMN_SALE_TOTAL = "total";
    private static final String COLUMN_SALE_PAYMENT_METHOD = "payment_method";
    private static final String COLUMN_SALE_TIMESTAMP = "timestamp";
    private static final String COLUMN_SALE_CLOUD_ID = "cloud_id";
    private static final String COLUMN_SALE_SYNCED = "synced_at";
    private static final String COLUMN_SALE_DELETED = "deleted";
    private static final String COLUMN_SALE_DIRTY_AT = "dirty_at";

    private static final String TABLE_SALE_ITEMS = "sale_items";
    private static final String COLUMN_SALE_ITEM_ID = "_id";
    private static final String COLUMN_SALE_ITEM_SALE_ID = "sale_id";
    private static final String COLUMN_SALE_ITEM_PRODUCT_ID = "product_id";
    private static final String COLUMN_SALE_ITEM_PRODUCT_NAME = "product_name";
    private static final String COLUMN_SALE_ITEM_QTY = "quantity";
    private static final String COLUMN_SALE_ITEM_UNIT_PRICE = "unit_price";
    private static final String COLUMN_SALE_ITEM_LINE_TOTAL = "line_total";
    private static final String COLUMN_SALE_ITEM_CLOUD_ID = "cloud_id";
    private static final String COLUMN_SALE_ITEM_SYNCED = "synced_at";
    private static final String COLUMN_SALE_ITEM_DELETED = "deleted";
    private static final String COLUMN_SALE_ITEM_DIRTY_AT = "dirty_at";




    private static final String CREATE_TABLE_INVOICES = "CREATE TABLE " + TABLE_INVOICES + " (" +
            COLUMN_INVOICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_INVOICE_CUSTOMER_NAME + " TEXT, " +
            COLUMN_INVOICE_CUSTOMER_ADDRESS + " TEXT, " +
            COLUMN_INVOICE_CUSTOMER_CONTACT + " TEXT, " +
            COLUMN_INVOICE_CUSTOMER_EMAIL + " TEXT, " +
            COLUMN_INVOICE_ITEM_DETAILS + " TEXT, " +
            COLUMN_INVOICE_TOTAL_AMOUNT + " REAL, " +
            COLUMN_INVOICE_FILE_PATH + " TEXT, " +
            COLUMN_INVOICE_DATE_CREATED + " TEXT, " +
            COLUMN_INVOICE_CLOUD_ID + " TEXT, " +
            COLUMN_INVOICE_SYNCED + " INTEGER DEFAULT 0, " +
            COLUMN_INVOICE_DELETED + " INTEGER DEFAULT 0, " +
            COLUMN_INVOICE_DIRTY_AT + " INTEGER DEFAULT 0, " +
            "paid INTEGER DEFAULT 0)";

    private static final String CREATE_TABLE_ESTIMATES = "CREATE TABLE " + TABLE_ESTIMATES + " (" +
            COLUMN_ESTIMATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ESTIMATE_CUSTOMER_NAME + " TEXT, " +
            COLUMN_ESTIMATE_CUSTOMER_ADDRESS + " TEXT, " +
            COLUMN_ESTIMATE_CUSTOMER_CONTACT + " TEXT, " +
            COLUMN_ESTIMATE_CUSTOMER_EMAIL + " TEXT, " +
            COLUMN_ESTIMATE_ITEM_DETAILS + " TEXT, " +
            COLUMN_ESTIMATE_TOTAL_AMOUNT + " REAL, " +
            COLUMN_ESTIMATE_FILE_PATH + " TEXT, " +
            COLUMN_ESTIMATE_CLOUD_ID + " TEXT, " +
            COLUMN_ESTIMATE_SYNCED + " INTEGER DEFAULT 0, " +
            COLUMN_ESTIMATE_DELETED + " INTEGER DEFAULT 0, " +
            COLUMN_ESTIMATE_DIRTY_AT + " INTEGER DEFAULT 0, " +
            COLUMN_ESTIMATE_DATE_CREATED + " TEXT)" ;

    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
            COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PRODUCT_NAME + " TEXT, " +
            COLUMN_PRODUCT_PRICE + " REAL, " +
            COLUMN_PRODUCT_DESCRIPTION + " TEXT, " +
            COLUMN_PRODUCT_QUANTITY + " INTEGER, " +
            COLUMN_PRODUCT_DATE_CREATED + " TEXT, " +
            COLUMN_PRODUCT_QR_CODE + " TEXT,"+
            COLUNM_PRODUCT_CATEGORY_ID + " INTEGER DEFAULT NULL, " +
            COLUMN_PRODUCT_CLOUD_ID + " TEXT, " +
            COLUMN_PRODUCT_SYNCED + " INTEGER DEFAULT 0, " +
            COLUMN_PRODUCT_UPDATED_AT + " TEXT,"+
            COLUMN_PRODUCT_DELETED + " INTEGER DEFAULT 0)";


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

    private static final String CREATE_TABLE_CUSTOMERS = "CREATE TABLE " + TABLE_CUSTOMERS + " (" +
            COLUMN_CUSTOMER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CUSTOMER_NAME + " TEXT, " +
            COLUMN_CUSTOMER_PHONE + " TEXT, " +
            COLUMN_CUSTOMER_EMAIL + " TEXT, " +
            COLUMN_CUSTOMER_ADDRESS + " TEXT, " +
            COLUMN_CUSTOMER_DATE_CREATED + " TEXT," +
            COLUMN_CUSTOMER_NUM_ESTIMATES + " INTEGER DEFAULT 0," +
            COLUMN_CUSTOMER_NUM_INVOICES + " INTEGER DEFAULT 0," +
            COLUMN_CUSTOMER_TOTAL_AMOUNT + " REAL DEFAULT 0,"+
            COLUMN_CUSTOMER_CLOUD_ID + " TEXT, " +
            COLUMN_CUSTOMER_SYNCED + " INTEGER DEFAULT 0, "+
            COLUMN_CUSTOMER_UPDATED_AT + " TEXT,"+
            COLUMN_CUSTOMER_DELETED + " INTEGER DEFAULT 0)";

    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES + " (" +
            COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CATEGORY_NAME + " TEXT, " +
            COLUMN_CATEGORY_DATE_CREATED + " TEXT,"+
            COLUMN_CATEGORY_CLOUD_ID + " TEXT, " +
            COLUMN_CATEGORY_SYNCED + " INTEGER DEFAULT 0,"+
            COLUMN_CATEGORY_DELETED + " INTEGER DEFAULT 0,"+
            COLUMN_CATEGORY_DIRTY_AT + " INTEGER DEFAULT 0)";
    private static final String CREATE_TABLE_SALES = "CREATE TABLE " + TABLE_SALES + " (" +
            COLUMN_SALE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SALE_USER_ID + " TEXT, " +
            COLUMN_SALE_USER_NAME + " TEXT, " +
            COLUMN_SALE_USER_ROLE + " TEXT, " +
            COLUMN_SALE_SUBTOTAL + " REAL, " +
            COLUMN_SALE_TAX + " REAL, " +
            COLUMN_SALE_TOTAL + " REAL, " +
            COLUMN_SALE_PAYMENT_METHOD + " TEXT, " +
            COLUMN_SALE_TIMESTAMP + " TEXT,"+
            COLUMN_SALE_CLOUD_ID + " TEXT, " +
            COLUMN_SALE_SYNCED + " INTEGER DEFAULT 0,"+
            COLUMN_SALE_DIRTY_AT + " INTEGER DEFAULT 0,"+
            COLUMN_SALE_DELETED + " INTEGER DEFAULT 0)";


    private static final String CREATE_TABLE_SALE_ITEMS = "CREATE TABLE " + TABLE_SALE_ITEMS + " (" +
            COLUMN_SALE_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SALE_ITEM_SALE_ID + " INTEGER, " +
            COLUMN_SALE_ITEM_PRODUCT_ID + " INTEGER, " +
            COLUMN_SALE_ITEM_PRODUCT_NAME + " TEXT, " +
            COLUMN_SALE_ITEM_QTY + " INTEGER, " +
            COLUMN_SALE_ITEM_UNIT_PRICE + " REAL, " +
            COLUMN_SALE_ITEM_LINE_TOTAL + " REAL, " +
            COLUMN_SALE_ITEM_CLOUD_ID + " TEXT, " +
            COLUMN_SALE_ITEM_SYNCED + " INTEGER DEFAULT 0, " +
            COLUMN_SALE_ITEM_DELETED + " INTEGER DEFAULT 0, " +
            COLUMN_SALE_ITEM_DIRTY_AT + " INTEGER DEFAULT 0, " +
            "FOREIGN KEY(" + COLUMN_SALE_ITEM_SALE_ID + ") REFERENCES " + TABLE_SALES + "(" + COLUMN_SALE_ID + "))";








    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_INVOICES);
        db.execSQL(CREATE_TABLE_PRODUCTS);
        db.execSQL(CREATE_TABLE_ESTIMATES);
        db.execSQL(CREATE_TABLE_BUSINESSES);
        db.execSQL(CREATE_TABLE_CUSTOMERS);
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_SALES);
        db.execSQL(CREATE_TABLE_SALE_ITEMS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVOICES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ESTIMATES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUSINESSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SALES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SALE_ITEMS);
        onCreate(db);


    }


    public boolean insertInvoice(String customerName, String customerAddress, String customerContact,String customerEmail, String itemDetails, double totalAmount, String filePath) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.beginTransaction();
            try {
                ContentValues values = new ContentValues();
                values.put(COLUMN_INVOICE_CUSTOMER_NAME, customerName);
                values.put(COLUMN_INVOICE_CUSTOMER_ADDRESS, customerAddress);
                values.put(COLUMN_INVOICE_CUSTOMER_CONTACT, customerContact);
                values.put(COLUMN_INVOICE_CUSTOMER_EMAIL, customerEmail);
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
            invoice.setCustomerEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_CUSTOMER_EMAIL)));
            invoice.setItemDetails(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_ITEM_DETAILS)));
            invoice.setTotalAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_TOTAL_AMOUNT)));
            invoice.setFilePath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_FILE_PATH)));
            invoice.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_DATE_CREATED)));
            invoices.add(invoice);
        }
        cursor.close();
        return invoices;
    }
    public List<Estimate> getAllEstimates() {
        List<Estimate> estimates = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String orderBy = COLUMN_ESTIMATE_DATE_CREATED + " DESC";
        Cursor cursor = db.query(TABLE_ESTIMATES, null, null, null, null, null, orderBy);
        while (cursor.moveToNext()) {
            Estimate estimate = new Estimate();
            estimate.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_ID)));
            estimate.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_CUSTOMER_NAME)));
            estimate.setCustomerAddress(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_CUSTOMER_ADDRESS)));
            estimate.setCustomerContact(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_CUSTOMER_CONTACT)));
            estimate.setCustomerEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_CUSTOMER_EMAIL)));
            estimate.setItemDetails(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_ITEM_DETAILS)));
            estimate.setTotalAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_TOTAL_AMOUNT)));
            estimate.setFilePath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_FILE_PATH)));
            estimate.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INVOICE_DATE_CREATED)));
            estimates.add(estimate);
        }
        cursor.close();
        return estimates;
    }

    public long insertProduct(String name, double price, int quantity, String qrCode, Long categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, name);
        values.put(COLUMN_PRODUCT_PRICE, price);
        values.put(COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(COLUMN_PRODUCT_DESCRIPTION, "");
        values.put(COLUMN_PRODUCT_DATE_CREATED, String.valueOf(System.currentTimeMillis()));
        values.put(COLUMN_PRODUCT_QR_CODE, qrCode);
        if (categoryId != null) values.put(COLUNM_PRODUCT_CATEGORY_ID, categoryId);
        long result = db.insert(TABLE_PRODUCTS, null, values);
        db.close();
        return result;
    }
    public List<Product> getProductsByCategory(Long categoryId) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = categoryId == null ? COLUNM_PRODUCT_CATEGORY_ID + " IS NULL" : COLUNM_PRODUCT_CATEGORY_ID + " = ?";
        String[] selArgs = categoryId == null ? null : new String[]{String.valueOf(categoryId)};
        Cursor cursor = db.query(TABLE_PRODUCTS, null, selection, selArgs, null, null, COLUMN_PRODUCT_NAME);
        while (cursor.moveToNext()) {
            Product product = readProductFromCursor(cursor);
            products.add(product);
        }
        cursor.close();
        db.close();
        return products;
    }

    private Product readProductFromCursor(Cursor cursor){
        Product product = new Product();
        product.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)));
        product.setProductName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)));
        product.setProductPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)));
        product.setProductDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION)));
        product.setProductQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY)));
        product.setProductDateCreated(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DATE_CREATED)));
        product.setProductQRCode(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QR_CODE)));
        if (cursor.getColumnIndex(COLUNM_PRODUCT_CATEGORY_ID) != -1) {
            product.setCategoryCId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUNM_PRODUCT_CATEGORY_ID)));
        }
        return product;
    }


    public boolean insertEstimate(String customerName, String customerAddress, String customerContact,String customerEmail, String itemDetails, double totalAmount, String filePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ESTIMATE_CUSTOMER_NAME, customerName);
        values.put(COLUMN_ESTIMATE_CUSTOMER_ADDRESS, customerAddress);
        values.put(COLUMN_ESTIMATE_CUSTOMER_CONTACT, customerContact);
        values.put(COLUMN_ESTIMATE_CUSTOMER_EMAIL, customerEmail);
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
    public long insertCustomer(String name, String phone, String email, String address, String dateCreated){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CUSTOMER_NAME, name);
        values.put(COLUMN_CUSTOMER_PHONE, phone);
        values.put(COLUMN_CUSTOMER_EMAIL, email);
        values.put(COLUMN_CUSTOMER_ADDRESS, address);
        values.put(COLUMN_CUSTOMER_DATE_CREATED, dateCreated);
        long id =  db.insert(TABLE_CUSTOMERS, null, values);
        db.close();
        return id;
    }
    public List<Customer> getAllCustomers(){
        List<Customer> customers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CUSTOMERS, null, null, null, null, null, COLUMN_CUSTOMER_ID );
        try{if (cursor != null && cursor.moveToFirst()){
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CUSTOMER_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CUSTOMER_NAME));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CUSTOMER_PHONE));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CUSTOMER_EMAIL));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CUSTOMER_ADDRESS));
                String totalAmount = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CUSTOMER_TOTAL_AMOUNT));
                String numEstimates = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CUSTOMER_NUM_ESTIMATES));

                Customer customer = new Customer(id, name, phone, email, address);
                customer.setAmountDue(totalAmount);
                customer.setNumEstimateSent(numEstimates);
                customers.add(customer);

            }while (cursor.moveToNext());

            }
        }finally {
            if(cursor != null) cursor.close();
            db.close();
        }
        return customers;
    }


    public long insertCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATEGORY_NAME, name);
            values.put(COLUMN_CATEGORY_DATE_CREATED, String.valueOf(System.currentTimeMillis()));
            long id = db.insert(TABLE_CATEGORIES, null, values);
            return id;
        } finally {
            db.close();
        }
    }

    public int updateCategory(long id, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATEGORY_NAME, newName);
            return db.update(TABLE_CATEGORIES, values, COLUMN_CATEGORY_ID + " = ?", new String[]{String.valueOf(id)});
        } finally {
            db.close();
        }
    }

    public int deleteCategory(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            return db.delete(TABLE_CATEGORIES, COLUMN_CATEGORY_ID + " = ?", new String[]{String.valueOf(id)});
        } finally {
            db.close();
        }
    }

    public List<CategoryC> getAllCategories() {
        List<CategoryC> out = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_CATEGORIES, null, null, null, null, null, COLUMN_CATEGORY_NAME + " COLLATE NOCASE");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME));
                    String dateCreated = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_DATE_CREATED));
                    out.add(new CategoryC(id, name, dateCreated));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return out;
    }

    public boolean createSale(String userId, String userName, String userRole,
                              List<SaleItem> items, double subtotal, double tax, double total, String paymentMethod) {
        if (items == null || items.isEmpty()) return false;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        long saleId = -1;
        try {
            // insert sale header
            ContentValues saleValues = new ContentValues();
            saleValues.put(COLUMN_SALE_USER_ID, userId);
            saleValues.put(COLUMN_SALE_USER_NAME, userName);
            saleValues.put(COLUMN_SALE_USER_ROLE, userRole);
            saleValues.put(COLUMN_SALE_SUBTOTAL, subtotal);
            saleValues.put(COLUMN_SALE_TAX, tax);
            saleValues.put(COLUMN_SALE_TOTAL, total);
            saleValues.put(COLUMN_SALE_PAYMENT_METHOD, paymentMethod);
            saleValues.put(COLUMN_SALE_TIMESTAMP, String.valueOf(System.currentTimeMillis()));

            saleId = db.insert(TABLE_SALES, null, saleValues);
            if (saleId == -1) throw new RuntimeException("Failed to insert sale header");

            // insert each sale item and decrement inventory
            for (SaleItem it : items) {
                // check stock
                int productId = it.product.getProductId();
                // query current quantity
                Cursor c = db.rawQuery("SELECT " + COLUMN_PRODUCT_QUANTITY + " FROM " + TABLE_PRODUCTS +
                        " WHERE " + COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(productId)});
                int currentQty = -1;
                if (c.moveToFirst()) currentQty = c.getInt(0);
                c.close();

                if (currentQty < it.quantity) {
                    throw new RuntimeException("Insufficient stock for product id=" + productId);
                }

                // insert item row (snapshot)
                ContentValues itemVals = new ContentValues();
                itemVals.put(COLUMN_SALE_ITEM_SALE_ID, saleId);
                itemVals.put(COLUMN_SALE_ITEM_PRODUCT_ID, it.product.getProductId());
                itemVals.put(COLUMN_SALE_ITEM_PRODUCT_NAME, it.product.getProductName());
                itemVals.put(COLUMN_SALE_ITEM_QTY, it.quantity);
                itemVals.put(COLUMN_SALE_ITEM_UNIT_PRICE, it.product.getProductPrice());
                itemVals.put(COLUMN_SALE_ITEM_LINE_TOTAL, it.lineTotal());
                long ir = db.insert(TABLE_SALE_ITEMS, null, itemVals);
                if (ir == -1) throw new RuntimeException("Failed to insert sale item");

                // decrement product quantity
                int newQty = currentQty - it.quantity;
                ContentValues upd = new ContentValues();
                upd.put(COLUMN_PRODUCT_QUANTITY, newQty);
                int rows = db.update(TABLE_PRODUCTS, upd, COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(productId)});
                if (rows <= 0) throw new RuntimeException("Failed to update product quantity");
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }
    public List<SaleRecord> getAllSales() {
        List<SaleRecord> out = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String orderBy = COLUMN_SALE_TIMESTAMP + " DESC";
        Cursor cursor = db.query(TABLE_SALES, null, null, null, null, null, orderBy);
        try {
            while (cursor.moveToNext()) {
                SaleRecord r = new SaleRecord();
                r.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_SALE_ID)));
                r.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SALE_USER_ID)));
                r.setUserName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SALE_USER_NAME)));
                r.setUserRole(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SALE_USER_ROLE)));
                r.setSubtotal(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SALE_SUBTOTAL)));
                r.setTax(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SALE_TAX)));
                r.setTotal(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SALE_TOTAL)));
                r.setPaymentMethod(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SALE_PAYMENT_METHOD)));
                r.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SALE_TIMESTAMP)));
                out.add(r);
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return out;
    }
    public List<SaleItem> getSaleItems(long saleId) {
        List<SaleItem> out = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SALE_ITEMS, null, COLUMN_SALE_ITEM_SALE_ID + " = ?", new String[]{String.valueOf(saleId)}, null, null, null);
        try {
            while (cursor.moveToNext()) {
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SALE_ITEM_PRODUCT_ID));
                String pname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SALE_ITEM_PRODUCT_NAME));
                int qty = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SALE_ITEM_QTY));
                double unitPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SALE_ITEM_UNIT_PRICE));
                double lineTotal = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SALE_ITEM_LINE_TOTAL));


                Product p = new Product();
                p.setProductId(productId);
                p.setProductName(pname);
                p.setProductPrice(unitPrice);

                SaleItem it = new SaleItem(p, qty);
                out.add(it);
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return out;
    }

    public boolean markInvoicePaid(long invoiceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("paid", 1);
            int rows = db.update(TABLE_INVOICES, values, COLUMN_INVOICE_ID + " = ?", new String[]{String.valueOf(invoiceId)});
            return rows > 0;
        } finally {
            db.close();
        }
    }


    public Estimate getEstimateById(long estimateId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ESTIMATES, null, COLUMN_ESTIMATE_ID + " = ?", new String[]{String.valueOf(estimateId)}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                Estimate e = new Estimate();
                e.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ESTIMATE_ID)));
                e.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTIMATE_CUSTOMER_NAME)));
                e.setCustomerAddress(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTIMATE_CUSTOMER_ADDRESS)));
                e.setCustomerContact(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTIMATE_CUSTOMER_CONTACT)));
                e.setCustomerEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTIMATE_CUSTOMER_EMAIL)));
                e.setItemDetails(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTIMATE_ITEM_DETAILS)));
                e.setTotalAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ESTIMATE_TOTAL_AMOUNT)));
                e.setFilePath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTIMATE_FILE_PATH)));
                e.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTIMATE_DATE_CREATED)));
                return e;
            }
            return null;
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    public long createInvoiceFromEstimate(long estimateId) {
        Estimate e = getEstimateById(estimateId);
        if (e == null) return -1L;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        long newId = -1L;
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_INVOICE_CUSTOMER_NAME, e.getCustomerName());
            values.put(COLUMN_INVOICE_CUSTOMER_ADDRESS, e.getCustomerAddress());
            values.put(COLUMN_INVOICE_CUSTOMER_CONTACT, e.getCustomerContact());
            values.put(COLUMN_INVOICE_CUSTOMER_EMAIL, e.getCustomerEmail());
            values.put(COLUMN_INVOICE_ITEM_DETAILS, e.getItemDetails());
            values.put(COLUMN_INVOICE_TOTAL_AMOUNT, e.getTotalAmount());
            values.put(COLUMN_INVOICE_FILE_PATH, e.getFilePath()); // if you want a new invoice pdf you can overwrite this later
            values.put(COLUMN_INVOICE_DATE_CREATED, String.valueOf(System.currentTimeMillis()));
            values.put("paid", 0);
            newId = db.insert(TABLE_INVOICES, null, values);
            if (newId == -1) {
                db.endTransaction();
                return -1L;
            }
            db.setTransactionSuccessful();
            return newId;
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1L;
        } finally {
            db.endTransaction();
            db.close();
        }
    }
    // Update existing customer
    public int updateCustomer(long id, String name, String phone, String email, String address) {
        SQLiteDatabase db = null;
        int rows = 0;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_CUSTOMER_NAME, name);
            values.put(COLUMN_CUSTOMER_PHONE, phone);
            values.put(COLUMN_CUSTOMER_EMAIL, email);
            values.put(COLUMN_CUSTOMER_ADDRESS, address);

            // Use transaction to make update atomic and safer
            db.beginTransaction();
            try {
                rows = db.update(TABLE_CUSTOMERS, values, COLUMN_CUSTOMER_ID + " = ?", new String[]{String.valueOf(id)});
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (Exception ex) {
            Log.w("DatabaseHelper", "updateCustomer failed for id=" + id, ex);
        }
        // do NOT close db here: the helper's close() should be used by the component that owns the helper
        return rows;
    }

    public int deleteCustomer(long id) {
        SQLiteDatabase db = null;
        int rows = 0;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                rows = db.delete(TABLE_CUSTOMERS, COLUMN_CUSTOMER_ID + " = ?", new String[]{String.valueOf(id)});
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (Exception ex) {
            Log.w("DatabaseHelper", "deleteCustomer failed for id=" + id, ex);
        }
        // do NOT close db here
        return rows;
    }


    public int getInvoiceCountForCustomer(String customerName) {
        if (customerName == null) customerName = "";
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sql = "SELECT COUNT(*) FROM " + TABLE_INVOICES + " WHERE " + COLUMN_INVOICE_CUSTOMER_NAME + " = ?";
            cursor = db.rawQuery(sql, new String[]{customerName});
            if (cursor.moveToFirst()) count = cursor.getInt(0);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return count;
    }


    public int getEstimateCountForCustomer(String customerName) {
        if (customerName == null) customerName = "";
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sql = "SELECT COUNT(*) FROM " + TABLE_ESTIMATES + " WHERE " + COLUMN_ESTIMATE_CUSTOMER_NAME + " = ?";
            cursor = db.rawQuery(sql, new String[]{customerName});
            if (cursor.moveToFirst()) count = cursor.getInt(0);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return count;
    }


    public double getInvoiceAmountForCustomer(String customerName) {
        if (customerName == null) customerName = "";
        double total = 0.0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sql = "SELECT SUM(" + COLUMN_INVOICE_TOTAL_AMOUNT + ") FROM " + TABLE_INVOICES + " WHERE " + COLUMN_INVOICE_CUSTOMER_NAME + " = ?";
            cursor = db.rawQuery(sql, new String[]{customerName});
            if (cursor.moveToFirst()) {
                // cursor.getDouble may return 0.0 if null; handle null explicitly:
                if (!cursor.isNull(0)) total = cursor.getDouble(0);
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return total;
    }

    public Cursor getUnsyncedCustomersCursor() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CUSTOMERS + " WHERE synced = 0 OR synced IS NULL", null);
    }





    public Customer getCustomerByLocalId(long localId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_CUSTOMERS, null, COLUMN_CUSTOMER_ID + " = ?", new String[]{String.valueOf(localId)}, null, null, null);
        try {
            if (c != null && c.moveToFirst()) {
                Customer out = new Customer();
                out.setId(c.getLong(c.getColumnIndexOrThrow(COLUMN_CUSTOMER_ID)));
                out.setName(c.getString(c.getColumnIndexOrThrow(COLUMN_CUSTOMER_NAME)));
                out.setPhone(c.getString(c.getColumnIndexOrThrow(COLUMN_CUSTOMER_PHONE)));
                out.setEmail(c.getString(c.getColumnIndexOrThrow(COLUMN_CUSTOMER_EMAIL)));
                out.setAddress(c.getString(c.getColumnIndexOrThrow(COLUMN_CUSTOMER_ADDRESS)));
                int idx = c.getColumnIndex("cloud_id");
                if (idx != -1) out.setCloudId(c.getString(idx));
                return out;
            }
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    public Customer getCustomerByCloudId(String cloudId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_CUSTOMERS, null, "cloud_id = ?", new String[]{cloudId}, null, null, null);
        try {
            if (c != null && c.moveToFirst()) {
                Customer out = new Customer();
                out.setId(c.getLong(c.getColumnIndexOrThrow(COLUMN_CUSTOMER_ID)));
                out.setName(c.getString(c.getColumnIndexOrThrow(COLUMN_CUSTOMER_NAME)));
                out.setPhone(c.getString(c.getColumnIndexOrThrow(COLUMN_CUSTOMER_PHONE)));
                out.setEmail(c.getString(c.getColumnIndexOrThrow(COLUMN_CUSTOMER_EMAIL)));
                out.setAddress(c.getString(c.getColumnIndexOrThrow(COLUMN_CUSTOMER_ADDRESS)));
                out.setCloudId(c.getString(c.getColumnIndexOrThrow("cloud_id")));
                return out;
            }
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    public long upsertCustomerFromCloud(String cloudId, String name, String phone, String email, String address, String cloudUpdatedAt, int totalAmount, int numEstimates, double dateCreated, long numInvoices) {
        // insert or update local row matching cloud_id (or create new)
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(TABLE_CUSTOMERS, new String[]{COLUMN_CUSTOMER_ID, "updated_at"}, "cloud_id = ?", new String[]{cloudId}, null, null, null);
        try {
            ContentValues v = new ContentValues();
            v.put(COLUMN_CUSTOMER_NAME, name);
            v.put(COLUMN_CUSTOMER_PHONE, phone);
            v.put(COLUMN_CUSTOMER_EMAIL, email);
            v.put(COLUMN_CUSTOMER_ADDRESS, address);
            v.put(COLUMN_CUSTOMER_TOTAL_AMOUNT, totalAmount);
            v.put(COLUMN_CUSTOMER_NUM_ESTIMATES, numEstimates);
            v.put(COLUMN_CUSTOMER_DATE_CREATED, dateCreated);
            v.put(COLUMN_CUSTOMER_NUM_INVOICES, numInvoices);
            v.put("cloud_id", cloudId);
            v.put("updated_at", cloudUpdatedAt);
            v.put("synced", 1);
            v.put("deleted", 0);
            if (c != null && c.moveToFirst()) {
                long localId = c.getLong(c.getColumnIndexOrThrow(COLUMN_CUSTOMER_ID));
                // optional: compare timestamps if you stored updated_at locally and want to avoid overwrite
                db.update(TABLE_CUSTOMERS, v, COLUMN_CUSTOMER_ID + " = ?", new String[]{String.valueOf(localId)});
                return localId;
            } else {
                v.put(COLUMN_CUSTOMER_DATE_CREATED, String.valueOf(System.currentTimeMillis()));
                return db.insert(TABLE_CUSTOMERS, null, v);
            }
        } finally {
            if (c != null) c.close();
        }
    }

    public int markCustomerSyncedByLocalId(long localId, String cloudId, long updatedAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        if (cloudId != null) v.put("cloud_id", cloudId);
        v.put("synced", 1);
        v.put("updated_at", updatedAt);
        return db.update(TABLE_CUSTOMERS, v, COLUMN_CUSTOMER_ID + " = ?", new String[]{String.valueOf(localId)});
    }

    public int markCustomerDirty(long localId, long updatedAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("synced", 0);
        v.put("updated_at", updatedAt);
        return db.update(TABLE_CUSTOMERS, v, COLUMN_CUSTOMER_ID + " = ?", new String[]{String.valueOf(localId)});
    }

    public int deleteCustomerByCloudId(String cloudId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CUSTOMERS, "cloud_id = ?", new String[]{cloudId});
    }


    public Cursor getUnsyncedProductsCursor() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE synced = 0 OR synced IS NULL", null);
    }

    public long upsertProductFromCloud(String cloudId, String name, double price, int quantity, long cloudUpdatedAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(TABLE_PRODUCTS, new String[]{COLUMN_PRODUCT_ID}, "cloud_id = ?", new String[]{cloudId}, null, null, null);
        try {
            ContentValues v = new ContentValues();
            v.put(COLUMN_PRODUCT_NAME, name);
            v.put(COLUMN_PRODUCT_PRICE, price);
            v.put(COLUMN_PRODUCT_QUANTITY, quantity);
            v.put("cloud_id", cloudId);
            v.put("synced", 1);
            v.put("updated_at", cloudUpdatedAt);
            v.put("deleted", 0);
            if (c != null && c.moveToFirst()) {
                int localId = c.getInt(c.getColumnIndexOrThrow(COLUMN_PRODUCT_ID));
                db.update(TABLE_PRODUCTS, v, COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(localId)});
                return localId;
            } else {
                v.put(COLUMN_PRODUCT_DATE_CREATED, String.valueOf(System.currentTimeMillis()));
                return db.insert(TABLE_PRODUCTS, null, v);
            }
        } finally {
            if (c != null) c.close();
        }
    }

    public Product getProductById1(int productId){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCT_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(productId)});
        try {
            if(cursor.moveToFirst()){
                Product product = new Product();
                product.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)));
                product.setProductName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)));
                product.setProductPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)));
                product.setProductQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY)));
                // cloud id
                int idx = cursor.getColumnIndex("cloud_id");
                if (idx != -1) product.setCloudId(cursor.getString(idx));
                return product;
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public int markProductSyncedByLocalId(int localId, String cloudId, long updatedAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        if (cloudId != null) v.put("cloud_id", cloudId);
        v.put("synced", 1);
        v.put("updated_at", updatedAt);
        return db.update(TABLE_PRODUCTS, v, COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(localId)});
    }

    public int markProductDirty(int localId, long updatedAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("synced", 0);
        v.put("updated_at", updatedAt);
        return db.update(TABLE_PRODUCTS, v, COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(localId)});
    }

    public int deleteProductByCloudId(String cloudId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PRODUCTS, "cloud_id = ?", new String[]{cloudId});
    }
    // Cursor for unsynced categories
    public Cursor getUnsyncedCategoriesCursor() {
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT * FROM categories WHERE cloud_id IS NULL OR dirty_at > synced_at";
        return db.rawQuery(q, null);
    }

    public void upsertCategoryFromCloud(String cloudId, String name, long updatedAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // try update by cloud_id
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_CATEGORY_NAME, name);
            cv.put("synced_at", updatedAt);
            cv.put("dirty_at", 0);
            cv.put("deleted", 0);
            int rows = db.update(TABLE_CATEGORIES, cv, "cloud_id = ?", new String[]{cloudId});
            if (rows == 0) {
                cv.put("cloud_id", cloudId);
                cv.put(COLUMN_CATEGORY_DATE_CREATED, String.valueOf(updatedAt));
                db.insert(TABLE_CATEGORIES, null, cv);
            }
        } finally {
            // keep DB open for caller
        }
    }

    public void deleteCategoryByCloudId(String cloudId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORIES, "cloud_id = ?", new String[]{cloudId});
    }

    public void markCategorySyncedByLocalId(long localId, String cloudId, long syncedAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("cloud_id", cloudId);
        cv.put("synced_at", syncedAt);
        cv.put("dirty_at", 0);
        db.update(TABLE_CATEGORIES, cv, COLUMN_CATEGORY_ID + " = ?", new String[]{String.valueOf(localId)});
    }

    public void markCategoryDirty(long localId, long dirtyAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("dirty_at", dirtyAt);
        db.update(TABLE_CATEGORIES, cv, COLUMN_CATEGORY_ID + " = ?", new String[]{String.valueOf(localId)});
    }
    // Cursor for unsynced sales
    public Cursor getUnsyncedSalesCursor() {
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT * FROM " + TABLE_SALES + " WHERE cloud_id IS NULL OR dirty_at > synced_at";
        return db.rawQuery(q, null);
    }

    // Get sale by local id
    public SaleRecord getSaleByLocalId(long localId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_SALES, null, COLUMN_SALE_ID + " = ?", new String[]{String.valueOf(localId)}, null, null, null);
        try {
            if (c != null && c.moveToFirst()) {
                SaleRecord s = new SaleRecord();
                s.setId(c.getLong(c.getColumnIndexOrThrow(COLUMN_SALE_ID)));
                s.setUserId(c.getString(c.getColumnIndexOrThrow(COLUMN_SALE_USER_ID)));
                s.setUserName(c.getString(c.getColumnIndexOrThrow(COLUMN_SALE_USER_NAME)));
                s.setSubtotal(c.getDouble(c.getColumnIndexOrThrow(COLUMN_SALE_SUBTOTAL)));
                s.setTax(c.getDouble(c.getColumnIndexOrThrow(COLUMN_SALE_TAX)));
                s.setTotal(c.getDouble(c.getColumnIndexOrThrow(COLUMN_SALE_TOTAL)));
                s.setPaymentMethod(c.getString(c.getColumnIndexOrThrow(COLUMN_SALE_PAYMENT_METHOD)));
                s.setTimestamp(c.getString(c.getColumnIndexOrThrow(COLUMN_SALE_TIMESTAMP)));
                int idx = c.getColumnIndex("cloud_id");
                if (idx != -1) s.setCloudId(c.getString(idx));
                return s;
            }
        } finally { if (c != null) c.close(); }
        return null;
    }

    // Get sale items for a sale (local sale_id)
    public List<SaleItem> getSaleItemsForSale(long saleLocalId) {
        List<SaleItem> out = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SALE_ITEMS, null, COLUMN_SALE_ITEM_SALE_ID + " = ?", new String[]{String.valueOf(saleLocalId)}, null, null, null);
        try {
            while (cursor.moveToNext()) {
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SALE_ITEM_PRODUCT_ID));
                String pname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SALE_ITEM_PRODUCT_NAME));
                int qty = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SALE_ITEM_QTY));
                double unitPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SALE_ITEM_UNIT_PRICE));
                SaleItem it = new SaleItem();
                it.product = getProductById(productId); // or set minimal product
                it.quantity = qty;
                out.add(it);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return out;
    }

    public void markSaleSyncedByLocalId(long localId, String cloudId, long syncedAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("cloud_id", cloudId);
        cv.put("synced_at", syncedAt);
        cv.put("dirty_at", 0);
        db.update(TABLE_SALES, cv, COLUMN_SALE_ID + " = ?", new String[]{String.valueOf(localId)});
    }

    public void markSaleItemSyncedByLocalId(long localId, String cloudId, long syncedAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("cloud_id", cloudId);
        cv.put("synced_at", syncedAt);
        cv.put("dirty_at", 0);
        db.update(TABLE_SALE_ITEMS, cv, COLUMN_SALE_ITEM_ID + " = ?", new String[]{String.valueOf(localId)});
    }
    public void upsertSaleItemFromCloud(String itemCloudId, String saleCloudId, String productName, int qty, double unitPrice, double lineTotal, long updatedAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // 1) Ensure sale header exists locally and obtain local sale id
            long saleLocalId = -1;
            Cursor c = db.query(TABLE_SALES, new String[]{COLUMN_SALE_ID},
                    "cloud_id = ?", new String[]{saleCloudId}, null, null, null);
            try {
                if (c != null && c.moveToFirst()) {
                    saleLocalId = c.getLong(c.getColumnIndexOrThrow(COLUMN_SALE_ID));
                }
            } finally {
                if (c != null) c.close();
            }

            if (saleLocalId == -1) {
                // Insert minimal sale header so we can attach items to it
                ContentValues sh = new ContentValues();
                sh.put("cloud_id", saleCloudId);
                sh.put("synced_at", updatedAt);
                sh.put("dirty_at", 0);
                // leave other fields null/0; caller may later upsert full header
                saleLocalId = db.insert(TABLE_SALES, null, sh);
                // if insert failed, abort gracefully
                if (saleLocalId == -1) {
                    db.endTransaction();
                    return;
                }
            }

            // 2) Prepare item values
            ContentValues iv = new ContentValues();
            iv.put(COLUMN_SALE_ITEM_PRODUCT_NAME, productName);
            iv.put(COLUMN_SALE_ITEM_QTY, qty);
            iv.put(COLUMN_SALE_ITEM_UNIT_PRICE, unitPrice);
            iv.put(COLUMN_SALE_ITEM_LINE_TOTAL, lineTotal);
            iv.put("synced_at", updatedAt);
            iv.put("dirty_at", 0);
            iv.put("deleted", 0);

            // 3) Try update by cloud id
            int rows = db.update(TABLE_SALE_ITEMS, iv, "cloud_id = ?", new String[]{itemCloudId});
            if (rows == 0) {
                // Insert new item and set its sale foreign key and cloud id
                iv.put("cloud_id", itemCloudId);
                iv.put(COLUMN_SALE_ITEM_SALE_ID, saleLocalId);
                db.insert(TABLE_SALE_ITEMS, null, iv);
            }

            db.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try { db.endTransaction(); } catch (Exception ignored) {}
        }
    }


    public void deleteSaleByCloudId(String saleCloudId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // find local sale id
            long saleLocalId = -1;
            Cursor c = db.query(TABLE_SALES, new String[]{COLUMN_SALE_ID},
                    "cloud_id = ?", new String[]{saleCloudId}, null, null, null);
            try {
                if (c != null && c.moveToFirst()) {
                    saleLocalId = c.getLong(c.getColumnIndexOrThrow(COLUMN_SALE_ID));
                }
            } finally {
                if (c != null) c.close();
            }

            // delete items for the sale local id (if found)
            if (saleLocalId != -1) {
                db.delete(TABLE_SALE_ITEMS, COLUMN_SALE_ITEM_SALE_ID + " = ?", new String[]{String.valueOf(saleLocalId)});
            }

            // delete sale header row(s) by cloud id
            db.delete(TABLE_SALES, "cloud_id = ?", new String[]{saleCloudId});

            db.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try { db.endTransaction(); } catch (Exception ignored) {}
        }
    }
    public long upsertSaleFromCloud(String saleCloudId, String userId, String userName, double subtotal, double tax, double total, String paymentMethod, Long timestamp, long updatedAt) {

        SQLiteDatabase db = this.getWritableDatabase();
        long localSaleId = -1;
        db.beginTransaction();
        try {
            // 1) Try find existing local sale by cloud_id
            String[] cols = new String[]{COLUMN_SALE_ID};
            String sel = "cloud_id = ?";
            String[] selArgs = new String[]{saleCloudId};

            Cursor c = null;
            try {
                c = db.query(TABLE_SALES, cols, sel, selArgs, null, null, null);
                if (c != null && c.moveToFirst()) {
                    localSaleId = c.getLong(c.getColumnIndexOrThrow(COLUMN_SALE_ID));
                }
            } finally {
                if (c != null) c.close();
            }

            // 2) Prepare values
            ContentValues vals = new ContentValues();
            vals.put(COLUMN_SALE_USER_ID, userId);
            vals.put(COLUMN_SALE_USER_NAME, userName);
            // user role not available from cloud for this call; leave untouched
            vals.put(COLUMN_SALE_SUBTOTAL, subtotal);
            vals.put(COLUMN_SALE_TAX, tax);
            vals.put(COLUMN_SALE_TOTAL, total);
            vals.put(COLUMN_SALE_PAYMENT_METHOD, paymentMethod);
            if (timestamp != null) vals.put(COLUMN_SALE_TIMESTAMP, String.valueOf(timestamp));
            // sync metadata
            vals.put("synced_at", updatedAt);
            vals.put("dirty_at", 0);
            vals.put("deleted", 0);

            if (localSaleId != -1) {
                // update existing row
                db.update(TABLE_SALES, vals, COLUMN_SALE_ID + " = ?", new String[]{String.valueOf(localSaleId)});
            } else {
                // insert new row (ensure we set cloud_id)
                vals.put("cloud_id", saleCloudId);
                localSaleId = db.insert(TABLE_SALES, null, vals);
            }

            db.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
            localSaleId = -1;
        } finally {
            try { db.endTransaction(); } catch (Exception ignored) {}
        }

        return localSaleId;
    }
    /* ---------- INVOICE helpers ---------- */

    /** Return a single Invoice model read from local DB by local id (you may have an Invoice class) */
    public Invoice getInvoiceByLocalId(long localId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_INVOICES, null, COLUMN_INVOICE_ID + " = ?", new String[]{String.valueOf(localId)}, null, null, null);
        try {
            if (c != null && c.moveToFirst()) {
                Invoice out = new Invoice();
                out.setId((int)(c.getLong(c.getColumnIndexOrThrow(COLUMN_INVOICE_ID))));
                out.setCustomerName(c.getString(c.getColumnIndexOrThrow(COLUMN_INVOICE_CUSTOMER_NAME)));
                out.setCustomerAddress(c.getString(c.getColumnIndexOrThrow(COLUMN_INVOICE_CUSTOMER_ADDRESS)));
                out.setCustomerContact(c.getString(c.getColumnIndexOrThrow(COLUMN_INVOICE_CUSTOMER_CONTACT)));
                out.setCustomerEmail(c.getString(c.getColumnIndexOrThrow(COLUMN_INVOICE_CUSTOMER_EMAIL)));
                out.setItemDetails(c.getString(c.getColumnIndexOrThrow(COLUMN_INVOICE_ITEM_DETAILS)));
                out.setTotalAmount(c.getDouble(c.getColumnIndexOrThrow(COLUMN_INVOICE_TOTAL_AMOUNT)));
                out.setFilePath(c.getString(c.getColumnIndexOrThrow(COLUMN_INVOICE_FILE_PATH)));
                out.setTimestamp(c.getString(c.getColumnIndexOrThrow(COLUMN_INVOICE_DATE_CREATED)));
                int idx = c.getColumnIndex("cloud_id");
                if (idx != -1) out.setCloudId(c.getString(idx));
                return out;
            }
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    /** Find invoice row by cloud id */
    public Invoice getInvoiceByCloudId(String cloudId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_INVOICES, null, "cloud_id = ?", new String[]{cloudId}, null, null, null);
        try {
            if (c != null && c.moveToFirst()) {
                Invoice out = new Invoice();
                out.setId((int) c.getLong(c.getColumnIndexOrThrow(COLUMN_INVOICE_ID)));
                out.setCustomerName(c.getString(c.getColumnIndexOrThrow(COLUMN_INVOICE_CUSTOMER_NAME)));
                out.setCustomerAddress(c.getString(c.getColumnIndexOrThrow(COLUMN_INVOICE_CUSTOMER_ADDRESS)));
                out.setCustomerContact(c.getString(c.getColumnIndexOrThrow(COLUMN_INVOICE_CUSTOMER_CONTACT)));
                out.setCustomerEmail(c.getString(c.getColumnIndexOrThrow(COLUMN_INVOICE_CUSTOMER_EMAIL)));
                out.setItemDetails(c.getString(c.getColumnIndexOrThrow(COLUMN_INVOICE_ITEM_DETAILS)));
                out.setTotalAmount(c.getDouble(c.getColumnIndexOrThrow(COLUMN_INVOICE_TOTAL_AMOUNT)));
                out.setFilePath(c.getString(c.getColumnIndexOrThrow(COLUMN_INVOICE_FILE_PATH)));
                out.setTimestamp(c.getString(c.getColumnIndexOrThrow(COLUMN_INVOICE_DATE_CREATED)));
                out.setCloudId(c.getString(c.getColumnIndexOrThrow("cloud_id")));
                return out;
            }
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    /** Mark invoice as synced (set cloud_id + synced_at, clear dirty flag) */
    public int markInvoiceSyncedByLocalId(long localId, String cloudId, long syncedAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        if (cloudId != null) v.put("cloud_id", cloudId);
        v.put("synced_at", syncedAt);
        v.put("dirty_at", 0);
        v.put("deleted", 0);
        return db.update(TABLE_INVOICES, v, COLUMN_INVOICE_ID + " = ?", new String[]{String.valueOf(localId)});
    }

    /** Cursor of local invoices that need to be pushed (new or dirty) */
    public Cursor getUnsyncedInvoicesCursor() {
        SQLiteDatabase db = this.getReadableDatabase();
        // rows where no cloud_id (new) OR dirty_at > synced_at (updated locally)
        String where = "cloud_id IS NULL OR dirty_at > IFNULL(synced_at,0)";
        return db.query(TABLE_INVOICES, null, where, null, null, null, COLUMN_INVOICE_DATE_CREATED + " DESC");
    }

    /** Create or update a local invoice from cloud data */
    public long upsertInvoiceFromCloud(String cloudId,
                                       String customerName,
                                       String customerAddress,
                                       String customerContact,
                                       String customerEmail,
                                       String itemDetails,
                                       double totalAmount,
                                       String filePath,
                                       Long timestamp,
                                       long updatedAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        long localId = -1;
        db.beginTransaction();
        try {
            // try find existing by cloud_id
            Cursor c = db.query(TABLE_INVOICES, new String[]{COLUMN_INVOICE_ID}, "cloud_id = ?", new String[]{cloudId}, null, null, null);
            try {
                if (c != null && c.moveToFirst()) {
                    localId = c.getLong(c.getColumnIndexOrThrow(COLUMN_INVOICE_ID));
                }
            } finally {
                if (c != null) c.close();
            }

            ContentValues values = new ContentValues();
            values.put(COLUMN_INVOICE_CUSTOMER_NAME, customerName);
            values.put(COLUMN_INVOICE_CUSTOMER_ADDRESS, customerAddress);
            values.put(COLUMN_INVOICE_CUSTOMER_CONTACT, customerContact);
            values.put(COLUMN_INVOICE_CUSTOMER_EMAIL, customerEmail);
            values.put(COLUMN_INVOICE_ITEM_DETAILS, itemDetails);
            values.put(COLUMN_INVOICE_TOTAL_AMOUNT, totalAmount);
            values.put(COLUMN_INVOICE_FILE_PATH, filePath);
            if (timestamp != null) values.put(COLUMN_INVOICE_DATE_CREATED, String.valueOf(timestamp));
            // sync metadata
            values.put("synced_at", updatedAt);
            values.put("dirty_at", 0);
            values.put("deleted", 0);
            if (localId != -1) {
                db.update(TABLE_INVOICES, values, COLUMN_INVOICE_ID + " = ?", new String[]{String.valueOf(localId)});
            } else {
                values.put("cloud_id", cloudId);
                localId = db.insert(TABLE_INVOICES, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
            localId = -1;
        } finally {
            try { db.endTransaction(); } catch (Exception ignore) {}
        }
        return localId;
    }

    /** Soft-delete invoice by cloud id (mark deleted flag) */
    public int deleteInvoiceByCloudId(String cloudId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("deleted", 1);
        v.put("dirty_at", System.currentTimeMillis());
        return db.update(TABLE_INVOICES, v, "cloud_id = ?", new String[]{cloudId});
    }

    /** Mark a local invoice as dirty (local change) */
    public int markInvoiceDirty(long localId, long now) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("dirty_at", now);
        return db.update(TABLE_INVOICES, v, COLUMN_INVOICE_ID + " = ?", new String[]{String.valueOf(localId)});
    }
    /* ---------- ESTIMATE helpers ---------- */

    public Estimate getEstimateByLocalId(long localId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_ESTIMATES, null, COLUMN_ESTIMATE_ID + " = ?", new String[]{String.valueOf(localId)}, null, null, null);
        try {
            if (c != null && c.moveToFirst()) {
                Estimate out = new Estimate();
                out.setId((int)c.getLong(c.getColumnIndexOrThrow(COLUMN_ESTIMATE_ID)));
                out.setCustomerName(c.getString(c.getColumnIndexOrThrow(COLUMN_ESTIMATE_CUSTOMER_NAME)));
                out.setCustomerAddress(c.getString(c.getColumnIndexOrThrow(COLUMN_ESTIMATE_CUSTOMER_ADDRESS)));
                out.setCustomerContact(c.getString(c.getColumnIndexOrThrow(COLUMN_ESTIMATE_CUSTOMER_CONTACT)));
                out.setCustomerEmail(c.getString(c.getColumnIndexOrThrow(COLUMN_ESTIMATE_CUSTOMER_EMAIL)));
                out.setItemDetails(c.getString(c.getColumnIndexOrThrow(COLUMN_ESTIMATE_ITEM_DETAILS)));
                out.setTotalAmount(c.getDouble(c.getColumnIndexOrThrow(COLUMN_ESTIMATE_TOTAL_AMOUNT)));
                out.setFilePath(c.getString(c.getColumnIndexOrThrow(COLUMN_ESTIMATE_FILE_PATH)));
                out.setTimestamp(c.getString(c.getColumnIndexOrThrow(COLUMN_ESTIMATE_DATE_CREATED)));
                int idx = c.getColumnIndex("cloud_id");
                if (idx != -1) out.setCloudId(c.getString(idx));
                return out;
            }
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    public Estimate getEstimateByCloudId(String cloudId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_ESTIMATES, null, "cloud_id = ?", new String[]{cloudId}, null, null, null);
        try {
            if (c != null && c.moveToFirst()) {
                Estimate out = new Estimate();
                out.setId((int)(c.getLong(c.getColumnIndexOrThrow(COLUMN_ESTIMATE_ID))));
                out.setCustomerName(c.getString(c.getColumnIndexOrThrow(COLUMN_ESTIMATE_CUSTOMER_NAME)));
                out.setCustomerAddress(c.getString(c.getColumnIndexOrThrow(COLUMN_ESTIMATE_CUSTOMER_ADDRESS)));
                out.setCustomerContact(c.getString(c.getColumnIndexOrThrow(COLUMN_ESTIMATE_CUSTOMER_CONTACT)));
                out.setCustomerEmail(c.getString(c.getColumnIndexOrThrow(COLUMN_ESTIMATE_CUSTOMER_EMAIL)));
                out.setItemDetails(c.getString(c.getColumnIndexOrThrow(COLUMN_ESTIMATE_ITEM_DETAILS)));
                out.setTotalAmount(c.getDouble(c.getColumnIndexOrThrow(COLUMN_ESTIMATE_TOTAL_AMOUNT)));
                out.setFilePath(c.getString(c.getColumnIndexOrThrow(COLUMN_ESTIMATE_FILE_PATH)));
                out.setTimestamp(c.getString(c.getColumnIndexOrThrow(COLUMN_ESTIMATE_DATE_CREATED)));
                out.setCloudId(c.getString(c.getColumnIndexOrThrow("cloud_id")));
                return out;
            }
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    public int markEstimateSyncedByLocalId(long localId, String cloudId, long syncedAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        if (cloudId != null) v.put("cloud_id", cloudId);
        v.put("synced_at", syncedAt);
        v.put("dirty_at", 0);
        v.put("deleted", 0);
        return db.update(TABLE_ESTIMATES, v, COLUMN_ESTIMATE_ID + " = ?", new String[]{String.valueOf(localId)});
    }

    public Cursor getUnsyncedEstimatesCursor() {
        SQLiteDatabase db = this.getReadableDatabase();
        String where = "cloud_id IS NULL OR dirty_at > IFNULL(synced_at,0)";
        return db.query(TABLE_ESTIMATES, null, where, null, null, null, COLUMN_ESTIMATE_DATE_CREATED + " DESC");
    }

    public long upsertEstimateFromCloud(String cloudId,
                                        String customerName,
                                        String customerAddress,
                                        String customerContact,
                                        String customerEmail,
                                        String itemDetails,
                                        double totalAmount,
                                        String filePath,
                                        Long timestamp,
                                        long updatedAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        long localId = -1;
        db.beginTransaction();
        try {
            Cursor c = db.query(TABLE_ESTIMATES, new String[]{COLUMN_ESTIMATE_ID}, "cloud_id = ?", new String[]{cloudId}, null, null, null);
            try {
                if (c != null && c.moveToFirst()) {
                    localId = c.getLong(c.getColumnIndexOrThrow(COLUMN_ESTIMATE_ID));
                }
            } finally {
                if (c != null) c.close();
            }

            ContentValues values = new ContentValues();
            values.put(COLUMN_ESTIMATE_CUSTOMER_NAME, customerName);
            values.put(COLUMN_ESTIMATE_CUSTOMER_ADDRESS, customerAddress);
            values.put(COLUMN_ESTIMATE_CUSTOMER_CONTACT, customerContact);
            values.put(COLUMN_ESTIMATE_CUSTOMER_EMAIL, customerEmail);
            values.put(COLUMN_ESTIMATE_ITEM_DETAILS, itemDetails);
            values.put(COLUMN_ESTIMATE_TOTAL_AMOUNT, totalAmount);
            values.put(COLUMN_ESTIMATE_FILE_PATH, filePath);
            if (timestamp != null) values.put(COLUMN_ESTIMATE_DATE_CREATED, String.valueOf(timestamp));
            values.put("synced_at", updatedAt);
            values.put("dirty_at", 0);
            values.put("deleted", 0);

            if (localId != -1) {
                db.update(TABLE_ESTIMATES, values, COLUMN_ESTIMATE_ID + " = ?", new String[]{String.valueOf(localId)});
            } else {
                values.put("cloud_id", cloudId);
                localId = db.insert(TABLE_ESTIMATES, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
            localId = -1;
        } finally {
            try { db.endTransaction(); } catch (Exception ignored) {}
        }
        return localId;
    }

    public int deleteEstimateByCloudId(String cloudId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("deleted", 1);
        v.put("dirty_at", System.currentTimeMillis());
        return db.update(TABLE_ESTIMATES, v, "cloud_id = ?", new String[]{cloudId});
    }

    public int markEstimateDirty(long localId, long now) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("dirty_at", now);
        return db.update(TABLE_ESTIMATES, v, COLUMN_ESTIMATE_ID + " = ?", new String[]{String.valueOf(localId)});
    }




    public int deleteSaleItemByCloudId(String cloudId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("deleted", 1);
        v.put("dirty_at", System.currentTimeMillis());
        return db.update(TABLE_SALE_ITEMS, v, "cloud_id = ?", new String[]{cloudId});
    }



    public Cursor getUnsyncedSaleItemsCursorForSale(long saleLocalId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String q = "SELECT * FROM " + TABLE_SALE_ITEMS + " WHERE " + COLUMN_SALE_ITEM_SALE_ID + " = ? AND (cloud_id IS NULL OR dirty_at > synced_at)";
        return db.rawQuery(q, new String[]{String.valueOf(saleLocalId)});
    }












}