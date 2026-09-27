import re

with open('apps/android/app/src/main/kotlin/org/amanahquran/app/feature/trust/TrustCenterScreen.kt', 'r') as f:
    content = f.read()

# Add imports
imports = """import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.clickable
import org.amanahquran.app.core.ui.AmanahDivider
"""
if "import androidx.compose.runtime.setValue" not in content:
    content = content.replace("import androidx.compose.runtime.getValue", "import androidx.compose.runtime.getValue\n" + imports)

# Find translations block
search_regex = r'uiState\.translations\.forEach \{ translation ->\s*AmanahCard\(modifier = Modifier\.fillMaxWidth\(\)\) \{.*?\n\s*\}\s*\}'

replacement = """uiState.translations.forEach { translation ->
                                    var expanded by remember(translation.translationId) { mutableStateOf(false) }
                                    AmanahCard(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .semantics { role = Role.Button }
                                            .clickable(
                                                onClickLabel = if (expanded) "Hide technical verification details" else "Show technical verification details",
                                                onClick = { expanded = !expanded }
                                            )
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text(
                                                text = translation.displayName,
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.onSurface,
                                            )
                                            Text(
                                                text = "Language: ${translation.languageName} · Translator: ${translation.translatorName}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                            Text(
                                                text = "Permission: ${translation.permissionStatus} · Content: ${translation.contentStatus}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                            )
                                            
                                            if (expanded) {
                                                AmanahDivider(modifier = Modifier.padding(vertical = AmanahSpacing.sm))
                                                Text(
                                                    text = "Technical verification details",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(bottom = AmanahSpacing.xs)
                                                )
                                                Text(
                                                    text = "Translation ID: ${translation.translationId}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                                Text(
                                                    text = "Canonical status: ${translation.canonicalStatus}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                                Text(
                                                    text = "Available translations: ${translation.availableCount} / ${translation.totalCanonicalCount}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                                Text(
                                                    text = "SOURCE_MISSING: ${translation.sourceMissingCount}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                                Text(
                                                    text = "Footnotes: ${translation.footnoteCount}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                                Text(
                                                    text = "Content checksum: ${translation.contentChecksum.take(16)}… (v${translation.contentVersion})",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                            } else {
                                                Text(
                                                    text = "Tap to view technical details",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(top = AmanahSpacing.xs)
                                                )
                                            }
                                        }
                                    }
                                }"""

content = re.sub(search_regex, replacement, content, flags=re.DOTALL)

with open('apps/android/app/src/main/kotlin/org/amanahquran/app/feature/trust/TrustCenterScreen.kt', 'w') as f:
    f.write(content)

print("Done patching.")
