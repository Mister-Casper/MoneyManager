package com.sgcdeveloper.moneymanager.presentation.ui.registration

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sgcdeveloper.moneymanager.R

@Composable
fun InitScreen() {
    Column(Modifier.fillMaxSize()) {
        Text(
            text = stringResource(id = R.string.add_account),
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colors.secondary
        )

        Text(
            text = stringResource(id = R.string.choose_a_name),
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colors.secondary
        )

        val accountName = rememberSaveable { mutableStateOf("") }
        val money = rememberSaveable { mutableStateOf("") }
        val currency = rememberSaveable { mutableStateOf("") }

        TextField(
            value = accountName.value,
            onValueChange = {
                accountName.value = it
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
            singleLine = true
        )

        Text(
            text = stringResource(id = R.string.choose_a_default_currency),
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp),
            color = MaterialTheme.colors.secondary
        )

        TextField(
            value = currency.value,
            onValueChange = {},
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
            singleLine = true
        )

        Text(
            text = stringResource(id = R.string.money_default),
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp),
            color = MaterialTheme.colors.secondary
        )

        TextField(
            value = money.value,
            onValueChange = {
                money.value = it
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
            singleLine = true
        )

        Button(
            onClick = {

            },
            modifier = Modifier
                .padding(top = 16.dp, start = 32.dp, end = 32.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
        ) {
            Text(text = stringResource(id = R.string.next), color = Color.White)
        }

    }
}