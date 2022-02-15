package com.sgcdeveloper.moneymanager.domain.use_case

import javax.inject.Inject

class WalletsUseCases @Inject constructor(
    val getWallets: GetWallets,
    val insertWallet: InsertWallet,
    val deleteWallet: DeleteWallet,
    val insertTransaction: InsertTransaction,
    val getTransactionItems: GetTransactionItems,
    val getCategoriesStatistic: GetCategoriesStatistic
)