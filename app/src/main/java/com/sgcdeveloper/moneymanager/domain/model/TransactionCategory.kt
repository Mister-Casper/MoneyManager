package com.sgcdeveloper.moneymanager.domain.model

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionCategoryEntry
import com.sgcdeveloper.moneymanager.presentation.theme.wallet_color_17
import com.sgcdeveloper.moneymanager.presentation.theme.wallet_color_18
import com.sgcdeveloper.moneymanager.presentation.theme.wallet_color_24

open class TransactionCategory(
    val id: Long,
    val icon: Int,
    val color: Int,
    val description: String,
    val isDefault: Boolean,
    val isExpense: Boolean,
    var entry:TransactionCategoryEntry
)

class AllExpense(context: Context) :
    TransactionCategory(
        33,
        R.drawable.infinity_icon,
        wallet_color_24.toArgb(),
        context.getString(R.string.all_category),
        true,
        true,
        TransactionCategoryEntry(order = -6)
    )

class None(context: Context) : TransactionCategory(
    0,
    R.drawable.transfer_icon,
    wallet_color_17.toArgb(),
    context.getString(R.string.none),
    true,
    false,
    TransactionCategoryEntry(order = -6)
)

class Transfers(context: Context) : TransactionCategory(
    1,
    R.drawable.transfer_icon,
    wallet_color_24.toArgb(),
    context.getString(R.string.transfers),
    true,
    false,
    TransactionCategoryEntry(order = -6)
)

class All(context: Context) : TransactionCategory(
    32,
    R.drawable.transfer_icon,
    wallet_color_18.toArgb(),
    context.getString(R.string.all),
    true,
    false,
    TransactionCategoryEntry(order = -6)
)