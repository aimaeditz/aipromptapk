package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorCmsDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val isAuthenticated by viewModel.isCreatorAuthenticated.collectAsState()
    val syncMessage by viewModel.syncMessage.collectAsState()

    var pinInput by remember { mutableStateOf("") }

    // Form inputs for adding prompt
    var promptCode by remember { mutableStateOf("#120") }
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Boy Prompts") }
    var platform by remember { mutableStateOf("Gemini") }
    var description by remember { mutableStateOf("") }
    var exactPrompt by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    val categories = listOf("Boy Prompts", "Girl Prompts", "Couple Prompts", "Islamic Prompts", "Eid Prompts", "Wedding Prompts", "Luxury Prompts", "AI Editing", "Cars")
    val platforms = listOf("Gemini", "Bing AI", "Midjourney", "DALL-E 3", "ChatGPT")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(text = "Creator Private CMS", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (!isAuthenticated) {
                    // PIN Authorization Screen
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Enter Creator Security PIN to unlock content management tools:",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = pinInput,
                            onValueChange = { pinInput = it },
                            label = { Text("Creator PIN Code") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                viewModel.authenticateCreator(pinInput)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "Unlock CMS System", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // AUTHENTICATED CMS DASHBOARD
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Welcome, M ABID", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            TextButton(onClick = { viewModel.logoutCreator() }) {
                                Text(text = "Lock CMS", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                            }
                        }

                        Divider()

                        // BLOGGER SYNC BUTTON
                        OutlinedButton(
                            onClick = { viewModel.syncBloggerContent() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Sync Blogger Content Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        syncMessage?.let { msg ->
                            Text(text = msg, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        }

                        Divider()

                        // ADD NEW PROMPT FORM
                        Text(text = "ADD NEW AI PROMPT ITEM", fontSize = 13.sp, fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = promptCode,
                            onValueChange = { promptCode = it },
                            label = { Text("Prompt Code (e.g. #120)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Short Description") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = exactPrompt,
                            onValueChange = { exactPrompt = it },
                            label = { Text("Exact AI Prompt Text") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = imageUrl,
                            onValueChange = { imageUrl = it },
                            label = { Text("Image URL") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (title.isNotBlank() && exactPrompt.isNotBlank()) {
                                    viewModel.addNewPrompt(
                                        promptCode = promptCode,
                                        title = title,
                                        category = category,
                                        platform = platform,
                                        description = description,
                                        exactPrompt = exactPrompt,
                                        imageUrl = imageUrl.ifBlank { "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800&auto=format&fit=crop" }
                                    )
                                    // Reset form
                                    title = ""
                                    description = ""
                                    exactPrompt = ""
                                    imageUrl = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Publish Prompt to App", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
