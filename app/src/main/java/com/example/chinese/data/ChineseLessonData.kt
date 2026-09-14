package com.example.chinese.data

data class ChinesePhrase(
    val id: String,
    val hanzi: String,       // Kichina (characters)
    val pinyin: String,      // Matamshi (pinyin with tone marks)
    val swahili: String,     // Tafsiri ya Kiswahili
    val english: String,     // English translation
    val categoryId: String,  // Kitengo
    val pronunciationTip: String = "" // Mwongozo wa kutamka kwa Kiswahili
)

data class ChineseCategory(
    val id: String,
    val titleSwahili: String,
    val titleEnglish: String,
    val iconEmoji: String,
    val colorHex: Long
)

object ChineseLessonData {
    val categories = listOf(
        ChineseCategory("greetings", "Salamu & Mazungumzo", "Greetings & Chat", "🤝", 0xFFE11D48),
        ChineseCategory("shopping", "Sokoni & Bei za Bidhaa", "Shopping & Bargaining", "🛍️", 0xFFD97706),
        ChineseCategory("numbers", "Namba, Pesa & Hesabu", "Numbers & Currency", "🔢", 0xFF059669),
        ChineseCategory("travel", "Usafiri, Teksi & Hoteli", "Travel, Taxi & Hotel", "🚖", 0xFF2563EB),
        ChineseCategory("food", "Chakula & Migahawa", "Food & Dining", "🍜", 0xFF7C3AED),
        ChineseCategory("emergency", "Dharura & Msaada", "Emergency & Help", "🚨", 0xFFDC2626)
    )

    val phrases = listOf(
        // SALAMU (GREETINGS)
        ChinesePhrase(
            id = "greet_1",
            hanzi = "你好",
            pinyin = "Nǐ hǎo",
            swahili = "Jambo / Habari yako",
            english = "Hello / Hi",
            categoryId = "greetings",
            pronunciationTip = "Tamka kama: Ni-hao"
        ),
        ChinesePhrase(
            id = "greet_2",
            hanzi = "早上好",
            pinyin = "Zǎoshang hǎo",
            swahili = "Habari ya asubuhi",
            english = "Good morning",
            categoryId = "greetings",
            pronunciationTip = "Tamka kama: Zao-shang hao"
        ),
        ChinesePhrase(
            id = "greet_3",
            hanzi = "谢谢",
            pinyin = "Xièxie",
            swahili = "Asante",
            english = "Thank you",
            categoryId = "greetings",
            pronunciationTip = "Tamka kama: Shie-shie"
        ),
        ChinesePhrase(
            id = "greet_4",
            hanzi = "不客气",
            pinyin = "Bù kèqì",
            swahili = "Karibu sana / Usijali",
            english = "You are welcome",
            categoryId = "greetings",
            pronunciationTip = "Tamka kama: Bu ke-chi"
        ),
        ChinesePhrase(
            id = "greet_5",
            hanzi = "对不起",
            pinyin = "Duìbuqǐ",
            swahili = "Samahani / Nisamehe",
            english = "Sorry / Excuse me",
            categoryId = "greetings",
            pronunciationTip = "Tamka kama: Dui-bu-chi"
        ),
        ChinesePhrase(
            id = "greet_6",
            hanzi = "再见",
            pinyin = "Zàijiàn",
            swahili = "Kwaheri / Tuonane tena",
            english = "Goodbye",
            categoryId = "greetings",
            pronunciationTip = "Tamka kama: Zai-jyen"
        ),
        ChinesePhrase(
            id = "greet_7",
            hanzi = "你叫什么名字？",
            pinyin = "Nǐ jiào shénme míngzi?",
            swahili = "Unaitwa nani?",
            english = "What is your name?",
            categoryId = "greetings",
            pronunciationTip = "Tamka kama: Ni jyao shen-me ming-zi?"
        ),
        ChinesePhrase(
            id = "greet_8",
            hanzi = "我叫...",
            pinyin = "Wǒ jiào...",
            swahili = "Ninaitwa...",
            english = "My name is...",
            categoryId = "greetings",
            pronunciationTip = "Tamka kama: Wo jyao..."
        ),
        ChinesePhrase(
            id = "greet_9",
            hanzi = "我来自坦桑尼亚",
            pinyin = "Wǒ láizì Tǎnsāngníyà",
            swahili = "Ninatoka Tanzania 🇹🇿",
            english = "I come from Tanzania",
            categoryId = "greetings",
            pronunciationTip = "Tamka kama: Wo lai-zi Tan-sang-ni-ya"
        ),

        // SOKONI & BEI (SHOPPING & BARGAINING)
        ChinesePhrase(
            id = "shop_1",
            hanzi = "这个多少钱？",
            pinyin = "Zhège duōshao qián?",
            swahili = "Hii ni bei gani?",
            english = "How much is this?",
            categoryId = "shopping",
            pronunciationTip = "Tamka kama: Je-ge dwo-shao chyen?"
        ),
        ChinesePhrase(
            id = "shop_2",
            hanzi = "太贵了！",
            pinyin = "Tài guì le!",
            swahili = "Ghali mno!",
            english = "Too expensive!",
            categoryId = "shopping",
            pronunciationTip = "Tamka kama: Tai gwi le!"
        ),
        ChinesePhrase(
            id = "shop_3",
            hanzi = "便宜一点吧",
            pinyin = "Piányi yīdiǎn ba",
            swahili = "Punguza kidogo basi",
            english = "A bit cheaper please",
            categoryId = "shopping",
            pronunciationTip = "Tamka kama: Pyen-yi yi-dyen ba"
        ),
        ChinesePhrase(
            id = "shop_4",
            hanzi = "我要这个",
            pinyin = "Wǒ yào zhège",
            swahili = "Ninataka hiki",
            english = "I want this one",
            categoryId = "shopping",
            pronunciationTip = "Tamka kama: Wo yao je-ge"
        ),
        ChinesePhrase(
            id = "shop_5",
            hanzi = "可以微信支付吗？",
            pinyin = "Kěyǐ Wēixìn zhīfù ma?",
            swahili = "Naweza kulipa kwa WeChat Pay?",
            english = "Can I pay with WeChat Pay?",
            categoryId = "shopping",
            pronunciationTip = "Tamka kama: Ke-yi Wey-shin ji-fu ma?"
        ),
        ChinesePhrase(
            id = "shop_6",
            hanzi = "有没有别的颜色？",
            pinyin = "Yǒu méiyǒu bié de yánsè?",
            swahili = "Kuna rangi nyingine?",
            english = "Do you have another color?",
            categoryId = "shopping",
            pronunciationTip = "Tamka kama: You mey-you bye de yan-se?"
        ),

        // NAMBA NA PESA (NUMBERS & CURRENCY)
        ChinesePhrase(
            id = "num_1",
            hanzi = "一 (1), 二 (2), 三 (3)",
            pinyin = "Yī, Èr, Sān",
            swahili = "Moja, Mbili, Tatu",
            english = "One, Two, Three",
            categoryId = "numbers",
            pronunciationTip = "Yi, Er, San"
        ),
        ChinesePhrase(
            id = "num_2",
            hanzi = "四 (4), 五 (5), 六 (6)",
            pinyin = "Sì, Wǔ, Liù",
            swahili = "Nne, Tano, Sita",
            english = "Four, Five, Six",
            categoryId = "numbers",
            pronunciationTip = "Si, Wu, Lyo"
        ),
        ChinesePhrase(
            id = "num_3",
            hanzi = "七 (7), 八 (8), 九 (9), 十 (10)",
            pinyin = "Qī, Bā, Jiǔ, Shí",
            swahili = "Saba, Nane, Tisa, Kumi",
            english = "Seven, Eight, Nine, Ten",
            categoryId = "numbers",
            pronunciationTip = "Chi, Ba, Jyo, Shi"
        ),
        ChinesePhrase(
            id = "num_4",
            hanzi = "一百",
            pinyin = "Yībǎi",
            swahili = "Mia moja (100)",
            english = "One hundred (100)",
            categoryId = "numbers",
            pronunciationTip = "Yi-bai"
        ),
        ChinesePhrase(
            id = "num_5",
            hanzi = "一千",
            pinyin = "Yīqiān",
            swahili = "Elfu moja (1,000)",
            english = "One thousand (1,000)",
            categoryId = "numbers",
            pronunciationTip = "Yi-chyen"
        ),
        ChinesePhrase(
            id = "num_6",
            hanzi = "十块钱",
            pinyin = "Shí kuài qián",
            swahili = "Yuan kumi (Pesa ya Kichina RMB)",
            english = "10 Yuan (RMB)",
            categoryId = "numbers",
            pronunciationTip = "Shi kuai chyen"
        ),

        // USAFIRI & HOTELI (TRAVEL & HOTEL)
        ChinesePhrase(
            id = "trav_1",
            hanzi = "我要去机场",
            pinyin = "Wǒ yào qù jīchǎng",
            swahili = "Ninataka kwenda uwanja wa ndege",
            english = "I want to go to the airport",
            categoryId = "travel",
            pronunciationTip = "Wo yao chi ji-chang"
        ),
        ChinesePhrase(
            id = "trav_2",
            hanzi = "请打表",
            pinyin = "Qǐng dǎbiǎo",
            swahili = "Washa mita ya teksi tafadhali",
            english = "Please use the meter",
            categoryId = "travel",
            pronunciationTip = "Ching da-byao"
        ),
        ChinesePhrase(
            id = "trav_3",
            hanzi = "洗手间在哪里？",
            pinyin = "Xǐshǒujiān zài nǎlǐ?",
            swahili = "Choo kipo wapi?",
            english = "Where is the restroom / toilet?",
            categoryId = "travel",
            pronunciationTip = "Shi-shou-jyen zai na-li?"
        ),
        ChinesePhrase(
            id = "trav_4",
            hanzi = "我有预订",
            pinyin = "Wǒ yǒu yùdìng",
            swahili = "Nina nafasi niliyoweka (Booking)",
            english = "I have a reservation",
            categoryId = "travel",
            pronunciationTip = "Wo you yu-ding"
        ),

        // CHAKULA & MIGAHAWA (FOOD & DINING)
        ChinesePhrase(
            id = "food_1",
            hanzi = "我不吃猪肉",
            pinyin = "Wǒ bù chī zhūròu",
            swahili = "Sili nyama ya nguruwe (Halal)",
            english = "I do not eat pork",
            categoryId = "food",
            pronunciationTip = "Wo bu chi ju-rou"
        ),
        ChinesePhrase(
            id = "food_2",
            hanzi = "请给我水",
            pinyin = "Qǐng gěi wǒ shuǐ",
            swahili = "Tafadhali nipe maji",
            english = "Please give me water",
            categoryId = "food",
            pronunciationTip = "Ching gey wo shwey"
        ),
        ChinesePhrase(
            id = "food_3",
            hanzi = "买单",
            pinyin = "Mǎidān",
            swahili = "Lete bili / Nataka kulipa",
            english = "The bill please / Check",
            categoryId = "food",
            pronunciationTip = "Mai-dan"
        ),
        ChinesePhrase(
            id = "food_4",
            hanzi = "很好吃！",
            pinyin = "Hěn hǎochī!",
            swahili = "Kitamu sana!",
            english = "Very delicious!",
            categoryId = "food",
            pronunciationTip = "Hen hao-chi!"
        ),

        // DHARURA & MSAADA (EMERGENCY)
        ChinesePhrase(
            id = "em_1",
            hanzi = "救命！",
            pinyin = "Jiùmìng!",
            swahili = "Nisaidieni! / Msaada!",
            english = "Help!",
            categoryId = "emergency",
            pronunciationTip = "Jyo-ming!"
        ),
        ChinesePhrase(
            id = "em_2",
            hanzi = "我不舒服",
            pinyin = "Wǒ bù shūfu",
            swahili = "Sijisikii vizuri / Ninaumwa",
            english = "I don't feel well / I'm sick",
            categoryId = "emergency",
            pronunciationTip = "Wo bu shu-fu"
        ),
        ChinesePhrase(
            id = "em_3",
            hanzi = "请叫医生",
            pinyin = "Qǐng jiào yīshēng",
            swahili = "Mwitie daktari tafadhali",
            english = "Please call a doctor",
            categoryId = "emergency",
            pronunciationTip = "Ching jyao yi-sheng"
        )
    )
}
