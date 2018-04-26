package com.hoc.kotlin

import com.hoc.kotlin.Constants.FONT
import kotlinx.coroutines.experimental.Job
import org.netbeans.lib.awtextra.AbsoluteConstraints
import org.netbeans.lib.awtextra.AbsoluteLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.lang.Math.*
import javax.swing.*
import javax.swing.border.LineBorder
import kotlin.math.pow
import com.hoc.kotlin.Point.Type as PointType

/**
 * Created by Peter Hoc on 26/04/2018
 */


class MainFrame : JFrame(), Contract.View {
    private val popup = JPopupMenu()
    private val point = Point(-1, -1, PointType.EMPTY)
    private lateinit var btnFindPath: JButton
    private lateinit var cbDiagonalMovement: JCheckBox
    private lateinit var cbDontCrossCorner: JCheckBox
    private lateinit var cbbAlgorithm: JComboBox<String>
    private lateinit var cbbHeuristic: JComboBox<String>
    private lateinit var graphicPanel: GraphicPanel
    private lateinit var lblStatus: JLabel
    private var job: Job? = null
    private var searchAlgorithm: SearchAlgorithm? = null

    init {
        initComponents()
        this.setLocationRelativeTo(null)
        val itemStart = JMenuItem("Set begin point")
        popup.add(itemStart)
        val itemEnd = JMenuItem("Set end point")
        popup.add(itemEnd)
        itemStart.addActionListener {
            graphicPanel.begin = point
            graphicPanel.repaint()
        }
        itemEnd.addActionListener {
            graphicPanel.end = point
            graphicPanel.repaint()
        }
    }

    private fun initComponents() {
        graphicPanel = GraphicPanel()
        lblStatus = JLabel()
        btnFindPath = JButton()
        cbbAlgorithm = JComboBox()
        cbDontCrossCorner = JCheckBox()
        cbDiagonalMovement = JCheckBox()
        cbbHeuristic = JComboBox()
        val jLabel1 = JLabel()
        val jLabel2 = JLabel()
        val lblStart = JLabel()
        val jLabel3 = JLabel()
        val jLabel4 = JLabel()
        val jLabel5 = JLabel()
        val jLabel6 = JLabel()
        val lblStart2 = JLabel()
        val jPanel1 = JPanel()
        val btnClear = JButton()

        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        title = "AStar / Dijkstra / BFS"
        isAlwaysOnTop = true
        background = Color(204, 255, 204)
        preferredSize = Dimension(776, 550)
        isResizable = false

        graphicPanel.run {
            background = Color.white
            border = LineBorder(Color(0, 0, 0), 1, true)
            preferredSize = Dimension(PANEL_WIDTH, PANEL_HEIGHT)
            addMouseMotionListener(object : MouseMotionAdapter() {
                override fun mouseDragged(evt: MouseEvent) {
                    graphicPanelMouseDragged(evt)
                }
            })
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(evt: MouseEvent) {
                    graphicPanelMouseClicked(evt)
                }
            })
        }

        val graphicPanelLayout = GroupLayout(graphicPanel)
        graphicPanel.layout = graphicPanelLayout
        graphicPanelLayout.setHorizontalGroup(
                graphicPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 498, java.lang.Short.MAX_VALUE.toInt())
        )
        graphicPanelLayout.setVerticalGroup(
                graphicPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 498, java.lang.Short.MAX_VALUE.toInt())
        )

        jPanel1.run {
            background = Color(39, 40, 34)
            border = LineBorder(Color(0, 102, 102), 1, true)
            layout = AbsoluteLayout()
        }

        lblStatus.run {
            background = Color.white
            font = FONT
            horizontalAlignment = SwingConstants.CENTER
            border = LineBorder(Color(0, 0, 0), 1, true)
            isFocusable = false
            horizontalTextPosition = SwingConstants.CENTER
            isOpaque = true
        }
        jPanel1.add(lblStatus, AbsoluteConstraints(8, 10, 222, 40))

        btnFindPath.text = "Find path"
        btnFindPath.addActionListener { btnFindPathActionPerformed() }
        jPanel1.add(btnFindPath, AbsoluteConstraints(130, 300, 90, 30))

        btnClear.text = "Clear"
        btnClear.addActionListener { btnClearActionPerformed() }
        jPanel1.add(btnClear, AbsoluteConstraints(20, 300, 100, 30))

        cbbAlgorithm.run {
            font = FONT
            model = DefaultComboBoxModel(arrayOf(ASTAR, DIJKSTRA, GREEDY_BEST_FIRST))
            toolTipText = "Choose algorithm"
            autoscrolls = true
        }
        jPanel1.add(cbbAlgorithm, AbsoluteConstraints(90, 70, 140, 40))

        cbDontCrossCorner.run {
            font = FONT
            foreground = Color.white
            isSelected = true
            text = "Don't cross corner"
            border = LineBorder(Color(0, 0, 0), 1, true)
            horizontalAlignment = SwingConstants.CENTER
            isOpaque = false
        }
        jPanel1.add(cbDontCrossCorner, AbsoluteConstraints(20, 200, 200, 20))

        cbDiagonalMovement.run {
            font = FONT
            foreground = Color.white
            isSelected = true
            text = "Diagonal movement"
            border = LineBorder(Color(0, 0, 0), 1, true)
            horizontalAlignment = SwingConstants.CENTER
            isOpaque = false
        }
        jPanel1.add(cbDiagonalMovement, AbsoluteConstraints(20, 250, 192, -1))

        cbbHeuristic.model = DefaultComboBoxModel<String>(arrayOf(EUCLIDEAN, MANHATTAN, OCTAGONAL))
        jPanel1.add(cbbHeuristic, AbsoluteConstraints(90, 130, 140, 40))

        jLabel1.run {
            font = FONT
            foreground = Color.white
            horizontalAlignment = SwingConstants.LEFT
            text = "Distance"
        }
        jPanel1.add(jLabel1, AbsoluteConstraints(10, 130, 90, 40))

        jLabel2.run {
            font = FONT
            foreground = Color.white
            horizontalAlignment = SwingConstants.LEFT
            text = "Algorithm"
        }
        jPanel1.add(jLabel2, AbsoluteConstraints(10, 70, 80, 40))

        lblStart.icon = ImageIcon(javaClass.getResource(START_IMAGE))
        jPanel1.add(lblStart, AbsoluteConstraints(20, 350, 37, 35))

        jLabel3.run {
            font = FONT
            foreground = Color.white
            text = "Path"
        }
        jPanel1.add(jLabel3, AbsoluteConstraints(80, 460, 90, -1))

        jLabel4.run {
            font = FONT
            foreground = Color.white
            text = "Start point"
        }
        jPanel1.add(jLabel4, AbsoluteConstraints(80, 360, 90, -1))

        jLabel5.icon = ImageIcon(javaClass.getResource(PATH_IMAGE))
        jPanel1.add(jLabel5, AbsoluteConstraints(20, 450, 37, 35))

        jLabel6.run {
            font = FONT
            foreground = Color.white
            text = "End point"
        }
        jPanel1.add(jLabel6, AbsoluteConstraints(80, 410, 90, -1))

        lblStart2.icon = ImageIcon(javaClass.getResource(END_IMAGE))
        jPanel1.add(lblStart2, AbsoluteConstraints(20, 400, 37, 35))

        val layout = GroupLayout(contentPane)
        contentPane.layout = layout
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(graphicPanel, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, java.lang.Short.MAX_VALUE.toInt()))
        )
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(11, 11, 11)
                                                .addComponent(graphicPanel, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(10, 10, 10)
                                                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, java.lang.Short.MAX_VALUE.toInt()))
        )
        pack()
    }

    private fun btnFindPathActionPerformed() {//GEN-FIRST:event_btnFindPathActionPerformed
        val begin = graphicPanel.begin
        val end = graphicPanel.end
        when {
            begin === null && end === null -> JOptionPane.showMessageDialog(rootPane, "No begin point and no end point", "Error", JOptionPane.ERROR_MESSAGE)
            end === null -> JOptionPane.showMessageDialog(rootPane, "No end point", "Error", JOptionPane.ERROR_MESSAGE)
            begin === null -> JOptionPane.showMessageDialog(rootPane, "No begin point", "Error", JOptionPane.ERROR_MESSAGE)
            else -> {
                setViewEnable(false)
                lblStatus.text = "Finding..."

                val hFunc = H_FUNCTIONS[cbbHeuristic.selectedItem]!!
                searchAlgorithm = when (cbbAlgorithm.selectedItem) {
                    ASTAR -> {
                        AStar(this, hFunc)
                    }
                    DIJKSTRA -> {
                        Dijkstra(this, hFunc)
                    }
                    else -> {
                        GreedyBestFirst(this, hFunc)
                    }
                }.apply {
                    isDiagonalMovement = cbDiagonalMovement.isSelected
                    isNotCrossCorner = cbDontCrossCorner.isSelected
                    job = run(begin, end, graphicPanel.walls)
                }
            }
        }
    }

    private fun btnClearActionPerformed() {
        job?.cancel()
        setViewEnable(true)
        graphicPanel.clear()
        graphicPanel.repaint()
        lblStatus.text = ""
    }

    private fun graphicPanelMouseClicked(evt: MouseEvent) {
        val x = evt.x / CELL_SIZE
        val y = evt.y / CELL_SIZE

        if (SwingUtilities.isLeftMouseButton(evt)) {
            if (graphicPanel[x, y].type !== PointType.WALL) {
                graphicPanel.addWall(x, y)
            } else {
                graphicPanel.removeWall(x, y)
            }
            graphicPanel.repaint()
        } else if (SwingUtilities.isRightMouseButton(evt)) {
            popup.show(rootPane, evt.x, evt.y)
            point.x = x
            point.y = y
        }
    }

    private fun graphicPanelMouseDragged(evt: MouseEvent) {
        val x = evt.x / CELL_SIZE
        val y = evt.y / CELL_SIZE
        graphicPanel.addWall(x, y)
        graphicPanel.repaint()
    }

    private fun setViewEnable(isEnable: Boolean) {
        cbbHeuristic.isEnabled = isEnable
        cbDiagonalMovement.isEnabled = isEnable
        cbDontCrossCorner.isEnabled = isEnable
        btnFindPath.isEnabled = isEnable
        cbbAlgorithm.isEnabled = isEnable
    }

    override fun changePointType(p: Point?, type: Point.Type) {
        if (p !== null) graphicPanel[p].type = type
    }

    override fun showMessage(message: String) {
        lblStatus.text = message
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            UIManager.getInstalledLookAndFeels()
                    .first { it.name == "Nimbus" }
                    .let {
                        UIManager.setLookAndFeel(it.className)
                    }
            SwingUtilities.invokeLater { MainFrame().isVisible = true }
        }

        private val H_FUNCTIONS = hashMapOf<String, HeuristicFunction>(
                EUCLIDEAN to { p1, p2 ->
                    val p = (p1.x - p2.x).toDouble().pow(2)
                    val q = (p1.y - p2.y).toDouble().pow(2)
                    sqrt(p + q)
                },

                MANHATTAN to { p1, p2 -> (abs(p1.x - p2.x) + abs(p1.y - p2.y)).toDouble() },

                OCTAGONAL to { p1, p2 ->
                    val deltaX = abs(p1.x - p2.x)
                    val deltaY = abs(p1.y - p2.y)
                    deltaX + deltaY - 0.6 * min(deltaX, deltaY)
                }
        )
    }
}
