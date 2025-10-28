package com.example.shoutless.util

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp

fun Modifier.glow(
    color: Color,
    radius: Dp,
    shape: Shape,
    alpha: Float = 0.9f
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.maskFilter = (BlurMaskFilter(
            radius.toPx(),
            BlurMaskFilter.Blur.NORMAL
        ))
        frameworkPaint.color = color.copy(alpha = alpha).toArgb()

        when (val outline = shape.createOutline(size, layoutDirection, this)) {
            is Outline.Generic -> canvas.drawPath(outline.path, paint)
            is Outline.Rectangle -> canvas.drawRect(outline.rect, paint)
            is Outline.Rounded -> {
                val path = Path().apply { addRoundRect(outline.roundRect) }
                canvas.drawPath(path, paint)
            }
        }
    }
}
