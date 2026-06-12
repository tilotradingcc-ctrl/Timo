package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class Screen {
    DASHBOARD,
    DOCUMENTS,
    INVENTORY,
    CUSTOMERS,
    CREATE_DOCUMENT,
    DOCUMENT_DETAIL
}

data class DraftItem(
    val itemId: Long? = null,
    val name: String,
    val quantity: Int,
    val price: Double,
    val receivedQuantity: Int = 0 // purely for GRN tracking
)

class PrintPosViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PrintPosRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = PrintPosRepository(database)
    }

    // UI Navigation State
    var currentScreen by mutableStateOf(Screen.DASHBOARD)
        private set

    // Selected items for Details / Creation
    var selectedDocumentId by mutableStateOf<Long?>(null)
        private set
    var selectedDocumentWithItems by mutableStateOf<DocumentWithItems?>(null)
        private set

    // Creation Helper States
    var creationDocType by mutableStateOf("INVOICE") // INVOICE, QUOTATION, PURCHASE_ORDER, GRN
        private set
    var creationSelectedCustomerId by mutableStateOf<Long?>(null)
        private set
    var creationSelectedSupplierName by mutableStateOf("")
        private set
    var creationRemarks by mutableStateOf("")
        private set
    var draftItems = mutableStateListOf<DraftItem>()
        private set

    // Filter and search
    var documentTypeFilter by mutableStateOf("ALL") // ALL, INVOICE, QUOTATION, PURCHASE_ORDER, GRN
        private set
    var inventorySearchQuery by mutableStateOf("")
    var customerSearchQuery by mutableStateOf("")

    // Flows for tables
    val customers: StateFlow<List<Customer>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inventory: StateFlow<List<InventoryItem>> = repository.allInventory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val documents: StateFlow<List<Document>> = repository.allDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Simulated alerts / Toast notifications
    var toastMessage by mutableStateOf<String?>(null)
        private set

    fun clearToast() {
        toastMessage = null
    }

    fun showToast(msg: String) {
        toastMessage = msg
    }

    // Navigation actions
    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }

    fun selectDocument(documentId: Long) {
        selectedDocumentId = documentId
        viewModelScope.launch {
            val doc = repository.getDocumentWithItems(documentId)
            selectedDocumentWithItems = doc
            if (doc != null) {
                navigateTo(Screen.DOCUMENT_DETAIL)
            } else {
                showToast("Document not found!")
            }
        }
    }

    // Setup Create screen
    fun startCreateDocument(type: String, prefilledCustomerId: Long? = null, prefilledSupplier: String? = null, prefilledItems: List<DraftItem> = emptyList(), linkedDocId: Long? = null) {
        creationDocType = type
        creationSelectedCustomerId = prefilledCustomerId
        creationSelectedSupplierName = prefilledSupplier ?: ""
        creationRemarks = if (linkedDocId != null) "Linked to Document ID #$linkedDocId" else ""
        draftItems.clear()
        draftItems.addAll(prefilledItems)
        navigateTo(Screen.CREATE_DOCUMENT)
    }

    // Customer operations
    fun addCustomer(name: String, companyName: String, email: String, phone: String, address: String) {
        viewModelScope.launch {
            repository.insertCustomer(
                Customer(
                    name = name,
                    companyName = companyName,
                    email = email,
                    phone = phone,
                    address = address
                )
            )
            showToast("Customer added successfully")
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
            showToast("Customer removed")
        }
    }

    // Inventory operations
    fun addInventoryItem(name: String, sku: String, category: String, qty: Int, price: Double, desc: String, lowStockAlert: Int) {
        viewModelScope.launch {
            repository.insertInventoryItem(
                InventoryItem(
                    name = name,
                    sku = sku,
                    category = category,
                    stockQuantity = qty,
                    price = price,
                    description = desc,
                    minStockAlert = lowStockAlert
                )
            )
            showToast("Item saved to Inventory")
        }
    }

    fun adjustStock(item: InventoryItem, change: Int) {
        viewModelScope.launch {
            repository.updateStock(item.id, change)
            showToast("Stock updated")
        }
    }

    fun deleteInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            repository.deleteInventoryItem(item)
            showToast("Item deleted from inventory")
        }
    }

    // POS / Document cart building
    fun addItemToDraft(item: InventoryItem, qty: Int) {
        val existingIndex = draftItems.indexOfFirst { it.itemId == item.id }
        if (existingIndex >= 0) {
            val currentQty = draftItems[existingIndex].quantity
            val currentPrice = draftItems[existingIndex].price
            draftItems[existingIndex] = draftItems[existingIndex].copy(quantity = currentQty + qty)
        } else {
            draftItems.add(
                DraftItem(
                    itemId = item.id,
                    name = item.name,
                    quantity = qty,
                    price = item.price
                )
            )
        }
        showToast("Added to draft")
    }

    fun updateDraftItemQty(index: Int, qty: Int) {
        if (index in draftItems.indices) {
            if (qty <= 0) {
                draftItems.removeAt(index)
            } else {
                draftItems[index] = draftItems[index].copy(quantity = qty)
            }
        }
    }

    fun updateDraftItemReceivedQty(index: Int, receivedQty: Int) {
        if (index in draftItems.indices) {
            draftItems[index] = draftItems[index].copy(receivedQuantity = receivedQty)
        }
    }

    fun removeItemFromDraft(index: Int) {
        if (index in draftItems.indices) {
            draftItems.removeAt(index)
        }
    }

    fun selectCreationCustomer(customerId: Long?) {
        creationSelectedCustomerId = customerId
    }

    fun updateCreationSupplierName(name: String) {
        creationSelectedSupplierName = name
    }

    fun updateRemarks(txt: String) {
        creationRemarks = txt
    }

    // Save final document
    fun saveDraftDocument(docNumber: String, customStatus: String? = null) {
        if (draftItems.isEmpty()) {
            showToast("Cannot save empty document")
            return
        }

        val type = creationDocType
        val total = draftItems.sumOf { it.price * it.quantity }

        viewModelScope.launch {
            // Find customer details if applicable
            var custName: String? = null
            if (creationSelectedCustomerId != null) {
                custName = customers.value.find { it.id == creationSelectedCustomerId }?.companyName
            }

            val statusVal = customStatus ?: when (type) {
                "QUOTATION" -> "DRAFT"
                "INVOICE" -> "COMPLETED"
                "PURCHASE_ORDER" -> "PENDING"
                "GRN" -> "RECEIVED"
                else -> "DRAFT"
            }

            val document = Document(
                docNumber = docNumber,
                docType = type,
                customerId = creationSelectedCustomerId,
                customerName = custName,
                supplierName = if (type == "PURCHASE_ORDER" || type == "GRN") creationSelectedSupplierName else null,
                totalAmount = total,
                status = statusVal,
                remarks = creationRemarks
            )

            val docItems = draftItems.map {
                DocumentItem(
                    documentId = 0,
                    itemId = it.itemId,
                    itemName = it.name,
                    quantity = it.quantity,
                    unitPrice = it.price,
                    receivedQuantity = if (type == "GRN") it.receivedQuantity else 0
                )
            }

            val docId = repository.insertDocumentWithItems(document, docItems)

            // Adjust inventory stock based on type
            if (statusVal == "COMPLETED" && type == "INVOICE") {
                // Sells items -> Decrease stock
                for (item in docItems) {
                    if (item.itemId != null) {
                        repository.updateStock(item.itemId, -item.quantity)
                    }
                }
            } else if (statusVal == "RECEIVED" && type == "GRN") {
                // Receives items -> Increase stock by received qty
                for (item in docItems) {
                    if (item.itemId != null) {
                        repository.updateStock(item.itemId, item.receivedQuantity)
                    }
                }
            }

            showToast("$type saved as $docNumber")
            draftItems.clear()
            navigateTo(Screen.DOCUMENTS)
        }
    }

    // Update existing document status (e.g. mark Invoice as PAID or VOIDED)
    fun setDocumentStatus(id: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateDocumentStatus(id, newStatus)
            showToast("Document status updated to $newStatus")
            val updated = repository.getDocumentWithItems(id)
            selectedDocumentWithItems = updated
        }
    }

    // Convert Quotation directly into Invoice (Outstanding feature!)
    fun convertQuotationToInvoice(quotationDoc: DocumentWithItems) {
        val nextInvoiceNum = "INV-${System.currentTimeMillis().toString().takeLast(6)}"
        val prefilledItems = quotationDoc.items.map {
            DraftItem(
                itemId = it.itemId,
                name = it.itemName,
                quantity = it.quantity,
                price = it.unitPrice
            )
        }
        startCreateDocument(
            type = "INVOICE",
            prefilledCustomerId = quotationDoc.document.customerId,
            prefilledSupplier = null,
            prefilledItems = prefilledItems,
            linkedDocId = quotationDoc.document.id
        )
        showToast("Draft Invoice populated from quotation!")
    }

    // Receive Goods from Purchase Order (Outstanding feature!)
    fun processGoodsReceiveFromPO(poDoc: DocumentWithItems) {
        val nextGrnNum = "GRN-${System.currentTimeMillis().toString().takeLast(6)}"
        val prefilledItems = poDoc.items.map {
            DraftItem(
                itemId = it.itemId,
                name = it.itemName,
                quantity = it.quantity, // ordered quantity
                price = it.unitPrice,
                receivedQuantity = it.quantity // default received same as ordered
            )
        }
        startCreateDocument(
            type = "GRN",
            prefilledCustomerId = null,
            prefilledSupplier = poDoc.document.supplierName,
            prefilledItems = prefilledItems,
            linkedDocId = poDoc.document.id
        )
        showToast("Status: Preparing Goods Receive Note from PO!")
    }

    fun deleteDocument(document: Document) {
        viewModelScope.launch {
            repository.deleteDocument(document)
            showToast("Document deleted")
            navigateTo(Screen.DOCUMENTS)
        }
    }

    // Helper to generate next document number
    fun getNextDocNumber(type: String): String {
        val rand = System.currentTimeMillis().toString().takeLast(4)
        return when (type) {
            "INVOICE" -> "INV-2026-$rand"
            "QUOTATION" -> "QT-2026-$rand"
            "PURCHASE_ORDER" -> "PO-2026-$rand"
            "GRN" -> "GRN-2026-$rand"
            else -> "DOC-$rand"
        }
    }

    // Search and display filter properties
    fun updateDocTypeFilter(type: String) {
        documentTypeFilter = type
    }
}

// A custom MutableStateList implementation wrapper to support Compose state
fun <T> mutableStateListOf() = androidx.compose.runtime.mutableStateListOf<T>()

class PrintPosViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrintPosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PrintPosViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
