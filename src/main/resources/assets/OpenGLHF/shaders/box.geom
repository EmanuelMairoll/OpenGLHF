#version 330 core

layout (points) in;
layout (line_strip, max_vertices = 2) out;

in vec3 VertexColor[];
out vec3 fColor;

void main()
{
   fColor = VertexColor[0];
   gl_Position = gl_in[0].gl_Position;
   EmitVertex();

   gl_Position = gl_in[0].gl_Position + vec4(0.2, 0.0, 0.0, 0.0);
   EmitVertex();
   EndPrimitive();
}