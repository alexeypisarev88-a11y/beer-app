package com.example

import androidx.compose.runtime.staticCompositionLocalOf

data class AppStrings(
    // App & Navigation
    val appName: String,
    val calendar: String,
    val achievements: String,
    val settings: String,
    val backToReminder: String,

    // Permission dialog
    val enableNotificationsTitle: String,
    val enableNotificationsText: String,
    val openSettings: String,
    val later: String,

    // Settings Screen
    val settingsTitle: String,
    val sectionLanguage: String,
    val sectionNotifications: String,
    val dailyReminders: String,
    val remindersActiveAt: (String) -> String,
    val remindersDisabled: String,
    val reminderTime: String,
    val dailyAlertAt: (String) -> String,
    val sectionAppearance: String,
    val darkTheme: String,
    val darkThemeEnabled: String,
    val lightThemeEnabled: String,
    val sectionGlassShape: String,
    val sectionDevOptions: String,
    val testNotification: String,
    val testNotificationSubtitle: String,
    val sendButton: String,

    // Glass shapes
    val pintName: String,
    val pintDesc: String,
    val mugName: String,
    val mugDesc: String,
    val weizenName: String,
    val weizenDesc: String,
    val tulipName: String,
    val tulipDesc: String,
    val bootName: String,
    val bootDesc: String,

    // Calendar
    val calendarTitle: String,
    val weekdayHeaders: List<String>,

    // Achievements Screen
    val achievementsTitle: String,
    val achConnoisseurTitle: String,
    val achConnoisseurDesc: String,
    val achVeteranTitle: String,
    val achVeteranDesc: String,
    val achLegendTitle: String,
    val achLegendDesc: String,
    val achWeekendTitle: String,
    val achWeekendDesc: String,
    val achFlawlessTitle: String,
    val achFlawlessDesc: String,
    val achOktoberfestTitle: String,
    val achOktoberfestDesc: String,

    // Notification messages
    val funNotificationMessages: List<ReminderScheduler.NotificationContent>,
    val fridayNotificationMessage: ReminderScheduler.NotificationContent
) {
    companion object {
        fun forLanguage(code: String): AppStrings = when (code) {
            "ru" -> russian
            "zh" -> chinese
            else -> english
        }

        val english = AppStrings(
            appName = "Beer Reminder",
            calendar = "Calendar",
            achievements = "Achievements",
            settings = "Settings",
            backToReminder = "Back to Reminder",

            enableNotificationsTitle = "Enable Notifications",
            enableNotificationsText = "Beer Reminder needs notification permissions to remind you when it's beer time. Please enable notifications so you never miss a toast!",
            openSettings = "Open Settings",
            later = "Later",

            settingsTitle = "Settings",
            sectionLanguage = "LANGUAGE",
            sectionNotifications = "NOTIFICATIONS",
            dailyReminders = "Daily Reminders",
            remindersActiveAt = { time -> "Active at $time" },
            remindersDisabled = "Reminders are disabled",
            reminderTime = "Reminder Time",
            dailyAlertAt = { time -> "Daily alert at $time" },
            sectionAppearance = "APPEARANCE",
            darkTheme = "Dark Theme",
            darkThemeEnabled = "Dark theme enabled",
            lightThemeEnabled = "Light theme enabled",
            sectionGlassShape = "GLASS SHAPE",
            sectionDevOptions = "DEVELOPER OPTIONS",
            testNotification = "Test Notification",
            testNotificationSubtitle = "Trigger push alert immediately",
            sendButton = "Send",

            pintName = "Pint",
            pintDesc = "Classic conical pint glass",
            mugName = "Beer Mug",
            mugDesc = "Sturdy mug with classic handle",
            weizenName = "Weizen",
            weizenDesc = "Curved German wheat glass",
            tulipName = "Tulip",
            tulipDesc = "Stemmed curvy aromatic glass",
            bootName = "Beer Boot",
            bootDesc = "Traditional Bavarian bierstiefel",

            calendarTitle = "Calendar",
            weekdayHeaders = listOf("S", "M", "T", "W", "T", "F", "S"),

            achievementsTitle = "Achievements",
            achConnoisseurTitle = "Beer Connoisseur",
            achConnoisseurDesc = "Drunk beer for 10 distinct days",
            achVeteranTitle = "Beer Veteran",
            achVeteranDesc = "Drunk beer for 100 distinct days",
            achLegendTitle = "Beer Legend",
            achLegendDesc = "Drunk beer for 1,000 distinct days",
            achWeekendTitle = "Weekend Warrior",
            achWeekendDesc = "Drunk beer on > 5 days in a week",
            achFlawlessTitle = "Flawless Week",
            achFlawlessDesc = "All 7 days in a single calendar week",
            achOktoberfestTitle = "Oktoberfest Spirit",
            achOktoberfestDesc = "20+ days with beer in a single calendar month",

            funNotificationMessages = listOf(
                ReminderScheduler.NotificationContent("Beer time! 🍺", "Time to cool down and take a refreshing break!"),
                ReminderScheduler.NotificationContent("Cheers to the day! 🍻", "The perfect moment for a crisp, cold brew!"),
                ReminderScheduler.NotificationContent("The glass won't drink itself! 🍺", "Time to take a break and enjoy a cold one."),
                ReminderScheduler.NotificationContent("Time to chill! ❄️🍺", "A refreshing golden glass is waiting for you."),
                ReminderScheduler.NotificationContent("The hop call! 🍻", "A great evening starts with good cold beer."),
                ReminderScheduler.NotificationContent("A moment for yourself 🍺", "Workday is done — time to pour a cold pint!"),
                ReminderScheduler.NotificationContent("Golden foam awaits 🍺", "Take a fresh sip and unwind after a long day."),
                ReminderScheduler.NotificationContent("Froth patrol 🍻", "Reminder: your day is complete with a cold beer!")
            ),
            fridayNotificationMessage = ReminderScheduler.NotificationContent(
                "Friday is here! 🍻",
                "End of the work week — time to crack open a cold one!"
            )
        )

        val russian = AppStrings(
            appName = "Beer Reminder",
            calendar = "Календарь",
            achievements = "Достижения",
            settings = "Настройки",
            backToReminder = "Назад",

            enableNotificationsTitle = "Включите уведомления",
            enableNotificationsText = "Приложению Beer Reminder требуется разрешение на уведомления, чтобы напоминать о времени для пива. Пожалуйста, включите уведомления!",
            openSettings = "Открыть настройки",
            later = "Позже",

            settingsTitle = "Настройки",
            sectionLanguage = "ЯЗЫК",
            sectionNotifications = "УВЕДОМЛЕНИЯ",
            dailyReminders = "Ежедневные напоминания",
            remindersActiveAt = { time -> "Включено в $time" },
            remindersDisabled = "Напоминания отключены",
            reminderTime = "Время напоминания",
            dailyAlertAt = { time -> "Ежедневно в $time" },
            sectionAppearance = "ОФОРМЛЕНИЕ",
            darkTheme = "Темная тема",
            darkThemeEnabled = "Темная тема включена",
            lightThemeEnabled = "Светлая тема включена",
            sectionGlassShape = "ФОРМА БОКАЛА",
            sectionDevOptions = "ДЛЯ РАЗРАБОТЧИКОВ",
            testNotification = "Тестовое уведомление",
            testNotificationSubtitle = "Отправить пуш-уведомление немедленно",
            sendButton = "Отправить",

            pintName = "Пинта",
            pintDesc = "Классический конический пивной бокал",
            mugName = "Кружка",
            mugDesc = "Массивная кружка с классической ручкой",
            weizenName = "Вайцен",
            weizenDesc = "Изогнутый бокал для пшеничного пива",
            tulipName = "Тюльпан",
            tulipDesc = "Бокал на ножке для ароматного крафта",
            bootName = "Сапог",
            bootDesc = "Традиционный баварский бокал-сапог",

            calendarTitle = "Календарь",
            weekdayHeaders = listOf("Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"),

            achievementsTitle = "Достижения",
            achConnoisseurTitle = "Знаток пива",
            achConnoisseurDesc = "Пиво пили 10 разных дней",
            achVeteranTitle = "Пивной ветеран",
            achVeteranDesc = "Пиво пили 100 разных дней",
            achLegendTitle = "Пивная легенда",
            achLegendDesc = "Пиво пили 1 000 разных дней",
            achWeekendTitle = "Воин выходных",
            achWeekendDesc = "Пиво пили более 5 дней за неделю",
            achFlawlessTitle = "Идеальная неделя",
            achFlawlessDesc = "Все 7 дней в рамках одной недели",
            achOktoberfestTitle = "Дух Октоберфеста",
            achOktoberfestDesc = "20+ дней с пивом за один месяц",

            funNotificationMessages = listOf(
                ReminderScheduler.NotificationContent("Время пива! 🍺", "Пора охладиться и сделать освежающий перерыв!"),
                ReminderScheduler.NotificationContent("За отличный день! 🍻", "Идеальный момент для прохладного пива!"),
                ReminderScheduler.NotificationContent("Бокал сам себя не выпьет! 🍺", "Время сделать паузу и насладиться пенным."),
                ReminderScheduler.NotificationContent("Время остыть! ❄️🍺", "Освежающий золотистый бокал уже ждет тебя."),
                ReminderScheduler.NotificationContent("Зов хмеля! 🍻", "Хороший вечер начинается со свежего холодного пива."),
                ReminderScheduler.NotificationContent("Минутка для себя 🍺", "Рабочий день окончен — пора налить холодную пинту!"),
                ReminderScheduler.NotificationContent("Золотая пена ждет 🍺", "Сделай свежий глоток и отдохни от суеты."),
                ReminderScheduler.NotificationContent("Пенный патруль 🍻", "Напоминание: день удался, если выпить бокал пива!")
            ),
            fridayNotificationMessage = ReminderScheduler.NotificationContent(
                "Пятница пришла! 🍻",
                "Конец рабочей недели — время открыть холодненькое!"
            )
        )

        val chinese = AppStrings(
            appName = "Beer Reminder",
            calendar = "日历",
            achievements = "成就",
            settings = "设置",
            backToReminder = "返回",

            enableNotificationsTitle = "开启通知",
            enableNotificationsText = "“Beer Reminder”需要通知权限以便在啤酒时间提醒您。请开启通知，不错过每一个精彩时刻！",
            openSettings = "打开设置",
            later = "稍后",

            settingsTitle = "设置",
            sectionLanguage = "语言",
            sectionNotifications = "通知",
            dailyReminders = "每日提醒",
            remindersActiveAt = { time -> "提醒时间 $time" },
            remindersDisabled = "提醒已关闭",
            reminderTime = "提醒时间",
            dailyAlertAt = { time -> "每天 $time 提醒" },
            sectionAppearance = "外观",
            darkTheme = "深色模式",
            darkThemeEnabled = "已开启深色模式",
            lightThemeEnabled = "已开启浅色模式",
            sectionGlassShape = "酒杯造型",
            sectionDevOptions = "开发者选项",
            testNotification = "测试通知",
            testNotificationSubtitle = "立即发送推送通知",
            sendButton = "发送",

            pintName = "品脱杯",
            pintDesc = "经典锥形品脱杯",
            mugName = "啤酒扎杯",
            mugDesc = "结实耐用的经典把手酒扎",
            weizenName = "小麦啤酒杯",
            weizenDesc = "优美弧线的德国小麦啤酒杯",
            tulipName = "郁金香杯",
            tulipDesc = "聚拢香气的高脚精酿杯",
            bootName = "酒靴杯",
            bootDesc = "传统巴伐利亚庆典啤酒靴",

            calendarTitle = "日历",
            weekdayHeaders = listOf("日", "一", "二", "三", "四", "五", "六"),

            achievementsTitle = "成就",
            achConnoisseurTitle = "品酒鉴赏家",
            achConnoisseurDesc = "累计打卡满 10 天",
            achVeteranTitle = "啤酒老兵",
            achVeteranDesc = "累计打卡满 100 天",
            achLegendTitle = "啤酒传奇",
            achLegendDesc = "累计打卡满 1000 天",
            achWeekendTitle = "周末勇士",
            achWeekendDesc = "单周打卡超过 5 天",
            achFlawlessTitle = "完美一周",
            achFlawlessDesc = "单周连续 7 天打卡",
            achOktoberfestTitle = "十月节精神",
            achOktoberfestDesc = "单个自然月打卡 20 天以上",

            funNotificationMessages = listOf(
                ReminderScheduler.NotificationContent("啤酒时间到了！🍺", "是时候放松一下，来杯清凉啤酒了！"),
                ReminderScheduler.NotificationContent("为美好的一天干杯！🍻", "享受一杯爽口冰镇啤酒的完美时刻！"),
                ReminderScheduler.NotificationContent("酒杯不会自己喝空！🍺", "休息一下，享受一杯冰镇啤酒吧。"),
                ReminderScheduler.NotificationContent("放松时刻！❄️🍺", "一杯清爽的金黄啤酒正在等着你。"),
                ReminderScheduler.NotificationContent("啤酒的召唤！🍻", "美好的夜晚从一杯好啤酒开始。"),
                ReminderScheduler.NotificationContent("专属自己的小憩 🍺", "工作结束了 — 该倒上一杯冰爽的啤酒了！"),
                ReminderScheduler.NotificationContent("金黄酒沫在等待 🍺", "喝上一大口，洗去一整天的疲惫。"),
                ReminderScheduler.NotificationContent("啤酒巡逻队 🍻", "提醒：来一杯冰镇啤酒，今天才算圆满！")
            ),
            fridayNotificationMessage = ReminderScheduler.NotificationContent(
                "周五到了！🍻",
                "工作周结束了 — 该开一瓶冰镇啤酒好好庆祝了！"
            )
        )
    }
}

val LocalAppStrings = staticCompositionLocalOf { AppStrings.english }
