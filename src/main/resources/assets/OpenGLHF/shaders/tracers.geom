#version 330 core

layout (points) in;
layout (line_strip, max_vertices = 2) out;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

void renderTracers(vec4 position)
{
    gl_Position = ProjMat * ModelViewMat * position;
    EmitVertex();
    gl_Position = vec4(0, 0, 0, 1);
    EmitVertex();
    EndPrimitive();
}

void main() {
    renderTracers(gl_in[0].gl_Position);
}