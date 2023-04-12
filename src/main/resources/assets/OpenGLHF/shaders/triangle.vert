#version 330 core
layout (location = 0) in vec3 aPos;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

void main()
{
    gl_Position = ProjMat * ModelViewMat * vec4(aPos, 1.0);
}