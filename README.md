# Тестовое задание

Android-приложение для просмотра городов.

## Требования

- Kotlin
- Jetpack Compose
- Orbit MVI
- Ktor
- Coroutines
- DI
- REST API / JSON
- SOLID и OOP

## Что сделано

- [x] Экран списка городов
- [x] Поиск города в реальном времени
- [x] Пагинация списка
- [x] Отображение названия и страны
- [x] Экран информации о городе
- [x] Отображение названия, страны и населения
- [x] Кнопка поиска информации о городе в браузере
- [x] Обработка загрузки, пустого результата и ошибки
- [x] Дополнительный экран с картой
- [x] Загрузка городов для выбранной области карты
- [x] Маркеры городов и модальное окно
- [x] Unit-тесты
- [x] Android Lint
- [x] Debug-сборка APK

## API

Список городов:

```text
GET http://dev-dep.tools.urent.tech:8080/api/cities
```

Параметры: `query`, `page`, `limit`.

Города на карте:

```text
GET http://dev-dep.tools.urent.tech:8080/api/cities/map
```

Параметры: `centerLat`, `centerLng`, `radius`.

## Сборка

```powershell
.\gradlew.bat assembleDebug
```

APK находится в:

```text
app/build/outputs/apk/debug/app-debug.apk
```
