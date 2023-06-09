#version 330 core

layout (lines) in;
layout (line_strip, max_vertices = 5) out;

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
    vec4 maxBox = gl_in[0].gl_Position;
    vec4 minBox = gl_in[1].gl_Position;

    vec4 upperRightBack = ProjMat * ModelViewMat * vec4(minBox.x, maxBox.y, maxBox.z, 1.0);
    vec4 upperLeftBack = ProjMat * ModelViewMat * vec4(maxBox);
    vec4 lowerRightBack = ProjMat * ModelViewMat * vec4(minBox.x, minBox.y, maxBox.z, 1.0);
    vec4 lowerLeftBack = ProjMat * ModelViewMat * vec4(maxBox.x, minBox.y, maxBox.z, 1.0);

    vec4 upperRightFront = ProjMat * ModelViewMat * vec4(minBox.x, maxBox.y, minBox.z, 1.0);
    vec4 upperLeftFront = ProjMat * ModelViewMat * vec4(maxBox.x, maxBox.y, minBox.z, 1.0);
    vec4 lowerRightFront = ProjMat * ModelViewMat * vec4(minBox);
    vec4 lowerLeftFront = ProjMat * ModelViewMat * vec4(maxBox.x, minBox.y, minBox.z, 1.0);


    vec4 v[8] = vec4[8](upperRightBack, upperLeftBack, lowerRightBack, lowerLeftBack, upperRightFront, upperLeftFront, lowerRightFront, lowerLeftFront);

    float minX = 1.0, maxX = -1.0, minY = 1.0, maxY = -1.0, w = 0;

    for (int i = 0; i < 8; i++) {
        maxX = max(maxX, v[i].x);
        minX = min(minX, v[i].x);
        maxY = max(maxY, v[i].y);
        minY = min(minY, v[i].y);
        w += v[i].w;
    }

    w /= 8;

    gl_Position = vec4(minX, maxY, 0.0, w);
    EmitVertex();
    gl_Position = vec4(maxX, maxY, 0.0, w);
    EmitVertex();
    gl_Position = vec4(maxX, minY, 0.0, w);
    EmitVertex();
    gl_Position = vec4(minX, minY, 0.0, w);
    EmitVertex();
    gl_Position = vec4(minX, maxY, 0.0, w);
    EmitVertex();
    EndPrimitive();






}