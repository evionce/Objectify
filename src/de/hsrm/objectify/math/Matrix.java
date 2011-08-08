package de.hsrm.objectify.math;

import Jama.SingularValueDecomposition;

/**
 * This class inherits from the matrix class of the Jama library and adds some
 * functionality both for arcball rotation and calculating the 3d object.
 * 
 * @author kwolf001
 * 
 */
public class Matrix extends Jama.Matrix {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Matrix(int m, int n) {
		super(m,n);
	}
	
	public Matrix(double[][] values) {
		super(values);
	}
	
	public void setRotation(Quat4f q1) {
		if (getColumnDimension() < 4 || getRowDimension() < 4) {
			throw new ArrayIndexOutOfBoundsException();
		}
		
        float n, s;
        float xs, ys, zs;
        float wx, wy, wz;
        float xx, xy, xz;
        float yy, yz, zz;

        n = (q1.x * q1.x) + (q1.y * q1.y) + (q1.z * q1.z) + (q1.w * q1.w);
        s = (n > 0.0f) ? (2.0f / n) : 0.0f;

        xs = q1.x * s;
        ys = q1.y * s;
        zs = q1.z * s;
        wx = q1.w * xs;
        wy = q1.w * ys;
        wz = q1.w * zs;
        xx = q1.x * xs;
        xy = q1.x * ys;
        xz = q1.x * zs;
        yy = q1.y * ys;
        yz = q1.y * zs;
        zz = q1.z * zs;
        
        set(0, 0, 1.0f - (yy + zz));
        set(0, 1, xy - wz);
        set(0, 2, xz + wy);
        set(0, 3, 0f);
        set(1, 0, xy + wz);
        set(1, 1, 1.0f - (xx + zz));
        set(1, 2, yz - wx);
        set(1, 3, 0f);
        set(2, 0, xz - wy);
        set(2, 1, yz + wx);
        set(2, 2, 1.0f - (xx + yy));
        set(2, 3, 0f);
        set(3, 0, 0f);
        set(3, 1, 0f);
        set(3, 2, 0f);
        set(3, 3, 1f);
	}

	/**
	 * Calculates the pseudoinverse of this matrix
	 * 
	 * @return pseudo inverted matrix
	 */
	public Matrix pseudoInverse() {
		if (rank() < 1) {
			return null;
		}
		
		if (getColumnDimension() > getRowDimension()) {
			setMatrix(0, getRowDimension()-1, 0, getColumnDimension()-1, transpose().transpose());
		}
		
		SingularValueDecomposition svdX = new SingularValueDecomposition(this);
		double[] singularValues = svdX.getSingularValues();
		double tol = Math.max(getColumnDimension(), getRowDimension()) * singularValues[0] * 2E-16;
		double[] singularValueReciprocals = new double[singularValues.length];
		for (int i = 0; i < singularValues.length; i++) {
			singularValueReciprocals[i] = Math.abs(singularValues[i]) < tol ? 0
					: (1.0 / singularValues[i]);

		}
		double[][] u = svdX.getU().getArray();
		double[][] v = svdX.getV().getArray();
		int min = Math.min(getColumnDimension(), u[0].length);
		double[][] inverse = new double[getColumnDimension()][getRowDimension()];
		for (int i = 0; i < getColumnDimension(); i++) {
			for (int j = 0; j < u.length; j++) {
				for (int k = 0; k < min; k++) {
					inverse[i][j] += v[i][k] * singularValueReciprocals[k]
							* u[j][k];
				}
			}
		}
		return new Matrix(inverse);
	}
	
	public void map(float[] pdata) {
		pdata[0] = (float) get(0, 0);
    	pdata[1] = (float) get(0, 1); 
    	pdata[2] = (float) get(0, 2); 
    	pdata[3] = (float) get(0, 3); 
    	
    	pdata[4] = (float) get(1, 0); 
    	pdata[5] = (float) get(1, 1); 
    	pdata[6] = (float) get(1, 2); 
    	pdata[7] = (float) get(1, 3); 
    	
    	pdata[8] = (float) get(2, 0); 
    	pdata[9] = (float) get(2, 1); 
    	pdata[10] = (float) get(2, 2); 
    	pdata[11] = (float) get(2, 3); 
    	
    	pdata[12] = (float) get(3, 0); 
    	pdata[13] = (float) get(3, 1); 
    	pdata[14] = (float) get(3, 2); 
    	pdata[15] = (float) get(3, 3); 
	}

	/**
	 * Convenient method for setting the matrix identity
	 */
	public void setIdentity() {
		for (int i=0; i<getRowDimension(); i++) {
			for (int j=0; j<getColumnDimension(); j++) {
				if (i==j) {
					set(i, j, 1.0);
				}
			}
		}
	}

	public Vector3f multiply(VectorNf intensity) {
		if (intensity.getDimension() != getColumnDimension()) {
			throw new AssertionError("Vector dimension must be equal to matrix column dimension");
		}
		Vector3f erg = new Vector3f();
		float[] tmp = new float[] { 0, 0, 0};
		for (int idx=0; idx<3; idx++) {
			for (int i=0; i<intensity.getDimension();i++) {
				tmp[idx] += get(idx, i) * intensity.get(i);
			}
		}
		erg.x = tmp[0];
		erg.y = tmp[1];
		erg.z = tmp[2];
		return erg;
	}

}