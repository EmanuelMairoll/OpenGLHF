package com.cgh.openglhf.openglhf.client;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;

public class ShaderProgram {

    private final int programId;

    private int vertexShaderId;

    private int geometryShaderId;

    private int fragmentShaderId;

    private GLUniform modelViewMat;

    private GLUniform projectionMat;

    public ShaderProgram() {
        programId = glCreateProgram();
        if (programId == 0) {
            throw new RuntimeException("Could not create Shader");
        }
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createGeometryShader(String shaderCode) throws Exception {
        geometryShaderId = createShader(shaderCode, GL_GEOMETRY_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    protected int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + "(" + shaderType + ") " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() throws Exception {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        deleteShaders();

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        modelViewMat = GLUniform.createIfExists(programId, "ModelViewMat");
        projectionMat = GLUniform.createIfExists(programId, "ProjMat");
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();

        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }

    public void setModelViewMat(Matrix4f modelViewMat) {
        if (this.modelViewMat != null) {
            this.modelViewMat.setUniformMatrix4fv(modelViewMat);
        }
    }

    public void setProjectionMat(Matrix4f projectionMat) {
        if (this.projectionMat != null) {
            this.projectionMat.setUniformMatrix4fv(projectionMat);
        }
    }

    private void deleteShaders() {
        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
            glDeleteShader(vertexShaderId);
        }
        if (geometryShaderId != 0) {
            glDetachShader(programId, geometryShaderId);
            glDeleteShader(geometryShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
            glDeleteShader(fragmentShaderId);
        }
    }

    public static class GLUniform {
        private final int location;
        private final int programId;

        public static GLUniform createIfExists(int programId, String name) {
            var uniform = new GLUniform(programId, name);
            return uniform.location == -1 ? null : uniform;
        }

        private GLUniform(int programId, String name) {
            this.programId = programId;
            this.location = GL20.glGetUniformLocation(programId, name);
        }

        public void setUniformMatrix4fv(Matrix4f matrix) {
            FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
            matrix.get(buffer);
            setUniformMatrix4fv(buffer);
        }

        public void setUniformMatrix4fv(FloatBuffer matrix) {
            GL20.glUseProgram(programId);
            GL20.glUniformMatrix4fv(location, false, matrix);
            GL20.glUseProgram(0);
        }
    }

}