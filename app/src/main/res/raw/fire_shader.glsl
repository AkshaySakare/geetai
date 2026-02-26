uniform float time;
uniform vec2 resolution;

float rand(vec2 c) {
    return fract(sin(dot(c, vec2(12.9898, 78.233))) * 43758.5453);
}

// Basic smooth noise
float noise(vec2 p){
    vec2 i = floor(p);
    vec2 f = fract(p);
    float a = rand(i);
    float b = rand(i + vec2(1.0, 0.0));
    float c = rand(i + vec2(0.0, 1.0));
    float d = rand(i + vec2(1.0, 1.0));
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(a, b, u.x) +
    (c - a) * u.y * (1.0 - u.x) +
    (d - b) * u.x * u.y;
}

float fbm(vec2 p) {
    float value = 0.0;
    float scale = 0.5;
    for (int i = 0; i < 4; i++) {
        value += scale * noise(p);
        p *= 2.0;
        scale *= 0.5;
    }
    return value;
}

half4 main(vec2 fragCoord) {
    vec2 uv = fragCoord / resolution;
    uv.y = 1.0 - uv.y;

    // Shift upward for scrolling effect
    vec2 p = uv * vec2(2.0, 8.0); // stretch vertically
    p.y += time * 0.5;

    // Noise to distort flame shape
    float distortion = fbm(p + vec2(0.0, -time * 0.5));

    // Shape mask (radial falloff)
   float flame = smoothstep(0.8 + 0.1 * distortion, 0.0,
    distance(uv + vec2(0.0, distortion * 0.2), vec2(0.5, 0.0)));

    // Final noise-based flicker
    float flicker = 0.3 + 0.7 * fbm(p + time * 0.3);

    // Color ramp (from red to yellow to white)
    vec3 fireColor = mix(
    vec3(1.0, 0.3, 0.0), // deep red-orange
    vec3(1.0, 0.6, 0.0), // orange
    clamp(uv.y + distortion * 0.3, 0.0, 1.0)
    );
    fireColor = mix(fireColor, vec3(1.0, 1.0, 0.8), pow(uv.y, 6.0));

    // Apply flicker and shape mask
    fireColor *= flame * flicker;

    return half4(fireColor, 1.0);
}
