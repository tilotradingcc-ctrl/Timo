package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val companyName: String,
    val email: String,
    val phone: String,
    val address: String
)

@Entity(tableName = "inventory")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val sku: String,
    val category: String, // "Equipment", "Consumables", "Spare Parts", "Services"
    val stockQuantity: Int,
    val price: Double,
    val description: String,
    val minStockAlert: Int = 3
)

@Entity(tableName = "documents")
data class Document(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val docNumber: String, // e.g. "INV-1001", "QT-1001", "PO-10001", "GRN-1001"
    val docType: String, // "INVOICE", "QUOTATION", "PURCHASE_ORDER", "GRN"
    val customerId: Long? = null,
    val customerName: String? = null, // cached for easy listing
    val supplierName: String? = null, // for PO and GRN
    val date: Long = System.currentTimeMillis(),
    val totalAmount: Double,
    val status: String, // "DRAFT", "PENDING", "COMPLETED", "VOIDED", "RECEIVED"
    val linkedDocId: Long? = null, // PO linked to GRN, or Quotation linked to Invoice
    val remarks: String = ""
)

@Entity(tableName = "document_items")
data class DocumentItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val documentId: Long,
    val itemId: Long? = null, // Null for custom services/unlisted products
    val itemName: String,
    val quantity: Int,
    val unitPrice: Double,
    val receivedQuantity: Int = 0 // purely for GRN comparison vs Ordered
)

data class DocumentWithItems(
    @Embedded val document: Document,
    @Relation(
        parentColumn = "id",
        entityColumn = "documentId"
    )
    val items: List<DocumentItem>
)

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomersFlow(): Flow<List<Customer>>

    @Query("SELECT * FROM customers")
    suspend fun getAllCustomers(): List<Customer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Delete
    suspend fun deleteCustomer(customer: Customer)
}

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory ORDER BY name ASC")
    fun getAllInventoryFlow(): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory")
    suspend fun getAllInventory(): List<InventoryItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItem(item: InventoryItem): Long

    @Query("UPDATE inventory SET stockQuantity = stockQuantity + :change WHERE id = :id")
    suspend fun updateStock(id: Long, change: Int)

    @Query("UPDATE inventory SET stockQuantity = :quantity WHERE id = :id")
    suspend fun setStock(id: Long, quantity: Int)

    @Delete
    suspend fun deleteInventoryItem(item: InventoryItem)
}

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents ORDER BY date DESC")
    fun getAllDocumentsFlow(): Flow<List<Document>>

    @Query("SELECT * FROM documents WHERE docType = :type ORDER BY date DESC")
    fun getDocumentsByTypeFlow(type: String): Flow<List<Document>>

    @Transaction
    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getDocumentWithItemsById(id: Long): DocumentWithItems?

    @Transaction
    @Query("SELECT * FROM documents WHERE docNumber = :docNumber")
    suspend fun getDocumentWithItemsByNumber(docNumber: String): DocumentWithItems?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: Document): Long

    @Query("UPDATE documents SET status = :status WHERE id = :id")
    suspend fun updateDocumentStatus(id: Long, status: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocumentItem(item: DocumentItem): Long

    @Query("DELETE FROM document_items WHERE documentId = :documentId")
    suspend fun deleteDocumentItems(documentId: Long)

    @Delete
    suspend fun deleteDocument(document: Document)
}
