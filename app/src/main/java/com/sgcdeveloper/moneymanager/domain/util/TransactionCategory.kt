package com.sgcdeveloper.moneymanager.domain.util

import androidx.compose.ui.graphics.toArgb
import com.google.gson.annotations.Expose
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.theme.*

open class TransactionCategory(@Expose open val color: Int, @Expose open val icon: Int, @Expose open val description: Int) {

    object None : TransactionCategory(wallet_color_17.toArgb(), R.drawable.transfer_icon, R.string.none)

    object Transfers : TransactionCategory(wallet_color_17.toArgb(), R.drawable.transfer_icon, R.string.transfers)

    sealed class ExpenseCategory(override val color: Int, override val icon: Int, override val description: Int) :
        TransactionCategory(color, icon, description) {
        object Bills : ExpenseCategory(wallet_color_1.toArgb(), R.drawable.bills_icon, R.string.bills)
        object Clothes : ExpenseCategory(wallet_color_2.toArgb(), R.drawable.clothes_icon, R.string.clothes)
        object Education : ExpenseCategory(wallet_color_3.toArgb(), R.drawable.education_icon, R.string.education)
        object Entertainment :
            ExpenseCategory(wallet_color_4.toArgb(), R.drawable.entertaiment_icon, R.string.entertainment)

        object Sport : ExpenseCategory(wallet_color_5.toArgb(), R.drawable.sport_icon, R.string.sport)
        object Food : ExpenseCategory(wallet_color_6.toArgb(), R.drawable.food_icon, R.string.food)
        object Gifts : ExpenseCategory(wallet_color_7.toArgb(), R.drawable.gift_icon, R.string.gifts)
        object Health : ExpenseCategory(wallet_color_8.toArgb(), R.drawable.health_icon, R.string.health)
        object Furniture : ExpenseCategory(wallet_color_9.toArgb(), R.drawable.furniture_icon, R.string.furniture)
        object Pet : ExpenseCategory(wallet_color_10.toArgb(), R.drawable.pet_icon, R.string.pet)
        object Shopping : ExpenseCategory(wallet_color_11.toArgb(), R.drawable.shopping_icon, R.string.shopping)
        object Transport : ExpenseCategory(wallet_color_12.toArgb(), R.drawable.transport_icon, R.string.transport)
        object Travel : ExpenseCategory(wallet_color_13.toArgb(), R.drawable.travel_icon, R.string.travel)
        object Others : ExpenseCategory(wallet_color_14.toArgb(), R.drawable.others_icon, R.string.others)

        companion object {
            fun getItems(): List<ExpenseCategory> {
                return listOf(
                    Bills,
                    Clothes,
                    Education,
                    Entertainment,
                    Sport,
                    Food,
                    Gifts,
                    Health,
                    Furniture,
                    Pet,
                    Shopping,
                    Transport,
                    Travel,
                    Others
                )
            }
        }
    }

    sealed class IncomeCategory(override val color: Int, override val icon: Int, override val description: Int) :
        TransactionCategory(color, icon, description) {
        object Award : IncomeCategory(wallet_color_1.toArgb(), R.drawable.sport_icon, R.string.award)
        object Bonus : IncomeCategory(wallet_color_2.toArgb(), R.drawable.bonus_icon, R.string.bonus)
        object Dividend : IncomeCategory(wallet_color_3.toArgb(), R.drawable.dividend_icon, R.string.dividend)
        object Investment : IncomeCategory(wallet_color_4.toArgb(), R.drawable.investment_icon, R.string.investment)
        object Lottery : IncomeCategory(wallet_color_5.toArgb(), R.drawable.lottery_icon, R.string.lottery)
        object Salary : IncomeCategory(wallet_color_6.toArgb(), R.drawable.salary_icon, R.string.salary)
        object Tips : IncomeCategory(wallet_color_7.toArgb(), R.drawable.tips_icon, R.string.tips)
        object Others : IncomeCategory(wallet_color_8.toArgb(), R.drawable.others_icon, R.string.others)

        companion object {
            fun getItems(): List<IncomeCategory> {
                return listOf(Award, Bonus, Dividend, Investment, Lottery, Salary, Tips, Others)
            }
        }
    }
}