package com.hoc.kotlin

import com.hoc.kotlin.Constants.FONT
import com.hoc.kotlin.swing.Swing
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.delay
import org.netbeans.lib.awtextra.AbsoluteConstraints
import org.netbeans.lib.awtextra.AbsoluteLayout
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.lang.Math.abs
import java.lang.Math.sqrt
import javax.swing.*
import javax.swing.border.BevelBorder
import javax.swing.border.LineBorder
import kotlin.math.pow


/**
 * Created by Peter Hoc on 26/04/2018
 */


class MainFrame : JFrame(), Contract.View {
    private val cols = COLS
    private val rows = ROWS
    private val cellSize = CELL_SIZE

    private val point = Point(-1, -1, PointType.EMPTY, cellSize)
    private var job: Job? = null
    private var searchAlgorithm: SearchAlgorithm? = null

    private lateinit var btnFindPath: JButton
    private lateinit var popup: JPopupMenu
    private lateinit var cbDiagonalMovement: JCheckBox
    private lateinit var cbDontCrossCorner: JCheckBox
    private lateinit var cbbAlgorithm: JComboBox<String>
    private lateinit var cbbHeuristic: JComboBox<String>
    private lateinit var graphicPanel: GraphicPanel
    private lateinit var lblStatus: JLabel

    private val actor = actor<MouseEvent>(context = Swing, capacity = Channel.CONFLATED) {
        for (evt in this) {
            graphicPanelMouseDragged(evt)
            delay(20)
            println("After delay")
        }
    }

    init {
        initComponents()
        setLocationRelativeTo(null)
    }

    private fun initComponents() {
        //Frame
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        title = "AStar / Dijkstra / BFS"
        isAlwaysOnTop = true
        preferredSize = Dimension(800, 600)
        isResizable = true

        //popup menu
        popup = JPopupMenu()
        JMenuItem("Set begin point").apply {
            addActionListener {
                graphicPanel.begin = point
                graphicPanel.repaint()
            }
        }.let(popup::add)
        JMenuItem("Set end point").apply {
            addActionListener {
                graphicPanel.end = point
                graphicPanel.repaint()
            }
        }.let(popup::add)


        val jPanel2 = JPanel()
        lblStatus = JLabel()
        btnFindPath = JButton()
        val btnClearPathAndWalls = JButton()
        cbbAlgorithm = JComboBox()
        cbDontCrossCorner = JCheckBox()
        cbDiagonalMovement = JCheckBox()
        cbbHeuristic = JComboBox()
        val jLabel1 = JLabel()
        val jLabel2 = JLabel()
        val jLabel3 = JLabel()
        val jLabel4 = JLabel()
        val jLabel6 = JLabel()
        val btnClear1 = JButton()
        val btnClearPath = JButton()
        val buttonFont = Font("Fira Code", 0, 11)


        graphicPanel = GraphicPanel(cols, rows, cellSize)
        graphicPanel.run {
            background = Color.white
            preferredSize = Dimension(PANEL_WIDTH, PANEL_HEIGHT)
            addMouseMotionListener(object : MouseMotionAdapter() {
                override fun mouseDragged(evt: MouseEvent) {
                    actor.offer(evt)

                }
            })
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(evt: MouseEvent) {
                    graphicPanelMouseClicked(evt)
                }
            })
        }
        val myPanel1Layout = GroupLayout(graphicPanel)
        graphicPanel.layout = myPanel1Layout
        myPanel1Layout.setHorizontalGroup(
                myPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 340, java.lang.Short.MAX_VALUE.toInt())
        )
        myPanel1Layout.setVerticalGroup(
                myPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 583, java.lang.Short.MAX_VALUE.toInt())
        )

        contentPane.add(graphicPanel, BorderLayout.CENTER)

        jPanel2.background = Color(33, 33, 33)
        jPanel2.border = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color(255, 255, 255), Color(255, 255, 255), null, null)
        jPanel2.foreground = Color(51, 51, 51)
        jPanel2.preferredSize = Dimension(280, 500)
        jPanel2.layout = AbsoluteLayout()

        lblStatus.run {
            background = Color(255, 255, 255)
            font = FONT
            horizontalAlignment = SwingConstants.CENTER
            border = LineBorder(Color(0, 0, 0), 1, true)
            isFocusable = false
            horizontalTextPosition = SwingConstants.CENTER
            isOpaque = true
        }
        jPanel2.add(lblStatus, AbsoluteConstraints(10, 10, 260, 40))

        btnFindPath.font = buttonFont
        btnFindPath.text = "Find path"
        btnFindPath.addActionListener { btnFindPathActionPerformed() }
        jPanel2.add(btnFindPath, AbsoluteConstraints(150, 300, 120, 40))

        btnClearPathAndWalls.run {
            font = buttonFont
            text = "<html>Clear path,<br/>walls</html>"
            addActionListener({ btnClearPathAndWallsActionPerformed() })
            jPanel2.add(this, AbsoluteConstraints(151, 360, 120, 40))
        }

        cbbAlgorithm.run {
            font = FONT
            model = DefaultComboBoxModel(arrayOf(ASTAR, DIJKSTRA, GREEDY_BEST_FIRST))
            toolTipText = "Choose algorithm"
            autoscrolls = true
            jPanel2.add(this, AbsoluteConstraints(100, 70, 170, 40))
        }

        cbDontCrossCorner.run {
            font = FONT
            foreground = Color(255, 255, 255)
            isSelected = true
            text = "Don't cross corner"
            border = LineBorder(Color(0, 0, 0), 1, true)
            horizontalAlignment = SwingConstants.CENTER
            isOpaque = false
            jPanel2.add(this, AbsoluteConstraints(40, 200, 170, 20))
        }

        cbDiagonalMovement.run {
            font = FONT
            foreground = Color(255, 255, 255)
            isSelected = true
            text = "Allow diagonal movement"
            border = LineBorder(Color(0, 0, 0), 1, true)
            horizontalAlignment = SwingConstants.CENTER
            isOpaque = false
            jPanel2.add(this, AbsoluteConstraints(40, 250, 210, -1))
        }

        cbbHeuristic.font = FONT
        cbbHeuristic.model = DefaultComboBoxModel(arrayOf(EUCLIDEAN, MANHATTAN, OCTAGONAL))
        jPanel2.add(cbbHeuristic, AbsoluteConstraints(100, 130, 170, 40))

        jLabel1.run {
            font = FONT
            foreground = Color(255, 255, 255)
            horizontalAlignment = SwingConstants.LEFT
            text = "Distance"
            jPanel2.add(this, AbsoluteConstraints(10, 130, 90, 40))
        }

        jLabel2.run {
            font = FONT
            foreground = Color(255, 255, 255)
            horizontalAlignment = SwingConstants.LEFT
            text = "Algorithm"
            jPanel2.add(this, AbsoluteConstraints(10, 70, 90, 40))
        }
        JLabel().run {
            icon = ImageIcon(this@MainFrame.javaClass.getResource(START_IMAGE_PATH))
            jPanel2.add(this, AbsoluteConstraints(60, 430, 37, 35))
        }


        jLabel3.run {
            font = FONT
            foreground = Color(255, 255, 255)
            text = "Path"
            jPanel2.add(this, AbsoluteConstraints(120, 540, 90, -1))
        }

        jLabel4.run {
            font = FONT
            foreground = Color(255, 255, 255)
            text = "Start point"
            jPanel2.add(this, AbsoluteConstraints(120, 440, 90, -1))
        }

        JLabel().run {
            icon = ImageIcon(this@MainFrame.javaClass.getResource(PATH_IMAGE_PATH))
            jPanel2.add(this, AbsoluteConstraints(60, 530, 37, 35))
        }

        jLabel6.run {
            font = FONT
            foreground = Color(255, 255, 255)
            text = "End point"
            jPanel2.add(this, AbsoluteConstraints(120, 490, 90, -1))
        }

        JLabel().run {
            icon = ImageIcon(this@MainFrame.javaClass.getResource(END_IMAGE_PATH))
            jPanel2.add(this, AbsoluteConstraints(60, 480, 37, 35))
        }

        btnClear1.run {
            font = buttonFont
            text = "Clear all"
            addActionListener { btnClearActionPerformed() }
            jPanel2.add(this, AbsoluteConstraints(10, 300, 130, 40))
        }

        btnClearPath.run {
            font = buttonFont
            text = "Clear path"
            addActionListener { btnClearPathActionPerformed() }
            jPanel2.add(this, AbsoluteConstraints(10, 360, 130, 40))
        }

        contentPane.add(jPanel2, BorderLayout.EAST)

        pack()
    }

    private fun btnClearPathActionPerformed() {
        job?.cancel()
        setViewEnable(true)
        graphicPanel.clearPath()
        graphicPanel.repaint()
        lblStatus.text = ""
    }

    private fun btnClearPathAndWallsActionPerformed() {
        job?.cancel()
        setViewEnable(true)
        graphicPanel.clearPathAndWalls()
        graphicPanel.repaint()
        lblStatus.text = ""
    }

    private fun btnFindPathActionPerformed() {
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
                    ASTAR -> AStar(this, hFunc, cols, rows)
                    DIJKSTRA -> Dijkstra(this, hFunc, cols, rows)
                    else -> GreedyBestFirst(this, hFunc, cols, rows)
                }.apply {
                    allowDiagonalMovement = cbDiagonalMovement.isSelected
                    notCrossCorner = cbDontCrossCorner.isSelected
                    job?.cancel()
                    job = run(begin, end, graphicPanel.walls)
                }
            }
        }
    }

    private fun btnClearActionPerformed() {
        job?.cancel()
        setViewEnable(true)
        graphicPanel.clearAll()
        graphicPanel.repaint()
        lblStatus.text = ""
    }

    private fun graphicPanelMouseClicked(evt: MouseEvent) {
        val job = job
        if (job !== null && job.isActive) return

        val x = evt.x / cellSize
        val y = evt.y / cellSize

        when {
            SwingUtilities.isLeftMouseButton(evt) -> {
                when (graphicPanel[x, y].type) {
                    PointType.WALL -> graphicPanel.removeWall(x, y)
                    else -> graphicPanel.addWall(x, y)
                }
                graphicPanel.repaint()
            }
            SwingUtilities.isRightMouseButton(evt) -> {
                popup.show(rootPane, evt.x, evt.y)
                point.x = x
                point.y = y
            }
        }
    }

    private fun graphicPanelMouseDragged(evt: MouseEvent) {
        println("Start")
        val job = job
        if (job !== null && job.isActive) return

        if (SwingUtilities.isLeftMouseButton(evt)) try {
            val x = evt.x / cellSize
            val y = evt.y / cellSize
            val point1 = graphicPanel[x, y]
            when (point1.type) {
                PointType.WALL -> graphicPanel.removeWall(x, y)
                else -> graphicPanel.addWall(x, y)
            }
            graphicPanel.repaint()
            println("Repaint")
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private fun setViewEnable(isEnable: Boolean) {
        cbbHeuristic.isEnabled = isEnable
        cbDiagonalMovement.isEnabled = isEnable
        cbDontCrossCorner.isEnabled = isEnable
        btnFindPath.isEnabled = isEnable
        cbbAlgorithm.isEnabled = isEnable
    }

    override fun changePointType(x: Int, y: Int, type: PointType) {
        graphicPanel[x, y].type = type
    }

    override fun showMessage(message: String) {
        lblStatus.text = message
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            UIManager.getInstalledLookAndFeels()
                    .firstOrNull { it.name == "Nimbus" }
                    ?.let { UIManager.setLookAndFeel(it.className) }
            SwingUtilities.invokeLater { MainFrame().isVisible = true }
        }

        private val H_FUNCTIONS = hashMapOf<String, HeuristicFunction>(
                EUCLIDEAN to { (x1, y1), (x2, y2) ->
                    val p = (x1 - x2).toDouble().pow(2)
                    val q = (y1 - y2).toDouble().pow(2)
                    sqrt(p + q)
                },

                MANHATTAN to { (x1, y1), (x2, y2) -> (abs(x1 - x2) + abs(y1 - y2)).toDouble() },

                OCTAGONAL to { (x1, y1), (x2, y2) ->
                    val deltaX = abs(x1 - x2)
                    val deltaY = abs(y1 - y2)
                    deltaX + deltaY - 0.6 * minOf(deltaX, deltaY)
                }
        )
    }
}
