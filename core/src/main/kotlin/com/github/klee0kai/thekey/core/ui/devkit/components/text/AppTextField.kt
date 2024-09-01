package com.github.klee0kai.thekey.core.ui.devkit.components.text

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.grayColors
import com.github.klee0kai.thekey.core.utils.views.transparentColors
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isSkeleton: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    onValueChange: (TextFieldValue) -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: TextFieldColors = TextFieldDefaults.grayColors(),
) {
    val skeletonAnimated by animateTargetCrossFaded(target = isSkeleton)
    if (skeletonAnimated.current) {
        val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.Window)
        val color = MaterialTheme.colorScheme.inverseSurface
        Box(
            modifier = modifier
                .alpha(skeletonAnimated.alpha)
                .defaultMinSize(
                    minWidth = TextFieldDefaults.MinWidth,
                    minHeight = TextFieldDefaults.MinHeight
                )
                .shimmer(shimmer)
                .background(
                    color = color,
                    shape = RoundedCornerShape(10.dp)
                )
        )
    } else {
        TextField(
            modifier = modifier,
            value = value,
            enabled = enabled,
            readOnly = readOnly,
            onValueChange = onValueChange,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            prefix = prefix,
            suffix = suffix,
            supportingText = supportingText,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            shape = RoundedCornerShape(10.dp),
            interactionSource = interactionSource,
            colors = colors,
        )
    }
}

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    value: String,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isSkeleton: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: TextFieldColors = TextFieldDefaults.grayColors(),
) {
    val skeletonAnimated by animateTargetCrossFaded(target = isSkeleton)
    if (skeletonAnimated.current) {
        val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.Window)
        val color = MaterialTheme.colorScheme.inverseSurface
        Box(
            modifier = modifier
                .alpha(skeletonAnimated.alpha)
                .defaultMinSize(
                    minWidth = TextFieldDefaults.MinWidth,
                    minHeight = TextFieldDefaults.MinHeight
                )
                .shimmer(shimmer)
                .background(
                    color = color,
                    shape = RoundedCornerShape(10.dp)
                )
        )
    } else {
        TextField(
            modifier = modifier
                .alpha(skeletonAnimated.alpha),
            value = value,
            enabled = enabled,
            readOnly = readOnly,
            onValueChange = onValueChange,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            prefix = prefix,
            suffix = suffix,
            supportingText = supportingText,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            shape = RoundedCornerShape(10.dp),
            interactionSource = interactionSource,
            colors = colors,
        )
    }
}


@Composable
@Preview
fun AppTextFieldPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    Box(
        modifier = Modifier.padding(10.dp)
    ) {
        AppTextField(
            modifier = Modifier,
            value = TextFieldValue("User some input text"),
            label = {
                Text(text = "label")
            }
        )
    }
}

@Composable
@Preview
fun AppTextFieldSkeletonPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    Box(
        modifier = Modifier.padding(10.dp),
    ) {
        AppTextField(
            modifier = Modifier,
            isSkeleton = true,
            value = "User some input text",
            label = {
                Text(text = "label")
            }
        )
    }
}

@Composable
@Preview
fun AppTextEmptyFieldPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    Box(
        modifier = Modifier.padding(10.dp)
    ) {
        AppTextField(
            modifier = Modifier,
            value = "",
            label = {
                Text(text = "label")
            }
        )
    }
}


@Composable
@Preview
fun AppTransparentTextFieldPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    Box(
        modifier = Modifier.padding(10.dp)
    ) {
        AppTextField(
            modifier = Modifier,
            value = TextFieldValue("User some input text"),
            label = {
                Text(text = "label")
            },
            colors = TextFieldDefaults.transparentColors(),
        )
    }
}

@Composable
@Preview
fun AppTransparentTextEmptyFieldPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    Box(
        modifier = Modifier.padding(10.dp)
    ) {
        AppTextField(
            modifier = Modifier,
            value = "",
            label = {
                Text(text = "label")
            },
            colors = TextFieldDefaults.transparentColors(),
        )
    }
}