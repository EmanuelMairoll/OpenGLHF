#version 330 core

layout (points) in;
layout (line_strip, max_vertices = 256) out;

#define SEGMENTS 250.0
#define M_PI 3.1415926535897932384626433832795

uniform float majorAxis;
uniform float minorAxis;

void main() {
    float x = 0.0;
    float y = 0.0;

    // https://community.khronos.org/t/how-can-i-draw-a-ellipse/31690
    for (float angle = 0.0f; angle <= (2.0f * M_PI); angle += (2.f * M_PI / SEGMENTS)) {
        x = cos(angle) * majorAxis;
        y = sin(angle) * minorAxis;
        gl_Position = vec4(x, y, 0.0, 1.0);
        EmitVertex();
    }
    EndPrimitive();

}