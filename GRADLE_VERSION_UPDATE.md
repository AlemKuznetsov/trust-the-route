# Обновление версии Gradle для KSP

## Проблема
```
The minimum compatible Gradle version is 8.5.
The maximum compatible Gradle JVM version is 20.
```

## Причина
KSP (Kotlin Symbol Processing) версии 1.9.20-1.0.14 требует Gradle 8.5 или выше, а в проекте был установлен Gradle 8.4.

## Решение

### ✅ Исправлено:
- Обновлена версия Gradle с **8.4** на **8.5** в `gradle/wrapper/gradle-wrapper.properties`

### Изменения:
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
```

## Что делать дальше:

1. **Синхронизируйте проект:**
   - **File → Sync Project with Gradle Files**
   - Gradle автоматически загрузит версию 8.5 при первой синхронизации

2. **Дождитесь завершения загрузки:**
   - Gradle 8.5 будет загружен автоматически (может занять 1-2 минуты)
   - В панели Build вы увидите прогресс загрузки

3. **После синхронизации:**
   - **Build → Clean Project**
   - **Build → Assemble Project**

## Проверка версии Gradle:

После синхронизации вы можете проверить версию:
- В панели **Build** должно быть сообщение о загрузке Gradle 8.5
- Или в файле `gradle/wrapper/gradle-wrapper.properties` должна быть строка с `gradle-8.5-bin.zip`

## Примечание:

Gradle 8.5 полностью совместим с:
- ✅ Android Gradle Plugin 8.2.0
- ✅ Kotlin 1.9.20
- ✅ KSP 1.9.20-1.0.14
- ✅ Java 17

После обновления Gradle проблема должна быть решена!
