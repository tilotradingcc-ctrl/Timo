package com.example.data

import kotlinx.coroutines.flow.Flow

class PrintPosRepository(private val database: AppDatabase) {
    private val customerDao = database.customerDao()
    private val inventoryDao = database.inventoryDao()
    private val documentDao = database.documentDao()

    val allCustomers: Flow<List<Customer>> = customerDao.getAllCustomersFlow()
    val allInventory: Flow<List<InventoryItem>> = inventoryDao.getAllInventoryFlow()
    val allDocuments: Flow<List<Document>> = documentDao.getAllDocumentsFlow()

    fun getDocumentsByType(type: String): Flow<List<Document>> {
        return documentDao.getDocumentsByTypeFlow(type)
    }

    suspend fun getAllCustomers(): List<Customer> = customerDao.getAllCustomers()
    suspend fun getAllInventory(): List<InventoryItem> = inventoryDao.getAllInventory()

    suspend fun insertCustomer(customer: Customer): Long {
        return customerDao.insertCustomer(customer)
    }

    suspend fun deleteCustomer(customer: Customer) {
        customerDao.deleteCustomer(customer)
    }

    suspend fun insertInventoryItem(item: InventoryItem): Long {
        return inventoryDao.insertInventoryItem(item)
    }

    suspend fun deleteInventoryItem(item: InventoryItem) {
        inventoryDao.deleteInventoryItem(item)
    }

    suspend fun updateStock(id: Long, change: Int) {
        inventoryDao.updateStock(id, change)
    }

    suspend fun setStock(id: Long, quantity: Int) {
        inventoryDao.setStock(id, quantity)
    }

    suspend fun getDocumentWithItems(id: Long): DocumentWithItems? {
        return documentDao.getDocumentWithItemsById(id)
    }

    suspend fun getDocumentWithItemsByNumber(docNumber: String): DocumentWithItems? {
        return documentDao.getDocumentWithItemsByNumber(docNumber)
    }

    suspend fun insertDocumentWithItems(document: Document, items: List<DocumentItem>): Long {
        val docId = documentDao.insertDocument(document)
        documentDao.deleteDocumentItems(docId)
        for (item in items) {
            documentDao.insertDocumentItem(item.copy(documentId = docId))
        }
        return docId
    }

    suspend fun updateDocumentStatus(id: Long, status: String) {
        documentDao.updateDocumentStatus(id, status)
    }

    suspend fun deleteDocument(document: Document) {
        documentDao.deleteDocumentItems(document.id)
        documentDao.deleteDocument(document)
    }
}
