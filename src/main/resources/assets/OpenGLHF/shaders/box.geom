#version 330 core

layout (lines) in;
layout (line_strip, max_vertices = 18) out;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;


//
//   maxX,maxY,maxZ   ________
//                   /       /|
//                  /_______/ |  <-- Back
//                  |      |  |
//                  |      |  |
//       Front -->  |      | /
//                  |______|/   minX,minY,minZ
//

void main()
{
    vec4 minBox = gl_in[0].gl_Position;
    vec4 maxBox = gl_in[1].gl_Position;

    vec4 upperRightBack = ProjMat * ModelViewMat * vec4(minBox.x, maxBox.y, maxBox.z, 1.0);
    vec4 upperLeftBack = ProjMat * ModelViewMat * vec4(maxBox.x, maxBox.y, maxBox.z, 1.0);
    vec4 lowerRightBack =  ProjMat * ModelViewMat * vec4(minBox.x, minBox.y, maxBox.z, 1.0);
    vec4 lowerLeftBack =  ProjMat * ModelViewMat * vec4(maxBox.x, minBox.y, maxBox.z, 1.0);
    vec4 upperRightFront =  ProjMat * ModelViewMat * vec4(minBox.x, maxBox.y, minBox.z, 1.0);
    vec4 upperLeftFront = ProjMat * ModelViewMat * vec4(maxBox.x, maxBox.y, minBox.z, 1.0);
    vec4 lowerRightFront = ProjMat * ModelViewMat * vec4(minBox.x, minBox.y, minBox.z, 1.0);
    vec4 lowerLeftFront = ProjMat * ModelViewMat * vec4(maxBox.x, minBox.y, minBox.z, 1.0);

    // draw front face
    gl_Position = upperLeftFront;
    EmitVertex();
    gl_Position = lowerLeftFront;
    EmitVertex();
    gl_Position = lowerRightFront;
    EmitVertex();
    gl_Position = upperRightFront;
    EmitVertex();
    gl_Position = upperLeftFront;
    EmitVertex();
    EndPrimitive();

    
    // draw back face
    gl_Position = upperLeftBack;
    EmitVertex();
    gl_Position = lowerLeftBack;
    EmitVertex();
    gl_Position = lowerRightBack;
    EmitVertex();
    gl_Position = upperRightBack;
    EmitVertex();
    gl_Position = upperLeftBack;
    EmitVertex();
    EndPrimitive();

    // draw upper left connection line
    gl_Position = upperLeftFront;
    EmitVertex();
    gl_Position = upperLeftBack;
    EmitVertex();
    EndPrimitive();

    // draw upper right connection line
    gl_Position = upperRightFront;
    EmitVertex();
    gl_Position = upperRightBack;
    EmitVertex();
    EndPrimitive();

    // draw upper left connection line
    gl_Position = lowerLeftFront;
    EmitVertex();
    gl_Position = lowerLeftBack;
    EmitVertex();
    EndPrimitive();

    // draw upper right connection line
    gl_Position = lowerRightFront;
    EmitVertex();
    gl_Position = lowerRightBack;
    EmitVertex();
    EndPrimitive();
}





