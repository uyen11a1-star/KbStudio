package com.example.kbstudio

import android.inputmethodservice.InputMethodService
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout

class KbImeService : InputMethodService(), KeyboardView.Listener {

    private lateinit var themeManager: ThemeManager
    private lateinit var keyboardView: KeyboardView

    override fun onCreate() {
        super.onCreate()
        themeManager = ThemeManager(this)
    }

    override fun onCreateInputView(): View {
        val container = FrameLayout(this)
        keyboardView = KeyboardView(this)
        keyboardView.listener = this
        keyboardView.previewMode = false
        applyTheme()
        container.addView(keyboardView, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        return container
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        applyTheme()
    }

    private fun applyTheme() {
        val t = themeManager.load()
        keyboardView.setTheme(t)
        keyboardView.resetToLetters()
        val h = (t.keyboardHeightDp * resources.displayMetrics.density).toInt()
        keyboardView.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, h)
    }

    override fun onEvaluateFullscreenMode(): Boolean = false

    override fun onKey(key: KeyDef) {
        val ic = currentInputConnection ?: return
        when (key.code) {
            KeyDef.CODE_CHAR -> commitChar(key.output)
            KeyDef.CODE_SPACE -> ic.commitText(" ", 1)
            KeyDef.CODE_BACKSPACE -> ic.deleteSurroundingText(1, 0)
            KeyDef.CODE_ENTER -> handleEnter(ic)
            KeyDef.CODE_LANG -> switchIme()
            KeyDef.CODE_HIDE -> requestHideSelf(0)
            KeyDef.CODE_TAB -> ic.commitText("\t", 1)
            KeyDef.CODE_LEFT -> ic.sendKeyEvent(android.view.KeyEvent(
                android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_DPAD_LEFT))
            KeyDef.CODE_RIGHT -> ic.sendKeyEvent(android.view.KeyEvent(
                android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_DPAD_RIGHT))
            KeyDef.CODE_UP -> ic.sendKeyEvent(android.view.KeyEvent(
                android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_DPAD_UP))
            KeyDef.CODE_DOWN -> ic.sendKeyEvent(android.view.KeyEvent(
                android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_DPAD_DOWN))
        }
    }

    private fun commitChar(text: String) {
        val ic = currentInputConnection ?: return
        if (themeManager.telexEnabled && text.length == 1 && text[0].lowercaseChar() in "aăâeêioôơuưyd") {
            val before = ic.getTextBeforeCursor(2, 0)?.toString() ?: ""
            val result = TelexEngine.tryApply(before, text[0])
            if (result != null) {
                val (del, replace) = result
                if (del > 0) ic.deleteSurroundingText(del, 0)
                ic.commitText(replace, 1)
                return
            }
        }
        ic.commitText(text, 1)
    }

    private fun handleEnter(ic: android.view.inputmethod.InputConnection) {
        val info = currentInputEditorInfo
        val action = info?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: EditorInfo.IME_ACTION_NONE
        if (action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED) {
            ic.performEditorAction(action)
        } else {
            ic.commitText("\n", 1)
        }
    }

    private fun switchIme() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            switchToNextInputMethod(false)
        } else {
            val imm = getSystemService(InputMethodManager::class.java)
            imm?.showInputMethodPicker()
        }
    }
}
