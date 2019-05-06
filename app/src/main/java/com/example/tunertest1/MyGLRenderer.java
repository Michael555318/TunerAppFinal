package com.example.tunertest1;

import android.content.Context;
import android.opengl.EGLConfig;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import javax.microedition.khronos.opengles.GL10;

import static android.content.ContentValues.TAG;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    Triangle pointer;
    Circle core;
    Sprite cr_scale;
    Context context;
    float angle1 = 0.09f;

    private final double[] noteFrequencies = new double[]{  7902,    7459,    7040,    6645,    6272,
            5920, 5587.65, 5274.04, 4978.03, 4698.64, 4434.92, 4186.01, 3951.07, 3729.31, 3520.00,
            3322.44, 3135.96, 2959.96, 2793.83, 2637.02, 2489.02, 2349.32, 2217.46, 2093.00, 1975.53,
            1864.66, 1760.00, 1661.22, 1567.98, 1479.98, 1396.91, 1318.51, 1244.51, 1174.66, 1108.73,
            1046.50, 987.767, 932.328, 880.000, 830.609, 783.991, 739.989, 698.456, 659.255, 622.254,
            587.330, 554.365, 523.251, 493.883, 466.164, 440.000, 415.305, 391.995, 369.994, 349.228,
            329.628, 311.127, 293.665, 277.183, 261.626, 246.942, 233.082, 220.000, 207.652, 195.998,
            184.997, 174.614, 164.814, 155.563, 146.832, 138.591, 130.813, 123.471, 116.541, 110.000,
            103.826, 97.9989, 92.4986, 87.3071, 82.4069, 77.7817, 73.4162, 69.2957, 65.4064, 61.7354,
            58.2705, 55.0000, 51.9131, 48.9994, 46.2493, 43.6535, 41.2034, 38.8909, 36.7081, 34.6478,
            32.7032, 30.8677, 29.1352, 27.5000, 25.9565, 24.4997, 23.1247, 21.8268, 20.6017, 19.4454,
            18.3540, 17.3239, 16.3516};

    private float[] rotationMatrix = new float[16];
    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    public MyGLRenderer(Context ctx) {
        this.context = ctx;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, javax.microedition.khronos.egl.EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        pointer = new Triangle();
        core = new Circle();
        cr_scale = new Sprite(context, 0f, -50f);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];

        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        // Create a rotation transformation for the triangle
        //long time = SystemClock.uptimeMillis() % 4000L;
        //float angle = 0.090f * ((int) time);
        float angle = getAngle(MainActivity.frequency);
        Matrix.setRotateM(rotationMatrix, 0, angle, 0, 0, -1.0f);


        // Combine the rotation matrix with the projection and camera view
        // Note that the vPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0);

        cr_scale.draw(vPMatrix);
        pointer.draw(scratch);
        core.draw(vPMatrix);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    private float getAngle(double frequency) {
        String noteName = findNote(frequency);
        if (noteName.equals("C")) {
            angle1 = 0.09f * 30;
            angle1+=findScaledDiff(frequency)*0.09f*30;
        } else if (noteName.equals("C♯")) {
            angle1 = 0.09f * -350;
            angle1+=findScaledDiff(frequency)*0.09f*30;
        } else if (noteName.equals("D")) {
            angle1 = 0.09f * -650;
            angle1+=findScaledDiff(frequency)*0.09f*30;
        } else if (noteName.equals("E♭")) {
            angle1 = 0.09f * -1000;
            angle1+=findScaledDiff(frequency)*0.09f*30;
        } else if (noteName.equals("E")) {
            angle1 = 0.09f * -1350;
            angle1+=findScaledDiff(frequency)*0.09f*30;
        } else if (noteName.equals("F")) {
            angle1 = 0.09f * -1650;
            angle1+=findScaledDiff(frequency)*0.09f*30;
        } else if (noteName.equals("F♯")) {
            angle1 = 0.09f * -2050;
            angle1+=findScaledDiff(frequency)*0.09f*30;
        } else if (noteName.equals("G")) {
            angle1 = 0.09f * -2350;
            angle1+=findScaledDiff(frequency)*0.09f*30;
        } else if (noteName.equals("G♯")) {
            angle1 = 0.09f * -2650;
            angle1+=findScaledDiff(frequency)*0.09f*30;
        } else if (noteName.equals("A")) {
            angle1 = 0.09f * -2950;
            angle1+=findScaledDiff(frequency)*0.09f*30;
        } else if (noteName.equals("B♭")) {
            angle1 = 0.09f * -3250;
            angle1+=findScaledDiff(frequency)*0.09f*30;
        } else {
            angle1 = 0.09f * -3650;
            angle1+=findScaledDiff(frequency)*0.09f*30;
        }
        return angle1;
    }

    private String findNote(double frequency) {
        double minDifference = 10;
        int index = 0;
        if (frequency != -1) {
            for (int i = 0; i < noteFrequencies.length; i++) {
                if (Math.abs(frequency - noteFrequencies[i]) <= minDifference) {
                    minDifference = Math.abs(frequency - noteFrequencies[i]);
                    index = i;
                }
            }
            return getNoteName(index);
        } else {
            return " ";
        }
    }

    private int findScaledDiff(double frequency) {  // percent off from a scale of 1 to 10
        double minDifference = 10;
        int index = 0;

        for (int i = 0; i < noteFrequencies.length; i++) {
            if (Math.abs(frequency - noteFrequencies[i]) < minDifference) {
                minDifference = Math.abs(frequency - noteFrequencies[i]);
                index = i;
            }
        }
        if (index > 0) {
            double nextFrequency = noteFrequencies[index-1];
            double thisFrequency = noteFrequencies[index];
            double lastFrequency = noteFrequencies[index+1];
            if (frequency >= lastFrequency && frequency <= thisFrequency) {
                double diff = thisFrequency - lastFrequency;
                double scaleSection = diff / 5;
                int n = 1;
                while (thisFrequency - n*scaleSection > frequency) {
                    thisFrequency -= n*scaleSection;
                    n++;
                }
                return +n;
            } else if (frequency >= thisFrequency && frequency <= nextFrequency) {
                double diff = nextFrequency - thisFrequency;
                double scaleSection = diff / 5;
                int n = 1;
                while (thisFrequency + n*scaleSection < frequency) {
                    thisFrequency += n*scaleSection;
                    n++;
                }
                return -n;
            }
        }
        return 0;
    }

    private String getNoteName(int index) {
        if (index%12 == 11) {
            return "C";
        } else if (index%12 == 10) {
            return "C♯";
        } else if (index%12 == 9) {
            return "D";
        } else if (index%12 == 8) {
            return "E♭";
        } else if (index%12 == 7) {
            return "E";
        } else if (index%12 == 6) {
            return "F";
        } else if (index%12 == 5) {
            return "F♯";
        } else if (index%12 == 4) {
            return "G";
        } else if (index%12 == 3) {
            return "G♯";
        } else if (index%12 == 2) {
            return "A";
        } else if (index%12 == 1) {
            return "B♭";
        } else {
            return "B";
        }
    }
}
