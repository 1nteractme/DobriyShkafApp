package org.example

import com.interactme.mvi.FamilyStore
import com.interactme.ui.FamilyAdminFrame
import javax.swing.SwingUtilities
import javax.swing.UIManager

// Точка входа frontend-приложения.
fun main() {
    SwingUtilities.invokeLater {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        FamilyAdminFrame(FamilyStore()).isVisible = true
    }
}