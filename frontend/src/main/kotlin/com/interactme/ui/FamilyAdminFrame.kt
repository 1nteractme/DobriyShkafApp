package com.interactme.ui

import com.interactme.AppInfo
import com.interactme.data.Family
import com.interactme.data.FamilyField
import com.interactme.data.PaymentType
import com.interactme.data.valueOf
import com.interactme.mvi.FamilyIntent
import com.interactme.mvi.FamilyState
import com.interactme.mvi.FamilyStore
import java.awt.BasicStroke
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.GridLayout
import java.awt.Image
import java.awt.Insets
import java.awt.RenderingHints
import java.time.LocalDate
import java.time.YearMonth
import javax.swing.BorderFactory
import javax.swing.Icon
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JPopupMenu
import javax.swing.JScrollPane
import javax.swing.JSplitPane
import javax.swing.JTable
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.JToggleButton
import javax.swing.ListSelectionModel
import javax.swing.SwingConstants
import javax.swing.SwingUtilities
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.table.AbstractTableModel

private const val TABLE_CARD = "table"
private const val ERROR_CARD = "error"

/// Главное окно frontend-приложения для просмотра и редактирования семей.
class FamilyAdminFrame(private val store: FamilyStore, private val appIcon: Image? = null) : JFrame("Dobriy Shkaf")
{
    private val tableModel = FamiliesTableModel()
    private val table = JTable(tableModel)
    private val statusLabel = JLabel("Готово")
    private val refreshButton = JButton("Обновить")
    private val searchButton = JButton("Поиск")
    private val newButton = JButton("Новая")
    private val saveButton = JButton("Сохранить")
    private val deleteButton = JButton("Удалить")
    private val settingsButton = JButton(UiIcon.Settings)
    private val tableCards = JPanel(CardLayout())
    private val tableErrorMessage = JLabel("", SwingConstants.CENTER)
    private val formPanel = JPanel()
    private lateinit var contentSplitPane: JSplitPane
    private lateinit var formContainer: JPanel
    private var renderingState = false
    private var displayedOperationError: String? = null

    private val textInputs = mutableMapOf<FamilyField, Component>()
    private val booleanInputs = mutableMapOf<FamilyField, JCheckBox>()
    private val paymentInputs = mutableMapOf<FamilyField, JComboBox<PaymentType>>()

    /// Собирает окно, подписывает UI на store и запускает первичную загрузку.
    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        minimumSize = Dimension(1120, 720)
        appIcon?.let { iconImage = it }
        setLocationRelativeTo(null)

        contentPane = JPanel(BorderLayout(12, 12)).apply {
            border = BorderFactory.createEmptyBorder(12, 12, 12, 12)
            add(toolbar(), BorderLayout.NORTH)
            add(content(), BorderLayout.CENTER)
            add(statusLabel, BorderLayout.SOUTH)
        }

        wireIntents()
        buildForm()
        store.subscribe(::render)
        store.dispatch(FamilyIntent.LoadFamilies)
    }

    /// Верхняя панель основных действий.
    private fun toolbar(): JPanel = JPanel(BorderLayout()).apply {
        add(JPanel(FlowLayout(FlowLayout.LEFT, 8, 0)).apply {
            add(refreshButton)
            add(searchButton)
            add(newButton)
            add(saveButton)
            add(deleteButton)
        }, BorderLayout.WEST)

        add(JPanel(FlowLayout(FlowLayout.RIGHT, 0, 0)).apply {
            settingsButton.toolTipText = "Настройки"
            add(settingsButton)
        }, BorderLayout.EAST)
    }

    /// Основная область: таблица слева и форма редактирования справа.
    private fun content(): JSplitPane {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        table.rowHeight = 28
        table.autoCreateRowSorter = true
        table.fillsViewportHeight = true

        val tablePanel = JPanel(BorderLayout()).apply {
            tableCards.add(JScrollPane(table), TABLE_CARD)
            tableCards.add(errorPlaceholder(), ERROR_CARD)

            add(JLabel("Семьи", SwingConstants.LEFT).styledHeader(), BorderLayout.NORTH)
            add(tableCards, BorderLayout.CENTER)
        }

        formContainer = JPanel(BorderLayout()).apply {
            add(JLabel("Карточка семьи", SwingConstants.LEFT).styledHeader(), BorderLayout.NORTH)
            add(JScrollPane(formPanel), BorderLayout.CENTER)
        }

        contentSplitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePanel, formContainer).apply {
            resizeWeight = 0.44
            dividerLocation = 500
        }
        return contentSplitPane
    }

    /// Строит форму по описанию полей и группирует номиналы в раскрывающиеся блоки.
    private fun buildForm() {
        formPanel.layout = GridBagLayout()
        val groupedFields = donationFields + exchangeFields + balanceFields
        var row = 0

        FamilyField.entries.forEach { field ->
            when (field) {
                FamilyField.Donated100 -> { addDropdown(row++, "Дарение", donationFields) }
                FamilyField.Spent100 -> { addDropdown(row++, "Обмен", exchangeFields) }
                FamilyField.Balance100 -> { addDropdown(row++, "Остаток", balanceFields) }

                in groupedFields -> Unit
                else -> addFieldRow(row++, field)
            }
        }
    }

    /// Добавляет обычную строку формы.
    private fun addFieldRow(row: Int, field: FamilyField) {
        val constraints = GridBagConstraints().apply {
            gridx = 0
            gridy = row
            anchor = GridBagConstraints.NORTHWEST
            insets = Insets(6, 6, 6, 10)
        }

        formPanel.add(JLabel(field.title), constraints)

        val inputConstraints = GridBagConstraints().apply {
            gridx = 1
            gridy = row
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
            insets = Insets(6, 0, 6, 6)
        }

        formPanel.add(createInput(field), inputConstraints)
    }

    /// Добавляет раскрывающийся блок для группы однотипных числовых полей.
    private fun addDropdown(row: Int, title: String, fields: List<FamilyField>) {
        val content = JPanel(GridBagLayout()).apply {
            border = BorderFactory.createEmptyBorder(4, 8, 8, 8)
            fields.forEachIndexed { index, field ->
                add(JLabel(field.title.removePrefix("$title ")), GridBagConstraints().apply {
                    gridx = 0
                    gridy = index
                    anchor = GridBagConstraints.WEST
                    insets = Insets(4, 0, 4, 8)
                })
                add(createInput(field), GridBagConstraints().apply {
                    gridx = 1
                    gridy = index
                    weightx = 1.0
                    fill = GridBagConstraints.HORIZONTAL
                    insets = Insets(4, 0, 4, 0)
                })
            }
        }

        val toggle = JToggleButton(title).apply {
            isSelected = true
            icon = UiIcon.ChevronDown
            horizontalAlignment = SwingConstants.LEFT
            addActionListener {
                content.isVisible = isSelected
                icon = if (isSelected) UiIcon.ChevronDown else UiIcon.ChevronRight
                formPanel.revalidate()
                formPanel.repaint()
            }
        }

        val dropdown = JPanel(BorderLayout()).apply {
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(6, 0, 6, 6),
                BorderFactory.createLineBorder(Color(0xD5D9E0))
            )

            add(toggle, BorderLayout.NORTH)
            add(content, BorderLayout.CENTER)
        }

        formPanel.add(dropdown, GridBagConstraints().apply {
            gridx = 0
            gridy = row
            gridwidth = 2
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
            insets = Insets(6, 6, 6, 6)
        })
    }

    /// Создаёт подходящий Swing-компонент для типа поля.
    private fun createInput(field: FamilyField): Component = when (field.kind) {
        FamilyField.Kind.Boolean -> JCheckBox().also { checkbox ->
            booleanInputs[field] = checkbox
            checkbox.addActionListener {
                if (!renderingState) store.dispatch(FamilyIntent.ChangeBooleanField(field, checkbox.isSelected))
            }
        }
        FamilyField.Kind.MultilineText -> JTextArea(3, 34).apply {
            lineWrap = true
            wrapStyleWord = true
            document.addDocumentListener(textListener(field, this))
        }.let { area ->
            JScrollPane(area).also {
                it.preferredSize = Dimension(360, 78)
                textInputs[field] = area
            }
        }
        FamilyField.Kind.Date -> DatePickerInput { value ->
            if (!renderingState) store.dispatch(FamilyIntent.ChangeTextField(field, value))
        }.also { dateInput ->
            textInputs[field] = dateInput
        }
        FamilyField.Kind.Payment -> JComboBox(PaymentType.entries.toTypedArray()).apply {
            renderer = PaymentTypeRenderer()
            selectedItem = null
            addActionListener {
                if (!renderingState) {
                    val value = (selectedItem as? PaymentType)?.title.orEmpty()
                    store.dispatch(FamilyIntent.ChangeTextField(field, value))
                }
            }
            paymentInputs[field] = this
        }
        else -> JTextField(34).apply {
            toolTipText = when (field.kind) {
                FamilyField.Kind.Number -> "Только целое число"
                else -> null
            }

            document.addDocumentListener(textListener(field, this))
            textInputs[field] = this
        }
    }

    /// Связывает действия пользователя с MVI-intent.
    private fun wireIntents() {
        refreshButton.addActionListener { store.dispatch(FamilyIntent.LoadFamilies) }

        searchButton.addActionListener { showFamilySearchDialog() }

        newButton.addActionListener {
            table.clearSelection()
            store.dispatch(FamilyIntent.CreateDraft)
        }

        saveButton.addActionListener { store.dispatch(FamilyIntent.SaveDraft) }

        settingsButton.addActionListener { showSettingsDialog() }

        deleteButton.addActionListener {
            val id = store.state.selectedId ?: return@addActionListener

            if (confirmDelete(id))
                store.dispatch(FamilyIntent.DeleteSelected)
        }

        table.selectionModel.addListSelectionListener {
            if (!it.valueIsAdjusting && !renderingState) {
                val viewRow = table.selectedRow
                val id = if (viewRow >= 0) tableModel.familyAt(table.convertRowIndexToModel(viewRow))?.id else null

                store.dispatch(FamilyIntent.SelectFamily(id))
            }
        }
    }

    /// Показывает заглушку ошибки вместо таблицы семей.
    private fun errorPlaceholder(): JPanel = JPanel(GridBagLayout()).apply {
        border = BorderFactory.createEmptyBorder(24, 24, 24, 24)
        add(JPanel(GridBagLayout()).apply {
            add(JLabel("Не удалось загрузить список семей", SwingConstants.CENTER).apply {
                font = font.deriveFont(Font.BOLD, 16f)
                foreground = Color(0xB00020)
            }, GridBagConstraints().apply {
                gridx = 0
                gridy = 0
                insets = Insets(0, 0, 8, 0)
            })
            add(tableErrorMessage.apply {
                foreground = Color(0x5D687A)
            }, GridBagConstraints().apply {
                gridx = 0
                gridy = 1
                fill = GridBagConstraints.HORIZONTAL
            })
        })
    }

    /// Открывает окно поиска семьи по номеру.
    private fun showFamilySearchDialog() {
        val rawValue = JOptionPane.showInputDialog(
            this,
            "Введите номер семьи:",
            "Поиск семьи",
            JOptionPane.PLAIN_MESSAGE
        ) ?: return

        val familyNumber = rawValue.trim().toIntOrNull()
        if (familyNumber == null) {
            JOptionPane.showMessageDialog(this, "Введите целое число.", "Поиск семьи", JOptionPane.PLAIN_MESSAGE)
            return
        }

        val family = store.state.families.firstOrNull { it.familyNumber == familyNumber }
        if (family == null) {
            JOptionPane.showMessageDialog(this, "Семья с номером $familyNumber не найдена.", "Поиск семьи", JOptionPane.PLAIN_MESSAGE)
            return
        }

        store.dispatch(FamilyIntent.SelectFamily(family.id))
        SwingUtilities.invokeLater(::scrollSelectedRowToVisible)
    }

    /// Открывает пустое окно настроек.
    private fun showSettingsDialog() {
        JOptionPane.showMessageDialog(this, "Настройки", "Настройки", JOptionPane.PLAIN_MESSAGE)
    }

    /// Показывает подтверждение удаления без иконки приложения в окне диалога.
    private fun confirmDelete(id: Long): Boolean {
        val optionPane = JOptionPane(
            "Удалить семью id=$id?",
            JOptionPane.PLAIN_MESSAGE,
            JOptionPane.YES_NO_OPTION)

        val dialog = optionPane.createDialog(this, "Подтверждение")
        dialog.iconImages = emptyList<Image>()
        dialog.isVisible = true
        dialog.dispose()

        return optionPane.value == JOptionPane.YES_OPTION
    }

    private fun scrollSelectedRowToVisible() {
        val row = table.selectedRow
        if (row >= 0) table.scrollRectToVisible(table.getCellRect(row, 0, true))
    }

    /// Отрисовывает новое состояние экрана.
    private fun render(state: FamilyState) {
        renderingState = true
        tableModel.submit(state.families)

        val selectedModelIndex = state.families.indexOfFirst { it.id == state.selectedId }
        if (selectedModelIndex >= 0) {
            val selectedViewIndex = table.convertRowIndexToView(selectedModelIndex)

            if (table.selectedRow != selectedViewIndex) table.selectionModel.setSelectionInterval(selectedViewIndex, selectedViewIndex)
        } else table.clearSelection()


        renderDraft(state.draft)
        val busy = state.isLoading || state.isSaving
        val hasLoadError = state.error != null
        renderTableError(state.error)
        refreshButton.isEnabled = !busy
        searchButton.isEnabled = !busy && !hasLoadError && state.families.isNotEmpty()
        newButton.isEnabled = !busy && !hasLoadError
        saveButton.isEnabled = !busy && !hasLoadError
        deleteButton.isEnabled = !busy && !hasLoadError && state.selectedId != null
        settingsButton.isEnabled = true
        statusLabel.foreground = Color(0x2F6F44)
        statusLabel.text = when {
            state.isLoading -> "Загрузка..."
            state.isSaving -> "Сохранение..."
            else -> "Записей: ${state.families.size} | Размер БД: ${state.databaseSizeBytes.asFileSize()} | Версия: ${AppInfo.version}"
        }
        renderingState = false
        renderOperationError(state.operationError)
    }

    private fun renderTableError(error: String?) {
        val layout = tableCards.layout as CardLayout
        if (error == null) {
            layout.show(tableCards, TABLE_CARD)
            showFormContainer()
            return
        }

        tableErrorMessage.text = "<html><div style='text-align:center;width:360px;'>${error.escapeHtml()}</div></html>"
        layout.show(tableCards, ERROR_CARD)
        hideFormContainer()
    }

    private fun showFormContainer() {
        if (contentSplitPane.rightComponent === formContainer) return

        contentSplitPane.rightComponent = formContainer
        contentSplitPane.resizeWeight = 0.44
        contentSplitPane.dividerLocation = 500
        contentSplitPane.revalidate()
        contentSplitPane.repaint()
    }

    private fun hideFormContainer() {
        if (contentSplitPane.rightComponent == null) return

        contentSplitPane.rightComponent = null
        contentSplitPane.resizeWeight = 1.0
        contentSplitPane.dividerLocation = contentSplitPane.width
        contentSplitPane.revalidate()
        contentSplitPane.repaint()
    }

    private fun renderOperationError(error: String?) {
        if (error == null) {
            displayedOperationError = null
            return
        }

        if (displayedOperationError == error) return
        displayedOperationError = error

        SwingUtilities.invokeLater {
            JOptionPane.showMessageDialog(this, error, "Ошибка", JOptionPane.PLAIN_MESSAGE)
            store.dispatch(FamilyIntent.ClearError)
        }
    }

    /// Заполняет форму значениями выбранной или новой семьи.
    private fun renderDraft(family: Family) {
        FamilyField.entries.forEach { field ->
            val value = family.valueOf(field)
            booleanInputs[field]?.isSelected = value as? Boolean ?: false
            paymentInputs[field]?.selectedItem = PaymentType.fromTitle(value as? String)
            when (val input = textInputs[field]) {
                is JTextField -> if (input.text != value.asText()) input.text = value.asText()
                is JTextArea -> if (input.text != value.asText()) input.text = value.asText()
                is DatePickerInput -> input.setDateText(value.asText())
            }
        }
    }

    /// Создаёт слушатель текстового ввода, который отправляет изменения в store.
    private fun textListener(field: FamilyField, component: Component): DocumentListener =
        object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) = changed()
            override fun removeUpdate(e: DocumentEvent) = changed()
            override fun changedUpdate(e: DocumentEvent) = changed()

            private fun changed() {
                if (renderingState) return
                val value = when (component) {
                    is JTextField -> component.text
                    is JTextArea -> component.text
                    else -> return
                }
                store.dispatch(FamilyIntent.ChangeTextField(field, value))
            }
        }

    private fun JLabel.styledHeader(): JLabel = apply {
        font = font.deriveFont(Font.BOLD, 16f)
        border = BorderFactory.createEmptyBorder(0, 0, 8, 0)
    }

    private fun Any?.asText(): String = when (this) {
        null -> ""
        is LocalDate -> toString()
        else -> toString()
    }

    private fun Long?.asFileSize(): String {
        val bytes = this ?: return "неизвестно"
        if (bytes < 1024) return "$bytes Б"

        val units = listOf("КБ", "МБ", "ГБ")
        var value = bytes.toDouble()
        var unitIndex = -1
        while (value >= 1024 && unitIndex < units.lastIndex) {
            value /= 1024
            unitIndex++
        }

        return "%.1f %s".format(value, units[unitIndex])
    }

    private fun String.escapeHtml(): String =
        replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
}

/// Простой Swing-виджет выбора даты без внешних зависимостей.
private class DatePickerInput(private val onChange: (String) -> Unit) : JPanel(BorderLayout(4, 0))
{
    private val textField = JTextField(22)
    private val selectButton = JButton(UiIcon.Calendar)
    private val clearButton = JButton(UiIcon.Close)
    private var visibleMonth = YearMonth.now()

    /// Настраивает поле даты, кнопку выбора и кнопку очистки.
    init {
        textField.isEditable = false
        textField.background = Color.WHITE
        selectButton.toolTipText = "Выбрать дату"
        clearButton.toolTipText = "Очистить дату"

        add(textField, BorderLayout.CENTER)
        add(JPanel(FlowLayout(FlowLayout.RIGHT, 2, 0)).apply {
            add(selectButton)
            add(clearButton)
        }, BorderLayout.EAST)

        selectButton.addActionListener { showCalendar() }
        clearButton.addActionListener { onChange("") }
    }

    /// Обновляет текст выбранной даты без повторной отправки intent.
    fun setDateText(value: String) {
        if (textField.text != value) {
            textField.text = value
        }
        visibleMonth = runCatching { YearMonth.from(LocalDate.parse(value)) }
            .getOrElse { YearMonth.now() }
    }

    /// Показывает всплывающий календарь.
    private fun showCalendar() {
        val popup = JPopupMenu()
        lateinit var rebuild: () -> Unit

        rebuild = {
            popup.removeAll()
            popup.add(calendarPanel(popup, rebuild))
            popup.pack()
        }

        rebuild()
        popup.show(this, 0, height)
    }

    /// Создаёт календарную сетку для текущего месяца.
    private fun calendarPanel(popup: JPopupMenu, rebuild: () -> Unit): JPanel =
        JPanel(BorderLayout(6, 6)).apply {
            border = BorderFactory.createEmptyBorder(8, 8, 8, 8)
            add(JPanel(BorderLayout(4, 0)).apply {
                add(JButton(UiIcon.ChevronLeft).apply {
                    addActionListener {
                        visibleMonth = visibleMonth.minusMonths(1)
                        rebuild()
                    }
                }, BorderLayout.WEST)
                add(JLabel("%02d.%d".format(visibleMonth.monthValue, visibleMonth.year), SwingConstants.CENTER), BorderLayout.CENTER)
                add(JButton(UiIcon.ChevronRight).apply {
                    addActionListener {
                        visibleMonth = visibleMonth.plusMonths(1)
                        rebuild()
                    }
                }, BorderLayout.EAST)
            }, BorderLayout.NORTH)

            add(JPanel(GridLayout(0, 7, 4, 4)).apply {
                listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach {
                    add(JLabel(it, SwingConstants.CENTER))
                }

                repeat(visibleMonth.atDay(1).dayOfWeek.value - 1) {
                    add(JLabel(""))
                }

                for (day in 1..visibleMonth.lengthOfMonth()) {
                    val date = visibleMonth.atDay(day)
                    add(JButton(day.toString()).apply {
                        margin = Insets(2, 4, 2, 4)
                        addActionListener {
                            onChange(date.toString())
                            popup.isVisible = false
                        }
                    })
                }
            }, BorderLayout.CENTER)
        }
}

/// Отрисовщик enum оплаты с русскими названиями.
private class PaymentTypeRenderer : javax.swing.DefaultListCellRenderer() {
    override fun getListCellRendererComponent(
        list: javax.swing.JList<*>?,
        value: Any?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        val component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
        text = (value as? PaymentType)?.title.orEmpty()
        return component
    }
}

/// Набор простых векторных иконок для Swing-кнопок.
private enum class UiIcon : Icon {
    ChevronLeft,
    ChevronRight,
    ChevronDown,
    Close,
    Calendar,
    Settings;

    override fun getIconWidth(): Int = 16

    override fun getIconHeight(): Int = 16

    override fun paintIcon(component: Component?, graphics: Graphics, x: Int, y: Int) {
        val g = graphics.create() as Graphics2D
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.color = component?.foreground ?: Color.DARK_GRAY
        g.stroke = BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)

        when (this) {
            ChevronLeft -> {
                g.drawLine(x + 10, y + 4, x + 6, y + 8)
                g.drawLine(x + 6, y + 8, x + 10, y + 12)
            }
            ChevronRight -> {
                g.drawLine(x + 6, y + 4, x + 10, y + 8)
                g.drawLine(x + 10, y + 8, x + 6, y + 12)
            }
            ChevronDown -> {
                g.drawLine(x + 4, y + 6, x + 8, y + 10)
                g.drawLine(x + 8, y + 10, x + 12, y + 6)
            }
            Close -> {
                g.drawLine(x + 5, y + 5, x + 11, y + 11)
                g.drawLine(x + 11, y + 5, x + 5, y + 11)
            }
            Calendar -> {
                g.drawRoundRect(x + 3, y + 4, 10, 9, 2, 2)
                g.drawLine(x + 3, y + 7, x + 13, y + 7)
                g.drawLine(x + 6, y + 3, x + 6, y + 5)
                g.drawLine(x + 10, y + 3, x + 10, y + 5)
            }
            Settings -> {
                g.drawOval(x + 5, y + 5, 6, 6)
                g.drawLine(x + 8, y + 2, x + 8, y + 4)
                g.drawLine(x + 8, y + 12, x + 8, y + 14)
                g.drawLine(x + 2, y + 8, x + 4, y + 8)
                g.drawLine(x + 12, y + 8, x + 14, y + 8)
                g.drawLine(x + 4, y + 4, x + 5, y + 5)
                g.drawLine(x + 11, y + 11, x + 12, y + 12)
                g.drawLine(x + 12, y + 4, x + 11, y + 5)
                g.drawLine(x + 5, y + 11, x + 4, y + 12)
            }
        }

        g.dispose()
    }
}

/// Табличная модель для списка семей.
private class FamiliesTableModel : AbstractTableModel() {
    private val columns = listOf("ID", "Номер", "Родители", "Телефон", "Остаток")
    private var families: List<Family> = emptyList()

    override fun getRowCount(): Int = families.size

    override fun getColumnCount(): Int = columns.size

    override fun getColumnName(column: Int): String = columns[column]

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any = families[rowIndex].let { family ->
        when (columnIndex) {
            0 -> family.id ?: ""
            1 -> family.familyNumber ?: ""
            2 -> family.parentsName ?: ""
            3 -> family.phone ?: ""
            4 -> family.balancePointsSum ?: ""
            else -> ""
        }
    }

    fun submit(nextFamilies: List<Family>) {
        families = nextFamilies
        fireTableDataChanged()
    }

    fun familyAt(row: Int): Family? = families.getOrNull(row)
}

private val donationFields = listOf(FamilyField.Donated100, FamilyField.Donated200, FamilyField.Donated300, FamilyField.Donated400, FamilyField.Donated500, FamilyField.Donated1000)

private val exchangeFields = listOf(FamilyField.Spent100, FamilyField.Spent200, FamilyField.Spent300, FamilyField.Spent400, FamilyField.Spent500, FamilyField.Spent1000)

private val balanceFields = listOf(FamilyField.Balance100, FamilyField.Balance200, FamilyField.Balance300, FamilyField.Balance400, FamilyField.Balance500, FamilyField.Balance1000)
