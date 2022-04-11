package com.sgcdeveloper.moneymanager.domain.model

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionCategoryEntry
import com.sgcdeveloper.moneymanager.presentation.theme.wallet_color_18
import com.sgcdeveloper.moneymanager.presentation.theme.wallet_color_24
import com.sgcdeveloper.moneymanager.presentation.theme.wallet_colors
import com.sgcdeveloper.moneymanager.util.categories_icons

open class TransactionCategory(
    val id: Long,
    val icon: Int,
    val color: Int,
    val description: String,
    val isDefault: Boolean,
    val isExpense: Boolean,
    var entry: TransactionCategoryEntry,
    val order:Int,
) {
    fun copy(
        id: Long = this.id,
        icon: Int = this.icon,
        color: Int = this.color,
        description: String = this.description,
        isDefault: Boolean = this.isDefault,
        isExpense: Boolean = this.isExpense,
        entry: TransactionCategoryEntry = this.entry,
        order: Int = this.order
    ): TransactionCategory {
        return TransactionCategory(id, icon, color, description, isDefault, isExpense, entry, order)
    }
}

class AllExpense(context: Context) :
    TransactionCategory(
        0,
        R.drawable.infinity_icon,
        wallet_color_24.toArgb(),
        context.getString(R.string.all_category),
        true,
        true,
        TransactionCategoryEntry(order = -6),
        -6
    )

class None(context: Context) : TransactionCategory(
    0,
    categories_icons[0],
    wallet_colors[0].toArgb(),
    context.getString(R.string.none),
    false,
    false,
    TransactionCategoryEntry(order = -6),
    -6
)

class Transfers(context: Context) : TransactionCategory(
    0,
    R.drawable.transfer_icon,
    wallet_color_24.toArgb(),
    context.getString(R.string.transfers),
    true,
    false,
    TransactionCategoryEntry(order = -6),
    -6
)

class All(context: Context) : TransactionCategory(
    0,
    R.drawable.transfer_icon,
    wallet_color_18.toArgb(),
    context.getString(R.string.all),
    true,
    false,
    TransactionCategoryEntry(order = -6),
    -6
)