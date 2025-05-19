package de.orat.math.view.euclidview3d.impl;

import de.orat.math.view.euclidview3d.ObjectLoader;
import de.orat.view3d.euclid3dviewapi.spi.iAABB;
import de.orat.view3d.euclid3dviewapi.spi.iEuclidViewer3D;
import de.orat.view3d.euclid3dviewapi.util.AxisAlignedBoundingBox;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import org.jogamp.vecmath.Matrix3d;
import org.jogamp.vecmath.Matrix4d;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Tuple3d;
import org.jogamp.vecmath.Vector3d;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.NewtCameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.IObjectPickedListener;
import org.jzy3d.chart.controllers.mouse.picking.NewtMousePickingController;
import org.jzy3d.chart.controllers.mouse.picking.PickingSupport;
import org.jzy3d.chart.factories.NewtChartFactory;
import org.jzy3d.chart.factories.SwingChartFactory;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Arrow;
import org.jzy3d.plot3d.primitives.ChessFloor;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.EuclidCube;
import org.jzy3d.plot3d.primitives.DrawableTypes;
import org.jzy3d.plot3d.primitives.EuclidCircle;
import org.jzy3d.plot3d.primitives.EuclidPart;
import org.jzy3d.plot3d.primitives.EuclidPlane;
import org.jzy3d.plot3d.primitives.EuclidRobot;
import org.jzy3d.plot3d.primitives.EuclidSkeleton;
import org.jzy3d.plot3d.primitives.EuclidSphere;
import org.jzy3d.plot3d.primitives.LabelFactory;
import org.jzy3d.plot3d.primitives.PickableObjects;
import org.jzy3d.plot3d.primitives.RobotType;
import org.jzy3d.plot3d.primitives.pickable.Pickable;
import org.jzy3d.plot3d.rendering.canvas.CanvasNewtAwt;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.lights.Light;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public final class EuclidViewer3DComp  implements iEuclidViewer3D{

    static float CHESS_FLOOR_WIDTH = 100;
    
    private int pickingId = 0;
    private ArrayList<PickableObjects> pickableObjects = new ArrayList();
    private ArrayList<PickableObjects> pickingSupportList = new ArrayList();
    private PickingSupport pickingSupport; // ==null if not available
    private NewtCameraMouseController cameraMouse;
    protected ObjectLoader colladaLoader;
    protected static ArrayList<EuclidRobot> robotList = new ArrayList();
    protected static ArrayList<EuclidSkeleton> skeletonList = new ArrayList();
    
    private Chart chart;
    
    /**
     * Constructor for a EuclidViewer3D to get created by a NewtChartFactory.
     */
    public EuclidViewer3DComp() throws Exception{
        this.init();
        //super(new NewtChartFactory());  
    }
    
     public void init() throws Exception {
        
        Quality q = Quality.Advanced(); 
        q.setDepthActivated(true);
        //q.setAlphaActivated(false);
        q.setAnimated(false); 
        q.setHiDPIEnabled(true); 
        q.setDisableDepthBufferWhenAlpha(false);
        q.setPreserveViewportSize(true);     
        
        //chart = new Chart(this.getFactory(), q);
        chart = new SwingChartFactory().newChart(Quality.Advanced());
        
        chart.getView().setSquared(false);
        chart.getView().setBackgroundColor(org.jzy3d.colors.Color.WHITE);
        chart.getView().getAxis().getLayout().setMainColor(org.jzy3d.colors.Color.BLACK);
        
        //Add the ChessFloor and set size
        //this.setUpChessFloor(100.f);
        //chart.getScene().getGraph().addGraphListener(() -> {
        //    updateChessFloor(true, CHESS_FLOOR_WIDTH);
        //});
            
        //Set up ObjectLoader and Mouse
        colladaLoader = ObjectLoader.getLoader();
        //setUpPickingSupport();
        //Light light = chart.addLight(chart.getView().getBounds().getCorners().getXmaxYmaxZmax());
        //light.setType(Light.Type.POSITIONAL);
        Light light = chart.addLightOnCamera();
        
        //addSkeleton("data/golembones/golembones.obj");
        
        /**
        addPoint(new Point3d(1,1,1), Color.BLUE, 0.6f, "Point1");
        addSphere(new Point3d(20,20,20), 10, Color.ORANGE, "Sphere1");
        
        addPlane(new Point3d(5d,5d,5d), new Vector3d(0d,0d,5d), new Vector3d(5d,0d,0d), Color.RED, "Plane1");
        
        addArrow(new Point3d(0d, 0d, 0d), new Vector3d(0d,0d,2d), 3f, 0.5f, Color.CYAN, "Arrow1");
        
        addLabel(new Point3d(10d, 10d, 10d), "Label", Color.BLACK);
        addCircle(new Point3d(20,20,20), new Vector3d(0,0,1),10,Color.RED, "Circle");
        
        addLine(new Vector3d(0d,0d,-1d), new Point3d(3d,0d,3d), Color.CYAN, 0.2f, 10, "ClipLinie");
        
        addPlane(new Point3d(0,1,5), new Vector3d(0,-10,0), new Vector3d(-10,0,0), Color.ORANGE, "ClipPlane");
        addPoint(new Point3d(0,0,0), Color.BLUE, 0.6f, "Point1");
        addPoint(new Point3d(1,10,1), Color.BLUE, 0.6f, "Point3");
        addPoint(new Point3d(20,20,20), Color.BLUE, 0.6f, "Point2");    
        addPlane(new Point3d(5d,5d,5d), new Vector3d(0d,0d,5d), new Vector3d(5d,0d,0d), Color.RED, "Plane1");
        addLine(new Vector3d(0d,0d,-1d), new Point3d(3d,0d,3d), Color.CYAN, 0.2f, 10, "ClipLinie");
        addArrow(new Point3d(7d, 7d, 7d), new Vector3d(0d,0d,2d), 3f, 0.5f, Color.CYAN, "Arrow1");
        **/
        
        /*
        double[] delta_theta_rad = new double[]{0d,0d,0d,0d,0d,0d,0d};
        //double[] delta_theta_rad = new double[]{0d, -8.27430119976213518e-08, 0.732984551101984239, 5.46919521494736127, 0.0810043775014757245, -3.53724730506321805e-07, -9.97447025669062626e-08};
        double[] delta_a_m = new double[]{0d, 0, -425, -392.2, 0, 0, 0};
        double[] delta_d_m = new double[]{0d, 162.5, 0, 0, 133.3, 997, 996};
        double[] delta_alpha_rad= new double[]{0d, Math.PI/2, 0, 0, Math.PI/2, Math.PI/2, 0};
        */
        
        
        double[] delta_theta_rad = new double[]{0d,0d,0d,0d,0d,0d,0d};      

        ArrayList<String> pathList = new ArrayList<>();
        pathList.add("/data/objfiles/base.dae");
        pathList.add("/data/objfiles/shoulder.dae");
        pathList.add("/data/objfiles/upperarm.dae");
        pathList.add("/data/objfiles/forearm.dae");
        pathList.add("/data/objfiles/wrist1.dae");
        pathList.add("/data/objfiles/wrist2.dae");
        pathList.add("/data/objfiles/wrist3.dae");
        addRobotUR5e(pathList, delta_theta_rad);
    }
    
    /** 
     * Open the visualization window.
     * 
     * @throws Exception 
     */
    public void open() throws Exception {
        //AnalysisLauncher.open(this);
        //Robots have to be rotated after initialisation.
        rotateRobotsCoordsystem();
        setRobotsDH();
        //setUpRobotMovementUIWithSliders();
        
        chart.open();
        chart.addMouseCameraController(); // besser nur addMouse() verwenden?
    }
    
    public JComponent getComponent(){
        return (JComponent) chart.getCanvas();
    }
    
    /**
     * Close the chart
     */
    public void close(){
        chart.dispose();
    }
    
    
    
    // implementation 
    
    /**
     * Prints out the centers of the skeletons
     */
    private void skeletonCenters(){
        for(EuclidSkeleton s: skeletonList){
            for(EuclidPart part: s.getParts()){
                Point3d p = new Point3d(part.getCenter().x, part.getCenter().y, part.getCenter().z);
                addPoint(p,org.jzy3d.colors.Color.RED,0.2f, "");
            }
        }
    }
    
    /**
     * Rotate all Robots to have the Z-Vector as the UP Vector.
     */
    protected static void rotateRobotsCoordsystem(){
        for(EuclidRobot robot: robotList){
            robot.rotateCoordSystem();
        }
    }
    
    /**
     * Set the robot right to its DH parameters
     */ 
    protected static void setRobotsDH(){
        for(EuclidRobot robot: robotList){
            robot.moveDH();
        }
    } 
    
    /**
     * Set up the movement of the robot per sliders.
     * 
     * Adds slider to the chart panal to control the movement of the robot.<p>
     * 
     * Usage for demonstration purpose only.
     */
    protected void setUpRobotMovementUIWithSliders(){
        CanvasNewtAwt c = (CanvasNewtAwt) chart.getCanvas();
        Component comp = c.getComponent(0);
        c.remove(comp);
        JPanel p = new JPanel();
        p.add(comp);
        p.setLayout(new BoxLayout(p, 1));
        for(int j = 0; j < robotList.size(); j++){
            int number = j + 1;
            String string = "Robot Number " + number;
            JLabel label = new JLabel(string);
            p.add(label);
            for(int i = 1; i < 7; i++){
                JSlider slider = new JSlider();
                slider.setMaximum(360);
                slider.setMinimum(0);
                slider.setVisible(true);
                slider.setValue((int) robotList.get(0).getDHs().get(i).getTheta());
                final int ix = i;
                final int jx = j;
                EuclidViewer3DComp g = this;
                slider.addChangeListener((ChangeEvent e) -> {
                    JSlider source = (JSlider)e.getSource();
                    //updateing chess floor after seting the Theta Values does not lead to tearing
                    robotList.get(jx).setTheta(ix, source.getValue(), false);
                    //updateChessFloor(true, CHESS_FLOOR_WIDTH);   
                });
                p.add(slider);
            }
        }
        p.setVisible(true);
        c.add(p); 
    }
    
    /**
     * Set up all the skeleton
     */
    protected void setUpSkeletons(){
        for(EuclidSkeleton skeleton: skeletonList){
            skeleton.setUpSkeleton();
        }
    }
    
    /**
     * Sets up the movement of the skeleton 
     */
    protected void setUpSkeletonMovement(){
       for(EuclidSkeleton skeleton: skeletonList){
            CanvasNewtAwt c = (CanvasNewtAwt) chart.getCanvas();
            Component comp = c.getComponent(0);
            c.remove(comp);
            JPanel p = new JPanel();
            p.add(comp);
            p.setLayout(new BoxLayout(p, 1));
            String[] names = new String[skeleton.getParts().size()];
            for(int i = 0; i < skeleton.getParts().size(); i++){
                names[i] = skeleton.getParts().get(i).getName();
            }
            JPanel comboPanel = new JPanel();
            comboPanel.setLayout(new BoxLayout(comboPanel, 1));
            JPanel comboPanel2 = new JPanel();
            comboPanel2.setLayout(new BoxLayout(comboPanel2, 1));
            JComboBox box = new JComboBox(names);
            box.addActionListener((ActionEvent e) -> {
                JComboBox b = (JComboBox) e.getSource();
                String name = (String)b.getSelectedItem();
                setUpRotationSkeleton(skeleton, name, comboPanel2);         
            });
            comboPanel.add(box);
            comboPanel.add(comboPanel2);
            setUpRotationSkeleton(skeleton,(String)box.getSelectedItem(), comboPanel2);
            p.add(comboPanel);
            p.setVisible(true);
            c.add(p);
        } 
    }
    
    /**
     * Sets up the slider rotation of a skeleton part.
     * 
     * @param skeleton The skeleton
     * @param name The name of the part of the skeleton
     * @param panel The panel where the sliders 
     */
    private void setUpRotationSkeleton(EuclidSkeleton skeleton, String name, JPanel panel){
        EuclidPart part = skeleton.getPart(name);
        panel.removeAll();
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, 2));
        if(part.getIsBallJoint()){
            for(int i = 0; i < 3; i++){
                sliderPanel = new JPanel();
                sliderPanel.setLayout(new BoxLayout(sliderPanel, 2));
                JLabel axisText = new JLabel(getCoordinateAxisFromForLoop(i));
                sliderPanel.add(axisText);
                panel.add(sliderPanel);
                JSlider slider = new JSlider();
                slider.setMaximum(360);
                slider.setMinimum(0);
                slider.setVisible(true);
                slider.setValue(0);
                final int ix = i;
                slider.addChangeListener((ChangeEvent e) -> {
                    JSlider source = (JSlider)e.getSource();
                    switch(ix){
                        case 0 -> {
                            skeleton.rotate(name, source.getValue(), part.getLocalVectorsystemX(), part.getCenter(), ix, false); updateChessFloor(true, CHESS_FLOOR_WIDTH);
                        }
                        case 1 -> {   
                            skeleton.rotate(name, source.getValue(), part.getLocalVectorsystemY(), part.getCenter(), ix, false); updateChessFloor(true, CHESS_FLOOR_WIDTH);
                        }
                        default -> {
                            skeleton.rotate(name, source.getValue(), part.getLocalVectorsystemZ(), part.getCenter(), ix, false); updateChessFloor(true, CHESS_FLOOR_WIDTH);
                        }
                    }
                });
                sliderPanel.add(slider);
            }
        } else {
            JLabel axisText = new JLabel(getCoordinateAxisFromForLoop(1));
            sliderPanel.add(axisText);
            panel.add(sliderPanel);
        }
        panel.updateUI();
    }
    
    /**
     * Returns if it is the X, Y or Z coordinate axis for the rotation of the robot. 
     * @param i The number of the loop
     * @return The string fo the Axis label
     */
    private String getCoordinateAxisFromForLoop(int i){
        return switch (i) {
            case 0 -> "X: ";
            case 1 -> "Y: ";
            default -> "Z: ";
        };
    }
    
    
    // api to add geometric objects
    
    /**
     * Add a point to the 3d view.
     * 
     * @param location location of the point [mm]
     * @param color color of the point
     * @param diameter diameter of the point [mm]
     * @param label the text of the label of the point
     */
    private void addPoint(Point3d location, org.jzy3d.colors.Color color, float diameter, String label){
        
        if (!isValid(location)){
            throw new IllegalArgumentException("addPoint(): location with illegal values!");
        }
        
        Point3d labelLocation = new Point3d(location.x, location.y,location.z - (diameter/2) - LabelFactory.getInstance().getOffset());
        EuclidSphere sphere = new EuclidSphere();
        sphere.setData(location, (float) (diameter/2), 20, color, label, labelLocation);
        sphere.setPolygonOffsetFillEnable(false);
        sphere.setWireframeDisplayed(false);
        if (pickingSupport != null){
            sphere.setPickingId(pickingId++);
            pickingSupport.registerDrawableObject(sphere, sphere);
        }
        chart.add(sphere);
        if (pickingSupport != null){
            pickingSupportList.add(sphere);
        }
    }
    
    
    public static boolean isValid(Tuple3d tuple3d){
        if (!Double.isFinite(tuple3d.x)) return false;
        if (!Double.isFinite(tuple3d.y)) return false;
        return Double.isFinite(tuple3d.z);
    }
   
    /**
     * Add a line to the 3d view.
     * 
     * @param attitude (normalization not needed, but magnitude is not used)
     * @param location unit in [mm]
     * @param color
     * @param radius unit in [mm]
     * @param label
     * @return true if the line is inside the bounding box and visible
     */
    /*Composite addLine(Point3d location, Vector3d attitude, org.jzy3d.colors.Color color, 
                           double radius, String label){
        
        if (!isValid(location) || !isValid(attitude)){
            throw new IllegalArgumentException("addLine(): location or attitude with illegal values!");
        }
        
        // Clipping
        Point3d[] p = clipLine(new de.orat.view3d.euclid3dviewapi.util.Line(new Vector3d(location), attitude));
        //System.out.println("line: p1=("+String.valueOf(p[0].x)+
        //        ", "+String.valueOf(p[0].y)+", "+String.valueOf(p[0].z)+"), p2=("+
        //        String.valueOf(p[1].x)+", "+String.valueOf(p[1].y)+", "+String.valueOf(p[1].z));
        
        if (p.length == 2){
            return addLine(p[0], p[1],  (float) radius, color, label);
            //FIXME
            // scheint nicht zu funktionieren
            //attitude.normalize();
            //return addLine(location, attitude, color, (float) radius, 100, label, true);
        } else {
            System.out.println("Clipping of line \""+label+"\" failed!");
            return null;
        }
    }*/
    
    /**
     * Add a line to the 3d view.
     * 
     * TODO
     * statt length + normalized(attitude) einfach nur die Attitude übergeben
     * 
     * @param attitude (normalized)
     * @param location unit in [mm]
     * @param color
     * @param radius unit in [mm]
     * @param length  unit in [mm]
     * @param label
     * @param withClipping true, if clipping is wanted
     * @return true if the line can be visualized in the axis-aligned-bounding-box
     */
    /*public boolean addLine(Point3d location, Vector3d attitude, Color color, 
            float radius, float length, String label, boolean withClipping){
        return addLine(location, 
            new Point3d(location.x+attitude.x*length, 
                        location.y+attitude.y*length, 
                        location.z+attitude.z*length), radius, color, label, withClipping);
    }*/
    /**
     * Add a line to the 3d view.
     * 
     * @param p1 start point of the cylinder unit in [mm]
     * @param p2 end point of the cylinder unit in [mm]
     * @param lineRadius lineRadius of the cylinder unit in [mm]
     * @param color color of the line
     * @param label the label of the line
     * @return false if line is outside the bounding-box
     */
    Composite addLine(Point3d p1, Point3d p2, float lineRadius, org.jzy3d.colors.Color color, 
                            String label){
        
        if (!isValid(p1) || !isValid(p2)){
            throw new IllegalArgumentException("addLine(): p1 or p2 with illegal values!");
        }
        
        System.out.println("line \""+label+"\": p1=("+String.valueOf(p1.x)+
                ", "+String.valueOf(p1.y)+", "+String.valueOf(p1.z)+"), p2=("+
                String.valueOf(p2.x)+", "+String.valueOf(p2.y)+", "+String.valueOf(p2.z)+")");
        // das clipping scheint keine Auswirkungen zu haben clipped-p1 = p1 ...
        //FIXME
        /*if (withClipping){
            p1 = clipPoint(p1);
            p2 = clipPoint(p2);
            System.out.println("line: clipped p1=("+String.valueOf(p1.x)+
                ", "+String.valueOf(p1.y)+", "+String.valueOf(p1.z)+"), clipped p2=("+
                String.valueOf(p2.x)+", "+String.valueOf(p2.y)+", "+String.valueOf(p2.z));
        }*/
        org.jzy3d.plot3d.primitives.Line line = new org.jzy3d.plot3d.primitives.Line();
        line.setData(p1,p2, lineRadius, 10, 0, color, label);
        if (pickingSupport != null){
            line.setPickingId(pickingId++);
            pickingSupport.registerDrawableObject(line, line);
        }
        chart.add(line);
        if (pickingSupport != null){
            pickingSupportList.add(line);
        }
        return line;
    }
   
    /**
     * Add circle to the 3d view.
     * 
     * @param location location of the circle in [mm]
     * @param direction (not normalized) normal vector of the (carrier) plane (the circle lays in)
     * @param radius radius of the circle in [mm]
     * @param color color of the circle
     * @param label the label for the circle
     * @param isDashed
     * 
     * TODO
     * implementation of isFull
     */
    Composite addCircle(Point3d location, Vector3d direction, float radius, 
                          org.jzy3d.colors.Color color, String label, boolean isDashed, boolean isFull){
        
        if (!isValid(location) || !isValid(direction)){
            throw new IllegalArgumentException("addCircle(): location or attitude with illegal values!");
        }
        
        EuclidCircle circle = new EuclidCircle();
        circle.setData(location, direction, radius, color, label, isDashed);
        
        if (pickingSupport != null){
            circle.setPickingId(pickingId++);
            pickingSupport.registerPickableObject(circle, circle);
        }
        chart.add(circle);
        if (pickingSupport != null){
            pickingSupportList.add(circle);
        }
        return circle;
    }
    
    @Override
    public long addCube(Point3d location, Vector3d dir, double width, 
            Color color, String label, boolean transparency) {
        
        if (!isValid(location) || !isValid(dir)){
            throw new IllegalArgumentException("addCube(): location or attitude with illegal values!");
        }
        EuclidCube cube = new EuclidCube();
        Point3d labelLocation = location;
        //TODO offset zu labelLocation entsprechend width hinzufügen aber in welche Richtung?
        // abhängig von dir?
        //TODO
        // transparency if true, dann default-value für alpha setzen
        
        org.jzy3d.colors.Color col = new org.jzy3d.colors.Color(color.getRed(),
                color.getGreen(),color.getBlue(), color.getAlpha());
        cube.setData(location, (float) width, (float) width, (float) width, 
                col, label, labelLocation);
                
        if (pickingSupport != null){
            cube.setPickingId(pickingId++);
            pickingSupport.registerPickableObject(cube, cube);
        }
        chart.add(cube);
        if (pickingSupport != null){
            pickingSupportList.add(cube);
        }
        
        viewObjects.put(compositeId, cube);
        return compositeId++;
    }
    
    /**
     * Add an arrow to the 3d view.
     * 
     * @param location midpoint of the arrow in [mm]
     * @param attitude attitude of the arrow in [mm]
     * @param radius radius of the arrow in [mm]
     * @param color color of the arrow
     * @param label the text of the label of the arrow
     */
    Composite addArrow(Point3d location, Vector3d attitude, 
                         float radius, org.jzy3d.colors.Color color, String label){
        
        if (!isValid(location) || !isValid(attitude)){
            throw new IllegalArgumentException("addArrow(): location or attitude with illegal values!");
        }
        
        Arrow arrow = new Arrow();
        arrow.setData(location, attitude, radius,10,0, color, label);
        arrow.setWireframeDisplayed(false);
        if (pickingSupport != null){
            arrow.setPickingId(pickingId++);
            pickingSupport.registerPickableObject(arrow, arrow);
        }
        chart.add(arrow);
        if (pickingSupport != null){
            pickingSupportList.add(arrow);
        }
        return arrow;
    }
    
    /**
     * Add a plane to the 3d view.
     * 
     * @param location center of the plane (first point) [mm]
     * @param dir1 vector which is added to the first point to get the second point, unit in [mm]
     * @param dir2 vector which is added to the second point to get the third point, unit in [mm]
     *             and which is added to the location to get the forth point
     * @param color color of the plane
     * @param label the text of the label of the plane
     * @return false, if the plane is outside the bounding-box and not visualized
     * @Deprecated
     */
    /*public boolean addPlaneDeprecated(Point3d location, Vector3d dir1, Vector3d dir2, 
                          Color color, String label){
        location = clipPoint(location);
        Point3d p1 = new Point3d(location.x+dir1.x,location.y+dir1.y, location.z+dir1.z);
        Point3d p2 = new Point3d(location.x+dir2.x,location.y+dir2.y, location.z+dir2.z);
        //p1 = clipPoint(p1);
        //p2 = clipPoint(p2);
        dir1 = new Vector3d(p1.x-location.x, p1.y-location.y, p1.z-location.z);
        dir2 = new Vector3d(p2.x-location.x, p2.y-location.y, p2.z-location.z);
        EuclidPlane plane = new EuclidPlane();
        plane.setData(location, dir1, dir2, color, label);
        plane.setPolygonOffsetFillEnable(false);
        plane.setWireframeDisplayed(true);
        if (pickingSupport != null){
            plane.setPickingId(pickingId++);
            pickingSupport.registerDrawableObject(plane, plane);
        }
        chart.add(plane);
        if (pickingSupport != null){    
            pickingSupportList.add(plane);
        }
        // wenn ausserhalb der bounding-box false
        //TODO
        return true;
    }*/


    @Override
    public long addPolygone(Point3d location, Point3d[] corners, Color color,
                            String label, boolean showNormal, boolean transparency, Transform transform) {
        org.jzy3d.colors.Color col = new org.jzy3d.colors.Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        Composite composite = addPolygone(location, corners,
                col, label, transform);
        viewObjects.put(compositeId, composite);
        return compositeId++;
    }

    /**
     * Add a plane to the 3d view.
     *
     * @param location center of the plane (first point) [mm]
     * @param corners
     * @param color    color of the plane
     * @param label    the text of the label of the plane
     */
    Composite addPolygone(Point3d location, Point3d[] corners,
                          org.jzy3d.colors.Color color, String label, Transform transform) {

        //TODO test corners auf illagal values
        if (!isValid(location)) {
            throw new IllegalArgumentException("addPlane(): location with illegal values!");
        }

        EuclidPlane plane = new EuclidPlane();
        plane.setData(location, corners, color, label);
        plane.setPolygonOffsetFillEnable(false);
        plane.setWireframeDisplayed(true);
        plane.setTransformBefore(transform);
        if (pickingSupport != null) {
            plane.setPickingId(pickingId++);
            pickingSupport.registerDrawableObject(plane, plane);
        }
        chart.add(plane);
        if (pickingSupport != null) {
            pickingSupportList.add(plane);
        }
        return plane;
    }
    
    
    // helper methods
    
    /**
     * Add a label with a text to the 3d view.
     * 
     * @param location the location of the label
     * @param text the text of the label
     * @param color color of the text
     */
    void addLabel(Point3d location, String text, org.jzy3d.colors.Color color){
         chart.add(LabelFactory.getInstance().addLabel(location, text, org.jzy3d.colors.Color.BLACK));
    }
    
    /**
     * Clips a point.
     * 
     * Projection of the point to the bounding box. Needed to implement mouse functionality.
     * 
     * @param point The point which should be clipped
     * @return the clipped point
     */
    private Point3d clipPoint(Point3d point){
        BoundingBox3d bounds = chart.getView().getAxis().getBounds();
        if (point.x < bounds.getXmin()){
            point.x = bounds.getXmin();
        } else if (point.x > bounds.getXmax()){
            point.x = bounds.getXmax();
        }
        if (point.y < bounds.getYmin()){
            point.y = bounds.getYmin();
        } else if (point.y > bounds.getYmax()){
            point.y = bounds.getYmax();
        } 
        if (point.z < bounds.getZmin()){
            point.z = bounds.getZmin();
        } else if (point.z > bounds.getZmax()){
            point.z = bounds.getZmax();
        }
        return point;
    }
    
     /**
     * Determine clipping point of a line with the bounding box of the current
     * visualization.
     * 
     * In principle "Liang-Barsky Line Clipping algorithm" is a possible 
     * implementation.
     * 
     * @param line
     * @param p output near point, far point, [] if no intersection, maybe only one point
     * @return true if there are intersection points
     */
    /*private Point3d[] clipLine(de.orat.view3d.euclid3dviewapi.util.Line line){
        AxisAlignedBoundingBox aabb = createAxisAlignedBoundBox();
        
        // bis auf L_45 scheint das zu funktionieren
        //return aabb.clip4(line);
        
        // funktioniert
        return aabb.clip(line);
        
        // funktioniert nicht, führt zum Absturz, out of memory
        //return aabb.clip3(line);
    }*/
    
    private AxisAlignedBoundingBox createAxisAlignedBoundBox(){
        BoundingBox3d bounds = chart.getView().getAxis().getBounds();
        
        // representation variant 1
        Point3d center = new Point3d(bounds.getCenter().x, bounds.getCenter().y, bounds.getCenter().z);
        Vector3d size = new Vector3d(bounds.getRange().x, bounds.getRange().y, bounds.getRange().z);
        
        // representation variant 2
        BoundingBox3d.Corners corners = bounds.getCorners();
        Point3f xyzmin = new Point3f(corners.getXminYminZmin().toArray());
        Point3f xyminzmax = new Point3f(corners.getXminYminZmax().toArray()); 
        Point3f xminymaxzmin = new Point3f(corners.getXminYmaxZmin().toArray());
        Point3f xminymaxzmax = new Point3f(corners.getXminYmaxZmax().toArray());
        Point3f xmaxyzmin = new Point3f(corners.getXmaxYminZmin().toArray());
        Point3f xmaxyminzmax = new Point3f(corners.getXmaxYminZmax().toArray());
        Point3f xymaxzmin = new Point3f(corners.getXmaxYmaxZmin().toArray());
        Point3f xyzmax = new Point3f(corners.getXmaxYmaxZmax().toArray());
        System.out.println("AABB at c=("+String.valueOf(center.x)+", "+String.valueOf(center.y)+
                ", "+String.valueOf(center.z)+") with size=("+String.valueOf(size.x)+", "+
                String.valueOf(size.y)+", "+String.valueOf(size.z)+")");
        // size scheint korrekt, aber die xyzmin, und andere min-Werte scheinen zu klein zu sein
        // und damit wird die AABB zu gross
        //TODO weiter oben wo sie berechnet werden
        // aber die corners werden doch direkt ausgelesen
        return new AxisAlignedBoundingBox(xyzmin, xyminzmax, xminymaxzmin,
                xminymaxzmax, xmaxyzmin, xmaxyminzmax, xymaxzmin, xyzmax, 
                center, size);
    }
    
    /**
     * Add a COLLADA (.dae) File Object to the Scene.
     * 
     * @param path the path to the COLLADA File
     */
    void addCOLLADA(String path){
        EuclidPart part = colladaLoader.getCOLLADA(path); 
        part.setChart(chart);
        part.addToChart();
    }
    
    /**
     * Adds a robot to the chart from COLLADA (.dae) files.
     * 
     * @param paths The paths to the Collada files for the robot as a List
     */
    public void addRobot(List<String> paths){
        EuclidRobot robot = new EuclidRobot(chart, RobotType.notype);
        robot.setDataDAEComponents(paths);
        robotList.add(robot);
        robot.addToChartParts();
    }
    
    /**
     * Add a UR5e Robot.
     * 
     * @param paths the paths to the robot parts
     * @param delta_theta_rad the angle of the single parts
     */
    void addRobotUR5e(List<String> paths, double[] delta_theta_rad){ 
        double[] delta_a_m = new double[]{0d, 0.000156734465764371306, 0.109039760794650886, 0.00135049423466820917, 0.30167176077633267e-05, 8.98147062591837358e-05, 0};
        double[] delta_d_m = new double[]{0d, -7.63582045015809285e-05, 136.026368377065324, -130.146527922606964, 0.12049886607637639, -0.13561334270734671, -0.000218168195914358876};
        double[] delta_alpha_rad= new double[]{0d, -0.000849612070594307767, 0.00209120614311242205, 0.0044565542371754396, -0.000376815598678081898, 0.000480742313784698894, 0};
        
        EuclidRobot robot = new EuclidRobot(chart, RobotType.UR5e);
        robot.setDataWithUR5eDHDeltas(paths, delta_theta_rad, delta_alpha_rad, delta_d_m, delta_a_m);
        robotList.add(robot);
        robot.addToChartParts();
    }

    public long addMesh(String path, Matrix4d transform){
        System.out.println("addMesh() not yet implemented!");
        return -1;
    }
    
    /**
     * Adds a new skeleton to the chart.
     * 
     * @param path the string to the path of the skeleton
     */
    public void addSkeleton(String path){
        EuclidSkeleton skeleton = new EuclidSkeleton(path, chart);
        skeletonList.add(skeleton);
        skeleton.drawOnChart();
    }
    
    /*public GeometryView3d(){
        
        
        //AWTChartFactory myfactory = new AWTChartFactory();
        //NewtChartFactory factory = new NewtChartFactory();
        //ChartFactory factory = new EmulGLChartFactory();

        // Emulgl will show limitations
        // 1-wireframe and face do not mix cleanly (polygon offset fill)
        // 2-wireframe color tend to saturate (here in green)

      
    }*/

    
    // mouse control
    
    /**
     * Sets up the mouse for picking
     */
    protected void setUpPickingSupport(){
        pickingSupport = new PickingSupport();
        pickingSupport.addObjectPickedListener(new EuclidPickListener());
        NewtMouse m = new NewtMouse();
        m.setPickingSupport(pickingSupport);
        m.register(chart);
    }

    
    @Override
    public boolean removeNode(long id) {
        Composite node = viewObjects.get(id);
        boolean result = false;
        if (node != null){
            chart.remove(node);
            result = true;
        }
        return result;
    }

    @Override
    public long addCircle(Point3d location, Vector3d normal, double radius, 
            Color color, String label, boolean isDashed, boolean isFull) {
        
        org.jzy3d.colors.Color col = new org.jzy3d.colors.Color(color.getRed(),color.getGreen(),color.getBlue(), color.getAlpha());
        Composite circle = addCircle(location, normal, (float) radius, 
                          col, label, isDashed, isFull);
        viewObjects.put(compositeId, circle);
        return compositeId++;
    }

    

    @Override
    public void transform(long id, Matrix4d transform) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public iAABB getAABB() {
        return this.createAxisAlignedBoundBox();
    }
    /*public de.orat.view3d.euclid3dviewapi.api.BoundingBox3D getBoundingBox(){
        BoundingBox3d bounds = chart.getView().getAxis().getBounds();
        
        // representation variant 1
        //Point3d center = new Point3d(bounds.getCenter().x, bounds.getCenter().y, bounds.getCenter().z);
        //Vector3d size = new Vector3d(bounds.getRange().x, bounds.getRange().y, bounds.getRange().z);
        
        // representation variant 2
        BoundingBox3d.Corners corners = bounds.getCorners();
        return new de.orat.view3d.euclid3dviewapi.api.BoundingBox3D(new Point3f(corners.getXminYminZmin().toArray()),
                                  new Point3f(corners.getXmaxYmaxZmax().toArray()));
    }*/
    
    /**
     * The MouseController for the picking of jakob
     */
    private class NewtMouse extends NewtMousePickingController{
        
        Coord3d currentMouse = null;
        
        @Override
        public void mouseMoved(com.jogamp.newt.event.MouseEvent e){
           //So hovering over a pickable Object doesn't select it when hovering over a pickable object
        }  
        
        @Override
        public void mouseDragged(com.jogamp.newt.event.MouseEvent e){          
            if (!pickableObjects.isEmpty()){
                if(e.getButton() == 1){
                    for(PickableObjects p: pickableObjects){
                        int yflip = -e.getY() + chart.getCanvas().getRendererHeight();
                        Camera camera = chart.getView().getCamera();
                        IPainter painter = chart.getPainter();

                        painter.acquireGL();

                        // 2D to 3D
                        float depthRange = 0.5f;// between 0 and 1, see gluUnproject
                        if(chart.getView().getCamera().getEye().x<0){
                            depthRange = 0.66f;
                        } else if(chart.getView().getCamera().getEye().y>chart.getView().getBounds().getYmax()){
                            depthRange = 0.4f;
                        }
                        currentMouse = new Coord3d(e.getX(), yflip, depthRange);
                        Coord3d pos = camera.screenToModel(chart.getPainter(), currentMouse);
                        painter.releaseGL();
                        Point3d clippedPos = clipPoint(new Point3d(pos.x,pos.y,pos.z));
                        moveObject(new Coord3d(clippedPos.x,clippedPos.y,clippedPos.z), p);
                    }
                    chart.render();
                }
            }
        }

        @Override
        public void mouseReleased(com.jogamp.newt.event.MouseEvent e){
            if (!pickableObjects.isEmpty()){
                for(PickableObjects object: pickableObjects){
                    if(object.getType().equals(DrawableTypes.PLANE)){
                        pickObject(object);
                    }
                }
                pickableObjects.clear();
                cameraMouse.register(chart);
            }    
        }
    }
    
    /**
     * The Listener on a picked object.
     */
    private class EuclidPickListener implements IObjectPickedListener {
        @Override
        public void objectPicked(List<? extends Object> list, PickingSupport ps) {
            if(!list.isEmpty()){
                pickableObjects.clear();
                for(Object o: list){
                    pickableObjects.add((PickableObjects) o);
                }
                if(cameraMouse == null){
                    cameraMouse = (NewtCameraMouseController) chart.getMouse();   
                }
                cameraMouse.unregister(chart);
            }
        }
    }
    
    /**
     * Moves a pickableObject.
     * 
     * @param position the position to which the object should be moved
     * @param object the object
     */
    public void moveObject(Coord3d position, PickableObjects object){
        if(object != null){
            object.setNewPosition(position);
        }
    }
    
    /**
     * Adds a moved object to the pickingSupport.
     * 
     * @param plane the moved object
     */
    private void pickObject(PickableObjects plane){
        pickingSupport.unRegisterAllPickableObjects();
        PickableObjects removeObject = null;
        for(PickableObjects object: pickingSupportList){
            if(object.getPickingId() == plane.getPickingId()){
                removeObject = object;
            }
        }
        if(removeObject != null){
            pickingSupportList.remove(removeObject);
            pickingSupportList.add(plane);
        }
        for(PickableObjects object: pickingSupportList){
            pickingSupport.registerPickableObject((Pickable) object, (Pickable) object);
        }
    }
    
    
    // chess floor
    
    /**
     * Set up the ChessFloor size.
     * 
     * @param width The length of one square
     */
    protected void setUpChessFloor(float width){
        ChessFloor.configure(chart, width);
    }
    
    /**
     * Updates the ChessFloor.
     * 
     * If using a lot of VBOs objects, like the Robot or Skeleton class does and
     * then removing and reading parts, call this function after the change to 
     * avoid stuttering.
     * 
     * If the function is called before the other VBOs update it leads to 
     * stuttering transformations.
     * 
     * @param update true if the chart should be updated directly
     * @param width
     */
    public void updateChessFloor(boolean update, float width){
        ChessFloor.update(width, chart, update);
    }
    
    /**
     * Removes the ChessFloor.
     * 
     * @param update true if the chart should be updated directly
     */
    private void removeChessFloor(boolean update){
        ChessFloor.removeSingelton(chart, update);
    }
    
    
    
    
    // implementation of iEuclidViewer3D
    
    private Map<Long, Composite> viewObjects = new HashMap();
    private long compositeId=0;
    
    /**
     * Add a sphere to the 3d view.
     * 
     * @param location unit in [mm]
     * @param radius [mm] > 0
     * @param color 
     * @param label the text of the label of the sphere
     */
    @Override
    public long addSphere(Point3d location, double radius, java.awt.Color color, 
                          String label, boolean transparent){
        
        if (!isValid(location) ){
            throw new IllegalArgumentException("addSphere(): location with illegal values!");
        }
        
        EuclidSphere sphere = new EuclidSphere();
        Point3d labelLocation = new Point3d(location.x,location.y, location.z - radius - LabelFactory.getInstance().getOffset());
        
        org.jzy3d.colors.Color col = new org.jzy3d.colors.Color(color.getRed(),color.getGreen(),color.getBlue(), color.getAlpha());
        sphere.setData(location,(float) radius,10, col, label, labelLocation);
        sphere.setPolygonOffsetFillEnable(false);
        sphere.setWireframeColor(org.jzy3d.colors.Color.BLACK);
        if (pickingSupport != null){
            sphere.setPickingId(pickingId++);
            pickingSupport.registerDrawableObject(sphere, sphere);
        }
        chart.add(sphere);
        if (pickingSupport != null){
            pickingSupportList.add(sphere);
        }
        
        viewObjects.put(compositeId, sphere);
        return compositeId++;
    }
  
    @Override
    public long addLine(Point3d p1, Point3d p2, Color color, double lineRadius, String label) {
        org.jzy3d.colors.Color col = new org.jzy3d.colors.Color(color.getRed(),color.getGreen(),color.getBlue(), color.getAlpha());
        Composite line = addLine(p1, p2, (float) lineRadius, col, label);
        viewObjects.put(compositeId, line);
        return compositeId++;
    }

    @Override
    public long addArrow(Point3d location, Vector3d direction, double radius, 
                         Color color, String label) {
        org.jzy3d.colors.Color col = new org.jzy3d.colors.Color(color.getRed(),color.getGreen(),color.getBlue(), color.getAlpha());
        Composite composite = addArrow(location, direction, (float) radius, col, label);
        viewObjects.put(compositeId, composite);
        return compositeId++;
    }


    /*@Override
    public long addPlane(Point3d location, Vector3d normal, Color color, String label, boolean showNormal) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        //TODO
    }*/

    /*@Override
    public long addRobot(int type, Point3d location, Matrix3d orientation) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }*/

    /*@Override
    public void moveRobot(long id, double[] angels) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }*/

}
