package com.android.greenmate.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.android.greenmate.R
import com.android.greenmate.presentation.ui.components.AddSpaceQuestion
import com.android.greenmate.presentation.ui.components.InputTextField
import com.android.greenmate.presentation.ui.components.SearchItem
import com.android.greenmate.presentation.viewmodel.PlantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantByNameScreen(
    initialText: String?,
    mainPlantNavController: NavHostController,
    plantViewModel: PlantViewModel = hiltViewModel()
) {
    val text = remember { mutableStateOf(initialText ?: "") }
    val description by plantViewModel.tDescription.observeAsState()
    val light by plantViewModel.tLight.observeAsState()
    val water by plantViewModel.tWater.observeAsState()
    val humidity by plantViewModel.tHumidity.observeAsState()
    val temperature by plantViewModel.tTemperature.observeAsState()
    val plantImage by plantViewModel.tPlantImage.observeAsState()
    val plantCategory by plantViewModel.tCategory.observeAsState()
    val plantTitle by plantViewModel.tTitle.observeAsState()
    val plantId by plantViewModel.tPlantId.observeAsState()

    LaunchedEffect(text.value) {
        if(text.value.isNotEmpty()) {
            plantViewModel.getPlantByTitle(text.value)
        }
    }

    // Background Image
    Box(
        modifier = Modifier.fillMaxHeight()
    ) {
        Image(
            painter = painterResource(id = R.drawable.find_name),
            contentDescription = "",
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.fillMaxHeight()
        )
    }

    Scaffold(
        containerColor = Color(0x98FFFFFF),
        topBar = {
            TopAppBar(
                title = {
                },
                colors = TopAppBarColors(Color.Transparent, Color.Transparent, Color.Black, Color.Transparent, Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = { mainPlantNavController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Stack"
                        )
                    }
                },
            )
        },
    ) {
        innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxHeight()
            ) {
                AddSpaceQuestion("주인님의 이름은..?", "")
                InputTextField(title = text, modifier = Modifier.padding(bottom = 10.dp), imageVector = Icons.Default.Search)
                if(text.value.isNotEmpty()) {
                    LazyColumn (
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .background(Color(0x45000000), RoundedCornerShape(10.dp))
                    ) {
                        plantTitle?.let { it ->
                            items(it.size) {
                                SearchItem(
                                    plantId!![it],
                                    plantCategory!![it],
                                    plantTitle!![it],
                                    plantImage!![it],
                                    description!![it],
                                    light!![it],
                                    water!![it],
                                    humidity!![it],
                                    temperature!![it],
                                    mainPlantNavController
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
