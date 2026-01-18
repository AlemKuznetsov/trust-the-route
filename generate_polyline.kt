// Временный скрипт для генерации polyline
// Координаты достопримечательностей в порядке следования (order 1-18)

val coordinates = listOf(
    Pair(55.732557, 37.604571),  // attraction_1 - Новая Третьяковка
    Pair(55.734102, 37.599051),  // attraction_2 - Пётр I
    Pair(55.746119, 37.583316),  // attraction_3 - МИД РФ
    Pair(55.755729, 37.582960),  // attraction_4 - Посольство США и Дом-музей Шаляпина
    Pair(55.759238, 37.580256),  // attraction_5 - Высотка на Кудринской площади
    Pair(55.759927, 37.585432),  // attraction_6 - Московский планетарий
    Pair(55.766862, 37.592374),  // attraction_7 - Музей Булгакова
    Pair(55.768639, 37.595068),  // attraction_8 - Театр сатиры
    Pair(55.772545, 37.605475),  // attraction_9 - Оружейный, Бизнес-центр
    Pair(55.773127, 37.610253),  // attraction_10 - Всероссийский музей декоративного искусства
    Pair(55.773295, 37.613249),  // attraction_11 - Театр кукол имени Образцова
    Pair(55.773515, 37.619266),  // attraction_12 - Памятник Сергею Образцову
    Pair(55.772581, 37.632677),  // attraction_13 - Институт Склифосовского
    Pair(55.769100, 37.647387),  // attraction_14 - Высотное здание у Красных ворот
    Pair(55.753951, 37.656385),  // attraction_15 - Усадьба Усачёвых — Найдёновых
    Pair(55.750482, 37.655740),  // attraction_16 - Высотное здание на Котельнической набережной
    Pair(55.740974, 37.652649),  // attraction_17 - Бункер-42
    Pair(55.731392, 37.637234)   // attraction_18 - Павелецкий вокзал
)

// Используем алгоритм Google Polyline Encoding для кодирования
fun encodePolyline(points: List<Pair<Double, Double>>): String {
    val encoded = StringBuilder()
    var prevLat = 0
    var prevLng = 0
    
    for ((lat, lng) in points) {
        val latInt = (lat * 1e5).toInt()
        val lngInt = (lng * 1e5).toInt()
        
        val deltaLat = latInt - prevLat
        val deltaLng = lngInt - prevLng
        
        encodeValue(encoded, deltaLat)
        encodeValue(encoded, deltaLng)
        
        prevLat = latInt
        prevLng = lngInt
    }
    
    return encoded.toString()
}

fun encodeValue(encoded: StringBuilder, value: Int) {
    var value = value
    value = if (value < 0) (value shl 1).inv() else value shl 1
    
    while (value >= 0x20) {
        encoded.append((0x20 or (value and 0x1F) + 63).toChar())
        value = value shr 5
    }
    
    encoded.append((value + 63).toChar())
}

// Генерируем polyline
val polyline = encodePolyline(coordinates)
println("Polyline: $polyline")
