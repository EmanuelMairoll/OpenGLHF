package com.cgh.openglhf.openglhf.client.renderer.custom;

import com.cgh.openglhf.openglhf.client.ShaderProgram;
import com.cgh.openglhf.openglhf.client.Utils;

public class RectRenderer extends BoxRenderer {

    @Override
    protected ShaderProgram makeShader() throws Exception {
        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/assets/OpenGLHF/shaders/rect.vert"));
        shaderProgram.createGeometryShader(Utils.loadResource("/assets/OpenGLHF/shaders/rect.geom"));
        shaderProgram.createFragmentShader(Utils.loadResource("/assets/OpenGLHF/shaders/rect.frag"));
        shaderProgram.link();
        return shaderProgram;
    }
}
