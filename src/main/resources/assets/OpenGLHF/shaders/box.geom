#version 330 core

layout (points) in;
layout (line_strip, max_vertices = 4) out;

in vec4 minBoxNDC[];

void main()
{
    vec4 maxBoxNDC = gl_in[0].gl_Position;

    gl_Position = vec4(maxBoxNDC.x, maxBoxNDC.y, minBoxNDC[0].z, 1.0);
    EmitVertex();
    gl_Position = vec4(maxBoxNDC.x, minBoxNDC[0].y, minBoxNDC[0].z, 1.0);
    EmitVertex();
    gl_Position = vec4(minBoxNDC[0].x, minBoxNDC[0].y, minBoxNDC[0].z, 1.0);
    EmitVertex();
    gl_Position = vec4(minBoxNDC[0].x, maxBoxNDC.y, minBoxNDC[0].z, 1.0);
    EmitVertex();
    EndPrimitive();


    gl_Position = vec4(maxBoxNDC.x, maxBoxNDC.y, minBoxNDC[0].z, 1.0);
    EmitVertex();
    gl_Position = vec4(minBoxNDC[0].x, maxBoxNDC.y, minBoxNDC[0].z, 1.0);
    EmitVertex();
    gl_Position = vec4(maxBoxNDC.x, minBoxNDC[0].y, minBoxNDC[0].z, 1.0);
    EmitVertex();
    gl_Position = vec4(minBoxNDC[0].x, minBoxNDC[0].y, minBoxNDC[0].z, 1.0);
    EmitVertex();
    EndPrimitive();

    gl_Position = vec4(maxBoxNDC.x, maxBoxNDC.y, maxBoxNDC.z, 1.0);
    EmitVertex();
    gl_Position = vec4(maxBoxNDC.x, maxBoxNDC.y, minBoxNDC[0].z, 1.0);
    EmitVertex();
    EndPrimitive();

    gl_Position = vec4(minBoxNDC[0].x, maxBoxNDC.y, maxBoxNDC.z, 1.0);
    EmitVertex();
    gl_Position = vec4(minBoxNDC[0].x, maxBoxNDC.y, minBoxNDC[0].z, 1.0);
    EmitVertex();
    EndPrimitive();

    gl_Position = vec4(maxBoxNDC.x, minBoxNDC[0].y, maxBoxNDC.z, 1.0);
    EmitVertex();
    gl_Position = vec4(maxBoxNDC.x, minBoxNDC[0].y, minBoxNDC[0].z, 1.0);
    EmitVertex();
    EndPrimitive();

    gl_Position = vec4(minBoxNDC[0].x, minBoxNDC[0].y, maxBoxNDC.z, 1.0);
    EmitVertex();
    gl_Position = vec4(minBoxNDC[0].x, minBoxNDC[0].y, minBoxNDC[0].z, 1.0);
    EmitVertex();
    EndPrimitive();
}