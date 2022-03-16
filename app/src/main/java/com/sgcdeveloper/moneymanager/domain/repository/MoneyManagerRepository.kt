package com.sgcdeveloper.moneymanager.domain.repository

import com.sgcdeveloper.moneymanager.data.db.dao.RateDao
import com.sgcdeveloper.moneymanager.data.db.dao.TransactionDao
import com.sgcdeveloper.moneymanager.data.db.dao.WalletDao

interface MoneyManagerRepository :WalletDao,TransactionDao,RateDao