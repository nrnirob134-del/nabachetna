package com.example.data.service

import com.example.ui.screens.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object OfficialWarehouseManager {
    private val _officialProducts = MutableStateFlow(
        listOf(
            Product(
                id = 7,
                title = "নবচেতনা অফিশিয়াল এক্সক্লুসিভ হুডি - প্রিমিয়াম ব্ল্যাক",
                price = 35.00,
                rating = 5.0f,
                reviewCount = 340,
                imageUrl = "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=500",
                category = "Fashion",
                description = "অফিশিয়াল নবচেতনা গুদাম থেকে সরাসরি সংগৃহীত ১০০% অর্গানিক কটন প্রিমিয়াম হুডি। সারা বিশ্বে ডেলিভারি উপলব্ধ।",
                isOfficialWarehouse = true,
                isGlobal = true,
                sellerName = "Nabachetna Official Warehouse 🏢"
            ),
            Product(
                id = 8,
                title = "নবচেতনা স্মার্ট ওয়াচ প্রো (অফিশিয়াল গুদাম - বাংলাদেশ)",
                price = 49.99,
                rating = 4.9f,
                reviewCount = 520,
                imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500",
                category = "Electronics",
                description = "এক্সক্লুসিভ নবচেতনা অফিশিয়াল স্মার্টওয়াচ। শুধুমাত্র বাংলাদেশ অঞ্চলের (+880) ইউজারদের জন্য নির্ধারিত ও স্পেশাল ডিসকাউন্টে প্রাপ্তিসাধ্য।",
                isOfficialWarehouse = true,
                isGlobal = false,
                targetCountryCode = "+880",
                sellerName = "Nabachetna Official Warehouse 🏢"
            )
        )
    )
    val officialProducts: StateFlow<List<Product>> = _officialProducts.asStateFlow()

    fun addOfficialProduct(product: Product) {
        val official = product.copy(isOfficialWarehouse = true, sellerName = "Nabachetna Official Warehouse 🏢")
        _officialProducts.value = listOf(official) + _officialProducts.value
    }

    fun removeOfficialProduct(productId: Int) {
        _officialProducts.value = _officialProducts.value.filter { it.id != productId }
    }
}
