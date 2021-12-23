package com.matrix.sphere

import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


/**
 * A two-dimensional triangle for use as a drawn object in OpenGL ES 2.0.
 */
class Triangle {
    private val vertexShaderCode =  // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "void main() {" +  // the matrix must be included as a modifier of gl_Position
                // Note that the uMVPMatrix factor *must be first* in order
                // for the matrix multiplication product to be correct.
                "  gl_Position = uMVPMatrix * vPosition;" +
                "}"
    private val fragmentShaderCode = "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}"
    private val vertexBuffer: FloatBuffer
    private val mProgram: Int
    private var mPositionHandle = 0
    private var mColorHandle = 0
    private var mMVPMatrixHandle = 0
    private val vertexCount = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex
    var color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 0.0f)

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
    fun draw(mvpMatrix: FloatArray?) {
        // Add program to OpenGL environment
        GLES30.glUseProgram(mProgram)

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition")

        // Enable a handle to the triangle vertices
        GLES30.glEnableVertexAttribArray(mPositionHandle)

        // Prepare the triangle coordinate data
        GLES30.glVertexAttribPointer(
            mPositionHandle, COORDS_PER_VERTEX,
            GLES30.GL_FLOAT, false,
            vertexStride, vertexBuffer
        )

        // get handle to fragment shader's vColor member
        mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor")

        // Set color for drawing the triangle
        GLES30.glUniform4fv(mColorHandle, 1, color, 0)

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
        MyGLRenderer.checkGlError("glGetUniformLocation")

        // Apply the projection and view transformation
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        MyGLRenderer.checkGlError("glUniformMatrix4fv")

        // Draw the triangle
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount)

        // Disable vertex array
        GLES30.glDisableVertexAttribArray(mPositionHandle)
    }

    companion object {
        // number of coordinates per vertex in this array
        const val COORDS_PER_VERTEX = 3
        var triangleCoords = floatArrayOf( // in counterclockwise order:
            0.0f, 0.622008459f, 0.0f,  // top
            -0.5f, -0.311004243f, 0.0f,  // bottom left
            0.5f, -0.311004243f, 0.0f // bottom right
        )
    }

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    init {
        // initialize vertex byte buffer for shape coordinates
        val bb = ByteBuffer.allocateDirect( // (number of coordinate values * 4 bytes per float)
            triangleCoords.size * 4
        )
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder())

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer()
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords)
        // set the buffer to read the first coordinate
        vertexBuffer.position(0)

        // prepare shaders and OpenGL program
        val vertexShader = MyGLRenderer.loadShader(
            GLES30.GL_VERTEX_SHADER, vertexShaderCode
        )
        val fragmentShader = MyGLRenderer.loadShader(
            GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode
        )
        mProgram = GLES30.glCreateProgram() // create empty OpenGL Program
        GLES30.glAttachShader(mProgram, vertexShader) // add the vertex shader to program
        GLES30.glAttachShader(mProgram, fragmentShader) // add the fragment shader to program
        GLES30.glLinkProgram(mProgram) // create OpenGL program executables
    }
}