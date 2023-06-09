#version 330 core
layout (location = 0) in vec3 boxWorld;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

void main()
{
     gl_Position = vec4(boxWorld, 1.0);
}