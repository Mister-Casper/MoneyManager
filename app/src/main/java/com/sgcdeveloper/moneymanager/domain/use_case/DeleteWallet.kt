package com.sgcdeveloper.moneymanager.domain.use_case

import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import javax.inject.Inject

class DeleteWallet @Inject constructor(
    private val moneyManagerRepository: MoneyManagerRepository,
) {
    suspend operator fun invoke(walletId: Long) {
        moneyManagerRepository.removeWallet(walletId)
    }
}