// ВРЕМЕННОЕ РЕШЕНИЕ: Если изображение все еще белое, замените код в MainMenuScreen.kt
// на этот вариант БЕЗ цветового фильтра для проверки:

/*
            Image(
                painter = painterResource(id = R.drawable.main_menu_image),
                contentDescription = "Trust The Route",
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .heightIn(min = 140.dp, max = 200.dp),
                contentScale = ContentScale.Fit
                // БЕЗ colorFilter для проверки оригинального изображения
            )
*/
