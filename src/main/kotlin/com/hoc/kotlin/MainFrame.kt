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
import java.util.*
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
    private var searchAlgorithm: ISearchAlgorithm? = null

    private lateinit var buttonFindPath: JButton
    private lateinit var popup: JPopupMenu
    private lateinit var cbDiagonalMovement: JCheckBox
    private lateinit var cbbDontCrossCorner: JCheckBox
    private lateinit var cbbAlgorithm: JComboBox<String>
    private lateinit var cbbHeuristic: JComboBox<String>
    private lateinit var graphicPanel: GraphicPanel
    private lateinit var labelStatus: JLabel
    private lateinit var buttonRandom: JButton
    private lateinit var textFieldPercent: JTextField
    private lateinit var labelVisited: JLabel
    private lateinit var labelTime: JLabel

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
        title = "AStar / Dijkstra / Best FS / BFS / DFS"
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
        labelStatus = JLabel()
        buttonFindPath = JButton()
        val btnClearPathAndWalls = JButton()
        cbbAlgorithm = JComboBox()
        cbbDontCrossCorner = JCheckBox()
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

        labelStatus.run {
            background = Color(255, 255, 255)
            font = FONT
            horizontalAlignment = SwingConstants.CENTER
            border = LineBorder(Color(0, 0, 0), 1, true)
            isFocusable = false
            horizontalTextPosition = SwingConstants.CENTER
            isOpaque = true
            jPanel2.add(this, AbsoluteConstraints(10, 10, 260, 26))
        }



        labelVisited = JLabel().apply {
            background = Color(255, 255, 255)
            font = FONT
            horizontalAlignment = SwingConstants.CENTER
            border = LineBorder(Color(0, 0, 0), 1, true)
            isFocusable = false
            horizontalTextPosition = SwingConstants.CENTER
            isOpaque = true
            jPanel2.add(this, AbsoluteConstraints(10, 36, 260, 26))
        }

        labelTime = JLabel().apply {
            background = Color(255, 255, 255)
            font = FONT
            horizontalAlignment = SwingConstants.CENTER
            border = LineBorder(Color(0, 0, 0), 1, true)
            isFocusable = false
            horizontalTextPosition = SwingConstants.CENTER
            isOpaque = true
            jPanel2.add(this, AbsoluteConstraints(10, 62, 260, 26))
        }


        buttonFindPath.font = buttonFont
        buttonFindPath.text = "Find path"
        buttonFindPath.addActionListener { btnFindPathActionPerformed() }
        jPanel2.add(buttonFindPath, AbsoluteConstraints(150, 340, 120, 40))

        btnClearPathAndWalls.run {
            font = buttonFont
            text = "<html>Clear path,<br/>walls</html>"
            addActionListener({ btnClearPathAndWallsActionPerformed() })
            jPanel2.add(this, AbsoluteConstraints(151, 400, 120, 40))
        }

        cbbAlgorithm.run {
            font = FONT
            model = DefaultComboBoxModel(arrayOf(ASTAR, DIJKSTRA, GREEDY_BEST_FIRST, BREADTH_FIRST_SEARCH, DEPTH_FIRST_SEARCH))
            toolTipText = "Choose algorithm"
            autoscrolls = true
            jPanel2.add(this, AbsoluteConstraints(100, 110, 170, 40))
            addActionListener {
                val selectedItem = cbbAlgorithm.selectedItem
                cbbHeuristic.isEnabled = selectedItem == ASTAR || selectedItem == GREEDY_BEST_FIRST
            }
        }

        cbbDontCrossCorner.run {
            font = FONT
            foreground = Color(255, 255, 255)
            isSelected = true
            text = "Don't cross corner"
            border = LineBorder(Color(0, 0, 0), 1, true)
            horizontalAlignment = SwingConstants.CENTER
            isOpaque = false
            jPanel2.add(this, AbsoluteConstraints(40, 240, 170, 20))
        }

        cbDiagonalMovement.run {
            font = FONT
            foreground = Color(255, 255, 255)
            isSelected = true
            text = "Allow diagonal movement"
            border = LineBorder(Color(0, 0, 0), 1, true)
            horizontalAlignment = SwingConstants.CENTER
            isOpaque = false
            jPanel2.add(this, AbsoluteConstraints(40, 290, 210, -1))
        }

        cbbHeuristic.font = FONT
        cbbHeuristic.model = DefaultComboBoxModel(arrayOf(EUCLIDEAN, MANHATTAN, OCTAGONAL))
        jPanel2.add(cbbHeuristic, AbsoluteConstraints(100, 170, 170, 40))

        jLabel1.run {
            font = FONT
            foreground = Color(255, 255, 255)
            horizontalAlignment = SwingConstants.LEFT
            text = "Distance"
            jPanel2.add(this, AbsoluteConstraints(10, 170, 90, 40))
        }

        jLabel2.run {
            font = FONT
            foreground = Color(255, 255, 255)
            horizontalAlignment = SwingConstants.LEFT
            text = "Algorithm"
            jPanel2.add(this, AbsoluteConstraints(10, 110, 90, 40))
        }
        JLabel().run {
            icon = ImageIcon(this@MainFrame.javaClass.getResource(START_IMAGE_PATH))
            jPanel2.add(this, AbsoluteConstraints(60, 470, 37, 35))
        }


        jLabel3.run {
            font = FONT
            foreground = Color(255, 255, 255)
            text = "Path"
            jPanel2.add(this, AbsoluteConstraints(120, 580, 90, -1))
        }

        buttonRandom = JButton().apply {
            font = buttonFont
            text = "Random"
            addActionListener { randomWall() }
            jPanel2.add(this, AbsoluteConstraints(90, 620, 120, 40))
        }

        textFieldPercent = JTextField().apply {
            font = FONT
            horizontalAlignment = JTextField.CENTER
            jPanel2.add(this, AbsoluteConstraints(90, 670, 120, 30))
        }

        jLabel4.run {
            font = FONT
            foreground = Color(255, 255, 255)
            text = "Start point"
            jPanel2.add(this, AbsoluteConstraints(120, 480, 90, -1))
        }

        JLabel().run {
            icon = ImageIcon(this@MainFrame.javaClass.getResource(PATH_IMAGE_PATH))
            jPanel2.add(this, AbsoluteConstraints(60, 570, 37, 35))
        }

        jLabel6.run {
            font = FONT
            foreground = Color(255, 255, 255)
            text = "End point"
            jPanel2.add(this, AbsoluteConstraints(120, 530, 90, -1))
        }

        JLabel().run {
            icon = ImageIcon(this@MainFrame.javaClass.getResource(END_IMAGE_PATH))
            jPanel2.add(this, AbsoluteConstraints(60, 520, 37, 35))
        }

        btnClear1.run {
            font = buttonFont
            text = "Clear all"
            addActionListener { btnClearActionPerformed() }
            jPanel2.add(this, AbsoluteConstraints(10, 340, 130, 40))
        }

        btnClearPath.run {
            font = buttonFont
            text = "Clear path"
            addActionListener { btnClearPathActionPerformed() }
            jPanel2.add(this, AbsoluteConstraints(10, 400, 130, 40))
        }

        contentPane.add(jPanel2, BorderLayout.EAST)

        pack()
    }

    private fun randomWall() {
        val p = textFieldPercent.text.toIntOrNull()
        if (p === null) {
            JOptionPane.showMessageDialog(rootPane, "Percent must be a number", "Input error", JOptionPane.ERROR_MESSAGE)
            return
        } else if (p !in 0..100) {
            JOptionPane.showMessageDialog(rootPane, "Percent must be in from 0 to 100", "Input error", JOptionPane.ERROR_MESSAGE)
            return
        }

        val random = Random()
        val seq = (1..rows).asSequence()

        val list = (1..cols).asSequence().flatMap { x ->
            seq.filter { random.nextDouble() > (100 - p) / 100.0 }
                    .map { x to it }
        }.toList()

        val percentWall = list.size.toDouble() / (rows * cols) * 100
        JOptionPane.showMessageDialog(rootPane, "Percent of walls: $percentWall")

        graphicPanel.walls = list
        graphicPanel.repaint()
    }

    private fun btnClearPathActionPerformed() {
        reset()
        graphicPanel.clearPath()
        graphicPanel.repaint()
    }

    private fun btnClearPathAndWallsActionPerformed() {
        reset()
        graphicPanel.clearPathAndWalls()
        graphicPanel.repaint()
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
                labelStatus.text = "Finding..."
                labelVisited.text = "Finding..."
                labelTime.text = "Finding..."

                val hFunc = H_FUNCTIONS[cbbHeuristic.selectedItem]!!
                searchAlgorithm = when (cbbAlgorithm.selectedItem) {
                    ASTAR -> AStar(this, hFunc, cols, rows)
                    DIJKSTRA -> Dijkstra(this, hFunc, cols, rows)
                    GREEDY_BEST_FIRST -> GreedyBestFirst(this, hFunc, cols, rows)
                    BREADTH_FIRST_SEARCH -> BFS(this, cols, rows)
                    DEPTH_FIRST_SEARCH -> DFS(this, cols, rows)
                    else -> return
                }.apply {
                    allowDiagonalMovement = cbDiagonalMovement.isSelected
                    notCrossCorner = cbbDontCrossCorner.isSelected
                    job?.cancel()
                    job = run(begin, end, graphicPanel.walls)
                }
            }
        }
    }

    private fun btnClearActionPerformed() {
        reset()
        graphicPanel.clearAll()
        graphicPanel.repaint()
    }

    private fun reset() {
        job?.cancel()
        setViewEnable(true)
        labelStatus.text = ""
        labelVisited.text = ""
        labelTime.text = ""
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
        cbbDontCrossCorner.isEnabled = isEnable
        buttonFindPath.isEnabled = isEnable
        cbbAlgorithm.isEnabled = isEnable
        buttonRandom.isEnabled = isEnable
    }

    override fun changePointType(x: Int, y: Int, type: PointType) {
        graphicPanel[x, y].type = type
    }

    override fun showMessage(pathInfo: String, visited: String, time: String) {
        labelStatus.text = pathInfo
        labelVisited.text = visited
        labelTime.text = time
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
