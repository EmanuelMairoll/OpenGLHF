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
    vec4 minBox = gl_in[0].gl_Position;
    vec4 maxBox = gl_in[1].gl_Position;

    vec4 upperRightBack = vec4(minBox.x, maxBox.y, maxBox.z, 1.0);
    vec4 upperLeftBack = vec4(maxBox.x, maxBox.y, maxBox.z, 1.0);
    vec4 lowerRightBack =  vec4(minBox.x, minBox.y, maxBox.z, 1.0);
    vec4 lowerLeftBack =  vec4(maxBox.x, minBox.y, maxBox.z, 1.0);
    vec4 upperRightFront =  vec4(minBox.x, maxBox.y, minBox.z, 1.0);
    vec4 upperLeftFront = vec4(maxBox.x, maxBox.y, minBox.z, 1.0);
    vec4 lowerRightFront = vec4(minBox.x, minBox.y, minBox.z, 1.0);
    vec4 lowerLeftFront = vec4(maxBox.x, minBox.y, minBox.z, 1.0);
    vec4 v[8] = vec4[8](upperRightBack, upperLeftBack, lowerRightBack, lowerLeftBack, upperRightFront, upperLeftFront, lowerRightFront, lowerLeftFront);

    float inf = 1.0 / 0.0;
    float minX = inf, maxX = -inf, minY = inf, maxY = -inf;

    for (int i = 0; i < 8; i++) {
        vec4 screenCoords = ProjMat * ModelViewMat * v[i];

        if (screenCoords.w < 0.0) {
            continue;
        }

        vec4 normalized = screenCoords / screenCoords.w;

        minX = min(minX, normalized.x);
        maxX = max(maxX, normalized.x);
        minY = min(minY, normalized.y);
        maxY = max(maxY, normalized.y);
    }

    gl_Position = vec4(minX, maxY, 0.0, 1.0);
    EmitVertex();
    gl_Position = vec4(maxX, maxY, 0.0, 1.0);
    EmitVertex();
    gl_Position = vec4(maxX, minY, 0.0, 1.0);
    EmitVertex();
    gl_Position = vec4(minX, minY, 0.0, 1.0);
    EmitVertex();
    gl_Position = vec4(minX, maxY, 0.0, 1.0);
    EmitVertex();
    EndPrimitive();

}





