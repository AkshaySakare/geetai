

uniform float time;
uniform float2 resolution;

half4 main(float2 fragCoord) {
    float2 uv = float2(fragCoord.x / resolution.x, fragCoord.y / resolution.y);

    float r = 0.5 + 0.5 * sin(time + uv.x * 6.2831);
    float g = 0.5 + 0.5 * sin(time + uv.x * 6.2831 + 2.0944);
    float b = 0.5 + 0.5 * sin(time + uv.x * 6.2831 + 4.1888);
    return half4(r, g, b, 1.0);
}