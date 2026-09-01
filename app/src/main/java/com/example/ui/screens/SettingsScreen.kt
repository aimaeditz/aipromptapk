package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.GlassCard
import com.example.ui.components.saasBackgroundGlow
import com.example.ui.theme.AppThemeMode
import com.example.ui.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val currentThemeMode by viewModel.themeMode.collectAsState()
    val adConfig by viewModel.adMobConfigRepository.config.collectAsState()

    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .saasBackgroundGlow()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Text(
                text = "Settings & Preferences",
                fontSize = 17.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Text(
            text = "App configuration & Play Store policy information",
            fontSize = 11.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // THEME SELECTION
        Text(text = "APPEARANCE & THEME", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))

        GlassCard(cornerRadius = 14.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Select App Visual Theme", fontSize = 13.5.sp, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = currentThemeMode == AppThemeMode.SYSTEM,
                        onClick = { viewModel.setThemeMode(AppThemeMode.SYSTEM) },
                        label = { Text("System", fontSize = 11.5.sp) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = currentThemeMode == AppThemeMode.DARK,
                        onClick = { viewModel.setThemeMode(AppThemeMode.DARK) },
                        label = { Text("Dark", fontSize = 11.5.sp) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = currentThemeMode == AppThemeMode.LIGHT,
                        onClick = { viewModel.setThemeMode(AppThemeMode.LIGHT) },
                        label = { Text("Light SaaS", fontSize = 11.5.sp) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ADMOB MONETIZATION
        Text(text = "MONETIZATION & ADMOB", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))

        GlassCard(cornerRadius = 14.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Google AdMob Integration", fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (adConfig.isTestMode) "Mode: Test Ad Units Active" else "Mode: Production Active",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = adConfig.isAdsEnabled,
                        onCheckedChange = { viewModel.adMobConfigRepository.toggleAdsEnabled(it) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // LEGAL & POLICIES
        Text(text = "LEGAL & POLICIES", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))

        GlassCard(cornerRadius = 14.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showPrivacyDialog = true }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(imageVector = Icons.Outlined.Security, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Text(text = "Privacy Policy", fontSize = 13.5.sp, fontWeight = FontWeight.Medium)
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTermsDialog = true }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(imageVector = Icons.Outlined.Gavel, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Text(text = "Terms of Service", fontSize = 13.5.sp, fontWeight = FontWeight.Medium)
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // APP INFO & CACHE
        GlassCard(cornerRadius = 14.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Application Version", fontSize = 13.5.sp, fontWeight = FontWeight.Medium)
                    Text(text = "v1.0.0 (Production)", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                }

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Local cache refreshed.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Clear Local Image Cache", fontSize = 12.sp)
                }
            }
        }
    }

    // PRIVACY POLICY DIALOG
    if (showPrivacyDialog) {
        Dialog(onDismissRequest = { showPrivacyDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "Privacy Policy - AiPromptXpert", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "AiPromptXpert is a free AI creative platform by M ABID (Powered by AiMAEditz). We respect your privacy. This application does not collect personal identity information. Local favorites are stored securely on your device using Room Database.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { showPrivacyDialog = false }, modifier = Modifier.align(Alignment.End)) {
                        Text("Close")
                    }
                }
            }
        }
    }

    // TERMS OF SERVICE DIALOG
    if (showTermsDialog) {
        Dialog(onDismissRequest = { showTermsDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "Terms of Service - AiPromptXpert", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "All AI prompts, photo editing templates, and resources provided in AiPromptXpert are free for personal and creative design use. Content is curated from AiMAEditz and AiPromptXpert Blogger platforms.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { showTermsDialog = false }, modifier = Modifier.align(Alignment.End)) {
                        Text("Close")
                    }
                }
            }
        }
    }
}
