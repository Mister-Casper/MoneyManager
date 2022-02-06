package com.sgcdeveloper.moneymanager.domain.use_case

import javax.inject.Inject

class WalletsUseCases @Inject constructor(val getWallets: GetWallets,val insertWallet: InsertWallet)