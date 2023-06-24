#version 330 core
out vec4 FragColor;

in float distanceGeom;
uniform float majorAxis;
uniform float minorAxis;
uniform ivec2 viewportDimensions;

#define MAX_DISTANCE 100.0

bool isInEllipse(vec2 point)
{
    // https://math.stackexchange.com/a/76463
    float rx = majorAxis / 2;
    float ry = minorAxis / 2;
    return (point.x - 0.5) * (point.x - 0.5) * (ry * ry) + (point.y - 0.5) * (point.y - 0.5) * (rx * rx) <= rx * rx * ry * ry;
}

bool isInCircle(vec2 point)
{
    float r = majorAxis / 2;
    return distance(point, vec2(0.5, 0.5)) <= r;
}

void main()
{

    vec2 FragCoord = gl_FragCoord.xy / viewportDimensions;

    if (majorAxis == minorAxis)
    {
        if (isInCircle(FragCoord)) discard;
    }
    else
    {
        if (isInEllipse(FragCoord)) discard;
    }

    float distanceClamped = clamp(MAX_DISTANCE, 0.0, distanceGeom);
    float x = distanceClamped / MAX_DISTANCE;

    // https://stackoverflow.com/q/6394304
    float r = 2.0 * (1.0 - x);
    float g = 2.0 * x;

    FragColor = vec4(r, g, 0.0, 1.0);
}
