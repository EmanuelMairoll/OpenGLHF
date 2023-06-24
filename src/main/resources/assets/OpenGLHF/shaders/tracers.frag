#version 330 core
out vec4 FragColor;

in float distanceGeom;

#define MAX_DISTANCE 100.0

void main()
{

    float distanceClamped = clamp(MAX_DISTANCE, 0.0, distanceGeom);
    float x = distanceClamped / MAX_DISTANCE;
    // https://stackoverflow.com/q/6394304
    FragColor = vec4(2.0 * (1.0 - x), 2.0 * x, 0.0, 1.0);
}