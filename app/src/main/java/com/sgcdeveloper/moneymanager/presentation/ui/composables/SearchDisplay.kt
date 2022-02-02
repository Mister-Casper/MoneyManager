package com.sgcdeveloper.moneymanager.presentation.ui.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.Currency

enum class SearchDisplay {
    InitialResults, Suggestions, Results, NoResults
}

@Stable
class SearchState(
    query: TextFieldValue,
    focused: Boolean,
    searching: Boolean,
    searchResults: List<Currency>
) {
    var query = mutableStateOf(query)
    var focused = mutableStateOf(focused)
    var searching = mutableStateOf(searching)
    var searchResults = mutableStateOf(searchResults)

    val searchDisplay: SearchDisplay
        get() = when {
            !focused.value && query.value.text.isEmpty() -> SearchDisplay.InitialResults
            focused.value && query.value.text.isEmpty() -> SearchDisplay.Suggestions
            searchResults.value.isEmpty() -> SearchDisplay.NoResults
            else -> SearchDisplay.Results
        }

    override fun toString(): String {
        return "🚀 State query: $query, focused: $focused, searching: $searching " +
                "searchResults: ${searchResults.value.size}, " +
                " searchDisplay: $searchDisplay"

    }
}

@Composable
fun rememberSearchState(
    query: TextFieldValue = TextFieldValue(""),
    focused: Boolean = false,
    searching: Boolean = false,
    searchResults: List<Currency> = emptyList()
): SearchState {
    return remember {
        SearchState(
            query = query,
            focused = focused,
            searching = searching,
            searchResults = searchResults
        )
    }
}

@Composable
private fun SearchHint(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)

    ) {
        Text(
            text = stringResource(id = R.string.search),
            color = MaterialTheme.colors.secondary
        )
    }
}

@Composable
fun SearchTextField(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onSearchFocusChange: (Boolean) -> Unit,
    onClearQuery: () -> Unit,
    searching: Boolean,
    focused: Boolean,
    modifier: Modifier = Modifier
) {

    val focusRequester = remember { FocusRequester() }

    Surface(
        modifier = modifier
            .then(
                Modifier
                    .height(56.dp)
                    .padding(
                        top = 8.dp,
                        bottom = 8.dp,
                        start = if (!focused) 8.dp else 0.dp,
                        end = 48.dp
                    )
            ),
        shape = RoundedCornerShape(percent = 20),
    ) {

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = modifier
            ) {

                if (query.text.isEmpty()) {
                    SearchHint(modifier.padding(start = 8.dp, end = 8.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .onFocusChanged {
                                onSearchFocusChange(it.isFocused)
                            }
                            .focusRequester(focusRequester)
                            .padding(top = 2.dp, bottom = 2.dp, start = 2.dp, end = 2.dp),
                        singleLine = true,
                        textStyle = TextStyle(color = MaterialTheme.colors.secondary),
                        cursorBrush = SolidColor(MaterialTheme.colors.secondary),
                        decorationBox = { innerTextField ->
                            val mainModifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    shape = com.sgcdeveloper.moneymanager.presentation.theme.Shapes.small,
                                    color = MaterialTheme.colors.secondary
                                )
                                .padding(8.dp)
                            Column(
                                modifier = mainModifier,
                                content = {
                                    innerTextField()
                                }
                            )
                        },
                    )

                    when {
                        searching -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(horizontal = 6.dp)
                                    .size(36.dp)
                            )
                        }
                        query.text.isNotEmpty() -> {
                            IconButton(onClick = onClearQuery) {
                                Icon(
                                    painter = painterResource(id = R.drawable.cancel_icon),
                                    contentDescription = null,
                                    tint = MaterialTheme.colors.secondary
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onSearchFocusChange: (Boolean) -> Unit,
    onClearQuery: () -> Unit,
    onBack: () -> Unit,
    searching: Boolean,
    focused: Boolean,
    modifier: Modifier = Modifier
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        SearchTextField(
            query,
            onQueryChange,
            onSearchFocusChange,
            onClearQuery,
            searching,
            focused,
            modifier.weight(1f)
        )
    }
}