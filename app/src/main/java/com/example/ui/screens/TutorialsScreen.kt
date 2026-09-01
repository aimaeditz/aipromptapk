package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.GlassCard
import com.example.ui.components.saasBackgroundGlow
import com.example.ui.viewmodel.MainViewModel
import org.json.JSONArray

@Composable
fun TutorialsScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val tutorials by viewModel.tutorials.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .saasBackgroundGlow()
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                }
            }
            Text(text = "AI Tutorials & Guides", fontSize = 17.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        }
        Text(
            text = "Step-by-step tutorials by AiMAEditz for 3D avatars & photo editing",
            fontSize = 11.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(tutorials, key = { it.id }) { tut ->
                var expanded by remember { mutableStateOf(true) }

                GlassCard(cornerRadius = 14.dp) {
                    Column {
                        AsyncImage(
                            model = tut.coverImageUrl,
                            contentDescription = tut.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = tut.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(text = tut.introduction, fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 17.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = !expanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "STEP-BY-STEP INSTRUCTIONS", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                            Icon(imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        }

                        AnimatedVisibility(visible = expanded) {
                            Column(
                                modifier = Modifier.padding(top = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val steps = parseSteps(tut.stepsJson)
                                steps.forEachIndexed { index, step ->
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                                            modifier = Modifier.size(22.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(text = "${index + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                                            }
                                        }

                                        Text(text = step, fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f), lineHeight = 17.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun parseSteps(json: String): List<String> {
    return try {
        val array = JSONArray(json)
        val list = mutableListOf<String>()
        for (i in 0 until array.length()) {
            list.add(array.getString(i))
        }
        list
    } catch (e: Exception) {
        listOf("Follow prompt instructions in the AiPromptXpert prompt library.")
    }
}
