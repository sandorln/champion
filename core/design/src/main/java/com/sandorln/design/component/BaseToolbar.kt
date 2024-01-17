package com.sandorln.design.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.sandorln.design.R
import com.sandorln.design.theme.Colors
import com.sandorln.design.theme.Dimens
import com.sandorln.design.theme.LolChampionTheme
import com.sandorln.design.theme.IconSize
import com.sandorln.design.theme.Spacings
import com.sandorln.design.theme.TextStyles


@Composable
fun BaseToolbar(
    modifier: Modifier = Modifier,
    onClickStartIcon: () -> Unit = {},
    title: String = "제목",
    @DrawableRes startIconResId: Int = R.drawable.ic_chevron_left,
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.BaseToolbarHeight)
                .background(Colors.Gray900)
                .padding(
                    top = Spacings.Spacing02,
                    bottom = Spacings.Spacing02,
                    start = Spacings.Spacing03,
                    end = Spacings.Spacing05
                )
        ) {
            Icon(
                modifier = Modifier
                    .size(IconSize.XLargeSize)
                    .align(alignment = Alignment.CenterStart)
                    .clickable {
                        onClickStartIcon.invoke()
                    },
                painter = painterResource(id = startIconResId),
                contentDescription = null,
                tint = Color.Unspecified
            )

            Text(
                modifier = Modifier.align(alignment = Alignment.Center),
                text = title,
                style = TextStyles.Title03
            )

        }

        Divider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun ButtonToolbar(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit = {},
    endButtonComposable: @Composable () -> Unit = {
        Text(
            modifier = Modifier.padding(end = Spacings.Spacing02),
            text = "건너뛰기",
            style = TextStyles.Body01
        )
    },
    title: String = "",
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.BaseToolbarHeight)
                .background(Colors.Gray900)
                .padding(
                    vertical = Spacings.Spacing02,
                    horizontal = Spacings.Spacing03
                )
        ) {
            Icon(
                modifier = Modifier
                    .size(IconSize.XLargeSize)
                    .align(alignment = Alignment.CenterStart)
                    .clickable {
                        onClickBack.invoke()
                    },
                painter = painterResource(id = R.drawable.ic_chevron_left),
                contentDescription = null,
                tint = Color.Unspecified
            )

            Text(
                modifier = Modifier.align(alignment = Alignment.Center),
                text = title,
                style = TextStyles.Title03
            )

            Box(modifier = Modifier.align(alignment = Alignment.CenterEnd)) {
                endButtonComposable.invoke()
            }
        }

        HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun HomeToolbar(
    modifier: Modifier = Modifier,
    endButtonComposable: @Composable () -> Unit = {
        Text(
            modifier = Modifier.padding(end = Spacings.Spacing02),
            text = "건너뛰기",
            style = TextStyles.Body01
        )
    },
    title: String = "",
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.BaseToolbarHeight)
                .background(Colors.Gray900)
                .padding(
                    vertical = Spacings.Spacing02,
                    horizontal = Spacings.Spacing05
                )
        ) {
            Text(
                modifier = Modifier.align(alignment = Alignment.CenterStart),
                text = title,
                style = TextStyles.Title02
            )

            Box(modifier = Modifier.align(alignment = Alignment.CenterEnd)) {
                endButtonComposable.invoke()
            }
        }

        HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Preview
@Composable
fun BaseToolbarPreview() {
    LolChampionTheme {
        Surface {
            BaseToolbar()
        }
    }
}

@Preview
@Composable
fun ButtonToolbarPreview() {
    LolChampionTheme {
        Surface {
            ButtonToolbar()
        }
    }
}