package com.sgcdeveloper.moneymanager.domain.model

import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.util.Date
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.util.*

sealed class RecurringInterval(
    val sameDay: Boolean = false,
    val days: List<DayOfWeek> = Collections.emptyList(),
    val recurring: Recurring,
    val lastTransactionDate: Date?,
    val isForever: Boolean,
    val endDate: Date = Date(LocalDateTime.now()),
    val repeatInterval: Int = 1,
    val type: RecurringEndType = RecurringEndType.Forever,
    val times: Int = 1
) {

    object None : RecurringInterval(false, Collections.emptyList(), Recurring.None, null, false)

    class Daily(
        _lastTransactionDate: Date?,
        _isForever: Boolean,
        _endDate: Date,
        _repeatIInterval: Int,
        times: Int,
        type: RecurringEndType
    ) :
        RecurringInterval(
            false,
            Collections.emptyList(),
            Recurring.Daily,
            _lastTransactionDate,
            _isForever,
            _endDate,
            _repeatIInterval,
            times = times,
            type = type
        )

    class Weekly(
        days: List<DayOfWeek>,
        _lastTransactionDate: Date?,
        _isForever: Boolean,
        _endDate: Date,
        _repeatIInterval: Int,
        times: Int, type: RecurringEndType
    ) :
        RecurringInterval(
            false,
            days,
            Recurring.Weekly,
            _lastTransactionDate,
            _isForever,
            _endDate,
            _repeatIInterval,
            times = times,
            type = type
        )

    class Monthly(
        sameDay: Boolean,
        _lastTransactionDate: Date?,
        _isForever: Boolean,
        _endDate: Date,
        _repeatIInterval: Int,
        times: Int, type: RecurringEndType
    ) :
        RecurringInterval(
            sameDay,
            Collections.emptyList(),
            Recurring.Monthly,
            _lastTransactionDate,
            _isForever,
            _endDate,
            _repeatIInterval, times = times,
            type = type
        )

    class Yearly(
        _lastTransactionDate: Date?,
        _isForever: Boolean,
        _endDate: Date,
        _repeatIInterval: Int,
        times: Int,
        type: RecurringEndType
    ) :
        RecurringInterval(
            false,
            Collections.emptyList(),
            Recurring.Yearly,
            _lastTransactionDate,
            _isForever,
            _endDate,
            _repeatIInterval, times = times,
            type = type
        )
}

enum class Recurring(val titleRes: Int, val nameRes: Int) {
    None(R.string.none_recurring_nterval, R.string.none),
    Daily(R.string.daily, R.string.day),
    Weekly(R.string.weekly, R.string.week),
    Monthly(R.string.monthly, R.string.month),
    Yearly(R.string.yearly, R.string.year)
}

enum class RecurringEndType(val nameRes: Int) {
    Forever(R.string.forever), Until(R.string.until), For(R.string.for_repeat)
}