package com.example.tunertest1;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer renderer;

    public MyGLSurfaceView(Context context, AttributeSet attrs) {

        super(context, attrs );

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        renderer = new MyGLRenderer(context);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
    }

    public MyGLSurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        renderer = new MyGLRenderer(context);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);

        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
