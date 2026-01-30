# Настройка иконки приложения

## Требования
Для установки иконки приложения из файла `icon.jpg` необходимо:

1. **Конвертировать JPG в PNG** (Android требует PNG формат)
2. **Создать иконки разных размеров** для разных плотностей экрана
3. **Поместить их в соответствующие папки mipmap**

## Размеры иконок для Android

Для обычной иконки (ic_launcher):
- **mipmap-mdpi**: 48x48 px
- **mipmap-hdpi**: 72x72 px
- **mipmap-xhdpi**: 96x96 px
- **mipmap-xxhdpi**: 144x144 px
- **mipmap-xxxhdpi**: 192x192 px

Для круглой иконки (ic_launcher_round) - те же размеры.

## Способы добавления иконки

### Способ 1: Использование Android Asset Studio (рекомендуется)

1. Откройте [Android Asset Studio](https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html)
2. Загрузите ваш файл `icon.jpg`
3. Настройте параметры (фон, обрезка и т.д.)
4. Скачайте готовый набор иконок
5. Скопируйте файлы из папки `res` в `app/src/main/res/`

### Способ 2: Ручное создание

1. Конвертируйте `icon.jpg` в PNG (можно использовать онлайн конвертер)
2. Используйте графический редактор (например, GIMP или Photoshop) для создания иконок нужных размеров
3. Сохраните файлы с именами:
   - `ic_launcher.png` (для обычной иконки)
   - `ic_launcher_round.png` (для круглой иконки)
4. Поместите файлы в соответствующие папки:
   - `app/src/main/res/mipmap-mdpi/`
   - `app/src/main/res/mipmap-hdpi/`
   - `app/src/main/res/mipmap-xhdpi/`
   - `app/src/main/res/mipmap-xxhdpi/`
   - `app/src/main/res/mipmap-xxxhdpi/`

### Способ 3: Использование Image Asset в Android Studio

1. Откройте Android Studio
2. Правой кнопкой мыши на папке `app/src/main/res`
3. Выберите `New > Image Asset`
4. Выберите `Launcher Icons (Adaptive and Legacy)`
5. Загрузите ваш `icon.jpg` в поле `Path`
6. Настройте параметры и нажмите `Next` > `Finish`

## Текущая настройка

В `AndroidManifest.xml` уже настроено:
```xml
android:icon="@drawable/ic_launcher"
android:roundIcon="@drawable/ic_launcher_round"
```

Если вы используете PNG файлы вместо XML drawable, просто поместите файлы `ic_launcher.png` и `ic_launcher_round.png` в папки mipmap, и Android автоматически их найдет.

## Примечание

Если у вас есть файл `icon.jpg` в корне проекта или в другой папке, сообщите путь к нему, и я помогу настроить иконку автоматически.
