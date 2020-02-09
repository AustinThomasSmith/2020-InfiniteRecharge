package com.spartronics4915.lib.math.threedim;

import java.util.ArrayList;
import org.ejml.data.DMatrix4x4;
import org.ejml.data.DMatrix3x3;
import org.ejml.data.DMatrix4;
import org.ejml.dense.fixed.CommonOps_DDF4;
/* http://ejml.org/javadoc/org/ejml/dense/fixed/CommonOps_DDF4.html */

/* Affine3 is a specialization of DMatrix4x4.  Its purpose is to
 * present a constrained interface to users, focused on the expression
 * and manipulation of coordinate frames in the context of FRC robotics.
 * There are many learning resources for affine transformations.
 * Here are a few:
 *  - http://graphics.cs.cmu.edu/nsp/course/15-462/Spring04/slides/04-transform.pdf
 *  - Matrices and transformations. Ronald Goldman.
 *     In "Graphics Gems I", pp 472-475. Morgan Kaufmann, 1990.
 *  - More matrices and transformations: shear and pseudo-perspective.
 *    Ronald Goldman. In "Graphics Gems II", pp 320-323. Morgan Kaufmann, 1991.
 *  - Decomposing a matrix into simple transformations. Spencer Thomas.
 *     In "Graphics Gems II", pp 320-323. Morgan Kaufmann, 1991.
 * 
 *  Basic idea: an affine matrix can be used to describe coordinate-system
 *  conversions as we require when expressing how to convert from the
 *  camera coordinate-frame to the robot-coordinate frame.  Here the camera
 *  has a natural coordinate system and so does the robot.  The question
 *  we'd like to answer: what does a point (or direction) in the camera's 
 *  coordinate system mean to the robot?  Once we've "designed" a transformation 
 *  matrix, we can "multiply" (via "dot product") the point in one coordinate 
 *  system by the transformation matrix to obtain the point in another 
 *  coordinate system. But wait, there's more! We can "chain" coordinate 
 *  systems together to convert points from camera, then to robot, then to 
 *  field.  A combined matrix can be produced (by concatenate) to represent
 *  multiple transformations in one matrix.  We can also "invert" these 
 *  transformations and now we can compute where a field location should 
 *  appear in the camera coordinate system.
 *
 *  To construct/design a matrix we must carefully consider the way that
 *  we must rotate one coordinate system to obtain another.  There are
 *  a number of ways to characterize this rotation, some more intuitive
 *  than others.  If you are dealing with 90 degree, rigid transformations,
 *  the "fromAxes" approach may be best.  Euler angles may be the most
 *  traditional in robotics, but multiple, sequential rotations can be difficult 
 *  to grasp.  Quaternions are very useful for interpolating arbitrary 3D
 *  rotations (and avoid "gimbal lock") but harder to design from scratch.  
 *  Quaternions are also a robust and compact representation for rotations.  
 *  So we offer/support a range of rotation specification methodologies.  
 *  See also the sibling Quaternion class.
 * 
 * This module follows the "column vectors on the right" and "row major storage"
 * (C contiguous) conventions. The translation components are in the right column
 * of the transformation matrix, i.e. this.a14-a34.
 * The transpose of the Affine3 matrices may have to be used to interface
 * with other graphics systems, e.g. OpenGL's glMultMatrixd().
 */

class Affine3
{
    /* static convenience methods  -------------------------------------*/

    public static Affine3 fromRotation(double angle, Vec3 axis)
    {
        return new Affine3(angle, axis, null);
    }

    public static Affine3 fromRotation(double angle, Vec3 axis, Vec3 pivot)
    {
        return new Affine3(angle, axis, pivot);
    }

    public static Affine3 fromTranslation(double x, double y, double z)
    {
        return new Affine3(x, y, z);
    }

    public static Affine3 fromQuaternion(Quaternion q)
    {
        return new Affine3(q); 
    } 

    /**
     * return Affine3 representing rotation of axes to targets. NB:
     * all targets should be unit vectors.
     * @param xtgt
     * @param ytgt
     * @param ztgt
     * @return
     */
    public static Affine3 fromAxes(final Vec3 xtgt, final Vec3 ytgt, 
                                   final Vec3 ztgt)
    {
        Affine3 a = new Affine3();
        a.mMatrix.a11 = xtgt.a1;
        a.mMatrix.a21 = xtgt.a2;
        a.mMatrix.a31 = xtgt.a3;
        a.mMatrix.a12 = ytgt.a1;
        a.mMatrix.a22 = ytgt.a2;
        a.mMatrix.a32 = ytgt.a3;
        a.mMatrix.a13 = ztgt.a1;
        a.mMatrix.a23 = ztgt.a2;
        a.mMatrix.a33 = ztgt.a3;
        return a;
    }

    public static Affine3 concatenate(Affine3 ... alist)
    {
        Affine3 result = new Affine3();
        for(Affine3 a : alist)
            result.concatenate(a);
        return result;
    }

    /* --------------------------------------------------------------------*/
    private DMatrix4x4 mMatrix;

    public Affine3()
    {
        this.mMatrix = new DMatrix4x4();
        CommonOps_DDF4.setIdentity(this.mMatrix);
    }

    public Affine3(DMatrix4x4 m)
    {
        this.mMatrix = m;
    }

    /**
     * Contruct an affine matrix as a translation
     * @param x
     * @param y
     * @param z
     */
    public Affine3(double x, double y, double z)
    {
        this.mMatrix = new DMatrix4x4();
        CommonOps_DDF4.setIdentity(this.mMatrix);
        this.mMatrix.a14 = x;
        this.mMatrix.a24 = y;
        this.mMatrix.a34 = z;
    }

    /**
     * Construct an Affine3 given a concise string represention of the form: 
     *     "o x y z q 1 2 3 4",
     * NB: this rep doesn't allow for scale and skew.  For now this is fine.
     * 
     * @param str
     * @return Affine3 transformation matrix
     */
    public Affine3(String str)
    {
        String[] vals = str.split(" ");
        assert vals.length == 9;
        assert vals[0].equals("o");
        assert vals[4].equals("q");

        double x = Double.parseDouble(vals[1]);
        double y = Double.parseDouble(vals[2]);
        double z = Double.parseDouble(vals[3]);
        double q0 = Double.parseDouble(vals[5]);
        double q1 = Double.parseDouble(vals[6]);
        double q2 = Double.parseDouble(vals[7]);
        double q3 = Double.parseDouble(vals[8]);

        Affine3 trans = new Affine3(x,y,z);
        Affine3 rot = new Affine3(new Quaternion(q0, q1, q2, q3));
        Affine3 result =  Affine3.concatenate(trans, rot);
        this.mMatrix = result.mMatrix;
    } 

    public Affine3(Quaternion q)
    {
        this.mMatrix = q.asDMatrix4x4();
    }

    /**
     * construct an Affine3 from a simple rotation around an optional point.
     * @param angle - amount to rotate in degrees
     * @param axis - unit vector rep of axis, ie: Vec3(0,1,0) is '+y' 
     * @param pt - optional rotation pivot, null implies Vec3(0,0,0)
     * @return rotation matrix
     */
    public Affine3(double angle, final Vec3 axis, final Vec3 pt)
    {
        Vec3 uaxis = axis.asUnit();
        double rads = Math.toRadians(angle);
        double sina = Math.sin(rads);
        double cosa = Math.cos(rads);
        // diag is scale
        DMatrix4x4 m = new DMatrix4x4();
        this.mMatrix = m;

        m.a11 = cosa;
        m.a22 = cosa;
        m.a33 = cosa;

        m.a41 = m.a42 = m.a43 = 0;
        m.a44 = 1;

        // a += d
        DMatrix3x3 d = uaxis.outerProduct(uaxis);
        double f1 = 1.0 - cosa;
        m.a11 += d.a11 * f1;
        m.a12 += d.a12 * f1;
        m.a13 += d.a13 * f1;
        m.a21 += d.a21 * f1;
        m.a22 += d.a22 * f1;
        m.a23 += d.a23 * f1;
        m.a31 += d.a31 * f1;
        m.a32 += d.a32 * f1;
        m.a33 += d.a33 * f1;

        uaxis.multiply(sina);

        // a.mMatrix.a11 += 0;
        m.a12 -= uaxis.a3;
        m.a13 += uaxis.a2;

        m.a21 += uaxis.a3;
        // a.mMatrix.a22 += 0;
        m.a23 -= uaxis.a1;

        m.a31 -= uaxis.a2;
        m.a32 += uaxis.a1;
        // a.mMatrix.a33 += 0

        if(pt != null)
        {
            // M[:3, 3] = point - numpy.dot(R, point)
            Vec3 pp = pt.subtract(this.transformVector(pt));
            m.a14 = pp.a1;
            m.a24 = pp.a2;
            m.a34 = pp.a3;
        }
    }

    /**
     * Expose our matrix represention to the outer world.
     * @return
     */
    public DMatrix4x4 asMatrix()
    {
        return this.mMatrix;
    }

    public String asString()
    {
        Quaternion q = new Quaternion(this.mMatrix);
        DMatrix4 qm = q.asDMatrix4();
        String result = String.format("o %g %g %g q %g %g %g %g", this.mMatrix.a14, 
                                this.mMatrix.a24, this.mMatrix.a34,
                                qm.a1, qm.a2, qm.a3, qm.a4);
        return result;
    }

    public boolean equals(final Affine3 rhs)
    {
        return this.equals(rhs, 1e-9);
    }

    public boolean equals(final Affine3 rhs, double epsilon)
    {
        final DMatrix4x4 m1 = this.mMatrix;
        final DMatrix4x4 m2 = rhs.mMatrix;
        if(Math.abs(m1.a11 - m2.a11) > epsilon) return false;
        if(Math.abs(m1.a12 - m2.a12) > epsilon) return false;
        if(Math.abs(m1.a13 - m2.a13) > epsilon) return false;
        if(Math.abs(m1.a14 - m2.a14) > epsilon) return false;

        if(Math.abs(m1.a21 - m2.a21) > epsilon) return false;
        if(Math.abs(m1.a22 - m2.a22) > epsilon) return false;
        if(Math.abs(m1.a23 - m2.a23) > epsilon) return false;
        if(Math.abs(m1.a24 - m2.a24) > epsilon) return false;

        if(Math.abs(m1.a31 - m2.a31) > epsilon) return false;
        if(Math.abs(m1.a32 - m2.a32) > epsilon) return false;
        if(Math.abs(m1.a33 - m2.a33) > epsilon) return false;
        if(Math.abs(m1.a34 - m2.a34) > epsilon) return false;

        if(Math.abs(m1.a41 - m2.a41) > epsilon) return false;
        if(Math.abs(m1.a42 - m2.a42) > epsilon) return false;
        if(Math.abs(m1.a43 - m2.a43) > epsilon) return false;
        if(Math.abs(m1.a44 - m2.a44) > epsilon) return false;
        return true;
    }

    /**
     * @return the sum of diagonal elements of matrix
     */
    public double trace()
    {
        return this.mMatrix.a11 + this.mMatrix.a22 + 
               this.mMatrix.a33 + this.mMatrix.a44;
    }

    public void print()
    {
        this.mMatrix.print();
    }

    /**
     * Apply a rotation to this instance
     * @param angle - measured in degrees
     * @param dir - unit vector representing the rotational pole
     * @return this (for chaining)
     */
    Affine3 rotate(double angle, Vec3 dir)
    {
        Affine3 rot = Affine3.fromRotation(angle, dir);
        this.concatenate(rot);
        return this;
    }

    /**
     * Apply a translation to this instance. Take care with order
     * of operations between rotations and translations.
     * @param v - vector representing translation
     * @return this (for chaining)
     */
    Affine3 translate(Vec3 v)
    {
        return this.translate(v.a1, v.a2, v.a3);
    }

    Affine3 translate(double x, double y, double z)
    {
        Affine3 xlate = Affine3.fromTranslation(x, y, z);
        this.concatenate(xlate);
        return this;
    }

    public Vec3 getTranslation()
    {
        return new Vec3(this.mMatrix.a14, this.mMatrix.a24, this.mMatrix.a34);
    }
    
    /**
     * Applies the inversion operation to this.
     * @return this (for chaining)
     */
    Affine3 invert()
    {
        DMatrix4x4 inv = new DMatrix4x4();
        CommonOps_DDF4.invert(this.mMatrix, inv);
        this.mMatrix = inv;
        return this;
    }

    /**
     * Combine the effects of rhs with the current transformation.
     * @param rhs
     */
    public void concatenate(final Affine3 rhs)
    {
        DMatrix4x4 result = new DMatrix4x4();
        CommonOps_DDF4.mult(this.mMatrix, rhs.mMatrix, result);
        this.mMatrix = result;
    }

    public Vec3 transformPoint(final Vec3 in)
    {
        DMatrix4 x = new DMatrix4();
        DMatrix4 out4 = new DMatrix4();
        x.a1 = in.a1;
        x.a2 = in.a2;
        x.a3 = in.a3;
        x.a4 = 1; // <---  as point
        CommonOps_DDF4.mult(this.mMatrix, x, out4);
        return new Vec3(out4.a1, out4.a2, out4.a3);
    }

    public ArrayList<Vec3> transformPoints(final Vec3 ...in)
    {
        ArrayList<Vec3> result = new ArrayList<Vec3>();
        DMatrix4 x = new DMatrix4();
        DMatrix4 out4 = new DMatrix4();
        x.a4 = 1; // <---  as point
        for(final Vec3 v : in)
        {
            x.a1 = v.a1;
            x.a2 = v.a2;
            x.a3 = v.a3;
            CommonOps_DDF4.mult(this.mMatrix, x, out4);
            result.add(new Vec3(out4.a1, out4.a2, out4.a3));
        }
        return result;
    }

    public Vec3 transformVector(final Vec3 in)
    {
        DMatrix4 x = new DMatrix4();
        DMatrix4 out4 = new DMatrix4();
        x.a1 = in.a1;
        x.a2 = in.a2;
        x.a3 = in.a3;
        x.a4 = 0; // <--  make it a direction
        CommonOps_DDF4.mult(this.mMatrix, x, out4);
        return new Vec3(out4.a1, out4.a2, out4.a3);
    }

    public ArrayList<Vec3> transformVectors(final Vec3 ...in)
    {
        ArrayList<Vec3> result = new ArrayList<Vec3>();
        DMatrix4 x = new DMatrix4();
        DMatrix4 out4 = new DMatrix4();
        x.a4 = 0; // <--- make it a direction
        for(Vec3 v : in)
        {
            x.a1 = v.a1;
            x.a2 = v.a2;
            x.a3 = v.a3;
            CommonOps_DDF4.mult(this.mMatrix, x, out4);
            result.add(new Vec3(out4.a1, out4.a2, out4.a3));
        }
        return result;
    }

    public ArrayList<Vec3> transformBases()
    {
        return this.transformVectors(new Vec3(1,0,0), 
                                     new Vec3(0,1,0),
                                     new Vec3(0,0,1));
    }
}