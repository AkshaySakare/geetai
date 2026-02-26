uniform float2 resolution;
uniform float2 lightPos;
uniform float intensity;
uniform float radius;
uniform float time;

float noise(float2 p) {
    return fract(sin(dot(p, float2(12.9898,78.233))) * 43758.5453);
}

float smoothNoise(float2 p) {
    float2 i = floor(p);
    float2 f = fract(p);
    float a = noise(i);
    float b = noise(i + float2(1.0, 0.0));
    float c = noise(i + float2(0.0, 1.0));
    float d = noise(i + float2(1.0, 1.0));
    float2 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float fbm(float2 p) {
    float total = 0.0;
    float amplitude = 0.5;
    for (int i = 0; i < 5; i++) {
        total += smoothNoise(p) * amplitude;
        p *= 2.0;
        amplitude *= 0.5;
    }
    return total;
}

half4 main(float2 fragCoord) {
    float2 uv = float2(fragCoord.x / resolution.x, fragCoord.y / resolution.y);
    uv.y = 1.0 - uv.y;

    // Move upward over time
    float2 smokePos = uv * 3.0 + float2(0.0, -time * 0.2);

    // Turbulent noise field
    float smoke = fbm(smokePos);

    // Shape fade (tight at bottom, fades upward)
    float mask = smoothstep(0.4, 1.0, uv.y);

    float alpha = smoothstep(0.5, 1.0, smoke) * mask;

    // White smoke on black background
    float3 smokeColor = float3(1.0);
    float3 bgColor = float3(0.0);
    float3 finalColor = mix(bgColor, smokeColor, alpha);

    return half4(finalColor, 1.0);
}
