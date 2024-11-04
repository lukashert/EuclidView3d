package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.pickable.PickableSphere;

/**
 * A sphere that is only drawn as a wireframe.
 * This avoids transparency problems.
 */
public class WireframeOnlyPickableSphere extends PickableSphere {

    public WireframeOnlyPickableSphere(Coord3d position, float radius, int slicing, Color color) {
        super(position, radius, slicing, color);
    }

    @Override
    protected void doDrawSphere(IPainter painter) {
        painter.glTranslatef(position.x, position.y, position.z);

        painter.glPolygonMode(PolygonMode.FRONT_AND_BACK, PolygonFill.FILL);
        painter.glPolygonMode(PolygonMode.FRONT_AND_BACK, PolygonFill.LINE);
        painter.glLineWidth(wireframeWidth);
        painter.glColor4f(wireframeColor.r, wireframeColor.g, wireframeColor.b, wireframeColor.a);
        painter.glutSolidSphere(radius, slices, stacks);
    }
}
