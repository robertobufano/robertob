package it.unisa.di.weblab.polyrec.gdt;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultRowSorter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.table.TableColumnModel;

import it.unisa.di.weblab.polyrec.Gesture;
import it.unisa.di.weblab.polyrec.GestureInfo;
import it.unisa.di.weblab.polyrec.Polyline;
import it.unisa.di.weblab.polyrec.TPoint;

public class TemplateScreenListener implements ActionListener, ItemListener {

	MainFrame mainClass;
	TemplateScreen templateScreen;

	public TemplateScreenListener(TemplateScreen templateScreen, MainFrame mainClass) {

		this.mainClass = mainClass;
		this.templateScreen = templateScreen;
	}

	public void actionPerformed(ActionEvent e) {
	
		templateScreen.canvas.removeMouseListener(templateScreen);
		templateScreen.canvas.removeMouseMotionListener(templateScreen);
		// pulisci tela canvas
		if (e.getSource() == templateScreen.clearCanvas) {
			templateScreen.gesturesPanel.removeAll();
			templateScreen.showOptions.removeAll();
			templateScreen.clearCanvas();
			return;
		}

		if (e.getSource() == templateScreen.drawGesture) {
			templateScreen.drawTemplate(templateScreen.mode, TemplateScreen.MOUSE, false);

			return;
		}
		if (e.getSource() == templateScreen.drawGestureBluetooth) {
			templateScreen.drawTemplate(templateScreen.mode, TemplateScreen.SMARTPHONE, false);

			return;
		}
		

		if (e.getSource() == templateScreen.saveGesture || e.getSource() == templateScreen.saveGestureRecognized) {
		
			templateScreen.currentGesture.setRotInv(templateScreen.rotInv.isSelected());
			templateScreen.currentGesture.setPointers((Integer)templateScreen.pointersNum.getValue());
			
			String className = ((JButton) e.getSource()).getName();
			templateScreen.currentGesture.setInfo(new GestureInfo(0, null, className, 0));
			int templateNumber = mainClass.getRecognizer().addTemplate(className, templateScreen.currentGesture);

			templateScreen.display.setText("Template added as additional version of " + templateScreen.className);

	
			
			TemplateScreen templateScreen;
			try {
				templateScreen = new TemplateScreen(mainClass);

				mainClass.setScreen(templateScreen);

				templateScreen.className = className;

				templateScreen.item = templateNumber - 1;
				templateScreen.saveGesture.setVisible(false);
				templateScreen.rotInv.setVisible(false);

				templateScreen.display.setText("Template saved in class " + templateScreen.className);

				templateScreen.showTemplate();
				if (TemplateScreen.waitThread != null)
					TemplateScreen.waitThread.setDraw(false);
			} catch (IOException e1) {

				e1.printStackTrace();
			}
			mainClass.getMenu().updateMenu();

			return;
		}
		if (e.getSource() instanceof JButton) {
			JButton button = (JButton) e.getSource();
			int buttonNumber;

			// mostra gesture template selezionato
			if (button.getName() != null && button.getName().startsWith("gesture")) {
				templateScreen.azzeraZoom();
				templateScreen.clearCanvas();
			
				buttonNumber = Integer.parseInt(button.getName().replace("gesture", ""));

				templateScreen.mode = TemplateScreen.GESTURE;
				templateScreen.item = buttonNumber;

				templateScreen.showGesture.setEnabled(true);
				templateScreen.showGesture.setSelected(true);

				templateScreen.showPolyline.setEnabled(true);
				templateScreen.showPolyline.setSelected(false);

				templateScreen.showVertex.setEnabled(true);
				templateScreen.showVertex.setSelected(false);

				templateScreen.showOptions.add(templateScreen.showGesture);
				templateScreen.showOptions.add(templateScreen.showPolyline);
				templateScreen.showOptions.add(templateScreen.showVertex);

				return;

			}

		}

		if (e.getSource() == templateScreen.showTimedGesture) {

			templateScreen.showPolyline.setSelected(false);
			templateScreen.showVertex.setSelected(false);
			templateScreen.mode = TemplateScreen.GESTURE_TIMED;

			templateScreen.draw(TemplateScreen.GESTURE);
			templateScreen.showGesture.setSelected(true);
			templateScreen.mode = TemplateScreen.GESTURE;

			return;
		}
		if (e.getSource() == templateScreen.features) {

			JTable table = getFeatures(
					mainClass.getRecognizer().getTemplate(templateScreen.className).get(templateScreen.item));
			JScrollPane scrollPane = new JScrollPane(table);
			scrollPane.setBorder(BorderFactory.createEmptyBorder());

			table.setOpaque(false);
			scrollPane.setPreferredSize(new Dimension(800, 300));
			scrollPane.setPreferredSize(new Dimension(800, Math.min(30+table.getRowCount()*16,300)));
			JOptionPane optionpanel = new JOptionPane(scrollPane);

			JDialog k = optionpanel.createDialog("Gesture Features");
			k.setModal(false); // Makes the dialog not modal
			k.setVisible(true);

			return;
		}
		// Rotation Invariant
		if (e.getSource() == templateScreen.rotInvCommand) {

			if (mainClass.getRecognizer().getTemplate(templateScreen.className).get(templateScreen.item).getGesture()
					.isRotInv()) {
				try {
					templateScreen.rotInvCommand
							.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/checkbox.png"))));
				} catch (IOException e1) {

					e1.printStackTrace();
				}
				mainClass.getRecognizer().getTemplate(templateScreen.className).get(templateScreen.item).getGesture()
						.setRotInv(false);
			
				templateScreen.display.setText("Template is now Not Rotation Invariant");

			} else {
				try {
					templateScreen.rotInvCommand
							.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/img/checked.png"))));
				} catch (IOException e1) {

					e1.printStackTrace();
				}
				mainClass.getRecognizer().getTemplate(templateScreen.className).get(templateScreen.item).getGesture()
						.setRotInv(true);
		
				templateScreen.display.setText("Template is now Rotation Invariant");
			}

			return;
		}
		// move right
		if (e.getSource() == templateScreen.right) {
			for (Map.Entry<Integer, Gesture> entry : templateScreen.canvasGestures.entrySet()) {
				List<TPoint> points = entry.getValue().getPoints();
				Gesture gesture = new Gesture();
				for (TPoint point : points)
					gesture.addPoint(new TPoint((point.x) + 10, point.y, point.time));
				templateScreen.canvasGestures.put(entry.getKey(), gesture);
			}

			templateScreen.repaint();
			return;

		}
		// move left
		if (e.getSource() == templateScreen.left) {
			for (Map.Entry<Integer, Gesture> entry : templateScreen.canvasGestures.entrySet()) {
				List<TPoint> points = entry.getValue().getPoints();
				Gesture gesture = new Gesture();
				for (TPoint point : points)
					gesture.addPoint(new TPoint((point.x) - 10, point.y, point.time));
				templateScreen.canvasGestures.put(entry.getKey(), gesture);
			}

			templateScreen.repaint();
			return;

		}
		// move up
		if (e.getSource() == templateScreen.up) {
			for (Map.Entry<Integer, Gesture> entry : templateScreen.canvasGestures.entrySet()) {
				List<TPoint> points = entry.getValue().getPoints();
				Gesture gesture = new Gesture();
				for (TPoint point : points)
					gesture.addPoint(new TPoint((point.x), point.y - 10, point.time));

				templateScreen.canvasGestures.put(entry.getKey(), gesture);
			}

			templateScreen.repaint();
			return;

		}
		// move down
		if (e.getSource() == templateScreen.down) {
			for (Map.Entry<Integer, Gesture> entry : templateScreen.canvasGestures.entrySet()) {
				List<TPoint> points = entry.getValue().getPoints();
				Gesture gesture = new Gesture();
				for (TPoint point : points)
					gesture.addPoint(new TPoint((point.x), point.y + 10, point.time));
				templateScreen.canvasGestures.put(entry.getKey(), gesture);
			}

			templateScreen.repaint();
			return;

		}
		// zoom in
		if (e.getSource() == templateScreen.zoomIn) {
			System.out.println("zooomingin");
			if (templateScreen.zoomLevel < templateScreen.slider.getMaximum()) {

				templateScreen.zoom += 1.0;

				templateScreen.zoomLevel = templateScreen.zoomLevel + 1;
				templateScreen.slider.setValue(templateScreen.zoomLevel);

				if (templateScreen.showGesture.isSelected())
					templateScreen.draw(TemplateScreen.GESTURE);
				if (templateScreen.showPolyline.isSelected())
					templateScreen.draw(TemplateScreen.POLYLINE);
				if (templateScreen.showVertex.isSelected())
					templateScreen.draw(TemplateScreen.VERTEX);

				templateScreen.repaint();
			}
			return;

		}
		// zoom out
		if (e.getSource() == templateScreen.zoomOut) {
			if (templateScreen.zoomLevel > templateScreen.slider.getMinimum()) {
				templateScreen.zoom -= 1.0;
				templateScreen.zoomLevel = templateScreen.zoomLevel - 1;
				templateScreen.slider.setValue(templateScreen.zoomLevel);

				if (templateScreen.showGesture.isSelected())
					templateScreen.draw(TemplateScreen.GESTURE);
				if (templateScreen.showPolyline.isSelected())
					templateScreen.draw(TemplateScreen.POLYLINE);
				if (templateScreen.showVertex.isSelected())
					templateScreen.draw(TemplateScreen.VERTEX);
				templateScreen.repaint();
			}

			return;

		}
		// rotate left
		if (e.getSource() == templateScreen.rotateleft) {

			templateScreen.rotationAngle -= 5;
			System.out.println(templateScreen.rotationAngle);
			if (templateScreen.showGesture.isSelected())
				templateScreen.draw(TemplateScreen.GESTURE);
			if (templateScreen.showPolyline.isSelected())
				templateScreen.draw(TemplateScreen.POLYLINE);
			if (templateScreen.showVertex.isSelected())
				templateScreen.draw(TemplateScreen.VERTEX);

			return;

		}
		// rotate right
		if (e.getSource() == templateScreen.rotateright) {

			templateScreen.rotationAngle += 5;
			System.out.println(templateScreen.rotationAngle);
			if (templateScreen.showGesture.isSelected())
				templateScreen.draw(TemplateScreen.GESTURE);
			if (templateScreen.showPolyline.isSelected())
				templateScreen.draw(TemplateScreen.POLYLINE);
			if (templateScreen.showVertex.isSelected())
				templateScreen.draw(TemplateScreen.VERTEX);

			return;

		}
		
		/*if (e.getSource() == detailScreen.mirror) {

			detailScreen.rotationAngle += 5;
			System.out.println(detailScreen.rotationAngle);
			if (detailScreen.showGesture.isSelected())
				detailScreen.draw(DetailScreen.GESTURE);
			if (detailScreen.showPolyline.isSelected())
				detailScreen.draw(DetailScreen.POLYLINE);
			if (detailScreen.showVertex.isSelected())
				detailScreen.draw(DetailScreen.VERTEX);

			return;

		}*/
		/*
		 * if(e.getSource()== detailScreen.scoreTableButton){
		 * 
		 * }
		 */

	}

	@Override
	public void itemStateChanged(ItemEvent e) {

		int cb = e.getStateChange();
		System.out
				.println("item state changed (value:" + cb + ") isSelected" + ((JCheckBox) e.getSource()).isSelected());
		// visualizza gesto

		if (e.getSource() == templateScreen.showGesture) {

			if (!templateScreen.showGesture.isSelected()) {

				templateScreen.canvasGestures.remove(TemplateScreen.GESTURE);

				templateScreen.canvas.paintGestures(null);

			} else {
				templateScreen.mode = TemplateScreen.GESTURE;
				templateScreen.draw(TemplateScreen.GESTURE);

			}

			return;
		}
		// visualizza polyline
		if (e.getSource() == templateScreen.showPolyline) {

			if (!templateScreen.showPolyline.isSelected()) {

				templateScreen.canvasGestures.remove(TemplateScreen.POLYLINE);

				templateScreen.canvas.paintGestures(null);

			} else {
				templateScreen.mode = TemplateScreen.POLYLINE;
				templateScreen.draw(TemplateScreen.POLYLINE);

			}

			return;
		}
		if (e.getSource() == templateScreen.showVertex) {
			if (!templateScreen.showVertex.isSelected()) {

				templateScreen.canvasGestures.remove(TemplateScreen.VERTEX);

				templateScreen.canvas.paintGestures(null);

			} else {
				templateScreen.mode = TemplateScreen.VERTEX;
				templateScreen.draw(TemplateScreen.VERTEX);

			}

			return;
		}

	}

	public void setDetailScreen(TemplateScreen templateScreen) {
		this.templateScreen = templateScreen;
	}

	
	
	/**
	 * Get features of a template
	 * @param polyline
	 * 		Polyline
	 * @return
	 */
	public JTable getFeatures(Polyline polyline) {
		Gesture normalizedGesture = polyline.getGesture().normalizedGesture(150, 150,0);

		
		double sumangle = 0;
		
		for (int i = 0; i < polyline.getNumVertexes(); i++) {

			sumangle += Math.abs(polyline.getSlopeChange(i));
		}
		

		Object[] columnHeaders = { "Class", "Template", "Polyline lines"/*"MYIsokoski"*/, "Curviness", "Length", "Dist First->Last","BBox Area",
				"BBox Diagonal", "BBox Angle","Cos(StartAngle)", "Sin(StartAngle)","Cos(EndAngle)", "GlobalOrientation(Angle First->Last)","Cos(Angle First->Last)","Sin(Angle First->Last)" };
		Object[][] rowData = new Object[1][columnHeaders.length];

	
		
		double f5 = Math.sqrt(Math.pow(normalizedGesture.getPoints().get(normalizedGesture.getPoints().size()-1).getX()-normalizedGesture.getPoints().get(0).getX(),2)+Math.pow(normalizedGesture.getPoints().get(normalizedGesture.getPoints().size()-1).getY()-normalizedGesture.getPoints().get(0).getY(),2));
		//angle between first and last point
		double globalOrientation = Polyline.getLineAngle(new TPoint((int) normalizedGesture.getBoundingBox().x, (int) normalizedGesture.getBoundingBox().y + (int) normalizedGesture.getBoundingBox().height,0),
				new TPoint((int) normalizedGesture.getBoundingBox().x+ (int) normalizedGesture.getBoundingBox().width,(int) normalizedGesture.getBoundingBox().y, 0));
		Object[] row = { 
				templateScreen.className, 
				String.valueOf(templateScreen.item), 
				polyline.getNumLines(), 
				round(sumangle, 2),
				round(normalizedGesture.getLength(), 2),
				round(f5, 2),
				round(normalizedGesture.getBoundingBox().height * normalizedGesture.getBoundingBox().width,	2),
				round(normalizedGesture.getDiagonal(), 2),
				round(Math.atan(normalizedGesture.getBoundingBox().height/normalizedGesture.getBoundingBox().width),2),
				round(Math.cos(polyline.getLineSlope(0)),2),//verificare problema linea punti
				round(Math.sin(polyline.getLineSlope(0)),2),//verificare problema linea punti
				round(Math.cos(polyline.getLineSlope(polyline.getNumLines() - 1)),2),//verificare problema linea punti
				round(globalOrientation,2),
				round(Math.cos(globalOrientation),2),
				round(Math.sin(globalOrientation),2),
				};
		rowData[0] = row;
	
		FeaturesTableModel mod = new FeaturesTableModel(rowData, columnHeaders);

		JTable table = new JTable(mod);
		table.setEnabled(false);
		table.setRowSelectionAllowed(true);
		table.setAutoCreateRowSorter(true);
		
		 table.setDefaultRenderer(String.class, new TableRenderer());
	        TableColumnModel tcm = table.getColumnModel();
	        FeaturesTableHeader header = new FeaturesTableHeader(tcm);
	        table.setTableHeader(header);
		

		DefaultRowSorter sorter = ((DefaultRowSorter) table.getRowSorter());
		ArrayList<SortKey> list = new ArrayList<SortKey>();
		list.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
		sorter.setSortKeys(list);
		sorter.sort();

		return table;

	}

	private double round(double n, double d) // round 'n' to 'd' decimals
	{
		d = Math.pow(10, d);
		return Math.round(n * d) / d;
	}

}
