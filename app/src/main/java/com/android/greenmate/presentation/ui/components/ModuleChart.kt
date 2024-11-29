package com.android.greenmate.presentation.ui.components

import android.annotation.SuppressLint
import android.content.res.AssetManager.AssetInputStream
import android.graphics.Typeface
import android.text.Layout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.greenmate.domain.model.Module
import com.android.greenmate.presentation.ui.screens.calcLight
import com.android.greenmate.presentation.ui.screens.calcSoilMoisture
import com.android.greenmate.presentation.viewmodel.MyPlantViewModel
import com.android.greenmate.presentation.viewmodel.PlantViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisTickComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.fixed
import com.patrykandpatrick.vico.compose.common.component.rememberLayeredComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShadow
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.shape.markerCornered
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.HorizontalDimensions
import com.patrykandpatrick.vico.core.cartesian.Insets
import com.patrykandpatrick.vico.core.cartesian.axis.BaseAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.LayeredComponent
import com.patrykandpatrick.vico.core.common.component.Shadow
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.copyColor
import com.patrykandpatrick.vico.core.common.shape.Corner
import com.patrykandpatrick.vico.core.common.shape.Shape
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ModuleChart(
    moduleValues: List<Module>,
    moduleValue: String,
    plantId: Long,
    plantViewModel: PlantViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val assetManager = LocalContext.current.assets

    val modelProducer = remember { CartesianChartModelProducer() }
    val dateFormat = SimpleDateFormat("yy.MM.dd E HH:mm", Locale.KOREAN)

    val chartColor = remember { mutableStateOf( Color(0x44FFFFFF) ) }

    val light by plantViewModel.light.observeAsState()

    LaunchedEffect(Unit) {
        plantViewModel.getPlantById(plantId)
    }

    LaunchedEffect(moduleValues) {
        if(moduleValues.isNotEmpty()) {
            when (moduleValue) {
                "soilMoisture" -> {
                    modelProducer.runTransaction { lineSeries { series(moduleValues.map {
                        when(calcSoilMoisture(it.soilMoisture)) {
                            "아주 촉촉해" -> 4
                            "촉촉해" -> 3
                            "적당해" -> 2
                            "건조해" -> 1
                            "너무 건조해" -> 0
                            else -> {-1}
                        }
                    }) } }
                    chartColor.value = Color(0xAB3F51B5)
                }
                "humidity" -> {
                    modelProducer.runTransaction { lineSeries { series(moduleValues.map { it.humidity }) } }
                    chartColor.value = Color(0xAB4CAF50)
                }
                "temperature" -> {
                    modelProducer.runTransaction { lineSeries { series(moduleValues.map { it.temperature }) } }
                    chartColor.value = Color(0xABF44336)
                }
                "light" -> {
                    modelProducer.runTransaction { lineSeries { series(moduleValues.map {
                        when(light?.let { it1 -> calcLight(it1, it.lightIntensity) }) {
                            "노곤노곤해" -> 2
                            "그늘로 옮겨줘" -> 1
                            "햇빛이 필요해" -> 0
                            else -> {-1}
                        }
                    }) } }
                    chartColor.value = Color(0xABFF9800)
                }
            }
        }
    }

    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    rememberLine(LineCartesianLayer.LineFill.single(fill(chartColor.value)))
                ),
            ),
            startAxis = if(moduleValue == "soilMoisture") {
                rememberStartAxis(
                    itemPlacer = VerticalAxis.ItemPlacer.count(count = { 5 }),
                    label = TextComponent(
                        typeface = Typeface.createFromAsset(assetManager, "dohyeon_regular.ttf"),
                        padding = Dimensions.of(horizontal = 5.dp),
                    ),
                    valueFormatter = { value, _, _ ->
                        val yAxisLabelData = listOf("너무 건조해", "건조해", "적당해", "촉촉해", "아주 촉촉해")
                        (yAxisLabelData[value.toInt()])
                    },
                )
            } else if(moduleValue == "light") {
                rememberStartAxis(
                    itemPlacer = VerticalAxis.ItemPlacer.count(count = { 3 }),
                    label = TextComponent(
                        typeface = Typeface.createFromAsset(assetManager, "dohyeon_regular.ttf"),
                        padding = Dimensions.of(horizontal = 5.dp),
                    ),
                    valueFormatter = { value, _, _ ->
                        val yAxisLabelData = listOf("햇빛이 필요해", "그늘로 옮겨줘", "노곤노곤해")
                        (yAxisLabelData[value.toInt()])
                    },
                )
            } else {
                rememberStartAxis(
                    label = TextComponent(
                        typeface = Typeface.createFromAsset(assetManager, "dohyeon_regular.ttf"),
                        padding = Dimensions.of(horizontal = 5.dp),
                    ),
                )
            },
            bottomAxis = rememberBottomAxis(
                label = TextComponent(
                    typeface = Typeface.createFromAsset(assetManager,"dohyeon_regular.ttf"),
                    textSizeSp = 7f,
                    padding = Dimensions.of(horizontal = 5.dp),
                ),
                labelRotationDegrees = -66f,
                sizeConstraint = BaseAxis.SizeConstraint.TextWidth("  24.08.24 토 03:51  "),
                valueFormatter = { value, _, _ ->
                    val xAxisLabelData = moduleValues.map { it.timestamp }
                    (dateFormat.format(xAxisLabelData[value.toInt()]))
                }
            ),
            marker = rememberMarker(),
        ),
        modelProducer,
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(10.dp))
            .height(screenHeight / 3f)
    )
}

@Composable
internal fun rememberMarker(
    labelPosition: DefaultCartesianMarker.LabelPosition = DefaultCartesianMarker.LabelPosition.Top,
    showIndicator: Boolean = true,
): CartesianMarker {
    val labelBackgroundShape = Shape.markerCornered(Corner.FullyRounded)
    val labelBackground =
        rememberShapeComponent(
            color = MaterialTheme.colorScheme.surfaceBright,
            shape = labelBackgroundShape,
            shadow =
            rememberShadow(
                radius = LABEL_BACKGROUND_SHADOW_RADIUS_DP.dp,
                dy = LABEL_BACKGROUND_SHADOW_DY_DP.dp,
            ),
        )
    val label =
        rememberTextComponent(
            color = MaterialTheme.colorScheme.onSurface,
            textAlignment = Layout.Alignment.ALIGN_CENTER,
            padding = Dimensions.of(8.dp, 4.dp),
            background = labelBackground,
            minWidth = TextComponent.MinWidth.fixed(40.dp),
        )
    val indicatorFrontComponent =
        rememberShapeComponent(MaterialTheme.colorScheme.surface, Shape.Pill)
    val indicatorCenterComponent = rememberShapeComponent(shape = Shape.Pill)
    val indicatorRearComponent = rememberShapeComponent(shape = Shape.Pill)
    val indicator =
        rememberLayeredComponent(
            rear = indicatorRearComponent,
            front =
            rememberLayeredComponent(
                rear = indicatorCenterComponent,
                front = indicatorFrontComponent,
                padding = Dimensions.of(5.dp),
            ),
            padding = Dimensions.of(10.dp),
        )
    val guideline = rememberAxisGuidelineComponent()
    return remember(label, labelPosition, indicator, showIndicator, guideline) {
        @SuppressLint("RestrictedApi")
        object :
            DefaultCartesianMarker(
                label = label,
                labelPosition = labelPosition,
                indicator =
                if (showIndicator) {
                    { color ->
                        LayeredComponent(
                            rear = ShapeComponent(color.copyColor(alpha = 0.15f), Shape.Pill),
                            front =
                            LayeredComponent(
                                rear =
                                ShapeComponent(
                                    color = color,
                                    shape = Shape.Pill,
                                    shadow = Shadow(radiusDp = 12f, color = color),
                                ),
                                front = indicatorFrontComponent,
                                padding = Dimensions.of(5.dp),
                            ),
                            padding = Dimensions.of(10.dp),
                        )
                    }
                } else {
                    null
                },
                indicatorSizeDp = 36f,
                guideline = guideline,
            ) {
            override fun updateInsets(
                context: CartesianMeasuringContext,
                horizontalDimensions: HorizontalDimensions,
                model: CartesianChartModel,
                insets: Insets,
            ) {
                with(context) {
                    val baseShadowInsetDp =
                        CLIPPING_FREE_SHADOW_RADIUS_MULTIPLIER * LABEL_BACKGROUND_SHADOW_RADIUS_DP
                    var topInset = (baseShadowInsetDp - LABEL_BACKGROUND_SHADOW_DY_DP).pixels
                    var bottomInset = (baseShadowInsetDp + LABEL_BACKGROUND_SHADOW_DY_DP).pixels
                    when (labelPosition) {
                        LabelPosition.Top,
                        LabelPosition.AbovePoint -> topInset += label.getHeight(context) + tickSizeDp.pixels
                        LabelPosition.Bottom -> bottomInset += label.getHeight(context) + tickSizeDp.pixels
                        LabelPosition.AroundPoint -> {}
                    }
                    insets.ensureValuesAtLeast(top = topInset, bottom = bottomInset)
                }
            }
        }
    }
}

private const val LABEL_BACKGROUND_SHADOW_RADIUS_DP = 4f
private const val LABEL_BACKGROUND_SHADOW_DY_DP = 2f
private const val CLIPPING_FREE_SHADOW_RADIUS_MULTIPLIER = 1.4f