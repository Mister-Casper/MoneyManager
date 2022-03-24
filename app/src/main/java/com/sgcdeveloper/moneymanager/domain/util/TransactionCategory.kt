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

    object Transfers : TransactionCategory(1, wallet_color_24.toArgb(), R.drawable.transfer_icon, R.string.transfers)

    object All : TransactionCategory(32, wallet_color_18.toArgb(), R.drawable.transfer_icon, R.string.all)

    open class ExpenseCategory(_id: Int, _color: Int, _icon: Int, _description: Int) :
        TransactionCategory(_id, _color, _icon, _description) {
        object AllExpense :
            ExpenseCategory(33, wallet_color_24.toArgb(), R.drawable.infinity_icon, R.string.all_category)

        object Bills : ExpenseCategory(2, wallet_color_1.toArgb(), R.drawable.bills_icon, R.string.bills)
        object Clothes : ExpenseCategory(3, wallet_color_2.toArgb(), R.drawable.clothes_icon, R.string.clothes)
        object Donation : ExpenseCategory(4, wallet_color_3.toArgb(), R.drawable.donation_icon, R.string.donation)
        object Education : ExpenseCategory(5, wallet_color_4.toArgb(), R.drawable.education_icon, R.string.education)
        object Entertainment :
            ExpenseCategory(6, wallet_color_5.toArgb(), R.drawable.entertaiment_icon, R.string.entertainment)

        object Sport : ExpenseCategory(7, wallet_color_6.toArgb(), R.drawable.sport_icon, R.string.sport)
        object Food : ExpenseCategory(8, wallet_color_7.toArgb(), R.drawable.food_icon, R.string.food)
        object Gifts : ExpenseCategory(9, wallet_color_8.toArgb(), R.drawable.gift_icon, R.string.gifts)
        object Health : ExpenseCategory(10, wallet_color_9.toArgb(), R.drawable.health_icon, R.string.health)
        object Household : ExpenseCategory(11, wallet_color_10.toArgb(), R.drawable.household_icon, R.string.household)
        object Insurance : ExpenseCategory(12, wallet_color_11.toArgb(), R.drawable.insurance_icon, R.string.insurance)
        object Furniture : ExpenseCategory(13, wallet_color_13.toArgb(), R.drawable.furniture_icon, R.string.furniture)
        object Pet : ExpenseCategory(14, wallet_color_14.toArgb(), R.drawable.pet_icon, R.string.pet)
        object SelfImprovement :
            ExpenseCategory(15, wallet_color_15.toArgb(), R.drawable.self_improving_icon, R.string.aelf_improvement)

        object Shopping : ExpenseCategory(16, wallet_color_16.toArgb(), R.drawable.shopping_icon, R.string.shopping)
        object Tax : ExpenseCategory(17, wallet_color_17.toArgb(), R.drawable.tax_icon, R.string.tax)
        object Transport : ExpenseCategory(18, wallet_color_18.toArgb(), R.drawable.transport_icon, R.string.transport)
        object Travel : ExpenseCategory(19, wallet_color_19.toArgb(), R.drawable.travel_icon, R.string.travel)
        object Others : ExpenseCategory(20, wallet_color_20.toArgb(), R.drawable.others_icon, R.string.others)

        companion object {
            fun getAllItems(): List<ExpenseCategory> {
                return listOf(AllExpense) + getItems()
            }

            fun getItems(): List<ExpenseCategory> {
                return listOf(
                    Bills,
                    Clothes,
                    Donation,
                    Education,
                    Entertainment,
                    Sport,
                    Food,
                    Gifts,
                    Health,
                    Household,
                    Insurance,
                    Furniture,
                    Pet,
                    SelfImprovement,
                    Shopping,
                    Tax,
                    Transport,
                    Travel,
                    Others
                )
            }

            fun getStringRes(category:ExpenseCategory):Int{
                   return findById (category.id ).description
            }
        }
    }

    open class IncomeCategory(_id: Int, _color: Int, _icon: Int, _description: Int) :
        TransactionCategory(_id, _color, _icon, _description) {
        object Award : IncomeCategory(21, wallet_color_11.toArgb(), R.drawable.sport_icon, R.string.award)
        object Bonus : IncomeCategory(22, wallet_color_12.toArgb(), R.drawable.bonus_icon, R.string.bonus)
        object Dividend : IncomeCategory(23, wallet_color_13.toArgb(), R.drawable.dividend_icon, R.string.dividend)
        object Investment :
            IncomeCategory(24, wallet_color_14.toArgb(), R.drawable.investment_icon, R.string.investment)

        object Lottery : IncomeCategory(25, wallet_color_15.toArgb(), R.drawable.lottery_icon, R.string.lottery)
        object Salary : IncomeCategory(26, wallet_color_16.toArgb(), R.drawable.salary_icon, R.string.salary)
        object Tips : IncomeCategory(27, wallet_color_17.toArgb(), R.drawable.tips_icon, R.string.tips)
        object Gifts : IncomeCategory(28, wallet_color_18.toArgb(), R.drawable.gift_icon, R.string.gifts)
        object Others : IncomeCategory(29, wallet_color_19.toArgb(), R.drawable.others_icon, R.string.others)

        companion object {
            fun getItems(): List<IncomeCategory> {
                return listOf(Award, Bonus, Dividend, Investment, Lottery, Salary, Tips, Gifts, Others)
            }
        }
    }

    companion object {
        private val allItems = (IncomeCategory.getItems() + ExpenseCategory.getAllItems()).associateBy { it.id }

        fun findById(id:Int):TransactionCategory{
            return allItems[id]!!
        }

        fun getById(id: Int): TransactionCategory {
            return getItems().find { it.id == id }!!
        }

        fun getItems(): List<TransactionCategory> {
            return listOf(
                None,
                Transfers,
            ) + ExpenseCategory.getItems() + IncomeCategory.getItems()
        }
    }
}