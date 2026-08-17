package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.components.GlassCard
import com.example.ui.viewmodel.MainViewModel

data class AiAppItem(
    val id: String,
    val name: String,
    val platform: String,
    val badge: String,
    val description: String,
    val iconUrl: String,
    val websiteUrl: String,
    val shortLabel: String
)

@Composable
fun AppsScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current

    val aiAppsList = listOf(
        AiAppItem(
            id = "app_1",
            name = "Bing Image Creator",
            platform = "Microsoft / DALL-E 3",
            badge = "FREE",
            description = "Microsoft Copilot image creator powered by OpenAI DALL-E 3. Perfect for rendering 3D name portraits and avatars.",
            iconUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=120&auto=format&fit=crop",
            websiteUrl = "https://www.bing.com/create",
            shortLabel = "Bing Image Creator"
        ),
        AiAppItem(
            id = "app_2",
            name = "Google Gemini AI",
            platform = "Google AI",
            badge = "FREE",
            description = "Google's premier multimodal AI model. Use Gemini for photo description, prompt refining, and creative concepts.",
            iconUrl = "https://images.unsplash.com/photo-1677442136019-21780ecad995?w=120&auto=format&fit=crop",
            websiteUrl = "https://gemini.google.com",
            shortLabel = "Google Gemini"
        ),
        AiAppItem(
            id = "app_3",
            name = "ChatGPT (OpenAI)",
            platform = "OpenAI",
            badge = "FREE / PLUS",
            description = "World-leading conversational AI. Generate creative prompts, storytelling narratives, and DALL-E visual guides.",
            iconUrl = "https://images.unsplash.com/photo-1684369175833-2895f8bc8789?w=120&auto=format&fit=crop",
            websiteUrl = "https://chatgpt.com",
            shortLabel = "ChatGPT"
        ),
        AiAppItem(
            id = "app_4",
            name = "Midjourney",
            platform = "Discord / Web",
            badge = "PAID",
            description = "Top-tier AI art generator specializing in hyperrealistic photorealism, cinematic lighting, and detailed textures.",
            iconUrl = "https://images.unsplash.com/photo-1620712943543-bcc4688e7485?w=120&auto=format&fit=crop",
            websiteUrl = "https://www.midjourney.com",
            shortLabel = "Midjourney"
        ),
        AiAppItem(
            id = "app_5",
            name = "Leonardo.Ai",
            platform = "Leonardo Interactive",
            badge = "FREE TIER",
            description = "High quality image generation platform with fine-tuned models for game assets, character portraits, and 3D art.",
            iconUrl = "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?w=120&auto=format&fit=crop",
            websiteUrl = "https://leonardo.ai",
            shortLabel = "Leonardo.Ai"
        ),
        AiAppItem(
            id = "app_6",
            name = "Remini AI Photo Enhancer",
            platform = "Bending Spoons",
            badge = "FREEMIUM",
            description = "Industry-standard AI photo enhancement tool. Turn blurry, low-resolution shots into crystal-clear 8K portraits.",
            iconUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=120&auto=format&fit=crop",
            websiteUrl = "https://remini.ai",
            shortLabel = "Remini AI"
        ),
        AiAppItem(
            id = "app_7",
            name = "Canva AI Magic Studio",
            platform = "Canva",
            badge = "FREE / PRO",
            description = "Graphic design suite with integrated AI Magic Media, background removal, and instant creative visual generation.",
            iconUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=120&auto=format&fit=crop",
            websiteUrl = "https://www.canva.com/magic-studio",
            shortLabel = "Canva Magic Studio"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Apps,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "AI CREATIVE APPS",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = "Official creative applications & generative AI platforms",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(aiAppsList, key = { it.id }) { app ->
                GlassCard(cornerRadius = 16.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(app.iconUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = app.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = app.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Text(
                                        text = app.badge,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = app.platform.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = app.description,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(app.websiteUrl))
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(36.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp)
                            ) {
                                Text(text = "Open App", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
