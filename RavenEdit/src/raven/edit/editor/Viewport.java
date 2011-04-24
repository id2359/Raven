package raven.edit.editor;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import raven.edit.tools.EditorTool;
import raven.game.RavenMap;
import raven.math.Vector2D;
import raven.math.Wall2D;

public class Viewport extends JPanel {
	private ViewportDelegate delegate;
	
	// Editor variables
	private float scrollSpeed = 10;
	private Vector2D levelPosition = new Vector2D(0,0); 
	
	private RavenMap level;
	
	// Drawing variables
	
	private EditorTool tool;

	private boolean drawGrid;
	
	public Viewport(RavenMap level) {
		this.level = level;
		
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		setDoubleBuffered(true);
//		enableEvents( AWTEvent.KEY_EVENT_MASK |
//					  AWTEvent.MOUSE_EVENT_MASK |
//					  AWTEvent.MOUSE_MOTION_EVENT_MASK |
//					  AWTEvent.MOUSE_WHEEL_EVENT_MASK );
		requestFocus();
		
	}
	
	public void setDelegate(ViewportDelegate delegate) {
		this.delegate = delegate;
	}

	public ViewportDelegate getDelegate() {
		return delegate;
	}

	@Override
	protected void paintComponent(Graphics g) {
		// Clear out background
		super.paintComponent(g);

		Line2D line;
		
		// Create an antialiased Graphics2D context
		Graphics2D g2d = (Graphics2D)g;
		RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHints(renderHints);

		// Draw the grid
		if (drawGrid) {
			for (int y = 0; y < getHeight(); y += 10) {
				line = new Line2D.Float(0, y, getWidth(), y);
			}
			for (int x = 0; x < getHeight(); x += 10) {
				line = new Line2D.Float(x, 0, x, getHeight());
			}
		}
		
		// Draw the origin
/*		Vector2D origin = new Vector2D(0,0);
		Vector2D origin_x1 = new Vector2D(origin);
		Vector2D origin_x2 = new Vector2D(origin);
		Vector2D origin_y1 = new Vector2D(origin);
		Vector2D origin_y2 = new Vector2D(origin);
		origin_x1.add(new Vector2D( 10f,  0f));
		origin_x2.add(new Vector2D(-10f,  0f));
		origin_y1.add(new Vector2D(  0f, 10f));
		origin_y2.add(new Vector2D(  0f,-10f));
		g2d.setColor(Color.RED);
		line = new Line2D.Float(levelToView(origin_x1), levelToView(origin_x2));
		g2d.draw(line);
		g2d.setColor(Color.GREEN);
		line = new Line2D.Float(levelToView(origin_y1), levelToView(origin_y2));
		g2d.draw(line);
*/		
		// Render the walls
		for (Wall2D wall : level.getWalls()) {
			g2d.setColor(Color.BLACK);
			line = new Line2D.Float(levelToView(wall.from()), levelToView(wall.to()));
			g2d.draw(line);
		}
		
		// Render the spawn points
		for (Vector2D point : level.getSpawnPoints()) {
			g2d.setPaint(Color.LIGHT_GRAY);
			g2d.fill(new Ellipse2D.Double(point.x - 7, point.y - 7, 14, 14));
		}
		
		tool.paintComponent(g);
	}

	/////////////////////////
	// Coordinate transform
	public Vector2D viewToLevel(Point viewCoords) {
		Vector2D result = new Vector2D(viewCoords.x, viewCoords.y);
		result.add(levelPosition);
		return result;		
	}
	
	public Point2D levelToView(Vector2D worldCoords) {
		Vector2D result = new Vector2D(worldCoords);
		result.sub(levelPosition);
		
		return new Point2D.Float((float)result.x, (float)result.y);
	}

	//////////////
	// Accessors

	public synchronized RavenMap getLevel() {
		return level;
	}

	public synchronized void setLevel(RavenMap level) {
		level = level;
		
		repaint();
	}

	public EditorTool getTool() {
		return tool;
	}

	public void setTool(EditorTool tool) {
		this.tool = tool;
	}
}