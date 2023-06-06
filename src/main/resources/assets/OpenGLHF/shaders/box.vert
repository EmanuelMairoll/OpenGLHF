#version 330 core
layout (location = 0) in vec3 maxBox;
layout (location = 1) in vec3 minBox;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 minBoxNDC;

void main()
{
     gl_Position = ProjMat * ModelViewMat * vec4(maxBox,1.0);
     minBoxNDC = ProjMat * ModelViewMat * vec4(minBox, 1.0);
}