# ДЕТАЛЬНЫЙ ПРОМПТ: ЭКРАН НАСТРОЕК "TRUST THE ROUTE" (Jetpack Compose)

## ОБЩАЯ СТРУКТУРА

### Контейнер экрана:
- **Модификатор**: `Modifier.fillMaxSize()`
- **Фон**: Градиент от `Color(0xFFEFF6FF)` (blue-50) через `Color(0xFFFFFFFF)` (white) к `Color(0xFFECFEFF)` (cyan-50)
- **Реализация**: `Brush.verticalGradient()` или `Brush.linearGradient()` с углом 135 градусов
- **Цвета градиента**: 
  - Начало: `#EFF6FF` (blue-50)
  - Середина: `#FFFFFF` (white)
  - Конец: `#ECFEFF` (cyan-50)

---

## ВЕРХНЯЯ ПАНЕЛЬ (HEADER)

### TopAppBar:
- **Фон**: `White` (`Color(0xFFFFFFFF)`)
- **Нижняя граница**: `BorderLight` (`Color(0xFFDBEAFE)`) толщиной 1.dp
- **Позиционирование**: Стандартный TopAppBar (sticky по умолчанию)
- **Elevation**: 2.dp (shadow-sm эквивалент)
- **Высота**: Стандартная высота TopAppBar (56.dp)

### Внутренний контейнер TopAppBar:
- **Максимальная ширина**: Не требуется (TopAppBar по умолчанию центрирован)
- **Padding**: `padding(horizontal = 16.dp, vertical = 16.dp)`
- **Layout**: `Row` с `horizontalArrangement = Arrangement.spacedBy(12.dp)`

### Кнопка "Назад":
- **Модификатор**: `IconButton`
- **Padding**: 8.dp со всех сторон
- **Hover-эффект**: `interactionSource` с `hoverable()` и изменением фона на `Color(0xFFEFF6FF)` (blue-50)
- **Скругление**: `RoundedCornerShape(12.dp)`
- **Transition**: `animateColorAsState()` для плавного перехода
- **Иконка**: `Icons.Default.ArrowBack`
- **Размер иконки**: 24.dp (w-6 h-6)
- **Цвет иконки**: `BluePrimary` (`Color(0xFF2563EB)`)

### Заголовок:
- **Стиль**: `MaterialTheme.typography.headlineSmall` (24.sp)
- **Цвет**: `BluePrimary` (`Color(0xFF2563EB)`)
- **Текст**: "Настройки" или название выбранной категории
- **FontWeight**: `FontWeight.SemiBold`

---

## ОСНОВНОЙ КОНТЕНТ

### Контейнер контента:
- **Модификатор**: `Column` с `Modifier.fillMaxSize()`
- **Максимальная ширина**: Не требуется (Column растягивается на всю ширину)
- **Padding**: `padding(horizontal = 16.dp, vertical = 24.dp)`
- **Scroll**: `verticalScroll(rememberScrollState())` для прокрутки при необходимости

---

## СПИСОК КАТЕГОРИЙ НАСТРОЕК

### Контейнер списка:
- **Модификатор**: `Column` с `verticalArrangement = Arrangement.spacedBy(12.dp)`
- **Анимация появления**: `AnimatedVisibility` с `slideInHorizontally(initialOffsetX = { -20 })` и `fadeIn()`

### Каждая карточка категории:

#### Card компонент:
- **Ширина**: `Modifier.fillMaxWidth()`
- **Высота**: 80.dp (увеличена для лучшей кликабельности)
- **Фон**: `White` (`Color(0xFFFFFFFF)`)
- **Hover фон**: При нажатии/наведении - `Color(0xFFEFF6FF)` (blue-50)
- **Граница**: `BorderLight` (`Color(0xFFDBEAFE)`) толщиной 2.dp
- **Hover граница**: `Color(0xFF93C5FD)` (blue-300)
- **Скругление**: `RoundedCornerShape(16.dp)` (rounded-2xl)
- **Padding**: 16.dp со всех сторон (`p-4`)
- **Transition**: `animateContentSize()` для плавных переходов
- **Layout**: `Row` с `horizontalArrangement = Arrangement.SpaceBetween` и `verticalAlignment = Alignment.CenterVertically`
- **Elevation**: 2.dp по умолчанию, 4.dp при нажатии (hover shadow-md)
- **Clickable**: `clickable { onClick() }` с `interactionSource` для отслеживания состояния

#### Структура карточки (слева направо):

##### Контейнер иконки:
- **Модификатор**: `Box` с `Modifier.size(48.dp)` (контейнер для иконки)
- **Скругление**: `RoundedCornerShape(12.dp)` (rounded-xl)
- **Padding**: 12.dp (`p-3`)
- **Фон**: Зависит от категории (см. ниже)
- **Выравнивание**: `Alignment.Center`

##### Иконка:
- **Размер**: 24.dp (w-6 h-6)
- **Цвет**: Зависит от категории (см. ниже)
- **Тип**: `Icons.Default.*` из Material Icons

##### Текст (название категории):
- **Модификатор**: `Modifier.weight(1f)` (занимает оставшееся пространство)
- **Выравнивание**: `TextAlign.Start` (text-left)
- **Стиль**: `MaterialTheme.typography.bodyLarge` (16.sp)
- **Цвет**: `LightOnSurface` (`Color(0xFF334155)`) - slate-700
- **FontWeight**: `FontWeight.Normal`

##### Иконка ChevronRight:
- **Размер**: 20.dp (w-5 h-5)
- **Цвет**: `LightOnSurfaceVariant` (`Color(0xFF94A3B8)`) - slate-400
- **Hover-эффект**: Сдвиг на 4.dp вправо при наведении (`offset(x = if (isHovered) 4.dp else 0.dp)`)
- **Transition**: `animateOffsetAsState()` для плавного сдвига

---

## ЦВЕТА ДЛЯ КАЖДОЙ КАТЕГОРИИ

### 1. Настройка учетной записи:
- **Иконка**: `Icons.Default.Person`
- **Фон иконки**: `Color(0xFFEFF6FF)` (bg-blue-50)
- **Цвет иконки**: `BluePrimary` (`Color(0xFF2563EB)`) - blue-600

### 2. Язык:
- **Иконка**: `Icons.Default.Public` или `Icons.Default.Language`
- **Фон иконки**: `Color(0xFFECFEFF)` (bg-cyan-50)
- **Цвет иконки**: `CyanAccent` (`Color(0xFF0891B2)`) - cyan-600

### 3. Тема приложения:
- **Иконка**: `Icons.Default.Palette`
- **Фон иконки**: `Color(0xFFEEF2FF)` (bg-indigo-50)
- **Цвет иконки**: `IndigoAccent` (`Color(0xFF4F46E5)`) - indigo-600

### 4. Уведомления:
- **Иконка**: `Icons.Default.Notifications`
- **Фон иконки**: `Color(0xFFEFF6FF)` (bg-blue-50)
- **Цвет иконки**: `Color(0xFF3B82F6)` (blue-500)

### 5. Размер шрифта:
- **Иконка**: `Icons.Default.FormatSize` или `Icons.Default.TextFields`
- **Фон иконки**: `Color(0xFFF8FAFC)` (bg-slate-50)
- **Цвет иконки**: `LightOnSurfaceVariant` (`Color(0xFF475569)`) - slate-600

### 6. Конфиденциальность и безопасность:
- **Иконка**: `Icons.Default.Security` или `Icons.Default.Shield`
- **Фон иконки**: `Color(0xFFF0FDF4)` (bg-green-50)
- **Цвет иконки**: `Color(0xFF16A34A)` (green-600)

### 7. Информация о приложении:
- **Иконка**: `Icons.Default.Info`
- **Фон иконки**: `Color(0xFFFAF5FF)` (bg-purple-50)
- **Цвет иконки**: `Color(0xFF9333EA)` (purple-600)

---

## ДЕТАЛЬНЫЕ ЭКРАНЫ (ПОДМЕНЮ)

### Анимация появления:
- **Компонент**: `AnimatedVisibility` с `slideInHorizontally(initialOffsetX = { 20 })` и `fadeIn()`
- **Направление**: Сдвиг справа налево (x: 20 → 0)
- **Opacity**: 0 → 1
- **Duration**: 300ms
- **Easing**: `FastOutSlowInEasing`

---

## 1. ЭКРАН "ЯЗЫК"

### Контейнер:
- **Модификатор**: `Column` с `verticalArrangement = Arrangement.spacedBy(12.dp)`
- **Padding**: `padding(horizontal = 16.dp, vertical = 24.dp)`

### Каждая кнопка выбора языка:

#### Card компонент:
- **Ширина**: `Modifier.fillMaxWidth()`
- **Выравнивание**: `TextAlign.Start` (text-left)
- **Padding**: `padding(horizontal = 16.dp, vertical = 16.dp)` (px-4 py-4)
- **Скругление**: `RoundedCornerShape(12.dp)` (rounded-xl)
- **Transition**: `animateContentSize()` и `animateColorAsState()` для плавных переходов

#### Состояния:

##### Выбран:
- **Фон**: `Color(0xFFDBEAFE)` (bg-blue-100)
- **Граница**: `BluePrimary` (`Color(0xFF3B82F6)`) толщиной 2.dp (border-blue-500)
- **Текст**: `BluePrimary` (`Color(0xFF3B82F6)`)
- **FontWeight**: `FontWeight.SemiBold`

##### Не выбран:
- **Фон**: `White` (`Color(0xFFFFFFFF)`)
- **Граница**: `BorderLight` (`Color(0xFFDBEAFE)`) толщиной 2.dp (border-blue-100)
- **Hover граница**: `Color(0xFF93C5FD)` (blue-300)
- **Текст**: `LightOnSurface` (`Color(0xFF334155)`)
- **FontWeight**: `FontWeight.Normal`

#### Опции:
- **Русский (ru)**
- **English (en)**
- **Deutsch (de)**

#### Структура кнопки:
- **Layout**: `Row` с `horizontalArrangement = Arrangement.SpaceBetween`
- **Левая часть**: Текст языка
- **Правая часть**: Иконка галочки (если выбрано) или пусто

---

## 2. ЭКРАН "ТЕМА ПРИЛОЖЕНИЯ"

### Контейнер:
- **Модификатор**: `Column` с `verticalArrangement = Arrangement.spacedBy(16.dp)`
- **Padding**: `padding(horizontal = 16.dp, vertical = 24.dp)`

### Карточка:
- **Layout**: `Row` с `horizontalArrangement = Arrangement.SpaceBetween` и `verticalAlignment = Alignment.CenterVertically`
- **Padding**: 16.dp (`p-4`)
- **Фон**: `White` (`Color(0xFFFFFFFF)`)
- **Граница**: `BorderLight` (`Color(0xFFDBEAFE)`) толщиной 2.dp
- **Скругление**: `RoundedCornerShape(12.dp)` (rounded-xl)

### Элементы:

#### Левая часть:
- **Текст**: "Темная тема"
- **Стиль**: `MaterialTheme.typography.bodyLarge` (16.sp)
- **Цвет**: `LightOnSurface` (`Color(0xFF334155)`) - slate-700

#### Правая часть:
- **Компонент**: `Switch` из Material3
- **Цвета**: 
  - `checkedThumbColor`: `BluePrimary`
  - `checkedTrackColor`: `BluePrimary.copy(alpha = 0.5f)`
  - `uncheckedThumbColor`: `Color(0xFFE2E8F0)`
  - `uncheckedTrackColor`: `Color(0xFFCBD5E1)`

---

## 3. ЭКРАН "УВЕДОМЛЕНИЯ"

### Контейнер:
- **Модификатор**: `Column` с `verticalArrangement = Arrangement.spacedBy(16.dp)`
- **Padding**: `padding(horizontal = 16.dp, vertical = 24.dp)`

### Карточка:
- **Layout**: `Row` с `horizontalArrangement = Arrangement.SpaceBetween` и `verticalAlignment = Alignment.CenterVertically`
- **Padding**: 16.dp (`p-4`)
- **Фон**: `White` (`Color(0xFFFFFFFF)`)
- **Граница**: `BorderLight` (`Color(0xFFDBEAFE)`) толщиной 2.dp
- **Скругление**: `RoundedCornerShape(12.dp)` (rounded-xl)

### Левая часть (Column):
- **Заголовок**: "Push-уведомления"
  - **Стиль**: `MaterialTheme.typography.bodyLarge` (16.sp)
  - **Цвет**: `Color(0xFF1E293B)` (slate-800)
  - **FontWeight**: `FontWeight.SemiBold`
- **Описание**: "Получать уведомления о новых маршрутах"
  - **Стиль**: `MaterialTheme.typography.bodySmall` (12.sp)
  - **Цвет**: `LightOnSurfaceVariant` (`Color(0xFF64748B)`) - slate-500
  - **Отступ сверху**: 4.dp (`mb-1`)

### Правая часть:
- **Компонент**: `Switch` из Material3
- **Цвета**: Аналогично экрану "Тема приложения"

---

## 4. ЭКРАН "РАЗМЕР ШРИФТА"

### Контейнер:
- **Модификатор**: `Column` с `verticalArrangement = Arrangement.spacedBy(12.dp)`
- **Padding**: `padding(horizontal = 16.dp, vertical = 24.dp)`

### Стили кнопок:
- Аналогично экрану "Язык" (те же стили и состояния)

### Опции:
- **Маленький (small)**: 14.sp базовый размер
- **Средний (medium)**: 16.sp базовый размер (по умолчанию)
- **Большой (large)**: 18.sp базовый размер

---

## 5. ЭКРАН "НАСТРОЙКА УЧЕТНОЙ ЗАПИСИ"

### Контейнер:
- **Модификатор**: `Column` с `verticalArrangement = Arrangement.spacedBy(16.dp)`
- **Padding**: `padding(horizontal = 16.dp, vertical = 24.dp)`

### Каждая карточка информации:

#### Card компонент:
- **Padding**: 16.dp (`p-4`)
- **Фон**: `White` (`Color(0xFFFFFFFF)`)
- **Граница**: `BorderLight` (`Color(0xFFDBEAFE)`) толщиной 2.dp
- **Скругление**: `RoundedCornerShape(12.dp)` (rounded-xl)

#### Структура (Column):
- **Метка**: 
  - **Стиль**: `MaterialTheme.typography.bodySmall` (12.sp)
  - **Цвет**: `LightOnSurfaceVariant` (`Color(0xFF64748B)`) - slate-500
  - **Отступ снизу**: 4.dp (`mb-1`)
- **Значение**: 
  - **Стиль**: `MaterialTheme.typography.bodyLarge` (16.sp)
  - **Цвет**: `Color(0xFF1E293B)` (slate-800)
  - **FontWeight**: `FontWeight.Normal`

#### Поля:
- **Имя пользователя**: "Иван Петров"
- **Email**: "ivan.petrov@example.com"

---

## 6. ЭКРАН "КОНФИДЕНЦИАЛЬНОСТЬ И БЕЗОПАСНОСТЬ"

### Контейнер:
- **Модификатор**: `Column` с `verticalArrangement = Arrangement.spacedBy(16.dp)`
- **Padding**: `padding(horizontal = 16.dp, vertical = 24.dp)`

### Карточка:
- **Padding**: 16.dp (`p-4`)
- **Фон**: `White` (`Color(0xFFFFFFFF)`)
- **Граница**: `BorderLight` (`Color(0xFFDBEAFE)`) толщиной 2.dp
- **Скругление**: `RoundedCornerShape(12.dp)` (rounded-xl)

### Содержимое (Column):
- **Заголовок**: "Политика конфиденциальности"
  - **Стиль**: `MaterialTheme.typography.bodyLarge` (16.sp)
  - **Цвет**: `Color(0xFF1E293B)` (slate-800)
  - **Отступ снизу**: 8.dp (`mb-2`)
  - **FontWeight**: `FontWeight.SemiBold`
- **Текст**: Описание политики
  - **Стиль**: `MaterialTheme.typography.bodySmall` (12.sp)
  - **Цвет**: `LightOnSurfaceVariant` (`Color(0xFF475569)`) - slate-600

---

## 7. ЭКРАН "ИНФОРМАЦИЯ О ПРИЛОЖЕНИИ"

### Контейнер:
- **Модификатор**: `Column` с `verticalArrangement = Arrangement.spacedBy(16.dp)`
- **Padding**: `padding(horizontal = 16.dp, vertical = 24.dp)`

### Каждая карточка (аналогично "Учетной записи"):
- **Padding**: 16.dp (`p-4`)
- **Фон**: `White` (`Color(0xFFFFFFFF)`)
- **Граница**: `BorderLight` (`Color(0xFFDBEAFE)`) толщиной 2.dp
- **Скругление**: `RoundedCornerShape(12.dp)` (rounded-xl)

### Поля:
- **Версия**: "1.0.0"
- **Разработчик**: "Trust The Route Team"

---

## ТОЧНЫЕ РАЗМЕРЫ В DP

### Padding:
- `padding(8.dp)` = p-2
- `padding(12.dp)` = p-3
- `padding(16.dp)` = p-4
- `padding(horizontal = 16.dp)` = px-4
- `padding(vertical = 16.dp)` = py-4
- `padding(vertical = 24.dp)` = py-6

### Spacing:
- `Arrangement.spacedBy(12.dp)` = gap-3, space-y-3
- `Arrangement.spacedBy(16.dp)` = gap-4, space-y-4
- `horizontalArrangement = Arrangement.spacedBy(12.dp)` = gap-3

### Размеры:
- `size(20.dp)` = w-5 h-5
- `size(24.dp)` = w-6 h-6
- `size(48.dp)` = контейнер иконки
- `RoundedCornerShape(12.dp)` = rounded-xl
- `RoundedCornerShape(16.dp)` = rounded-2xl
- `border(2.dp)` = border-2

### Typography:
- `MaterialTheme.typography.bodySmall` = 12.sp (text-sm)
- `MaterialTheme.typography.bodyMedium` = 14.sp
- `MaterialTheme.typography.bodyLarge` = 16.sp
- `MaterialTheme.typography.headlineSmall` = 24.sp (text-2xl)

### Отступы:
- `padding(bottom = 4.dp)` = mb-1
- `padding(bottom = 8.dp)` = mb-2

---

## ТЕХНИЧЕСКИЕ ТРЕБОВАНИЯ

### Библиотеки (уже используются в проекте):
- **Jetpack Compose** - UI фреймворк
- **Material3** - компоненты (Card, Switch, TopAppBar, IconButton)
- **Material Icons** - иконки (Icons.Default.*)
- **Compose Animation** - анимации (AnimatedVisibility, animateColorAsState, animateOffsetAsState)

### Анимации:

#### Список категорий:
- **Компонент**: `AnimatedVisibility`
- **Анимация**: `slideInHorizontally(initialOffsetX = { -20 })` + `fadeIn()`
- **Направление**: Появление слева (x: -20 → 0)
- **Opacity**: 0 → 1
- **Duration**: 300ms
- **Easing**: `FastOutSlowInEasing`

#### Детальный экран:
- **Компонент**: `AnimatedVisibility`
- **Анимация**: `slideInHorizontally(initialOffsetX = { 20 })` + `fadeIn()`
- **Направление**: Появление справа (x: 20 → 0)
- **Opacity**: 0 → 1
- **Duration**: 300ms
- **Easing**: `FastOutSlowInEasing`

#### Переходы между экранами:
- **Компонент**: `AnimatedContent` с `targetState`
- **Transition**: `slideHorizontally()` + `fadeIn()` / `fadeOut()`
- **Duration**: 300ms

### Hover-эффекты:

#### Карточка категории:
- **Изменение фона**: `Color(0xFFEFF6FF)` (blue-50) при наведении
- **Изменение границы**: `Color(0xFF93C5FD)` (blue-300) при наведении
- **Добавление тени**: `elevation = 4.dp` при наведении
- **Transition**: `animateColorAsState()` и `animateDpAsState()` с `durationMillis = 200`

#### ChevronRight иконка:
- **Сдвиг**: `offset(x = if (isHovered) 4.dp else 0.dp)`
- **Transition**: `animateOffsetAsState()` с `durationMillis = 200`

#### Кнопка "Назад":
- **Изменение фона**: `Color(0xFFEFF6FF)` (blue-50) при наведении
- **Transition**: `animateColorAsState()` с `durationMillis = 200`

### Состояния взаимодействия:

#### Использование `interactionSource`:
```kotlin
val interactionSource = remember { MutableInteractionSource() }
val isHovered by interactionSource.collectIsHoveredAsState()
val isPressed by interactionSource.collectIsPressedAsState()
```

#### Условные стили:
- При `isHovered`: изменяем фон, границу, тень
- При `isPressed`: дополнительное затемнение или уменьшение масштаба

---

## ЦВЕТОВАЯ ПАЛИТРА (АДАПТАЦИЯ ПОД ПРОЕКТ)

### Основные цвета (из Color.kt):
- `BluePrimary` = `Color(0xFF3B82F6)` = `Color(0xFF2563EB)` (blue-600)
- `CyanAccent` = `Color(0xFF06B6D4)` = `Color(0xFF0891B2)` (cyan-600)
- `IndigoAccent` = `Color(0xFF6366F1)` = `Color(0xFF4F46E5)` (indigo-600)

### Фоновые цвета:
- `White` = `Color(0xFFFFFFFF)`
- `LightBackground` = `Color(0xFFFFFFFF)`
- `LightSurface` = `Color(0xFFF8FAFC)` (slate-50)

### Цвета текста:
- `LightOnSurface` = `Color(0xFF1E293B)` (slate-800) для основного текста
- `LightOnSurfaceVariant` = `Color(0xFF64748B)` (slate-500) для вторичного текста

### Границы:
- `BorderLight` = `Color(0xFFDBEAFE)` (blue-100)
- `BorderMedium` = `Color(0xFFC7D2FE)` (blue-200)

### Дополнительные цвета для категорий:
- **Green-600**: `Color(0xFF16A34A)` - для Security
- **Purple-600**: `Color(0xFF9333EA)` - для Info
- **Blue-500**: `Color(0xFF3B82F6)` - для Notifications

### Фоны иконок (light variants):
- **Blue-50**: `Color(0xFFEFF6FF)`
- **Cyan-50**: `Color(0xFFECFEFF)`
- **Indigo-50**: `Color(0xFFEEF2FF)`
- **Slate-50**: `Color(0xFFF8FAFC)`
- **Green-50**: `Color(0xFFF0FDF4)`
- **Purple-50**: `Color(0xFFFAF5FF)`

---

## СТРУКТУРА КОМПОНЕНТОВ

### Основной экран:
```kotlin
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onCategoryClick: (SettingsCategory) -> Unit
)
```

### Компонент карточки категории:
```kotlin
@Composable
fun SettingsCategoryCard(
    title: String,
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconTint: Color,
    onClick: () -> Unit
)
```

### Компонент кнопки выбора (для языка/размера шрифта):
```kotlin
@Composable
fun SelectionButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
)
```

### Компонент переключателя (для темы/уведомлений):
```kotlin
@Composable
fun SettingsSwitch(
    title: String,
    description: String? = null,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
)
```

### Компонент информационной карточки:
```kotlin
@Composable
fun InfoCard(
    label: String,
    value: String
)
```

---

## ПРИМЕЧАНИЯ ПО РЕАЛИЗАЦИИ

1. **Градиент фона**: Использовать `Box` с `Modifier.background()` и `Brush.linearGradient()`
2. **Анимации**: Все анимации должны быть плавными (200-300ms)
3. **Доступность**: Добавить `contentDescription` для всех иконок
4. **Темная тема**: Адаптировать цвета для темной темы (использовать `MaterialTheme.colorScheme`)
5. **Ripple эффект**: Использовать стандартный `clickable()` для ripple эффекта при нажатии
6. **Состояния загрузки**: Добавить индикаторы загрузки при сохранении настроек
7. **Валидация**: Добавить валидацию для полей ввода (если будут)

---

## ФАЙЛЫ ДЛЯ РЕАЛИЗАЦИИ

1. `SettingsScreen.kt` - основной экран настроек
2. `LanguageScreen.kt` - экран выбора языка
3. `ThemeScreen.kt` - экран выбора темы
4. `NotificationsScreen.kt` - экран настроек уведомлений
5. `FontSizeScreen.kt` - экран выбора размера шрифта
6. `AccountSettingsScreen.kt` - экран настроек учетной записи
7. `PrivacyScreen.kt` - экран конфиденциальности
8. `AboutScreen.kt` - экран информации о приложении
9. `SettingsComponents.kt` - переиспользуемые компоненты
10. `SettingsViewModel.kt` - ViewModel для управления состоянием настроек

---

**Дата создания**: 18 января 2026  
**Версия**: 1.0  
**Адаптировано для**: Jetpack Compose + Material3 + Kotlin
