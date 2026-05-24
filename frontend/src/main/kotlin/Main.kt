package org.example

import com.interactme.mvi.FamilyStore
import com.interactme.ui.FamilyAdminFrame
import java.awt.Image
import java.awt.Taskbar
import javax.imageio.ImageIO
import javax.swing.SwingUtilities
import javax.swing.UIManager

// Точка входа frontend-приложения.
private const val APP_NAME = "Dobriy Shkaf"

fun main() {
    System.setProperty("apple.awt.application.name", APP_NAME)
    val appIcon = loadAppIcon()
    configureDesktopIcon(appIcon)

    SwingUtilities.invokeLater {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        FamilyAdminFrame(FamilyStore(), appIcon).isVisible = true
    }
}

private fun loadAppIcon(): Image? =
    runCatching {
        object {}.javaClass.getResource("/logo.png")?.let(ImageIO::read)
    }.getOrNull()

private fun configureDesktopIcon(icon: Image?) {
    if (icon == null) return

    runCatching {
        if (Taskbar.isTaskbarSupported()) {
            val taskbar = Taskbar.getTaskbar()
            if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE))
                taskbar.iconImage = icon
        }
    }
}