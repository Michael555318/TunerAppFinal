package com.example.tunertest1;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Circle {
    static final int COORDS_PER_VERTEX = 3;
    private float radius = 0.1f;

    private int mProgram, mPositionHandle, mColorHandle, mMVPMatrixHandle;
    private FloatBuffer vertexBuffer;
    private float vertices[];

    float color[] = {0.00f, 0.76953125f, 0.22265625f, 1.0f};
    private final int vertexCount = 364 * 3 / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private final String vertexShaderCode ="attribute vec4 vPosition;"
            + "uniform mat4 uMVPMatrix;"
            + "void main() {"
            + "  gl_Position = uMVPMatrix * vPosition;"
            + "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    public Circle() {
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        vertices = new float[364 * 3];
        vertices[0] = 0;
        vertices[1] = 0;
        vertices[2] = 0;

        for (int i =1; i <364; i++){
            vertices[(i * 3)+ 0] = (float) (radius * Math.cos((3.14/180) * (float)i ));
            vertices[(i * 3)+ 1] = (float) (radius * Math.sin((3.14/180) * (float)i ));
            vertices[(i * 3)+ 2] = 0;
        }


        //Log.v("Thread", "" + vertices[0] + "," + vertices[1] + "," + vertices[2]);
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = vertexByteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);


        mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);
    }


    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);


        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the circle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

}
