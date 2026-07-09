package net.irext.ircontrol.compose.ui.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.irext.ircontrol.compose.R
import net.irext.ircontrol.compose.ui.theme.IRControlTheme


/**
 * Filename:       ItemSingleText.kt
 * Created:        Date: 2026-07-04
 *
 * Description:    Displays a selectable single-line list item.
 *
 * Revision log:
 * 2026-07-04: created by shdmfire
 */
/**
 * Composable replacement for item_brand.xml, item_category.xml, item_city.xml,
 * item_operator.xml, and item_remote.xml.
 *
 * Original XML: Horizontal LinearLayout (match_parent x match_parent, padding=4dp)
 *   + TextView (0dp x 64dp, weight=4, marginStart/End=10dp, center_vertical, bold, 20sp)
 */
@Composable
fun ItemSingleText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .heightIn(min = 64.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ItemSingleTextPreview() {
    IRControlTheme {
        ItemSingleText(text = stringResource(R.string.sample_item))
    }
}
