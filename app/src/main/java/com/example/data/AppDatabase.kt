package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Customer::class,
        InventoryItem::class,
        Document::class,
        DocumentItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun documentDao(): DocumentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "printpos_database"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedDatabase(database)
                    }
                }
            }
        }

        private suspend fun seedDatabase(db: AppDatabase) {
            val customerDao = db.customerDao()
            val inventoryDao = db.inventoryDao()
            val documentDao = db.documentDao()

            // Seed Customers
            val c1 = Customer(
                name = "Marcus Vance",
                companyName = "Apex Printing Limited",
                email = "info@apexprint.com",
                phone = "+1 555-0192",
                address = "782 Industrial Blvd, Suite C, Chicago IL"
            )
            val c2 = Customer(
                name = "Sarah Jenkins",
                companyName = "GigaGraphics Designs",
                email = "sarah@gigagraphics.com",
                phone = "+1 555-0348",
                address = "109 Creative Avenue, New York NY"
            )
            val c3 = Customer(
                name = "David Lin",
                companyName = "Swift Press Works",
                email = "contact@swiftpress.com",
                phone = "+1 555-0824",
                address = "44 Main St, Seattle WA"
            )
            val cid1 = customerDao.insertCustomer(c1)
            val cid2 = customerDao.insertCustomer(c2)
            val cid3 = customerDao.insertCustomer(c3)

            // Seed Inventory Items
            val items = listOf(
                InventoryItem(
                    name = "Titan Digital Inkjet Press V3",
                    sku = "EQ-TITAN-V3",
                    category = "Equipment",
                    stockQuantity = 2,
                    price = 24500.0,
                    description = "Commercial-grade high speed digital inkjet printing press."
                ),
                InventoryItem(
                    name = "LaserJet Heavy Duty Guillotine B2",
                    sku = "EQ-GUIL-B2",
                    category = "Equipment",
                    stockQuantity = 3,
                    price = 8900.0,
                    description = "Automatic clamp hydraulic paper cutter for large print run finishing."
                ),
                InventoryItem(
                    name = "Premium Cyan Laser Toner C90",
                    sku = "CS-TONER-CY",
                    category = "Consumables",
                    stockQuantity = 15,
                    price = 120.0,
                    description = "High-yield laser monochrome & colour printer toner cartridge cyan."
                ),
                InventoryItem(
                    name = "Premium Magenta Laser Toner M90",
                    sku = "CS-TONER-MG",
                    category = "Consumables",
                    stockQuantity = 12,
                    price = 120.0,
                    description = "High-yield laser monochrome & colour printer toner cartridge magenta."
                ),
                InventoryItem(
                    name = "Artic White Glossy Art Paper A3",
                    sku = "CS-PAP-A3G",
                    category = "Consumables",
                    stockQuantity = 40,
                    price = 45.0,
                    description = "500-sheet ream of glossy art paper ideal for flyers and catalogs."
                ),
                InventoryItem(
                    name = "Matte Vinyl Banner Roll 1.2m x 50m",
                    sku = "CS-VIN-1250",
                    category = "Consumables",
                    stockQuantity = 10,
                    price = 180.0,
                    description = "Heavy duty matte vinyl rolls for trade show banner printing."
                ),
                InventoryItem(
                    name = "Digital Press Spare Feeder Belt",
                    sku = "SP-FEED-BELT",
                    category = "Spare Parts",
                    stockQuantity = 5,
                    price = 75.0,
                    description = "Replacement rubber feed conveyor belts for sheet-fed presses."
                )
            )
            for (item in items) {
                inventoryDao.insertInventoryItem(item)
            }

            // Seed initial sample documents to instantly showcase features:
            // 1. One draft Quotation
            val qId = documentDao.insertDocument(
                Document(
                    docNumber = "QT-2026-001",
                    docType = "QUOTATION",
                    customerId = cid1,
                    customerName = "Apex Printing Limited",
                    totalAmount = 24500.0,
                    status = "DRAFT",
                    remarks = "Quotation requested for Titan Digital Press installation."
                )
            )
            documentDao.insertDocumentItem(
                DocumentItem(
                    documentId = qId,
                    itemId = 1, // Titan Press
                    itemName = "Titan Digital Inkjet Press V3",
                    quantity = 1,
                    unitPrice = 24500.0
                )
            )

            // 2. One completed Invoice
            val invId = documentDao.insertDocument(
                Document(
                    docNumber = "INV-2026-001",
                    docType = "INVOICE",
                    customerId = cid2,
                    customerName = "GigaGraphics Designs",
                    totalAmount = 525.0,
                    status = "COMPLETED",
                    remarks = "Standard consumables delivery."
                )
            )
            documentDao.insertDocumentItem(
                DocumentItem(
                    documentId = invId,
                    itemId = 3, // toner
                    itemName = "Premium Cyan Laser Toner C90",
                    quantity = 2,
                    unitPrice = 120.0
                )
            )
            documentDao.insertDocumentItem(
                DocumentItem(
                    documentId = invId,
                    itemId = 5, // A3 paper
                    itemName = "Artic White Glossy Art Paper A3",
                    quantity = 5,
                    unitPrice = 45.0
                )
            )
            documentDao.insertDocumentItem(
                DocumentItem(
                    documentId = invId,
                    itemId = 6, // vinyl
                    itemName = "Matte Vinyl Banner Roll 1.2m x 50m",
                    quantity = 1,
                    unitPrice = 180.0
                )
            )

            // 3. One Purchase Order (PO) to Supplier
            val poId = documentDao.insertDocument(
                Document(
                    docNumber = "PO-2026-001",
                    docType = "PURCHASE_ORDER",
                    supplierName = "Global Printing Supplies Ltd",
                    totalAmount = 1440.0,
                    status = "PENDING",
                    remarks = "Restock toner and printer parts."
                )
            )
            documentDao.insertDocumentItem(
                DocumentItem(
                    documentId = poId,
                    itemId = 3,
                    itemName = "Premium Cyan Laser Toner C90",
                    quantity = 10,
                    unitPrice = 100.0 // Wholesale rate
                )
            )
            documentDao.insertDocumentItem(
                DocumentItem(
                    documentId = poId,
                    itemId = 7,
                    itemName = "Digital Press Spare Feeder Belt",
                    quantity = 10,
                    unitPrice = 44.0 // Wholesale rate
                )
            )
        }
    }
}
