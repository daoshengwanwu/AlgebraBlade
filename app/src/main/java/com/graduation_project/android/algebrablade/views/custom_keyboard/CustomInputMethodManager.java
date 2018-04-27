package com.graduation_project.android.algebrablade.views.custom_keyboard;


public class CustomInputMethodManager {
    private static CustomKeyboard sCurrentKeyboard = null;


    public static void registerKeyboard(CustomKeyboard customKeyboard) {
        sCurrentKeyboard = customKeyboard;
    }

    public static CustomKeyboard getCurrentKeyboard() {
        return sCurrentKeyboard;
    }
}
