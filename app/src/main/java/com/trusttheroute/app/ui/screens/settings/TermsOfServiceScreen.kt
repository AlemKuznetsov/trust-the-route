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
fun TermsOfServiceScreen(
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
                        text = "Пользовательское соглашение",
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
                            text = "Пользовательское соглашение",
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
                            text = "Настоящее Пользовательское соглашение (далее — Соглашение) определяет условия использования мобильного приложения Trust The Route (далее — Приложение) и связанных сервисов (далее совместно — Сервис).",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        
                        Text(
                            text = "Используя Сервис, вы подтверждаете, что ознакомились и согласны с условиями Соглашения и Политикой конфиденциальности.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        
                        Divider()
                        
                        SectionTitle("1. Стороны", isDarkTheme)
                        BulletPoint("Правообладатель/Оператор Сервиса: Кузнецов Александр Михайлович (физическое лицо) (далее — Администрация).", isDarkTheme)
                        BulletPoint("Пользователь: физическое лицо, использующее Сервис.", isDarkTheme)
                        
                        Divider()
                        
                        SectionTitle("2. Описание Сервиса", isDarkTheme)
                        Text(
                            text = "Сервис предназначен для:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        BulletPoint("просмотра маршрутов и остановок общественного транспорта (в рамках доступных данных);", isDarkTheme)
                        BulletPoint("отображения маршрута и объектов на карте;", isDarkTheme)
                        BulletPoint("предоставления информации о достопримечательностях и воспроизведения аудиогида при приближении (при включенной геолокации).", isDarkTheme)
                        Text(
                            text = "Сервис может работать в офлайн‑режиме в части данных, предварительно сохраненных на устройстве (кэширование).",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        
                        Divider()
                        
                        SectionTitle("3. Учетная запись", isDarkTheme)
                        SubSectionTitle("3.1. Регистрация и вход", isDarkTheme)
                        Text(
                            text = "Для доступа к отдельным функциям может потребоваться учетная запись. Регистрация возможна, в частности:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        BulletPoint("по email/паролю;", isDarkTheme)
                        BulletPoint("через Yandex ID (OAuth).", isDarkTheme)
                        Text(
                            text = "Пользователь обязуется предоставлять достоверные сведения и поддерживать их актуальность.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        
                        SubSectionTitle("3.2. Безопасность", isDarkTheme)
                        Text(
                            text = "Пользователь обязан обеспечивать конфиденциальность учетных данных и не передавать доступ третьим лицам. Все действия, совершенные с использованием учетной записи Пользователя, считаются совершенными Пользователем, если не доказано иное.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        
                        Divider()
                        
                        SectionTitle("4. Правила использования и ограничения", isDarkTheme)
                        Text(
                            text = "Пользователю запрещается:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        BulletPoint("использовать Сервис в незаконных целях;", isDarkTheme)
                        BulletPoint("пытаться получить несанкционированный доступ к инфраструктуре Сервиса, учетным записям, данным или исходному коду;", isDarkTheme)
                        BulletPoint("нарушать права третьих лиц, включая права на результаты интеллектуальной деятельности;", isDarkTheme)
                        BulletPoint("распространять вредоносные программы, вмешиваться в работу Сервиса, осуществлять атаки (DDoS, подбор паролей и т.п.);", isDarkTheme)
                        BulletPoint("загружать в Сервис (если функциональность доступна) файлы, содержащие незаконный контент, персональные данные третьих лиц без законных оснований, либо материалы, нарушающие права третьих лиц.", isDarkTheme)
                        
                        Divider()
                        
                        SectionTitle("5. Контент и интеллектуальная собственность", isDarkTheme)
                        Text(
                            text = "Все элементы Сервиса (дизайн, тексты, данные, программный код, логотипы, аудиоматериалы и иные объекты), если не указано иное, принадлежат Администрации или используются на законных основаниях и защищены применимым правом.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        Text(
                            text = "Пользователь получает ограниченное, неисключительное, непередаваемое право использования Сервиса по назначению и в пределах функциональности.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        
                        Divider()
                        
                        SectionTitle("6. Геолокация, карты и точность данных", isDarkTheme)
                        Text(
                            text = "Сервис может использовать геолокацию для отображения местоположения и работы сценариев \"близости\" (например, запуска аудиогида).",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        Text(
                            text = "Пользователь понимает и соглашается, что:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        BulletPoint("точность геолокации зависит от устройства, условий приема, настроек ОС и иных факторов;", isDarkTheme)
                        BulletPoint("карты и маршрутизация предоставляются с использованием сторонних технологий (например, Яндекс), и их точность может отличаться от фактической;", isDarkTheme)
                        BulletPoint("информация о маршрутах/достопримечательностях носит справочный характер и может быть неполной/устаревшей.", isDarkTheme)
                        
                        Divider()
                        
                        SectionTitle("7. Push‑уведомления", isDarkTheme)
                        Text(
                            text = "Если в Приложении включены push‑уведомления, Пользователь может получать уведомления следующих типов:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        BulletPoint("NEW_ROUTE — новый маршрут;", isDarkTheme)
                        BulletPoint("ROUTE_UPDATE — обновление маршрута;", isDarkTheme)
                        BulletPoint("ATTRACTION_UPDATE — обновление достопримечательности;", isDarkTheme)
                        BulletPoint("SYSTEM — системное уведомление;", isDarkTheme)
                        BulletPoint("PROMOTION — рекламное/промо уведомление.", isDarkTheme)
                        Text(
                            text = "Пользователь вправе отключить уведомления в настройках устройства.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        
                        Divider()
                        
                        SectionTitle("8. Ответственность и отказ от гарантий", isDarkTheme)
                        Text(
                            text = "Сервис предоставляется \"как есть\" (AS IS), если иное не предусмотрено применимым законодательством.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        Text(
                            text = "Администрация не гарантирует:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        BulletPoint("непрерывную и безошибочную работу Сервиса;", isDarkTheme)
                        BulletPoint("соответствие Сервиса ожиданиям Пользователя;", isDarkTheme)
                        BulletPoint("абсолютную точность данных о маршрутах, расписаниях и/или достопримечательностях.", isDarkTheme)
                        Text(
                            text = "Администрация не несет ответственности за:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        BulletPoint("последствия использования Пользователем информации, полученной через Сервис;", isDarkTheme)
                        BulletPoint("сбои связи, действия операторов связи, поставщиков устройств/ПО, сторонних сервисов;", isDarkTheme)
                        BulletPoint("любые косвенные убытки, упущенную выгоду и т.п., если иное не предусмотрено законом.", isDarkTheme)
                        
                        Divider()
                        
                        SectionTitle("9. Изменение и прекращение работы Сервиса", isDarkTheme)
                        Text(
                            text = "Администрация вправе:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        BulletPoint("изменять функциональность Сервиса;", isDarkTheme)
                        BulletPoint("временно ограничивать доступ к Сервису по техническим или иным причинам;", isDarkTheme)
                        BulletPoint("обновлять Соглашение и иные документы.", isDarkTheme)
                        Text(
                            text = "Новая редакция Соглашения вступает в силу с момента публикации, если не указано иное.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        
                        Divider()
                        
                        SectionTitle("10. Удаление учетной записи", isDarkTheme)
                        Text(
                            text = "Пользователь вправе удалить учетную запись через доступные механизмы Сервиса (если предусмотрено). После удаления учетной записи Администрация прекращает обработку персональных данных, кроме случаев и в объемах, когда хранение необходимо по закону или для защиты прав и законных интересов.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        
                        Divider()
                        
                        SectionTitle("11. Применимое право и разрешение споров", isDarkTheme)
                        Text(
                            text = "Соглашение регулируется правом Российской Федерации. Споры подлежат разрешению в порядке, установленном законодательством Российской Федерации. При наличии обязательного претензионного порядка, он соблюдается сторонами.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        
                        Divider()
                        
                        SectionTitle("12. Контакты", isDarkTheme)
                        Text(
                            text = "По вопросам использования Сервиса:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                        )
                        Text(
                            text = "Email: support.trusttheroute@yandex.ru",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) Blue400 else BluePrimary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        
                        Divider()
                        
                        SectionTitle("Приложение А. Возрастное ограничение", isDarkTheme)
                        Text(
                            text = "Сервис предназначен для лиц, достигших 13 лет.",
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
