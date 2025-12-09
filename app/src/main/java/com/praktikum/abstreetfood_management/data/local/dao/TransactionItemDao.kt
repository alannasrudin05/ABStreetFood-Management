// data/local/dao/TransactionItemDao.kt (BUAT FILE INI)
package com.praktikum.abstreetfood_management.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.praktikum.abstreetfood_management.data.local.entity.TransactionItemEntity
import com.praktikum.abstreetfood_management.data.local.model.TransactionItemWithProduct
import com.praktikum.abstreetfood_management.domain.model.SaleReportItem
import com.praktikum.abstreetfood_management.domain.model.TopSellingProduct
import kotlinx.coroutines.flow.Flow
@Dao
interface TransactionItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionItems(items: List<TransactionItemEntity>)

    @Query("SELECT * FROM transaction_items WHERE transactionId = :transactionId")
    suspend fun getItemsByTransactionId(transactionId: String): List<TransactionItemEntity>

    // Query untuk menghitung produk terlaris
    @Query("""
        SELECT 
            pi.name AS name,
            SUM(ti.itemPrice * ti.quantity) AS totalRevenue,    
            SUM(ti.quantity) AS totalQuantitySold
            -- HAPUS: SUM(CASE WHEN ti.isExtraRice = 1 THEN ti.quantity ELSE 0 END) AS extraRiceSold
        FROM transaction_items ti
        JOIN product_item pi ON ti.productItemId = pi.id
        GROUP BY pi.name
        ORDER BY totalQuantitySold DESC
        LIMIT 5
    """)
    fun getTopSellingProducts(): Flow<List<TopSellingProduct>>

    @Query("""
        SELECT
            PI.name AS name,
            SUM(TI.quantity) AS totalQuantitySold,
            SUM(TI.quantity * TI.itemPrice) AS totalRevenue
        FROM transaction_items AS TI
        JOIN product_item AS PI ON TI.productItemId = PI.id
        JOIN transactions AS T ON TI.transactionId = T.id
        WHERE T.transactionTime BETWEEN :startTime AND :endTime
        GROUP BY PI.name
        ORDER BY totalQuantitySold DESC
        LIMIT 5
    """)
    fun getTopSellingProductsForPeriod(
        startTime: Long,
        endTime: Long
    ): Flow<List<TopSellingProduct>>

    @Query("""
        SELECT SUM(ti.quantity) 
        FROM transaction_items ti
        JOIN transactions t ON ti.transactionId = t.id
        WHERE t.transactionTime >= :startOfDay
    """)
    fun getTotalQuantitySold(startOfDay: Long): Flow<Int?>


    @Query("""
    SELECT 
        T.id AS transactionId,
        T.transactionTime AS transactionTime,
        PI.name AS productName, -- Nama Produk Item
        TI.quantity AS quantity,
        TI.itemPrice AS itemPrice,
        (TI.quantity * TI.itemPrice) AS totalItemRevenue,
        T.outletId AS outletId
    FROM transaction_items TI
    JOIN transactions T ON TI.transactionId = T.id
    JOIN product_item PI ON TI.productItemId = PI.id -- Asumsi ProductItemEntity punya 'name'
    WHERE T.transactionTime BETWEEN :startTime AND :endTime
    ORDER BY T.transactionTime DESC
""")
    fun getSalesReport(startTime: Long, endTime: Long): List<SaleReportItem>

//    @Transaction
//    @Query("""
//    SELECT
//        TI.transactionId, TI.productItemId, TI.quantity, TI.itemPrice,
//        P.name AS productName, P.variantName AS variantName
//    FROM transaction_item_table TI
//    INNER JOIN product_item_table P ON TI.productItemId = P.id
//    WHERE TI.transactionId = :transactionId
//""")
@Transaction
@Query("""
SELECT 
    TI.transactionId, 
    TI.productItemId, 
    TI.quantity, 
    TI.itemPrice, 
    P.name AS productName, 
    P.variantType AS variantName 
FROM transaction_items AS TI -- ✅ Gunakan AS TI agar alias lebih jelas
INNER JOIN product_item AS P ON TI.productItemId = P.id
WHERE TI.transactionId = :transactionId
""")
    suspend fun getItemsDetailByTransactionId(transactionId: String): List<TransactionItemWithProduct>

}