package com.sandorln.design.component.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialogProperties
import com.sandorln.design.R
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.GodadviceTheme
import com.sandorln.design.theme.Radius
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles

@Composable
fun SelectExperienceYearDialog(
    onDismissRequest: () -> Unit = {},
    selectYear: Int = 0,
    onSelectExperienceYear: (Int?) -> Unit = {}
) {
    BottomSheetDialog(
        onDismissRequest = onDismissRequest,
        properties = BottomSheetDialogProperties()
    ) {
        SelectExperienceYearBody(
            onDismissRequest = onDismissRequest,
            selectYear = selectYear,
            onSelectExperienceYear = onSelectExperienceYear
        )
    }
}

@Composable
fun SelectExperienceYearBody(
    onDismissRequest: () -> Unit = {},
    selectYear: Int = 0,
    onSelectExperienceYear: (Int?) -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = Radius.Radius04,
            topEnd = Radius.Radius04
        ),
        colors = CardDefaults.cardColors(containerColor = Colors.Gray800)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 519.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier
                    .padding(top = Spacings.Spacing02)
                    .background(Colors.Gray700, RoundedCornerShape(Radius.Radius01))
                    .width(40.dp)
                    .height(4.dp)
            )
            Text(
                modifier = Modifier
                    .padding(top = Spacings.Spacing03)
                    .fillMaxWidth()
                    .padding(Spacings.Spacing05),
                text = "연차 선택",
                style = TextStyles.Title02
            )

            LazyColumn {
                items(count = 15) { index ->
                    val year = index + 1
                    ExperienceYearItem(
                        year = year,
                        isSelectYear = year == selectYear,
                        onDismissRequest = onDismissRequest,
                        onSelectExperienceYear = onSelectExperienceYear
                    )
                }
            }
        }
    }
}

@Composable
fun ExperienceYearItem(
    year: Int = 0,
    isSelectYear: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onSelectExperienceYear: (Int?) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp)
            .clickable {
                onSelectExperienceYear.invoke(if (isSelectYear) null else year)
                onDismissRequest.invoke()
            }
            .padding(
                horizontal = Spacings.Spacing05,
                vertical = Spacings.Spacing03
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = if (year > 14) "15년 이상" else year.toString(),
            style = TextStyles.SubTitle01,
            color = if (isSelectYear) Colors.Green300 else Colors.BasicWhite
        )

        if (isSelectYear)
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = null,
                tint = Colors.Green300
            )
    }
}

@Composable
@Preview
fun SelectExperienceYearBodyPreview() {
    GodadviceTheme {
        Surface {
            SelectExperienceYearBody()
        }
    }
}