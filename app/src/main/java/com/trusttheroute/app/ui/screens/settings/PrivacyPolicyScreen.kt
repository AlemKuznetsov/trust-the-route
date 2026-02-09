package com.trusttheroute.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trusttheroute.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit
) {
    val isDarkTheme = isDarkTheme()
    
    val gradientBrush = if (isDarkTheme) {
        Brush.linearGradient(
            colors = listOf(DarkBackground, DarkSurface, DarkBackground),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(LightBackground, LightSurface, LightBackground),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Политика конфиденциальности",
                        color = if (isDarkTheme) Blue400 else LightOnSurface,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = if (isDarkTheme) Blue400 else LightOnSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkTheme) DarkSurface else LightSurface
                )
            )
        },
        containerColor = if (isDarkTheme) DarkBackground else LightBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) DarkSurface else LightSurface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Политика конфиденциальности",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Blue400 else BluePrimary
                        )
                        
                        Text(
                            text = "Дата вступления в силу: 2 февраля 2026 года\nПоследнее обновление: 2 февраля 2026 года",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        
                        Text(
                            text = "Настоящая Политика конфиденциальности (далее — Политика) определяет порядок обработки и защиты персональных данных пользователей мобильного приложения Trust The Route (далее — Приложение) и связанных сервисов (далее совместно — Сервис).",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        
                        Text(
                            text = "Если вы не согласны с условиями Политики, пожалуйста, прекратите использование Сервиса.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        
                        Divider()
                        
                        SectionTitle("1. Термины и определения", isDarkTheme)
                        BulletPoint("Оператор — лицо, организующее и (или) осуществляющее обработку персональных данных пользователей Сервиса: Кузнецов Александр Михайлович (физическое лицо).", isDarkTheme)
                        BulletPoint("Пользователь — физическое лицо, использующее Приложение.", isDarkTheme)
                        BulletPoint("Персональные данные — любая информация, относящаяся прямо или косвенно к Пользователю.", isDarkTheme)
                        BulletPoint("Обработка — любое действие (операция) с персональными данными, включая сбор, запись, систематизацию, хранение, использование, передачу, удаление и т.п.", isDarkTheme)
                        
                        Divider()
                        
                        SectionTitle("2. Какие данные мы обрабатываем", isDarkTheme)
                        Text(
                            text = "В зависимости от того, какие функции Сервиса вы используете, могут обрабатываться следующие категории данных:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        
                        SubSectionTitle("2.1. Данные учетной записи", isDarkTheme)
                        BulletPoint("адрес электронной почты (email);", isDarkTheme)
                        BulletPoint("имя (отображаемое имя);", isDarkTheme)
                        BulletPoint("служебные идентификаторы учетной записи (например, UUID на стороне сервера);", isDarkTheme)
                        BulletPoint("данные авторизации/сессии (например, токен доступа JWT).", isDarkTheme)
                        Text(
                            text = "Где используется: регистрация/вход, управление профилем, безопасность учетной записи, удаление аккаунта.",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                        
                        SubSectionTitle("2.2. Данные о местоположении", isDarkTheme)
                        Text(
                            text = "Приложение может получать данные о местоположении устройства (геолокацию) при предоставлении соответствующего разрешения (например, ACCESS_FINE_LOCATION/ACCESS_COARSE_LOCATION).",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        Text(
                            text = "Зачем нужно: отображение вашего положения на карте, определение приближения к достопримечательностям, запуск аудиогида по мере приближения.",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                        
                        SubSectionTitle("2.3. Технические данные и данные использования", isDarkTheme)
                        Text(
                            text = "При взаимодействии с Сервисом могут автоматически обрабатываться технические данные, которые обычно передаются при сетевом взаимодействии:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        BulletPoint("IP-адрес;", isDarkTheme)
                        BulletPoint("сведения об устройстве и ОС (модель/версия ОС);", isDarkTheme)
                        BulletPoint("технические метаданные запросов (дата/время, код ответа, адрес запрошенного ресурса).", isDarkTheme)
                        Text(
                            text = "Примечание: состав логов и сроки хранения могут зависеть от конфигурации сервера и средств администрирования.",
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                        
                        SubSectionTitle("2.4. Данные, получаемые при входе через Yandex ID", isDarkTheme)
                        Text(
                            text = "Если вы выбираете вход через Yandex ID, то в рамках OAuth‑авторизации Приложение получает от Яндекса информацию об аккаунте, которую вы разрешили предоставить (например: email и данные профиля, такие как имя/логин).",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        
                        SubSectionTitle("2.5. Медиафайлы (изображения/аудио)", isDarkTheme)
                        Text(
                            text = "В Сервисе могут использоваться медиафайлы (изображения/аудиофайлы), размещаемые в облачном хранилище и доступные по прямым ссылкам. В рамках этого могут обрабатываться:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        BulletPoint("загружаемые файлы (изображения/аудио);", isDarkTheme)
                        BulletPoint("технические параметры файла (имя, размер, MIME‑тип);", isDarkTheme)
                        BulletPoint("ссылка (URL) на загруженный файл.", isDarkTheme)
                        Text(
                            text = "Важно: мультимедийные материалы (изображения, аудиофайлы) размещаются в открытом доступе и предоставляются пользователю по прямым ссылкам. Доступ к таким материалам не требует авторизации и не содержит персональных данных. Однако любому лицу, получившему такую ссылку, материал может быть доступен для просмотра/скачивания.",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                        
                        Divider()
                        
                        SectionTitle("3. Правовые основания обработки", isDarkTheme)
                        Text(
                            text = "Мы обрабатываем персональные данные на следующих основаниях (в зависимости от применимого права и сценария использования):",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        BulletPoint("исполнение соглашения с Пользователем (предоставление функций Сервиса);", isDarkTheme)
                        BulletPoint("согласие Пользователя (например, на обработку геолокации, где это требуется);", isDarkTheme)
                        BulletPoint("законный интерес Оператора (например, обеспечение безопасности, предотвращение злоупотреблений);", isDarkTheme)
                        BulletPoint("выполнение требований законодательства (при наличии такой обязанности).", isDarkTheme)
                        
                        Divider()
                        
                        SectionTitle("4. Цели обработки персональных данных", isDarkTheme)
                        Text(
                            text = "Мы обрабатываем данные для:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        BulletPoint("предоставления функциональности Приложения (маршруты, карта, достопримечательности, аудиогид);", isDarkTheme)
                        BulletPoint("регистрации, аутентификации и управления учетной записью;", isDarkTheme)
                        BulletPoint("обеспечения безопасности (в том числе контроля доступа, предотвращения злоупотреблений);", isDarkTheme)
                        BulletPoint("поддержки пользователей и обработки обращений;", isDarkTheme)
                        BulletPoint("улучшения качества Сервиса и диагностики ошибок (в пределах доступных технических данных).", isDarkTheme)
                        
                        Divider()
                        
                        SectionTitle("5. Передача данных третьим лицам и поручение обработки", isDarkTheme)
                        Text(
                            text = "Мы можем использовать сторонние сервисы и поставщиков инфраструктуры. В зависимости от сценария, данные могут передаваться следующим категориям получателей:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        BulletPoint("Яндекс (Yandex MapKit / Yandex ID) — для отображения карт и/или авторизации через Yandex ID. При использовании карт и/или авторизации соответствующие данные могут передаваться в адрес сервисов Яндекса согласно их условиям.", isDarkTheme)
                        BulletPoint("Yandex Object Storage (S3‑совместимое хранилище) — для хранения медиафайлов (изображений/аудио) и выдачи ссылок на них (если функция включена).", isDarkTheme)
                        BulletPoint("Поставщик хостинга/инфраструктуры (например, виртуальная машина/сервер) — для работы backend и базы данных.", isDarkTheme)
                        BulletPoint("Сервисы push‑уведомлений (если включены) — для доставки уведомлений на устройство Пользователя.", isDarkTheme)
                        Text(
                            text = "Мы рекомендуем вам дополнительно ознакомиться с политиками конфиденциальности соответствующих поставщиков.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        
                        SubSectionTitle("5.1. Push‑уведомления (категории)", isDarkTheme)
                        Text(
                            text = "Если push‑уведомления включены, Пользователь может получать уведомления следующих типов:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        BulletPoint("NEW_ROUTE — новый маршрут;", isDarkTheme)
                        BulletPoint("ROUTE_UPDATE — обновление маршрута;", isDarkTheme)
                        BulletPoint("ATTRACTION_UPDATE — обновление достопримечательности;", isDarkTheme)
                        BulletPoint("SYSTEM — системное уведомление;", isDarkTheme)
                        BulletPoint("PROMOTION — рекламное/промо уведомление.", isDarkTheme)
                        
                        SubSectionTitle("5.2. Аналитика и отчеты о сбоях", isDarkTheme)
                        Text(
                            text = "Приложение не использует сторонние аналитические сервисы и SDK для сбора статистики, анализа поведения пользователей и автоматического сбора отчетов о сбоях.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        
                        Divider()
                        
                        SectionTitle("6. Хранение данных и сроки", isDarkTheme)
                        SubSectionTitle("6.1. На сервере", isDarkTheme)
                        Text(
                            text = "На стороне backend могут храниться, в частности: email, имя, хэш пароля (а не пароль в открытом виде), а также технические метаданные учетной записи (например, даты создания/обновления).",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        Text(
                            text = "Срок хранения: персональные данные Пользователя хранятся в течение срока использования Приложения и удаляются по запросу Пользователя либо при удалении учетной записи.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "Технические логи хранятся не более 30 (тридцати) календарных дней и используются исключительно для обеспечения работоспособности Приложения.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        
                        SubSectionTitle("6.2. На устройстве", isDarkTheme)
                        Text(
                            text = "Приложение может сохранять локально данные для офлайн‑режима и удобства использования (например, кэш маршрутов/контента, токены авторизации, настройки).",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        
                        Divider()
                        
                        SectionTitle("7. Защита данных", isDarkTheme)
                        Text(
                            text = "Мы применяем организационные и технические меры защиты, включая:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        BulletPoint("хранение паролей в виде стойких криптографических хэшей;", isDarkTheme)
                        BulletPoint("контроль доступа на уровне токенов (JWT) для защищенных API‑операций;", isDarkTheme)
                        BulletPoint("валидацию типов и размеров загружаемых файлов на backend.", isDarkTheme)
                        Text(
                            text = "Важно: безопасность передачи данных также зависит от конфигурации окружения. Для production рекомендуется использовать HTTPS для сетевого взаимодействия Приложения с backend.",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                        
                        Divider()
                        
                        SectionTitle("8. Права Пользователя", isDarkTheme)
                        Text(
                            text = "Пользователь вправе (в пределах применимого законодательства):",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        BulletPoint("получать информацию об обработке его персональных данных;", isDarkTheme)
                        BulletPoint("требовать уточнения/обновления данных (например, имени);", isDarkTheme)
                        BulletPoint("требовать удаления данных и/или удаления учетной записи (если это предусмотрено Сервисом);", isDarkTheme)
                        BulletPoint("отозвать согласие на обработку данных в случаях, когда обработка основана на согласии.", isDarkTheme)
                        
                        Divider()
                        
                        SectionTitle("9. Дети", isDarkTheme)
                        Text(
                            text = "Сервис не предназначен для использования лицами младше 13 лет. Если вы считаете, что данные ребенка были предоставлены нам без согласия законного представителя, пожалуйста, свяжитесь с нами.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        
                        Divider()
                        
                        SectionTitle("10. Изменения Политики", isDarkTheme)
                        Text(
                            text = "Мы можем обновлять Политику. Новая редакция вступает в силу с момента публикации в Приложении и/или в репозитории/на сайте Сервиса, если не указано иное.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        
                        Divider()
                        
                        SectionTitle("11. Контакты Оператора", isDarkTheme)
                        Text(
                            text = "По вопросам, связанным с обработкой персональных данных:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        Text(
                            text = "Обращения по вопросам обработки персональных данных принимаются по адресу электронной почты: support.trusttheroute@yandex.ru",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) Blue400 else BluePrimary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        
                        Divider()
                        
                        SectionTitle("Приложение А. Применимое право", isDarkTheme)
                        Text(
                            text = "Настоящая Политика составлена и подлежит применению в соответствии с законодательством Российской Федерации.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String, isDarkTheme: Boolean) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = if (isDarkTheme) Blue400 else BluePrimary,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun SubSectionTitle(text: String, isDarkTheme: Boolean) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = if (isDarkTheme) Blue400 else BluePrimary,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp, start = 8.dp)
    )
}

@Composable
private fun BulletPoint(text: String, isDarkTheme: Boolean) {
    Row(
        modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = "• ",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}
