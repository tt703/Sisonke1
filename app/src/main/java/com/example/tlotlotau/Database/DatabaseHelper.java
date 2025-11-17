package com.example.tlotlotau.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    private static final String DATABASE_NAME = "invoices.db";
    private static final int DATABASE_VERSION = 18;

    private static final String TABLE_CUSTOMERS = "customers";
    private static final String COLUMN_CUSTOMER_ID = "_id";
    private static final String COLUMN_CUSTOMER_NAME = "name";
    private static final String COLUMN_CUSTOMER_PHONE = "phone";
    private static final String COLUMN_CUSTOMER_EMAIL = "email";
    private static final String COLUMN_CUSTOMER_ADDRESS = "address";
    private static final String COLUMN_CUSTOMER_DATE_CREATED = "date_created";
    private static final String COLUMN_NUM_ESTIMATES = "num_estimates";
    private static final String COLUMN_NUM_INVOICES = "num_invoices";
    private static final String COLUMN_TOTAL_AMOUNT = "total_amount";


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

    private static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_PRODUCT_ID = "_id";
    private static final String COLUMN_PRODUCT_NAME = "product_name";
    private static final String COLUMN_PRODUCT_PRICE = "price";
    private static final String COLUMN_PRODUCT_DESCRIPTION = "description";
    private static final String COLUMN_PRODUCT_QUANTITY = "quantity";
    private static final String COLUMN_PRODUCT_DATE_CREATED = "date_created";
    private static final String COLUMN_PRODUCT_QR_CODE = "qr_code";
    private static final String  COLUNM_PRODUCT_CATEGORY_ID = "category_id";


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
    private static final String COLUMN_CATEGORY_ID = "_id";
    private static final String COLUMN_CATEGORY_NAME = "name";
    private static final String COLUMN_CATEGORY_DATE_CREATED = "date_created";
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

    private static final String TABLE_SALE_ITEMS = "sale_items";
    private static final String COLUMN_SALE_ITEM_ID = "_id";
    private static final String COLUMN_SALE_ITEM_SALE_ID = "sale_id";
    private static final String COLUMN_SALE_ITEM_PRODUCT_ID = "product_id";
    private static final String COLUMN_SALE_ITEM_PRODUCT_NAME = "product_name";
    private static final String COLUMN_SALE_ITEM_QTY = "quantity";
    private static final String COLUMN_SALE_ITEM_UNIT_PRICE = "unit_price";
    private static final String COLUMN_SALE_ITEM_LINE_TOTAL = "line_total";

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "_id";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_ROLE = "role";
    private static final String COLUMN_USER_EMAIL = "email";


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
            COLUMN_ESTIMATE_DATE_CREATED + " TEXT)";

    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
            COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PRODUCT_NAME + " TEXT, " +
            COLUMN_PRODUCT_PRICE + " REAL, " +
            COLUMN_PRODUCT_DESCRIPTION + " TEXT, " +
            COLUMN_PRODUCT_QUANTITY + " INTEGER, " +
            COLUMN_PRODUCT_DATE_CREATED + " TEXT, " +
            COLUMN_PRODUCT_QR_CODE + " TEXT,"+
            COLUNM_PRODUCT_CATEGORY_ID + " INTEGER DEFAULT NULL)";


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
            COLUMN_NUM_ESTIMATES + " INTEGER DEFAULT 0," +
            COLUMN_NUM_INVOICES + " INTEGER DEFAULT 0," +
            COLUMN_TOTAL_AMOUNT + " REAL DEFAULT 0)";

    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES + " (" +
            COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CATEGORY_NAME + " TEXT, " +
            COLUMN_CATEGORY_DATE_CREATED + " TEXT)";
    private static final String CREATE_TABLE_SALES = "CREATE TABLE " + TABLE_SALES + " (" +
            COLUMN_SALE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SALE_USER_ID + " TEXT, " +
            COLUMN_SALE_USER_NAME + " TEXT, " +
            COLUMN_SALE_USER_ROLE + " TEXT, " +
            COLUMN_SALE_SUBTOTAL + " REAL, " +
            COLUMN_SALE_TAX + " REAL, " +
            COLUMN_SALE_TOTAL + " REAL, " +
            COLUMN_SALE_PAYMENT_METHOD + " TEXT, " +
            COLUMN_SALE_TIMESTAMP + " TEXT)";

    private static final String CREATE_TABLE_SALE_ITEMS = "CREATE TABLE " + TABLE_SALE_ITEMS + " (" +
            COLUMN_SALE_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SALE_ITEM_SALE_ID + " INTEGER, " +
            COLUMN_SALE_ITEM_PRODUCT_ID + " INTEGER, " +
            COLUMN_SALE_ITEM_PRODUCT_NAME + " TEXT, " +
            COLUMN_SALE_ITEM_QTY + " INTEGER, " +
            COLUMN_SALE_ITEM_UNIT_PRICE + " REAL, " +
            COLUMN_SALE_ITEM_LINE_TOTAL + " REAL, " +
            "FOREIGN KEY(" + COLUMN_SALE_ITEM_SALE_ID + ") REFERENCES " + TABLE_SALES + "(" + COLUMN_SALE_ID + "))";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " (" +
            COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
            COLUMN_USER_NAME + " TEXT, " +
            COLUMN_USER_ROLE + " TEXT )" ;






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
        db.execSQL(CREATE_TABLE_USERS);
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
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
                String totalAmount = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_AMOUNT));
                String numEstimates = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NUM_ESTIMATES));

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
    public boolean upsertUser(String userId, String name, String role, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_ID, userId);
            values.put(COLUMN_USER_NAME, name);
            values.put(COLUMN_USER_ROLE, role);
            long res = db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            return res != -1;
        } finally {
            db.close();
        }
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

}