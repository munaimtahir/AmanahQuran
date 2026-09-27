import re

with open('apps/android/app/src/main/kotlin/org/amanahquran/app/feature/trust/TrustCenterScreen.kt', 'r') as f:
    content = f.read()

if "import androidx.compose.runtime.setValue" not in content:
    content = content.replace("import androidx.compose.runtime.getValue", "import androidx.compose.runtime.getValue\nimport androidx.compose.runtime.setValue\nimport androidx.compose.runtime.mutableStateOf\nimport androidx.compose.runtime.remember\nimport androidx.compose.ui.semantics.Role\nimport androidx.compose.ui.semantics.role\nimport androidx.compose.ui.semantics.semantics\nimport androidx.compose.foundation.clickable\nimport org.amanahquran.app.core.theme.AmanahDivider\n")

translations_regex = re.compile(
    r'(if \(uiState\.translations\.isNotEmpty\(\)\) \{\s*item \{\s*TrustSectionHeader\(title = "Translations", icon = Icons\.Rounded\.VerifiedUser\)\s*Column\(verticalArrangement = Arrangement\.spacedBy\(AmanahSpacing\.sm\)\) \{\s*uiState\.translations\.forEach \{ translation ->\s*)AmanahCard\(modifier = Modifier\.fillMaxWidth\(\)\) \{.*?\}'
    r'(\s*\}\s*\}\s*\}\s*\})',
    re.DOTALL
)

new_translation_card = """var expanded by remember { mutableStateOf(false) }
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
                                    }"""

content = translations_regex.sub(r'\1' + new_translation_card + r'\2', content)

with open('apps/android/app/src/main/kotlin/org/amanahquran/app/feature/trust/TrustCenterScreen.kt', 'w') as f:
    f.write(content)

print("Done patching.")
