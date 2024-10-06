package com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkContentPreview
import com.github.klee0kai.thekey.core.utils.views.linkToParent
import com.github.klee0kai.thekey.core.utils.views.toAnnotationString
import com.github.klee0kai.thekey.dynamic.findstorage.R
import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import com.github.klee0kai.thekey.dynamic.findstorage.di.hardResetToPreview
import com.github.klee0kai.thekey.dynamic.findstorage.domain.model.FileItem
import java.io.File

@Composable
fun FileNameElement(
    modifier: Modifier = Modifier,
    fileItem: FileItem = FileItem("/"),
) {
    val theme = LocalTheme.current
    val pathInputHelper = remember { FSDI.pathInputHelper() }
    val shortPathsHelper = remember { FSDI.userShortPaths() }

    val file = remember(fileItem) { File(fileItem.absPath) }

    val short = remember(fileItem) {
        with(pathInputHelper) {
            shortPathsHelper.shortPathName(fileItem.absPath)
                .let { File(it).name }
                .toAnnotationString()
        }
    }
    val isShowHint = remember(fileItem) { file.name != short.text }

    ConstraintLayout(
        modifier = modifier
            .padding(bottom = 12.dp, top = 12.dp)
            .padding(start = 8.dp, end = 8.dp)
    ) {
        val (
            shortField,
            hintField,
            iconField,
        ) = createRefs()

        Icon(
            painter = painterResource(id = R.drawable.ic_folder),
            contentDescription = "folder",
            modifier = Modifier
                .alpha(if (fileItem.isFolder) 1f else 0f)
                .padding(end = 8.dp)
                .constrainAs(iconField) {
                    linkToParent(
                        horizontalBias = 0f,
                        end = shortField.start,
                    )
                }
        )

        Text(
            text = short,
            modifier = Modifier
                .constrainAs(shortField) {
                    width = Dimension.fillToConstraints
                    linkToParent(
                        start = iconField.end,
                        bottom = if (isShowHint) hintField.top else parent.bottom,
                        horizontalBias = 0f,
                    )
                },
            style = theme.typeScheme.body,
        )

        if (isShowHint) {
            Text(
                text = file.absolutePath,
                modifier = Modifier
                    .constrainAs(hintField) {
                        linkToParent(
                            start = iconField.end,
                            horizontalBias = 0f,
                            top = shortField.bottom,
                        )
                    },
                style = theme.typeScheme.bodySmall,
                color = theme.colorScheme.textColors.hintTextColor,
            )
        }
    }
}


@OptIn(DebugOnly::class)
@Composable
@Preview
fun FileNameElementHintPreview() {
    FSDI.hardResetToPreview()
    DebugDarkContentPreview {
        Box(modifier = Modifier.width(300.dp)) {
            FileNameElement(
                modifier = Modifier.fillMaxWidth(),
                fileItem = FileItem(
                    absPath = "/storage/emulated/0",
                    isFolder = true,
                )
            )
        }
    }
}

@OptIn(DebugOnly::class)
@Composable
@Preview
fun FileNameElementDocumentsPreview() {
    FSDI.hardResetToPreview()
    DebugDarkContentPreview {
        Box(modifier = Modifier.width(300.dp)) {
            FileNameElement(
                modifier = Modifier.fillMaxWidth(),
                fileItem = FileItem(
                    absPath = "/storage/emulated/0/Documents",
                    isFolder = true,
                )
            )
        }
    }
}

@OptIn(DebugOnly::class)
@Composable
@Preview
fun FileNameElementTxtPreview() {
    FSDI.hardResetToPreview()
    DebugDarkContentPreview {
        Box(modifier = Modifier.width(300.dp)) {
            FileNameElement(
                modifier = Modifier.fillMaxWidth(),
                fileItem = FileItem(
                    absPath = "/storage/emulated/0/Documents/simple.txt",
                    isFolder = false,
                )
            )
        }
    }
}