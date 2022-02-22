package com.sgcdeveloper.moneymanager.domain.util

import androidx.compose.ui.graphics.toArgb
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.theme.*

open class TransactionCategory(
    open val id: Int,
    open val color: Int,
    open val icon: Int,
    open val description: Int
) {

    object None : TransactionCategory(0, wallet_color_17.toArgb(), R.drawable.transfer_icon, R.string.none)

    object Transfers : TransactionCategory(1, wallet_color_17.toArgb(), R.drawable.transfer_icon, R.string.transfers)

    object All : TransactionCategory(24, wallet_color_18.toArgb(), R.drawable.transfer_icon, R.string.all)

    sealed class ExpenseCategory(_id: Int, _color: Int, _icon: Int, _description: Int) :
        TransactionCategory(_id, _color, _icon, _description) {
        object Bills : ExpenseCategory(2, wallet_color_1.toArgb(), R.drawable.bills_icon, R.string.bills)
        object Clothes : ExpenseCategory(3, wallet_color_2.toArgb(), R.drawable.clothes_icon, R.string.clothes)
        object Education : ExpenseCategory(4, wallet_color_3.toArgb(), R.drawable.education_icon, R.string.education)
        object Entertainment :
            ExpenseCategory(5, wallet_color_4.toArgb(), R.drawable.entertaiment_icon, R.string.entertainment)

        object Sport : ExpenseCategory(6, wallet_color_5.toArgb(), R.drawable.sport_icon, R.string.sport)
        object Food : ExpenseCategory(7, wallet_color_6.toArgb(), R.drawable.food_icon, R.string.food)
        object Gifts : ExpenseCategory(8, wallet_color_7.toArgb(), R.drawable.gift_icon, R.string.gifts)
        object Health : ExpenseCategory(9, wallet_color_8.toArgb(), R.drawable.health_icon, R.string.health)
        object Furniture : ExpenseCategory(10, wallet_color_9.toArgb(), R.drawable.furniture_icon, R.string.furniture)
        object Pet : ExpenseCategory(11, wallet_color_10.toArgb(), R.drawable.pet_icon, R.string.pet)
        object Shopping : ExpenseCategory(12, wallet_color_11.toArgb(), R.drawable.shopping_icon, R.string.shopping)
        object Transport : ExpenseCategory(13, wallet_color_12.toArgb(), R.drawable.transport_icon, R.string.transport)
        object Travel : ExpenseCategory(14, wallet_color_13.toArgb(), R.drawable.travel_icon, R.string.travel)
        object Others : ExpenseCategory(15, wallet_color_14.toArgb(), R.drawable.others_icon, R.string.others)

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

    sealed class IncomeCategory(_id: Int, _color: Int, _icon: Int, _description: Int) :
        TransactionCategory(_id, _color, _icon, _description) {
        object Award : IncomeCategory(16, wallet_color_1.toArgb(), R.drawable.sport_icon, R.string.award)
        object Bonus : IncomeCategory(17, wallet_color_2.toArgb(), R.drawable.bonus_icon, R.string.bonus)
        object Dividend : IncomeCategory(18, wallet_color_3.toArgb(), R.drawable.dividend_icon, R.string.dividend)
        object Investment : IncomeCategory(19, wallet_color_4.toArgb(), R.drawable.investment_icon, R.string.investment)
        object Lottery : IncomeCategory(20, wallet_color_5.toArgb(), R.drawable.lottery_icon, R.string.lottery)
        object Salary : IncomeCategory(21, wallet_color_6.toArgb(), R.drawable.salary_icon, R.string.salary)
        object Tips : IncomeCategory(22, wallet_color_7.toArgb(), R.drawable.tips_icon, R.string.tips)
        object Others : IncomeCategory(23, wallet_color_8.toArgb(), R.drawable.others_icon, R.string.others)

        companion object {
            fun getItems(): List<IncomeCategory> {
                return listOf(Award, Bonus, Dividend, Investment, Lottery, Salary, Tips, Others)
            }
        }
    }

    companion object {
        fun getById(id: Int): TransactionCategory {
            return getItems().find { it.id == id }!!
        }

        fun getItems(): List<TransactionCategory> {
            return listOf(
                None,
                Transfers,
                ExpenseCategory.Bills,
                ExpenseCategory.Clothes,
                ExpenseCategory.Education,
                ExpenseCategory.Entertainment,
                ExpenseCategory.Sport,
                ExpenseCategory.Food,
                ExpenseCategory.Gifts,
                ExpenseCategory.Health,
                ExpenseCategory.Furniture,
                ExpenseCategory.Pet,
                ExpenseCategory.Shopping,
                ExpenseCategory.Transport,
                ExpenseCategory.Travel,
                ExpenseCategory.Others,
                IncomeCategory.Award,
                IncomeCategory.Bonus,
                IncomeCategory.Dividend,
                IncomeCategory.Investment,
                IncomeCategory.Lottery,
                IncomeCategory.Salary,
                IncomeCategory.Tips,
                IncomeCategory.Others
            )
        }
    }
}