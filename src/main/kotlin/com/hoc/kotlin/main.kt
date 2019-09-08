package com.hoc.kotlin

import kotlinx.coroutines.ObsoleteCoroutinesApi
import javax.swing.SwingUtilities
import javax.swing.UIManager

@ObsoleteCoroutinesApi
fun main() {
  UIManager.getInstalledLookAndFeels()
      .firstOrNull { it.name == "Nimbus" }
      ?.let { UIManager.setLookAndFeel(it.className) }
  SwingUtilities.invokeLater { MainFrame().isVisible = true }
}