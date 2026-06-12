package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrintPosApp(viewModel: PrintPosViewModel) {
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val inventory by viewModel.inventory.collectAsStateWithLifecycle()
    val documents by viewModel.documents.collectAsStateWithLifecycle()

    val toastMessage = viewModel.toastMessage

    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            viewModel.clearToast()
        }
    }

    Scaffold(
        topBar = {
            val current = viewModel.currentScreen
            if (current == Screen.DASHBOARD || current == Screen.DOCUMENTS || current == Screen.INVENTORY || current == Screen.CUSTOMERS) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .height(64.dp)
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Indigo600),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "P",
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Column {
                                    Text(
                                        text = when(current) {
                                            Screen.DASHBOARD -> "PrintFlow POS"
                                            Screen.DOCUMENTS -> "Documents Ledger"
                                            Screen.INVENTORY -> "Stock Control"
                                            Screen.CUSTOMERS -> "Client CRM"
                                            else -> "PrintFlow"
                                        },
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "MAIN BRANCH • 08:35 AM",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MutedSlate,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(Color(0xFFEEF2F6)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Filled.Search,
                                        contentDescription = "Search",
                                        tint = MutedSlate,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(Color(0xFFEEF2F6)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Filled.Notifications,
                                        contentDescription = "Notifications",
                                        tint = MutedSlate,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                        Divider(color = BorderColor, thickness = 1.dp)
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val current = viewModel.currentScreen
                val navColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Indigo600,
                    selectedTextColor = Indigo600,
                    indicatorColor = Color(0xFFE2E8F0),
                    unselectedIconColor = MutedSlate,
                    unselectedTextColor = MutedSlate
                )
                NavigationBarItem(
                    selected = current == Screen.DASHBOARD,
                    onClick = { viewModel.navigateTo(Screen.DASHBOARD) },
                    icon = { Icon(Icons.Filled.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Home", fontWeight = FontWeight.SemiBold) },
                    colors = navColors,
                    modifier = Modifier.testTag("nav_dashboard")
                )
                NavigationBarItem(
                    selected = current == Screen.DOCUMENTS,
                    onClick = { viewModel.navigateTo(Screen.DOCUMENTS) },
                    icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Documents") },
                    label = { Text("Stock Docs", fontWeight = FontWeight.SemiBold) },
                    colors = navColors,
                    modifier = Modifier.testTag("nav_documents")
                )
                NavigationBarItem(
                    selected = current == Screen.INVENTORY,
                    onClick = { viewModel.navigateTo(Screen.INVENTORY) },
                    icon = { Icon(Icons.Filled.Archive, contentDescription = "Inventory") },
                    label = { Text("Stock", fontWeight = FontWeight.SemiBold) },
                    colors = navColors,
                    modifier = Modifier.testTag("nav_inventory")
                )
                NavigationBarItem(
                    selected = current == Screen.CUSTOMERS,
                    onClick = { viewModel.navigateTo(Screen.CUSTOMERS) },
                    icon = { Icon(Icons.Filled.Group, contentDescription = "Customers") },
                    label = { Text("Clients", fontWeight = FontWeight.SemiBold) },
                    colors = navColors,
                    modifier = Modifier.testTag("nav_customers")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = viewModel.currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
                },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    Screen.DASHBOARD -> DashboardScreen(viewModel, customers, inventory, documents)
                    Screen.DOCUMENTS -> DocumentsScreen(viewModel, documents)
                    Screen.INVENTORY -> InventoryScreen(viewModel, inventory)
                    Screen.CUSTOMERS -> CustomersScreen(viewModel, customers)
                    Screen.CREATE_DOCUMENT -> CreateDocumentScreen(viewModel, customers, inventory)
                    Screen.DOCUMENT_DETAIL -> DocumentDetailScreen(viewModel)
                }
            }

            // Toast feedback layer overlay
            if (toastMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MagentaPrint),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth()
                        .testTag("toast_alert"),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Info, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = toastMessage,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// FORMAT HELPERS
fun formatPrice(value: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    return format.format(value)
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

// POS HUB SCREEN
@Composable
fun DashboardScreen(
    viewModel: PrintPosViewModel,
    customers: List<Customer>,
    inventory: List<InventoryItem>,
    documents: List<Document>
) {
    var showQuickAddCartDialog by remember { mutableStateOf(false) }
    var selectedItemForCart by remember { mutableStateOf<InventoryItem?>(null) }
    var quantityToAddToCart by remember { mutableStateOf("1") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Welcome back, Operator",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Monitor sales stream and pipeline activity.",
                    fontSize = 12.sp,
                    color = MutedSlate
                )
            }
            Icon(
                Icons.Filled.LocalPrintshop,
                contentDescription = null,
                tint = Indigo600,
                modifier = Modifier.size(28.dp)
            )
        }

        // 1. QUICK INSIGHTS HERO CARD (Indigo-900)
        val completedInvoices = documents.filter { it.docType == "INVOICE" && it.status == "COMPLETED" }
        val grandTotalSales = completedInvoices.sumOf { it.totalAmount }
        val lowStockCount = inventory.count { it.stockQuantity <= it.minStockAlert }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Indigo900)
                .drawBehind {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.08f),
                        radius = 120.dp.toPx(),
                        center = Offset(this.size.width * 0.85f, this.size.height * 0.85f)
                    )
                }
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Daily Sales Revenue",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatPrice(grandTotalSales),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(99.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "+12% vs yesterday",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(99.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${completedInvoices.size} Invoices",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. OPERATION SHORTCUTS 2x2 GRID (Indigo, Blue, Emerald, Orange, Purple accents)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Operational Shortcuts",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Invoicing
                Card(
                    onClick = { viewModel.startCreateDocument("INVOICE") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFEFF6FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ReceiptLong,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "Invoicing",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                // Quotations
                Card(
                    onClick = { viewModel.startCreateDocument("QUOTATION") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFECFDF5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.RequestQuote,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "Quotations",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Purchase Order
                Card(
                    onClick = { viewModel.startCreateDocument("PURCHASE_ORDER") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFFFF7ED)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.ShoppingBag,
                                contentDescription = null,
                                tint = Color(0xFFEA580C),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "Purchase Order",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                // Receive Goods / GRN
                Card(
                    onClick = { viewModel.startCreateDocument("GRN") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF5F3FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Inventory2,
                                contentDescription = null,
                                tint = Color(0xFF7C3AED),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "Receive Goods",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // 3. CRITICAL INVENTORY ALERT (Amber border alert widget)
        val lowStockItem = inventory.firstOrNull { it.stockQuantity <= it.minStockAlert }
        if (lowStockItem != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(32.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFFF59E0B)) // border-amber-500
                        )
                        
                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = "Warning",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Column {
                            Text(
                                "Low Stock Alert",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                lowStockItem.name,
                                fontSize = 10.sp,
                                color = MutedSlate,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFEF3C7), RoundedCornerShape(4.dp)) // bg-amber-100
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "${lowStockItem.stockQuantity} Left",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309) // text-amber-700
                        )
                    }
                }
            }
        } else {
            // Happy State: All clear stock alert
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(32.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFF10B981)) // border-emerald-500
                    )
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = "Success",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            "Inventory Health Stable",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "All registered stocks are above alert limits.",
                            fontSize = 10.sp,
                            color = MutedSlate
                        )
                    }
                }
            }
        }

        // BASKET LAYOUT
        Text(
            text = "⚡ Instant POS Checkout Creator",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                var showCustomerDropdown by remember { mutableStateOf(false) }
                val selectedCustomer = customers.find { it.id == viewModel.creationSelectedCustomerId }

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { showCustomerDropdown = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("checkout_customer_picker"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Person, contentDescription = null, tint = CyanPrint)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    selectedCustomer?.let { "${it.companyName} (${it.name})" } ?: "Select billing customer",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                        }
                    }

                    DropdownMenu(
                        expanded = showCustomerDropdown,
                        onDismissRequest = { showCustomerDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        DropdownMenuItem(
                            text = { Text("None / Walk-in Customer", fontWeight = FontWeight.Bold) },
                            onClick = {
                                viewModel.selectCreationCustomer(null)
                                showCustomerDropdown = false
                            }
                        )
                        customers.forEach { customer ->
                            DropdownMenuItem(
                                text = { Text("${customer.companyName} — Contact: ${customer.name}") },
                                onClick = {
                                    viewModel.selectCreationCustomer(customer.id)
                                    showCustomerDropdown = false
                                }
                            )
                        }
                    }
                }

                if (viewModel.draftItems.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Filled.ShoppingCart,
                            contentDescription = null,
                            tint = MutedSlate.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            "Basket is completely empty.",
                            color = MutedSlate,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Select printing items from the stock list below to build an invoice or quotation.",
                            color = MutedSlate.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Selected Items (${viewModel.draftItems.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedSlate
                        )

                        viewModel.draftItems.forEachIndexed { idx, item ->
                            val itemTotal = item.price * item.quantity
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1.5f)) {
                                    Text(
                                        item.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        "${formatPrice(item.price)} each",
                                        fontSize = 11.sp,
                                        color = MutedSlate
                                    )
                                }

                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    IconButton(
                                        onClick = { viewModel.updateDraftItemQty(idx, item.quantity - 1) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Filled.RemoveCircleOutline, contentDescription = "Decrease", tint = MagentaPrint, modifier = Modifier.size(20.dp))
                                    }
                                    Text(
                                        "${item.quantity}",
                                        modifier = Modifier.padding(horizontal = 6.dp),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    IconButton(
                                        onClick = { viewModel.updateDraftItemQty(idx, item.quantity + 1) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Filled.AddCircleOutline, contentDescription = "Increase", tint = CyanPrint, modifier = Modifier.size(20.dp))
                                    }
                                }

                                Text(
                                    formatPrice(itemTotal),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )

                                IconButton(
                                    onClick = { viewModel.removeItemFromDraft(idx) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Grand Total Amount:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            val finalTotal = viewModel.draftItems.sumOf { it.price * it.quantity }
                            Text(
                                formatPrice(finalTotal),
                                color = CyanPrint,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.startCreateDocument(
                                        type = "INVOICE",
                                        prefilledCustomerId = viewModel.creationSelectedCustomerId,
                                        prefilledSupplier = null,
                                        prefilledItems = viewModel.draftItems.toList()
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("checkout_invoice_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = CyanPrint),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generate Invoice", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    viewModel.startCreateDocument(
                                        type = "QUOTATION",
                                        prefilledCustomerId = viewModel.creationSelectedCustomerId,
                                        prefilledSupplier = null,
                                        prefilledItems = viewModel.draftItems.toList()
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("checkout_quotation_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = YellowPrint),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Filled.Description, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generate Quote", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "📦 Select Printing Stock & Equipment",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Tap to add to POS",
                fontSize = 11.sp,
                color = MagentaPrint,
                fontWeight = FontWeight.SemiBold
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            inventory.forEach { item ->
                Card(
                    onClick = {
                        selectedItemForCart = item
                        quantityToAddToCart = "1"
                        showQuickAddCartDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(2f)) {
                            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(item.sku, fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = MutedSlate)
                                Text(
                                    text = item.category,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.category == "Equipment") CyanPrint else MagentaPrint,
                                    modifier = Modifier
                                        .background(
                                            (if (item.category == "Equipment") CyanPrint else MagentaPrint).copy(
                                                alpha = 0.12f
                                            ),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text(formatPrice(item.price), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            val isLow = item.stockQuantity <= item.minStockAlert
                            Text(
                                "Stock: ${item.stockQuantity}",
                                color = if (isLow) MagentaPrint else MutedSlate,
                                fontWeight = if (isLow) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Filled.AddCircle,
                            contentDescription = "Quick Add",
                            tint = CyanPrint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }

    if (showQuickAddCartDialog && selectedItemForCart != null) {
        val item = selectedItemForCart!!
        Dialog(onDismissRequest = { showQuickAddCartDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Add Item to checkout", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(item.name, fontSize = 14.sp, color = MutedSlate)

                    OutlinedTextField(
                        value = quantityToAddToCart,
                        onValueChange = { quantityToAddToCart = it },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showQuickAddCartDialog = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val qtyInt = quantityToAddToCart.toIntOrNull() ?: 1
                                viewModel.addItemToDraft(item, qtyInt)
                                showQuickAddCartDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrint)
                        ) {
                            Text("Add to Basket")
                        }
                    }
                }
            }
        }
    }
}

// DOCUMENTS TAB
@Composable
fun DocumentsScreen(viewModel: PrintPosViewModel, documents: List<Document>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Operational Documents", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Invoices, Quotes, POs, and Goods Receipts", fontSize = 12.sp, color = MutedSlate)
            }

            FilledIconButton(
                onClick = { viewModel.startCreateDocument("INVOICE") },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = CyanPrint),
                modifier = Modifier.testTag("add_document_direct")
            ) {
                Icon(Icons.Filled.Add, contentDescription = "New Document", tint = Color.White)
            }
        }

        val docTypes = listOf("ALL", "INVOICE", "QUOTATION", "PURCHASE_ORDER", "GRN")
        ScrollableTabRow(
            selectedTabIndex = docTypes.indexOf(viewModel.documentTypeFilter).coerceAtLeast(0),
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            divider = {}
        ) {
            docTypes.forEach { type ->
                Tab(
                    selected = viewModel.documentTypeFilter == type,
                    onClick = { viewModel.updateDocTypeFilter(type) },
                    text = {
                        Text(
                            text = when(type) {
                                "ALL" -> "All Docs"
                                "INVOICE" -> "Invoices (C)"
                                "QUOTATION" -> "Quotations (Y)"
                                "PURCHASE_ORDER" -> "POs (M)"
                                "GRN" -> "GRNs (K)"
                                else -> type
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }

        val filteredDocs = if (viewModel.documentTypeFilter == "ALL") {
            documents
        } else {
            documents.filter { it.docType == viewModel.documentTypeFilter }
        }

        if (filteredDocs.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ReceiptLong,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MutedSlate.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("No matching documents found.", color = MutedSlate, fontWeight = FontWeight.Bold)
                Text("Generate documents in POS tab or press the (+) button above.", fontSize = 11.sp, color = MutedSlate)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredDocs) { doc ->
                    Card(
                        onClick = { viewModel.selectDocument(doc.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("doc_item_${doc.docNumber}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val typeColor = when(doc.docType) {
                                        "INVOICE" -> CyanPrint
                                        "QUOTATION" -> YellowPrint
                                        "PURCHASE_ORDER" -> MagentaPrint
                                        "GRN" -> BorderColor
                                        else -> MutedSlate
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(typeColor)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        doc.docNumber,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }

                                val statusColor = when (doc.status) {
                                    "COMPLETED", "RECEIVED" -> Color(0xFF10B981)
                                    "PENDING" -> Color(0xFFF59E0B)
                                    "VOIDED" -> Color(0xFFEF4444)
                                    else -> Color(0xFF64748B)
                                }
                                Text(
                                    text = doc.status,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = statusColor,
                                    modifier = Modifier
                                        .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column(modifier = Modifier.weight(1.5f)) {
                                    Text(
                                        text = when(doc.docType) {
                                            "INVOICE", "QUOTATION" -> doc.customerName ?: "Walk-in Retail Client"
                                            "PURCHASE_ORDER", "GRN" -> "Supplier: ${doc.supplierName ?: "Unknown"}"
                                            else -> ""
                                        },
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = formatDate(doc.date),
                                        fontSize = 11.sp,
                                        color = MutedSlate
                                    )
                                }

                                Text(
                                    text = formatPrice(doc.totalAmount),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// DOCUMENT DETAIL SCREEN
@Composable
fun DocumentDetailScreen(viewModel: PrintPosViewModel) {
    val container = viewModel.selectedDocumentWithItems ?: return
    val doc = container.document
    val items = container.items

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(Screen.DOCUMENTS) },
                modifier = Modifier.testTag("back_to_docs")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Document Viewer", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            IconButton(onClick = { viewModel.deleteDocument(doc) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (doc.docType == "QUOTATION" && doc.status == "DRAFT") {
                    Button(
                        onClick = { viewModel.convertQuotationToInvoice(container) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrint),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("convert_quote_invoice"),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Filled.Cached, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Convert to Invoice", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (doc.docType == "PURCHASE_ORDER" && doc.status == "PENDING") {
                    Button(
                        onClick = { viewModel.processGoodsReceiveFromPO(container) },
                        colors = ButtonDefaults.buttonColors(containerColor = MagentaPrint),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("process_grn_po"),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Filled.LocalShipping, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Process Goods Receipt", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (doc.docType == "INVOICE" && doc.status != "COMPLETED") {
                    Button(
                        onClick = { viewModel.setDocumentStatus(doc.id, "COMPLETED") },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrint),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mark Paid & Complete", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = { viewModel.showToast("Mock Command: Sent printer instruction for ${doc.docNumber}") },
                    colors = ButtonDefaults.buttonColors(containerColor = MutedSlate),
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Filled.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Print Document", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Document look-alike card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BorderColorLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            "PRINTPOS MACHINERY CORP",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text("Commercial Printing Equipment Specialists", fontSize = 9.sp, color = Color.Gray)
                        Text("Email: operations@printposmachinery.com", fontSize = 8.sp, color = Color.Gray)
                        Text("Ph: +1-800-555-PRINT", fontSize = 8.sp, color = Color.Gray)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = doc.docType,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = when (doc.docType) {
                                "INVOICE" -> CyanPrint
                                "QUOTATION" -> YellowPrint
                                "PURCHASE_ORDER" -> MagentaPrint
                                else -> Color.Black
                            }
                        )
                        Text(
                            text = "# ${doc.docNumber}",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.Black
                        )
                    }
                }

                Divider(color = BorderColorLight)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("DATE OF ISSUE:", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text(formatDate(doc.date), fontSize = 11.sp, color = Color.Black)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("STATUS:", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text(doc.status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanPrint)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (doc.docType == "INVOICE" || doc.docType == "QUOTATION") {
                        Column {
                            Text("BILL TO CUSTOMER:", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text(doc.customerName ?: "Walk-In Retail Client", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                            Text("Customer ID Reference: #${doc.customerId ?: "N/A"}", fontSize = 10.sp, color = Color.DarkGray)
                        }
                    } else {
                        Column {
                            Text("SUPPLIER REFERENCE:", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text(doc.supplierName ?: "Primary Wholesale Distributor", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F5F9))
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Item description", fontWeight = FontWeight.Bold, fontSize = 9.sp, color = Color.DarkGray, modifier = Modifier.weight(1.8f))
                        Text("Qty", fontWeight = FontWeight.Bold, fontSize = 9.sp, color = Color.DarkGray, modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                        if (doc.docType == "GRN") {
                            Text("Recv", fontWeight = FontWeight.Bold, fontSize = 9.sp, color = Color.DarkGray, modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                        }
                        Text("Price", fontWeight = FontWeight.Bold, fontSize = 9.sp, color = Color.DarkGray, modifier = Modifier.weight(0.7f), textAlign = TextAlign.End)
                        Text("Total", fontWeight = FontWeight.Bold, fontSize = 9.sp, color = Color.DarkGray, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                    }

                    items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(item.itemName, fontSize = 11.sp, color = Color.Black, modifier = Modifier.weight(1.8f))
                            Text("${item.quantity}", fontSize = 11.sp, color = Color.Black, modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                            if (doc.docType == "GRN") {
                                Text("${item.receivedQuantity}", fontSize = 11.sp, color = MagentaPrint, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                            }
                            Text(formatPrice(item.unitPrice), fontSize = 11.sp, color = Color.Black, modifier = Modifier.weight(0.7f), textAlign = TextAlign.End)
                            val lineTotal = item.unitPrice * if (doc.docType == "GRN") item.receivedQuantity else item.quantity
                            Text(formatPrice(lineTotal), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                        }
                    }
                }

                Divider(color = BorderColorLight)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("Subtotal:", fontSize = 11.sp, color = Color.Gray)
                            Text(formatPrice(doc.totalAmount), fontSize = 11.sp, color = Color.Black)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("Taxes (0% exempt):", fontSize = 11.sp, color = Color.Gray)
                            Text(formatPrice(0.0), fontSize = 11.sp, color = Color.Black)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("Grand Total Payable:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(formatPrice(doc.totalAmount), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = CyanPrint)
                        }
                    }
                }

                if (doc.remarks.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("REMARKS / INSTRUCTIONS:", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text(doc.remarks, fontSize = 10.sp, color = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }
}

// CREATE COMPILER SCREEN
@Composable
fun CreateDocumentScreen(
    viewModel: PrintPosViewModel,
    customers: List<Customer>,
    inventory: List<InventoryItem>
) {
    var docNumberInput by remember { mutableStateOf(viewModel.getNextDocNumber(viewModel.creationDocType)) }

    var customItemName by remember { mutableStateOf("") }
    var customItemPrice by remember { mutableStateOf("") }
    var customItemQty by remember { mutableStateOf("1") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo(Screen.DASHBOARD) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cancel")
            }
            Text("Create ${viewModel.creationDocType.replace("_", " ")}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(48.dp))
        }

        OutlinedTextField(
            value = docNumberInput,
            onValueChange = { docNumberInput = it },
            label = { Text("Document ID / Number") },
            textStyle = TextStyle(fontFamily = FontFamily.Monospace),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        val type = viewModel.creationDocType
        if (type == "INVOICE" || type == "QUOTATION") {
            var expandedCust by remember { mutableStateOf(false) }
            val sel = customers.find { it.id == viewModel.creationSelectedCustomerId }

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = sel?.let { "${it.companyName} (${it.name})" } ?: "Walk-in Retail Client",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Client / Customer Entity") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { expandedCust = true }) {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                        }
                    }
                )

                DropdownMenu(
                    expanded = expandedCust,
                    onDismissRequest = { expandedCust = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DropdownMenuItem(
                        text = { Text("None / Walk-in Customer (Guest)") },
                        onClick = {
                            viewModel.selectCreationCustomer(null)
                            expandedCust = false
                        }
                    )
                    customers.forEach { customer ->
                        DropdownMenuItem(
                            text = { Text("${customer.companyName} — Contact: ${customer.name}") },
                            onClick = {
                                viewModel.selectCreationCustomer(customer.id)
                                expandedCust = false
                            }
                        )
                    }
                }
            }
        } else {
            OutlinedTextField(
                value = viewModel.creationSelectedSupplierName,
                onValueChange = { viewModel.updateCreationSupplierName(it) },
                label = { Text("Wholesale Supplier Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("supplier_input_field"),
                singleLine = true
            )
        }

        OutlinedTextField(
            value = viewModel.creationRemarks,
            onValueChange = { viewModel.updateRemarks(it) },
            label = { Text("Operator Remarks / Custom Instructions") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        Divider(color = MaterialTheme.colorScheme.outlineVariant)

        Text("Document Line Items", fontWeight = FontWeight.Bold, fontSize = 14.sp)

        if (viewModel.draftItems.isEmpty()) {
            Text(
                "No line items added yet. Add custom products or inventory below.",
                color = MutedSlate,
                fontSize = 12.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.draftItems.forEachIndexed { idx, item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1.5f))
                                IconButton(
                                    onClick = { viewModel.removeItemFromDraft(idx) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = "${item.quantity}",
                                    onValueChange = { viewModel.updateDraftItemQty(idx, it.toIntOrNull() ?: 1) },
                                    label = { Text("Qty ordered") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                if (type == "GRN") {
                                    OutlinedTextField(
                                        value = "${item.receivedQuantity}",
                                        onValueChange = { viewModel.updateDraftItemReceivedQty(idx, it.toIntOrNull() ?: 0) },
                                        label = { Text("Qty received") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("received_qty_input_${idx}"),
                                        singleLine = true
                                    )
                                }

                                Text(
                                    text = "Rate: " + formatPrice(item.price),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Add Custom Print Job / Item", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CyanPrint)
                OutlinedTextField(
                    value = customItemName,
                    onValueChange = { customItemName = it },
                    label = { Text("Job description (e.g., Flyers 2000 Units)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = customItemPrice,
                        onValueChange = { customItemPrice = it },
                        label = { Text("Price rate") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = customItemQty,
                        onValueChange = { customItemQty = it },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Button(
                    onClick = {
                        val priceVal = customItemPrice.toDoubleOrNull() ?: 0.0
                        val qtyVal = customItemQty.toIntOrNull() ?: 1
                        if (customItemName.isNotEmpty() && priceVal > 0.0) {
                            viewModel.addItemToDraft(
                                InventoryItem(name = customItemName, sku = "CUSTOM-JOB", category = "Service", stockQuantity = 0, price = priceVal, description = ""),
                                qtyVal
                            )
                            customItemName = ""
                            customItemPrice = ""
                            customItemQty = "1"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrint)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("Add Line Item")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val itemsCount = viewModel.draftItems.size
        Button(
            onClick = { viewModel.saveDraftDocument(docNumberInput) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_compiled_document"),
            colors = ButtonDefaults.buttonColors(containerColor = MagentaPrint),
            shape = RoundedCornerShape(8.dp),
            enabled = itemsCount > 0
        ) {
            Icon(Icons.Filled.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save & Generate document", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// INVENTORY VIEW
@Composable
fun InventoryScreen(viewModel: PrintPosViewModel, inventory: List<InventoryItem>) {
    var showAddItemDialog by remember { mutableStateOf(false) }

    var itemNameInput by remember { mutableStateOf("") }
    var itemSkuInput by remember { mutableStateOf("") }
    var itemCategoryInput by remember { mutableStateOf("Equipment") }
    var itemStockInput by remember { mutableStateOf("5") }
    var itemPriceInput by remember { mutableStateOf("150") }
    var itemDescInput by remember { mutableStateOf("") }
    var itemAlertInput by remember { mutableStateOf("3") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Stock & Equipment Inventory", fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Text("Printers, spare components, papers, and toners.", fontSize = 12.sp, color = MutedSlate)
            }

            FilledIconButton(
                onClick = { showAddItemDialog = true },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = CyanPrint),
                modifier = Modifier.testTag("add_inventory_btn")
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Item", tint = Color.White)
            }
        }

        OutlinedTextField(
            value = viewModel.inventorySearchQuery,
            onValueChange = { viewModel.inventorySearchQuery = it },
            placeholder = { Text("Search by name, SKU or category...") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        val criticalItems = inventory.filter { it.stockQuantity <= it.minStockAlert }
        if (criticalItems.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MagentaPrint.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MagentaPrint),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = MagentaPrint)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Warehouse Alert: ${criticalItems.size} items are critically low in stock! Create a Purchase Order (PO) to replenish soon.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MagentaPrint
                    )
                }
            }
        }

        val filteredInventory = inventory.filter {
            it.name.contains(viewModel.inventorySearchQuery, ignoreCase = true) ||
            it.sku.contains(viewModel.inventorySearchQuery, ignoreCase = true) ||
            it.category.contains(viewModel.inventorySearchQuery, ignoreCase = true)
        }

        if (filteredInventory.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.Inventory,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MutedSlate.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("No items match search", color = MutedSlate)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredInventory) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("inventory_item_${item.sku}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1.5f)) {
                                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Text(
                                            item.sku,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = CyanPrint
                                        )
                                        Text(
                                            item.category,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (item.category == "Equipment") CyanPrint else MagentaPrint,
                                            modifier = Modifier
                                                .background(
                                                    (if (item.category == "Equipment") CyanPrint else MagentaPrint).copy(
                                                        alpha = 0.12f
                                                    ),
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    formatPrice(item.price),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(0.8f),
                                    textAlign = TextAlign.End
                                )
                            }

                            if (item.description.isNotEmpty()) {
                                Text(
                                    text = item.description,
                                    fontSize = 11.sp,
                                    color = MutedSlate,
                                    modifier = Modifier.padding(top = 8.dp),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val isLow = item.stockQuantity <= item.minStockAlert
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        if (isLow) Icons.Filled.Warning else Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = if (isLow) MagentaPrint else Color(0xFF10B981)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Warehouse stock: ${item.stockQuantity} units",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isLow) MagentaPrint else MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = { viewModel.adjustStock(item, -1) },
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier
                                            .size(width = 44.dp, height = 32.dp)
                                            .testTag("adjust_down_${item.sku}"),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text("-1", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = { viewModel.adjustStock(item, 5) },
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier
                                            .size(width = 44.dp, height = 32.dp)
                                            .testTag("adjust_up_${item.sku}"),
                                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrint),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text("+5", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteInventoryItem(item) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddItemDialog) {
        Dialog(onDismissRequest = { showAddItemDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Register Warehouse Item", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = itemNameInput,
                        onValueChange = { itemNameInput = it },
                        label = { Text("Item Commercial Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_item_name"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = itemSkuInput,
                        onValueChange = { itemSkuInput = it },
                        label = { Text("SKU Part Number Code") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_item_sku"),
                        singleLine = true
                    )

                    var showCatDropdown by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = itemCategoryInput,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category Classification") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { showCatDropdown = true }) {
                                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenu(expanded = showCatDropdown, onDismissRequest = { showCatDropdown = false }) {
                            listOf("Equipment", "Consumables", "Spare Parts").forEach { cat ->
                                DropdownMenuItem(text = { Text(cat) }, onClick = {
                                    itemCategoryInput = cat
                                    showCatDropdown = false
                                })
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = itemStockInput,
                            onValueChange = { itemStockInput = it },
                            label = { Text("Initial Stock") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = itemPriceInput,
                            onValueChange = { itemPriceInput = it },
                            label = { Text("Unit Price") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = itemAlertInput,
                        onValueChange = { itemAlertInput = it },
                        label = { Text("Min Low Stock Bar") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = itemDescInput,
                        onValueChange = { itemDescInput = it },
                        label = { Text("Product Description / Fitment") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showAddItemDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val stock = itemStockInput.toIntOrNull() ?: 5
                                val price = itemPriceInput.toDoubleOrNull() ?: 150.0
                                val alertLimit = itemAlertInput.toIntOrNull() ?: 3
                                if (itemNameInput.isNotEmpty() && itemSkuInput.isNotEmpty()) {
                                    viewModel.addInventoryItem(
                                        name = itemNameInput,
                                        sku = itemSkuInput,
                                        category = itemCategoryInput,
                                        qty = stock,
                                        price = price,
                                        desc = itemDescInput,
                                        lowStockAlert = alertLimit
                                    )
                                    showAddItemDialog = false
                                    itemNameInput = ""
                                    itemSkuInput = ""
                                    itemCategoryInput = "Equipment"
                                    itemStockInput = "5"
                                    itemPriceInput = "150"
                                    itemDescInput = ""
                                    itemAlertInput = "3"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrint),
                            modifier = Modifier.testTag("submit_new_item")
                        ) {
                            Text("Save stock")
                        }
                    }
                }
            }
        }
    }
}

// CUSTOMERS VIEW
@Composable
fun CustomersScreen(viewModel: PrintPosViewModel, customers: List<Customer>) {
    var showAddCustomerDialog by remember { mutableStateOf(false) }

    var custNameInput by remember { mutableStateOf("") }
    var custCompanyInput by remember { mutableStateOf("") }
    var custEmailInput by remember { mutableStateOf("") }
    var custPhoneInput by remember { mutableStateOf("") }
    var custAddressInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Customer CRM", fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Text("Client roster and business delivery addresses.", fontSize = 12.sp, color = MutedSlate)
            }

            FilledIconButton(
                onClick = { showAddCustomerDialog = true },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = CyanPrint),
                modifier = Modifier.testTag("add_customer_btn")
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Client", tint = Color.White)
            }
        }

        OutlinedTextField(
            value = viewModel.customerSearchQuery,
            onValueChange = { viewModel.customerSearchQuery = it },
            placeholder = { Text("Search customers database...") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        val filteredCustomers = customers.filter {
            it.name.contains(viewModel.customerSearchQuery, ignoreCase = true) ||
            it.companyName.contains(viewModel.customerSearchQuery, ignoreCase = true) ||
            it.email.contains(viewModel.customerSearchQuery, ignoreCase = true)
        }

        if (filteredCustomers.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.Group,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MutedSlate.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("No customers registered yet", color = MutedSlate)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredCustomers) { customer ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("customer_card_${customer.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(
                                        customer.companyName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        customer.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.deleteCustomer(customer) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant)

                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Phone, contentDescription = null, tint = MutedSlate, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(customer.phone, fontSize = 12.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Email, contentDescription = null, tint = MutedSlate, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(customer.email, fontSize = 12.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = MutedSlate, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(customer.address, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddCustomerDialog) {
        Dialog(onDismissRequest = { showAddCustomerDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Add Customer to CRM", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = custCompanyInput,
                        onValueChange = { custCompanyInput = it },
                        label = { Text("Company Name (e.g. Acme Press Inc)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_cust_company"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = custNameInput,
                        onValueChange = { custNameInput = it },
                        label = { Text("Contact Person Full Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_cust_name"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = custPhoneInput,
                        onValueChange = { custPhoneInput = it },
                        label = { Text("Phone Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = custEmailInput,
                        onValueChange = { custEmailInput = it },
                        label = { Text("Email Address") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = custAddressInput,
                        onValueChange = { custAddressInput = it },
                        label = { Text("Workplace Delivery Address") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showAddCustomerDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (custCompanyInput.isNotEmpty() && custNameInput.isNotEmpty()) {
                                    viewModel.addCustomer(
                                        name = custNameInput,
                                        companyName = custCompanyInput,
                                        email = custEmailInput,
                                        phone = custPhoneInput,
                                        address = custAddressInput
                                    )
                                    showAddCustomerDialog = false
                                    custCompanyInput = ""
                                    custNameInput = ""
                                    custEmailInput = ""
                                    custPhoneInput = ""
                                    custAddressInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrint),
                            modifier = Modifier.testTag("submit_new_customer")
                        ) {
                            Text("Save Client")
                        }
                    }
                }
            }
        }
    }
}
