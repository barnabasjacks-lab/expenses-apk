package com.example.chinese.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chinese.data.ChineseLessonData
import com.example.chinese.data.ChinesePhrase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChineseAppScreen(
    viewModel: ChineseViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFE11D48).copy(alpha = 0.15f),
                            modifier = Modifier.padding(end = 10.dp)
                        ) {
                            Text(
                                text = "🇨🇳 🇹🇿",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Jifunze Kichina",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF991B1B)
                            )
                            Text(
                                text = "Kiswahili • English • Sauti 🔊",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${uiState.quizScore} Pts",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = uiState.currentTab == AppTab.LESSONS,
                    onClick = { viewModel.setTab(AppTab.LESSONS) },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "Masomo") },
                    label = { Text("Masomo") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE11D48),
                        indicatorColor = Color(0xFFE11D48).copy(alpha = 0.12f)
                    )
                )
                NavigationBarItem(
                    selected = uiState.currentTab == AppTab.FLASHCARDS,
                    onClick = { viewModel.setTab(AppTab.FLASHCARDS) },
                    icon = { Icon(Icons.Default.CreditCard, contentDescription = "Kadi") },
                    label = { Text("Kadi (Cards)") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE11D48),
                        indicatorColor = Color(0xFFE11D48).copy(alpha = 0.12f)
                    )
                )
                NavigationBarItem(
                    selected = uiState.currentTab == AppTab.QUIZ,
                    onClick = { viewModel.setTab(AppTab.QUIZ) },
                    icon = { Icon(Icons.Default.Quiz, contentDescription = "Mtihani") },
                    label = { Text("Mchezo (Quiz)") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE11D48),
                        indicatorColor = Color(0xFFE11D48).copy(alpha = 0.12f)
                    )
                )
                NavigationBarItem(
                    selected = uiState.currentTab == AppTab.ABOUT,
                    onClick = { viewModel.setTab(AppTab.ABOUT) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Kuhusu") },
                    label = { Text("Kuhusu App") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE11D48),
                        indicatorColor = Color(0xFFE11D48).copy(alpha = 0.12f)
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState.currentTab) {
                AppTab.LESSONS -> LessonsView(uiState = uiState, viewModel = viewModel)
                AppTab.FLASHCARDS -> FlashcardsView(uiState = uiState, viewModel = viewModel)
                AppTab.QUIZ -> QuizView(uiState = uiState, viewModel = viewModel)
                AppTab.ABOUT -> AboutDeveloperView()
            }
        }
    }
}

@Composable
fun LessonsView(
    uiState: ChineseUiState,
    viewModel: ChineseViewModel
) {
    val allPhrases = ChineseLessonData.phrases.filter {
        it.categoryId == uiState.selectedCategory.id
    }

    val filteredPhrases = if (uiState.searchQuery.isBlank()) {
        allPhrases
    } else {
        ChineseLessonData.phrases.filter {
            it.hanzi.contains(uiState.searchQuery, ignoreCase = true) ||
                    it.pinyin.contains(uiState.searchQuery, ignoreCase = true) ||
                    it.swahili.contains(uiState.searchQuery, ignoreCase = true) ||
                    it.english.contains(uiState.searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Banner with Developer Brand
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF991B1B), Color(0xFFDC2626), Color(0xFFB91C1C))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🇨🇳 JIFUNZE KICHINA KWA SAUTI 🔊",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFDE047)
                            )
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Barnabas Msuku",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Bofya kitufe cha Spika 🔊 kusikia jinsi neno linavyotamkwa kwa Kichina fasaha!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.95f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Search Box
        item {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Tafuta neno kwa Kiswahili, Kichina au English...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Futa")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE11D48),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
        }

        // Categories Chips
        item {
            Column {
                Text(
                    text = "Chagua Kitengo cha Kujifunza:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ChineseLessonData.categories) { cat ->
                        val isSelected = cat.id == uiState.selectedCategory.id
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.selectCategory(cat)
                                viewModel.setSearchQuery("")
                            },
                            label = { Text("${cat.iconEmoji} ${cat.titleSwahili}") },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFE11D48),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (uiState.searchQuery.isBlank()) uiState.selectedCategory.titleSwahili else "Matokeo ya Utafutaji",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${filteredPhrases.size} misemo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Phrases list
        items(filteredPhrases) { phrase ->
            PhraseItemCard(
                phrase = phrase,
                isFavorite = uiState.favoritePhraseIds.contains(phrase.id),
                onPlayAudio = { viewModel.playChineseAudio(phrase.hanzi) },
                onToggleFavorite = { viewModel.toggleFavorite(phrase.id) }
            )
        }
    }
}

@Composable
fun PhraseItemCard(
    phrase: ChinesePhrase,
    isFavorite: Boolean,
    onPlayAudio: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Speaker Button
            Surface(
                shape = CircleShape,
                color = Color(0xFFE11D48),
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onPlayAudio() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Sikiliza Sauti ya Kichina",
                    tint = Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Phrase content
            Column(modifier = Modifier.weight(1f)) {
                // Hanzi (Big Chinese character)
                Text(
                    text = phrase.hanzi,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF991B1B)
                )

                // Pinyin with tones
                Text(
                    text = phrase.pinyin,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD97706)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Kiswahili translation
                Text(
                    text = "🇹🇿 ${phrase.swahili}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // English translation
                Text(
                    text = "🇬🇧 ${phrase.english}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (phrase.pronunciationTip.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "💡 ${phrase.pronunciationTip}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF059669)
                    )
                }
            }

            // Favorite button
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorite) Color(0xFFE11D48) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FlashcardsView(
    uiState: ChineseUiState,
    viewModel: ChineseViewModel
) {
    val phrases = ChineseLessonData.phrases.filter { it.categoryId == uiState.selectedCategory.id }
    if (phrases.isEmpty()) return

    val currentPhrase = phrases.getOrElse(uiState.flashcardIndex) { phrases.first() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Kadi za Kujikumbusha (Flashcards)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Gusa kadi kuona maana yake kwa Kiswahili na English!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Big Flashcard
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clickable { viewModel.flipFlashcard() },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.isFlashcardFlipped) Color(0xFFFEF3C7) else Color(0xFFFFF1F2)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!uiState.isFlashcardFlipped) {
                    // Front: Chinese Characters & Pinyin
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "KICHINA (MANDARIN)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF991B1B)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = currentPhrase.hanzi,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF991B1B),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentPhrase.pinyin,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.playChineseAudio(currentPhrase.hanzi) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sikiliza Sauti 🔊")
                        }
                    }
                } else {
                    // Back: Kiswahili & English Translation
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TAFSIRI NA MAANA",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "🇹🇿 ${currentPhrase.swahili}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "🇬🇧 ${currentPhrase.english}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF475569),
                            textAlign = TextAlign.Center
                        )
                        if (currentPhrase.pronunciationTip.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "💡 ${currentPhrase.pronunciationTip}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF059669),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Kadi ya ${uiState.flashcardIndex + 1} kati ya ${phrases.size}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Navigation controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                onClick = { viewModel.prevFlashcard(phrases.size) },
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Iliyopita")
            }

            Button(
                onClick = { viewModel.nextFlashcard(phrases.size) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Inayofuata")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
fun QuizView(
    uiState: ChineseUiState,
    viewModel: ChineseViewModel
) {
    val quizPhrases = ChineseLessonData.phrases
    val currentPhrase = quizPhrases.getOrElse(uiState.quizCurrentQuestionIndex) { quizPhrases.first() }

    // Prepare 3 wrong options + 1 correct option
    val wrongOptions = remember(uiState.quizCurrentQuestionIndex) {
        quizPhrases.filter { it.id != currentPhrase.id }
            .shuffled()
            .take(3)
            .map { it.swahili }
    }

    val options = remember(uiState.quizCurrentQuestionIndex) {
        (wrongOptions + currentPhrase.swahili).shuffled()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Swali la ${uiState.quizCurrentQuestionIndex + 1} / ${quizPhrases.size}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF059669).copy(alpha = 0.15f)
            ) {
                Text(
                    text = "Alama: ${uiState.quizScore}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF059669),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Question Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Neno hili lina maana gani kwa Kiswahili?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF991B1B),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = currentPhrase.hanzi,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF991B1B)
                )
                Text(
                    text = currentPhrase.pinyin,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD97706)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.playChineseAudio(currentPhrase.hanzi) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sikiliza Sauti 🔊")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Multiple choice options
        for (option in options) {
            val isSelected = uiState.quizSelectedOption == option
            val isCorrect = option == currentPhrase.swahili

            val bgColor = when {
                !uiState.isQuizAnswerSubmitted && isSelected -> Color(0xFFE11D48).copy(alpha = 0.15f)
                uiState.isQuizAnswerSubmitted && isCorrect -> Color(0xFF22C55E).copy(alpha = 0.2f)
                uiState.isQuizAnswerSubmitted && isSelected && !isCorrect -> Color(0xFFEF4444).copy(alpha = 0.2f)
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            }

            val borderColor = when {
                !uiState.isQuizAnswerSubmitted && isSelected -> Color(0xFFE11D48)
                uiState.isQuizAnswerSubmitted && isCorrect -> Color(0xFF22C55E)
                uiState.isQuizAnswerSubmitted && isSelected && !isCorrect -> Color(0xFFEF4444)
                else -> Color.Transparent
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        if (!uiState.isQuizAnswerSubmitted) {
                            viewModel.selectQuizOption(option)
                        }
                    },
                color = bgColor,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor)
            ) {
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (!uiState.isQuizAnswerSubmitted) {
            Button(
                onClick = { viewModel.submitQuizAnswer(currentPhrase.swahili) },
                enabled = uiState.quizSelectedOption != null,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Hakiki Jibu Langu", fontWeight = FontWeight.Bold)
            }
        } else {
            Button(
                onClick = { viewModel.nextQuizQuestion(quizPhrases.size) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Swali Linalofuata ➡️", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AboutDeveloperView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFE11D48).copy(alpha = 0.15f),
            modifier = Modifier.size(90.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("🇨🇳", fontSize = 42.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Jifunze Kichina Pro",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF991B1B)
        )

        Text(
            text = "Mandarin Chinese in Swahili & English with Audio",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Developer Credit Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "IMETENGENEZWA NA",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Barnabas Msuku",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFB45309)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Lead Android Developer & Creator 🇹🇿",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF78350F)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Kuhusu App Hii:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• Inafundisha misemo ya Kichina ya kila siku na ya biashara.\n• Inayo sauti halisi ya Kichina (Mandarin Speech Engine).\n• Inafanya kazi bila bando (100% Offline).\n• Inajumuisha tafsiri ya Kiswahili fasaha na Kiingereza.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
